package com.astral.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "astral")
public class AstralConfig {

    private static String uploadType;

    private static String uploadDir;

    public static String getUploadType() {
        return uploadType;
    }

    public void setUploadType(String uploadType) {
        AstralConfig.uploadType = uploadType;
    }

    public static String getUploadDir() {
        return uploadDir;
    }

    public void setUploadDir(String uploadDir) {
        AstralConfig.uploadDir = uploadDir;
    }
}
