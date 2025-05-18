package com.astral.common.utils;

import com.astral.common.config.AstralConfig;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

/**
 * 文件上传工具类
 */
public class FileUploadUtils {

    public static final int DEFAULT_FILE_NAME_LENGTH = 100;

    public static final long DEFAULT_MAX_SIZE = 10L * 1024 * 1024 * 1024;

    private static String defaultBaseDir = AstralConfig.getUploadDir();

    public static String getDefaultBaseDir() {
        return defaultBaseDir;
    }



    public static final String upload(String baseDir, MultipartFile file) throws IOException{
        int fileNamelength = Objects.requireNonNull(file.getOriginalFilename()).length();
        if (fileNamelength > FileUploadUtils.DEFAULT_FILE_NAME_LENGTH) {
            throw new RuntimeException("文件名长度超过限制");
        }
//        String fileName = extractFilename(file);
        String fileName = file.getOriginalFilename();
        String absPath = getAbsoluteFile(baseDir, fileName).getAbsolutePath();
        file.transferTo(Paths.get(absPath));
        return getPathFileName(baseDir, fileName);
    }

    public static final String upload(String baseDir, File file) throws IOException {
        int fileNamelength = Objects.requireNonNull(file.getName()).length();
        if (fileNamelength > FileUploadUtils.DEFAULT_FILE_NAME_LENGTH) {
            throw new RuntimeException("文件名长度超过限制");
        }
        String fileName = file.getName();
        String absPath = getAbsoluteFile(baseDir, fileName).getAbsolutePath();
        Files.copy(file.toPath(), Paths.get(absPath), StandardCopyOption.REPLACE_EXISTING);
        return getPathFileName(baseDir, fileName);
    }

    public static final File getAbsoluteFile(String uploadDir, String fileName) {
        File desc = new File(defaultBaseDir +  File.separator + getPathFileName(uploadDir, fileName));
        if (!desc.exists()) {
            if (!desc.getParentFile().exists()) {
                desc.getParentFile().mkdirs();
            }
        }
        return desc;
    }

    public static final String getPathFileName(String uploadDir, String fileName) {
        if (StringUtils.hasLength(uploadDir)) {
            return uploadDir +  File.separator + fileName;
        } else {
            return fileName;
        }

    }


}
