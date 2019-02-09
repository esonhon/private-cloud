package net.irext.decode.service.businesslogic;

import com.squareup.okhttp.*;
import net.irext.decode.service.cache.IDecodeSessionRepository;
import net.irext.decode.service.cache.IIRBinaryRepository;
import net.irext.decode.service.model.RemoteIndex;
import net.irext.decode.service.utils.FileUtil;
import net.irext.decode.service.utils.LoggerUtil;
import net.irext.decode.service.utils.MD5Util;
import net.irext.decode.sdk.IRDecode;
import net.irext.decode.sdk.bean.ACStatus;
import org.apache.commons.io.IOUtils;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;

/**
 * Filename:       CollectCodeLogic
 * Revised:        Date: 2018-12-08
 * Revision:       Revision: 1.0
 * <p>
 * Description:    IRext Code Collector Collect Code Logic
 * <p>
 * Revision log:
 * 2018-12-08: created by strawmanbobi
 */
public class DecodeLogic {

    private static final String TAG = DecodeLogic.class.getSimpleName();

    private static final String IR_BIN_FILE_PREFIX = "irda_";
    private static final String IR_BIN_FILE_SUFFIX = ".bin";

    private static final String IR_BIN_DOWNLOAD_PREFIX = "http://irext-debug.oss-cn-hangzhou.aliyuncs.com/";

    private static DecodeLogic decodeLogic;

    public static DecodeLogic getInstance() {
        if (null == decodeLogic) {
            decodeLogic = new DecodeLogic();
        }
        return decodeLogic;
    }

    public RemoteIndex openIRBinary(ServletContext context, IIRBinaryRepository irBinaryRepository,
                               RemoteIndex remoteIndex) {
        if (null == remoteIndex) {
            return null;
        }
        try {
            String checksum = remoteIndex.getBinaryMd5().toUpperCase();
            String remoteMap = remoteIndex.getRemoteMap();

            LoggerUtil.getInstance().trace(TAG, "checksum for remoteIndex " +
                    remoteIndex.getId() + " = " + checksum);

            RemoteIndex cachedRemoteIndex = irBinaryRepository.find(remoteIndex.getId());
            if (null != cachedRemoteIndex) {
                LoggerUtil.getInstance().trace(TAG, "binary content fetched from redis : " +
                        cachedRemoteIndex.getRemoteMap());
                // validate binary content
                String cachedChecksum =
                        MD5Util.byteArrayToHexString(MessageDigest.getInstance("MD5")
                                .digest(cachedRemoteIndex.getBinaries())).toUpperCase();
                if (cachedChecksum.equals(checksum)) {
                    return cachedRemoteIndex;
                }
            }

            // otherwise, read from file or OSS
            if (null != context) {
                String downloadPath = context.getRealPath("") + "bin_cache" + File.separator;
                String fileName = IR_BIN_FILE_PREFIX + remoteMap + IR_BIN_FILE_SUFFIX;
                String localFilePath = downloadPath + fileName;

                File binFile = new File(localFilePath);
                FileInputStream fin = getFile(binFile, downloadPath, fileName, checksum);
                if (null != fin) {
                    byte[] newBinaries = IOUtils.toByteArray(fin);
                    LoggerUtil.getInstance().trace(TAG, "binary content get, save it to redis");
                    remoteIndex.setBinaries(newBinaries);
                    irBinaryRepository.add(remoteIndex.getId(), remoteIndex);
                    return remoteIndex;
                }
            } else {
                LoggerUtil.getInstance().trace(TAG, "servlet context is null");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        return null;
    }

    public int[] decode(RemoteIndex remoteIndex, ACStatus acStatus, int keyCode, int changeWindDirection) {
        int[] decoded = null;
        if (null != remoteIndex) {
            int categoryId = remoteIndex.getCategoryId();
            int subCate = remoteIndex.getSubCate();
            byte[] binaryContent = remoteIndex.getBinaries();
            IRDecode irDecode = IRDecode.getInstance();
            int ret = irDecode.openBinary(categoryId, subCate, binaryContent, binaryContent.length);
            if (0 == ret) {
                decoded = irDecode.decodeBinary(keyCode, acStatus, changeWindDirection);
            }
            irDecode.closeBinary();
            return decoded;
        }
        return null;
    }

    public void close(IDecodeSessionRepository decodeSessionRepository, String sessionId) {
        decodeSessionRepository.delete(sessionId);
    }

    // helper methods
    private FileInputStream getFile(File binFile, String downloadPath, String fileName, String checksum) {
        try {
            if (binFile.exists()) {
                FileInputStream fileInputStream = new FileInputStream(binFile);
                // validate binary content
                byte[] binaries = IOUtils.toByteArray(fileInputStream);
                String fileChecksum =
                        MD5Util.byteArrayToHexString(MessageDigest.getInstance("MD5").digest(binaries)).toUpperCase();

                if (fileChecksum.equals(checksum)) {
                    return new FileInputStream(binFile);
                }
            }
            InputStream inputStream = getBinInputStream(fileName);
            // validate binary content
            if (null != inputStream) {
                byte[] binaries = IOUtils.toByteArray(inputStream);
                inputStream.close();
                String ossChecksum =
                        MD5Util.byteArrayToHexString(MessageDigest.getInstance("MD5").digest(binaries)).toUpperCase();
                if (ossChecksum.equals(checksum)) {
                    FileUtil.createDirs(downloadPath);
                    if (FileUtil.write(binFile, binaries)) {
                        LoggerUtil.getInstance().trace(TAG,"fatal : download file successfully");
                        return new FileInputStream(binFile);
                    } else {
                        LoggerUtil.getInstance().trace(TAG,"fatal : write file to local path failed");
                    }
                } else {
                    LoggerUtil.getInstance().trace(TAG,"fatal : checksum does not match even downloaded from OSS, " +
                            " please contact the admin");
                }
            } else{
                LoggerUtil.getInstance().trace(TAG,"fatal : download file failed");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private InputStream getBinInputStream(String fileName) {
        String downloadURL = IR_BIN_DOWNLOAD_PREFIX + fileName;
        try {
            LoggerUtil.getInstance().trace(TAG,"download file from OSS : " + downloadURL);
            return getFileByteStreamByURL(downloadURL);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private InputStream getFileByteStreamByURL(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        Response response = new OkHttpClient().newCall(request).execute();
        return response.body().byteStream();
    }
}
