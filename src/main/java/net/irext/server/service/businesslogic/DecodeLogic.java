package net.irext.server.service.businesslogic;

import com.squareup.okhttp.*;
import net.irext.server.sdk.bean.TemperatureRange;
import net.irext.server.service.cache.IDecodeSessionRepository;
import net.irext.server.service.cache.IIRBinaryRepository;
import net.irext.server.service.model.ACParameters;
import net.irext.server.service.model.RemoteIndex;
import net.irext.server.service.utils.FileUtil;
import net.irext.server.service.utils.LoggerUtil;
import net.irext.server.service.utils.MD5Util;
import net.irext.server.sdk.IRDecode;
import net.irext.server.sdk.bean.ACStatus;
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
@SuppressWarnings("Duplicates")
public class DecodeLogic {

    private static final String TAG = DecodeLogic.class.getSimpleName();
    private static final boolean DEBUG = false;

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

    public int[] decode(RemoteIndex remoteIndex, ACStatus acStatus,
                        int keyCode, int changeWindDirection) {
        try {
            int[] decoded = null;
            synchronized (this) {
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
                    /*
                    decoded = irDecode.decodeBinary(categoryId, subCate, binaryContent, binaryContent.length,
                            keyCode, acStatus, changeWindDirection);
                    */
                    return decoded;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public ACParameters getACParameters(RemoteIndex remoteIndex, int mode) {
        if (null != remoteIndex) {
            try {
                ACParameters acParameters = new ACParameters();
                int categoryId = remoteIndex.getCategoryId();
                int subCate = remoteIndex.getSubCate();
                byte[] binaryContent = remoteIndex.getBinaries();
                IRDecode irDecode = IRDecode.getInstance();
                int ret = irDecode.openBinary(categoryId, subCate, binaryContent, binaryContent.length);
                if (0 == ret) {
                    int[] supportedModes = irDecode.getACSupportedMode();

                    if (DEBUG) {
                        LoggerUtil.getInstance().trace(TAG, "supported modes got : ");
                        for (int i = 0; i < supportedModes.length; i++) {
                            LoggerUtil.getInstance().trace(TAG, "supported mode [" + i + "] = " + supportedModes[i]);
                        }
                    }

                    acParameters.setSupportedModes(supportedModes);
                    if (1 == supportedModes[mode]) {
                        // if this mode is really supported by this AC, get other parameters
                        TemperatureRange temperatureRange = irDecode.getTemperatureRange(mode);
                        int[] supportedWindSpeed = irDecode.getACSupportedWindSpeed(mode);

                        if (DEBUG) {
                            LoggerUtil.getInstance().trace(TAG, "supported wind speed got for mode : " + mode);
                            for (int i = 0; i < supportedWindSpeed.length; i++) {
                                LoggerUtil.getInstance().trace(TAG, "supported wind speed [" + i + "] = " + supportedWindSpeed[i]);
                            }
                        }
                        int[] supportedSwing = irDecode.getACSupportedSwing(mode);

                        if (DEBUG) {
                            LoggerUtil.getInstance().trace(TAG, "supported swing got for mode : " + mode);
                            for (int i = 0; i < supportedSwing.length; i++) {
                                LoggerUtil.getInstance().trace(TAG, "supported swing [" + i + "] = " + supportedSwing[i]);
                            }
                        }

                        int supportedWindDirection = irDecode.getACSupportedWindDirection(mode);

                        if (DEBUG) {
                            LoggerUtil.getInstance().trace(TAG,
                                    "supported wind directions for mode : " + mode +
                                            " = " + supportedWindDirection);
                        }

                        acParameters.setTempMax(temperatureRange.getTempMax());
                        acParameters.setTempMin(temperatureRange.getTempMin());
                        acParameters.setSupportedWindSpeed(supportedWindSpeed);
                        acParameters.setSupportedSwing(supportedSwing);
                        acParameters.setSupportedWindSpeed(supportedWindSpeed);
                    }
                }
                irDecode.closeBinary();
                return acParameters;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
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
