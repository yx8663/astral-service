package com.astral.common.utils;

import com.astral.common.config.AstralConfig;
import com.astral.common.constant.CommonConstant;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;

public class CommonUtils {

    private static String defaultUploadType = AstralConfig.getUploadType();

    // 重载方法，保持原签名（默认需要时间戳后缀）
    public static String upload(String bizPath, MultipartFile file) {
        return upload(bizPath, file, true);
    }

    public static String upload(String bizPath, MultipartFile file, boolean needTimestampSuffix) {
        String result = "";
        try {
            if (CommonConstant.UPLOAD_TYPE_UPYUN.equals(defaultUploadType)) {
                result = UpYunUtil.upload(bizPath, FileUtils.convertFile(file,needTimestampSuffix));
            } else {
                result = FileUploadUtils.upload(bizPath, file,needTimestampSuffix);
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return result;
    }

    public static String upload(String bizPath, File file) {
        return upload(bizPath, file, true);
    }

    public static String upload(String bizPath, File file, boolean needTimestampSuffix) {
        String result = "";
        try {
            if (CommonConstant.UPLOAD_TYPE_UPYUN.equals(defaultUploadType)) {
                result = UpYunUtil.upload(bizPath, file);
            } else {
                result = FileUploadUtils.upload(bizPath, file, needTimestampSuffix);
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return result;
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
                File file = new File(dirPath);
                org.apache.commons.io.FileUtils.forceDelete(file);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("文件删除失败：" + e.getMessage());
        }

    }

    /**
     * 验证是否是json字符串
     */
    public static Boolean isJsonValid(String jsonStr) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.readTree(jsonStr);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
