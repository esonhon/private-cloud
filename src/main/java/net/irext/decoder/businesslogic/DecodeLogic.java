package net.irext.decoder.businesslogic;

import net.irext.decoder.alioss.OSSHelper;
import net.irext.decoder.model.IRBinary;
import net.irext.decoder.model.RemoteIndex;
import net.irext.decoder.redisrepo.IIRBinaryRepository;
import net.irext.decoder.utils.FileUtils;
import net.irext.decoder.utils.MD5Util;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
@Controller
public class DecodeLogic {

    private static final String IR_BIN_FILE_PREFIX = "irda_";
    private static final String IR_BIN_FILE_SUFFIX = ".bin";

    private static DecodeLogic decodeLogic;

    @Autowired
    private ServletContext context;

    public static DecodeLogic getInstance() {
        if (null == decodeLogic) {
            decodeLogic = new DecodeLogic();
        }
        return decodeLogic;
    }

    public DecodeLogic() {

    }

    public byte[] openIRBinary(IIRBinaryRepository irBinaryRepository, RemoteIndex remoteIndex) {
        if (null == remoteIndex) {
            return null;
        }
        try {
            String checksum = remoteIndex.getBinaryMd5().toUpperCase();
            String remoteMap = remoteIndex.getRemoteMap();

            IRBinary irBinary = irBinaryRepository.find(remoteIndex.getId());
            if (null != irBinary) {
                byte[] binaries = irBinary.getBinary();
                if (null != binaries) {
                    System.out.println("binary content fetched from redis : " + binaries.length);
                    // validate binary content
                    String cachedChecksum =
                            MD5Util.byteArrayToHexString(MessageDigest.getInstance("MD5").digest(binaries)).toUpperCase();
                    if (cachedChecksum.equals(checksum)) {
                        return binaries;
                    }
                }
            }

            // otherwise, read from file or OSS
            String downloadPath = context.getRealPath("") + "bin_cache" + File.separator;
            String fileName = IR_BIN_FILE_PREFIX + remoteMap + IR_BIN_FILE_SUFFIX;
            String localFilePath = downloadPath + fileName;

            File binFile = new File(localFilePath);
            FileInputStream fin = getFile(binFile, downloadPath, fileName, checksum);
            if (null != fin) {
                byte[] binaries = IOUtils.toByteArray(fin);
                System.out.println("binary content get, save it to redis");
                irBinaryRepository.add(new IRBinary(remoteIndex.getId(), binaries));
                return binaries;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        return null;
    }

    public int[] decode() {
        return null;
    }

    // helper methods
    private FileInputStream getFile(File binFile, String downloadPath, String fileName, String checksum) {
        try {
            if (binFile.exists()) {
                FileInputStream fileInputStream = new FileInputStream(binFile);
                // validate binary content
                byte []binaries = IOUtils.toByteArray(fileInputStream);
                String fileChecksum =
                        MD5Util.byteArrayToHexString(MessageDigest.getInstance("MD5").digest(binaries)).toUpperCase();

                if (fileChecksum.equals(checksum)) {
                    return new FileInputStream(binFile);
                }
            }
            OSSHelper ossHelper = new OSSHelper();
            InputStream inputStream = ossHelper.getOSS2InputStream(OSSHelper.BUCKET_NAME, "", fileName);
            // validate binary content
            byte []binaries = IOUtils.toByteArray(inputStream);
            String ossChecksum =
                    MD5Util.byteArrayToHexString(MessageDigest.getInstance("MD5").digest(binaries)).toUpperCase();
            if (ossChecksum.equals(checksum)) {
                FileUtils.createDirs(downloadPath);
                if (FileUtils.write(binFile, binaries)) {
                    return new FileInputStream(binFile);
                } else {
                    System.out.println("fatal : write file to local path failed");
                }
            } else {
                System.out.println("fatal : checksum does not match even downloaded from OSS," +
                        " please contact the admin");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
