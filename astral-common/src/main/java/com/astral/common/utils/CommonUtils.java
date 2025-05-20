package com.astral.common.utils;

import com.astral.common.config.AstralConfig;
import com.astral.common.constant.CommonConstant;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public class CommonUtils {

    private static String defaultUploadType = AstralConfig.getUploadType();

    public static String upload(String bizPath, MultipartFile file) {
        String result = "";
        try {
            if (CommonConstant.UPLOAD_TYPE_UPYUN.equals(defaultUploadType)) {
                result = UpYunUtil.upload(bizPath, FileUtils.convertFile(file));
            } else {
                result = FileUploadUtils.upload(bizPath, file);
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return result;
    }

    public static void upload(String bizPath, File file) {
        try {
            if (CommonConstant.UPLOAD_TYPE_UPYUN.equals(defaultUploadType)) {
                UpYunUtil.upload(bizPath, file);
            } else {
                FileUploadUtils.upload(bizPath, file);
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 通用文件删除（慎用）
     * @param dirPath
     * @return
     */
    public static Boolean deleteFile(String dirPath) {
        if (StringUtils.isBlank(dirPath)) {
            return false;
        }
        dirPath = AstralConfig.getUploadDir() + "/" + dirPath;
        try {
            if (CommonConstant.UPLOAD_TYPE_UPYUN.equals(defaultUploadType)) {
                return UpYunUtil.deleteFile(dirPath);
            } else {
                org.apache.commons.io.FileUtils.deleteDirectory(new File(dirPath));
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("文件删除失败：" + e.getMessage());
        }

    }

}
