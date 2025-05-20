package com.astral.common.utils;

import com.UpYun;
import com.astral.common.config.AstralConfig;
import com.upyun.FormUploader;
import com.upyun.Params;
import com.upyun.Result;
import com.upyun.UpException;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpYunUtil {

    private static String bucket;

    private static String operator;

    private static String password;

    private static String domain;

    public static void setBucket(String bucket) {
        UpYunUtil.bucket = bucket;
    }

    public static String getBucket() {
        return bucket;
    }

    public static void setOperator(String operator) {
        UpYunUtil.operator = operator;
    }

    public static String getOperator() {
        return operator;
    }

    public static void setPassword(String password) {
        UpYunUtil.password = password;
    }

    public static String getPassword() {
        return password;
    }

    public static String getDomain() {
        return domain;
    }

    public static void setDomain(String domain) {
        UpYunUtil.domain = domain;
    }

    public static String upload(String savePath, File file) {

        // 创建FormUploader实例
        FormUploader uploader = new FormUploader(bucket, operator, password);

        // 设置上传参数
        Map<String, Object> paramsMap = new HashMap<>();
        String uploadDir = AstralConfig.getUploadDir();
        if (StringUtils.hasLength(uploadDir)) {
            savePath = uploadDir + "/" + savePath;
        }
        paramsMap.put(Params.SAVE_KEY, savePath);

        try {
            // 执行上传
            Result result = uploader.upload(paramsMap, file);
            // 处理上传结果
            if (result.isSucceed()) {
                System.out.println("文件上传成功！");
            } else {
                System.out.println("文件上传失败！");
                System.out.println("错误信息: " + result.getMsg());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("文件上传失败->" + e.getMessage());
        }
        return savePath;
    }

    public static boolean deleteFile(String dirPath) throws IOException, UpException {
        UpYun upyun = new UpYun(bucket, operator, password);
        Map<String, String> params = new HashMap<String, String>();
        params.put(UpYun.PARAMS.KEY_X_LIST_LIMIT.getValue(), "100");
        List<UpYun.FolderItem> folderItems = new ArrayList<>();
        do {
            folderItems = upyun.readDir(dirPath, params);
            if (!CollectionUtils.isEmpty(folderItems)) {
                folderItems.forEach(item -> {
                    try {
                        if ("folder".equals(item.type) || "Folder".equals(item.type)) {
                            deleteFile(dirPath + "/" + item.name);
                        } else {
                            upyun.deleteFile(dirPath + "/" + item.name, null);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (UpException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        } while (!CollectionUtils.isEmpty(folderItems));
        return upyun.rmDir(dirPath);
    }

}
