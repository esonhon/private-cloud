package net.irext.decoder.alioss;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.Bucket;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectResult;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Filename:       OSSHelper.java
 * Revised:        Date: 2017-05-10
 * Revision:       Revision: 1.0
 * <p>
 * Description:    Aliyun OSS file operation helper
 * <p>
 * Revision log:
 * 2017-05-10: created by strawmanbobi
 */
public class OSSHelper {

    public static String BUCKET_NAME = "irext-debug";

    private OSSClient mOSSClient;

    public OSSHelper() {
        String END_POINT = "oss-cn-hangzhou.aliyuncs.com";
        String ACCESS_KEY = "T82nbipHSESmHzd8";
        String ACCESS_SECRET = "SOweQ8UVwCwPr2NC8EC89EOeKJc5Um";

        mOSSClient = new OSSClient(END_POINT, ACCESS_KEY, ACCESS_SECRET);
    }

    public boolean createBucket(String bucketName) {
        Bucket bucket = mOSSClient.createBucket(bucketName);
        return bucketName.equals(bucket.getName());
    }

    public final void deleteBucket(String bucketName) {
        mOSSClient.deleteBucket(bucketName);
    }

    public final String uploadObject2OSS(File file, String bucketName, String diskName) {
        String resultStr = null;
        try {
            InputStream is = new FileInputStream(file);
            String fileName = file.getName();
            Long fileSize = file.length();
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(is.available());
            metadata.setCacheControl("no-cache");
            metadata.setHeader("Pragma", "no-cache");
            metadata.setContentEncoding("utf-8");
            metadata.setContentType(getContentType(fileName));
            metadata.setContentDisposition("filename/filesize=" + fileName + "/" + fileSize + "Byte.");
            PutObjectResult putResult = mOSSClient.putObject(bucketName, diskName + fileName, is, metadata);
            resultStr = putResult.getETag();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultStr;
    }

    public InputStream getOSS2InputStream(String bucketName, String diskName, String key) {
        OSSObject ossObj = mOSSClient.getObject(bucketName, diskName + key);
        return ossObj.getObjectContent();
    }

    public void deleteFile(String bucketName, String diskName, String key) {
        mOSSClient.deleteObject(bucketName, diskName + key);
    }

    private String getContentType(String fileName) {
        String fileExtension = fileName.substring(fileName.lastIndexOf("."));
        if ("bmp".equalsIgnoreCase(fileExtension)) return "image/bmp";
        if ("gif".equalsIgnoreCase(fileExtension)) return "image/gif";
        if ("jpeg".equalsIgnoreCase(fileExtension) ||
                "jpg".equalsIgnoreCase(fileExtension) ||
                "png".equalsIgnoreCase(fileExtension))
            return "image/jpeg";
        if ("html".equalsIgnoreCase(fileExtension)) return "text/html";
        if ("txt".equalsIgnoreCase(fileExtension)) return "text/plain";
        if ("vsd".equalsIgnoreCase(fileExtension)) return "application/vnd.visio";
        if ("ppt".equalsIgnoreCase(fileExtension) || "pptx".equalsIgnoreCase(fileExtension))
            return "application/vnd.ms-powerpoint";
        if ("doc".equalsIgnoreCase(fileExtension) || "docx".equalsIgnoreCase(fileExtension))
            return "application/msword";
        if ("xml".equalsIgnoreCase(fileExtension)) return "text/xml";
        return "text/html";
    }
}
