package com.bwie.utils;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * @Author Sun
 * @Version 1.0
 * @description: TODO
 * @Date 2022/11/18 15:26:00
 */
@Data
public class AliyunOssUtil {
    private static AliyunOssUtil _instance = null;
    private static OSS _ossClient = null;

    private String fileUrl = null;
    private String fileName = null;
    private long fileSize = 0L;
    private static String endpoint = "http://oss-cn-beijing.aliyuncs.com";
    private static String accessKeyId = "LTAI5t8Hzh8n5jqbxEiREyGB";
    private static String accessKeySecret = "J3uK7anA86sdAf0OySlIfCrAwqQCzj";
    private static String bucketName = "examplebucket-2005a";
    private static String domain = "https://images.weserv.nl/?url=examplebucket-2005a.oss-cn-beijing.aliyuncs.com/";

    private AliyunOssUtil() {

    }

    public static AliyunOssUtil OSS() {
        if(_instance == null) {
            _instance = new AliyunOssUtil();
            _ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
            return _instance;
        }

        return _instance;
    }

    /**
     * @description 上传本地文件接口
     * @author 军哥
     * @date 2022/6/25 11:35
     * @version 1.0
     */
    public String upload(String fileName) {
        //--1 随机起名
        UUID uuid = UUID.randomUUID();
        String suffix = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
        String newName = "temp/"+uuid.toString() + suffix;

        String url = null;

        try {
            InputStream inputStream = new FileInputStream(fileName);
            _ossClient.putObject(bucketName, newName, inputStream);

            url = domain + newName;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return url;
    }

    public String upload(MultipartFile file) {
        //--1 判断文件是否有效
        if(file.isEmpty()){
            return null;
        }

        //--2 生成随机文件名
        this.fileName = file.getOriginalFilename();
        this.fileSize = file.getSize();
        UUID uuid = UUID.randomUUID();
        String suffix = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
        String newName = "temp/"+uuid.toString() + suffix;

        String url = null;
        try {
            PutObjectResult result = _ossClient.putObject(bucketName, newName, file.getInputStream());
            url = domain + newName;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        this.fileUrl=url;
        return url;
    }
}
