package com.astral.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipUtil {

    private static final Logger logger = LoggerFactory.getLogger(ZipUtil.class);

    /**
     * 解压ZIP文件到指定目录
     */
    public static void extractZip(MultipartFile zipFile, String targetDirectory) throws IOException {
        // 创建目标目录
        File targetDir = new File(targetDirectory);
        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }
        try {
            ZipInputStream zipInputStream = new ZipInputStream(zipFile.getInputStream());
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                if (!entry.isDirectory()) {
                    File extractedFile = extractFile(zipInputStream, targetDir, entry.getName());
                }
                zipInputStream.closeEntry();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean extractZipCheckFile(MultipartFile zipFile, String targetDirectory, String checkFileName) throws IOException {
        // 创建目标目录
        File targetDir = new File(targetDirectory);
        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }
        boolean flag = false;
        try {
            ZipInputStream zipInputStream = new ZipInputStream(zipFile.getInputStream());
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                if (!entry.isDirectory()) {
                    File extractedFile = extractFile(zipInputStream, targetDir, entry.getName());
                    System.out.println(entry.getName());
                    if (checkFileName.equals(entry.getName())) {
                        flag = true;
                    }
                }
                zipInputStream.closeEntry();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return flag;
    }

    private static File extractFile(ZipInputStream zipInputStream, File targetDir, String fileName) throws IOException {
        File file = new File(targetDir, fileName);

        // 创建父目录
        File parent = file.getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }

        try (FileOutputStream fos = new FileOutputStream(file);
             BufferedOutputStream bos = new BufferedOutputStream(fos)) {

            byte[] buffer = new byte[1024];
            int length;
            while ((length = zipInputStream.read(buffer)) > 0) {
                bos.write(buffer, 0, length);
            }
        }

        return file;
    }

}
