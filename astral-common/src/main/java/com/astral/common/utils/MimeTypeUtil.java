package com.astral.common.utils;

import javax.activation.MimetypesFileTypeMap;
import java.io.IOException;
import java.io.InputStream;

public class MimeTypeUtil {
    private static final MimetypesFileTypeMap MIME_TYPES;

    static {
        try (InputStream is = MimeTypeUtil.class.getResourceAsStream("/mime.types")) {
            MIME_TYPES = new MimetypesFileTypeMap(is);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load mime.types", e);
        }
    }

    public static String getContentType(String fileName) {
        return MIME_TYPES.getContentType(fileName);
    }
}
