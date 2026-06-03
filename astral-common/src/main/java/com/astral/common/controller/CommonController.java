package com.astral.common.controller;

import com.astral.common.config.AstralConfig;
import com.astral.common.config.UpyunConfig;
import com.astral.common.constant.CommonConstant;
import com.astral.common.result.Result;
import com.astral.common.utils.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerMapping;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;

@RestController
@RequestMapping("/common")
public class CommonController {

    @GetMapping("/download")
    public void download(String fileUrl, HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            if (!FileUtils.checkAllowDownload(fileUrl)) {
                throw new RuntimeException("资源文件(" + fileUrl + ")非法，不允许下载。 ");
            }
            // 本地资源路径
            String localPath = AstralConfig.getUploadDir();
            // 数据库资源地址
            String downloadPath = localPath + "/" + fileUrl;
            // 下载名称
            String downloadName = StringUtils.substringAfterLast(downloadPath, "/");
//            downloadName = URLDecoder.decode(downloadName, "UTF-8");
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            FileUtils.setAttachmentResponseHeader(response, downloadName);
            FileUtils.writeBytes(downloadPath, response.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @GetMapping(value = "/static/**")
    public void getFile(HttpServletRequest request, HttpServletResponse response) {
        String imgPath = extractPathFromPattern(request);
        if(StringUtils.isBlank(imgPath) || "null".equals(imgPath)){
            return;
        }
        if (CommonConstant.UPLOAD_TYPE_UPYUN.equals(AstralConfig.getUploadType())) {
            String url = UpYunUtil.getDomain() + "/" + imgPath;
            try {
                response.sendRedirect(url);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                imgPath = imgPath.replace("..", "").replace("../","");
                if (imgPath.endsWith(",")) {
                    imgPath = imgPath.substring(0, imgPath.length() - 1);
                }
                String filePath = AstralConfig.getUploadDir() + "/" + imgPath;
                // 先将加号替换为URL编码形式，避免被转为空格
                filePath = filePath.replaceAll("\\+", "%2B");
                filePath = URLDecoder.decode(filePath, "UTF-8");
                File file = new File(filePath);
                if(!file.exists()){
                    response.setStatus(404);
                    throw new RuntimeException("文件["+imgPath+"]不存在..");
                }

                // 获取文件扩展名
                String fileName = file.getName();
                String extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();

                // 自动获取MIME类型
                String mimeType = MimeTypeUtil.getContentType(fileName);
                response.setContentType(mimeType);

                // 根据文件类型确定处理方式
                if (shouldDisplayInline(extension)) {
                    // 可预览文件 - 内联显示
                    response.setHeader("Content-Disposition", "inline; filename=\"" + new String(file.getName().getBytes("UTF-8"),"iso-8859-1") + "\"");
                } else {
                    // 不可预览文件 - 下载
                    response.setHeader("Content-Disposition", "attachment; filename=\"" + new String(file.getName().getBytes("UTF-8"),"iso-8859-1") + "\"");
                }
//                response.addHeader("Content-Disposition", "attachment;fileName=" + new String(file.getName().getBytes("UTF-8"),"iso-8859-1"));
                // 强制缓存
                response.setHeader("Cache-Control", "max-age=31536000");

                inputStream = new BufferedInputStream(new FileInputStream(filePath));
                outputStream = response.getOutputStream();
                byte[] buf = new byte[1024];
                int len;
                while ((len = inputStream.read(buf)) > 0) {
                    outputStream.write(buf, 0, len);
                }
                response.flushBuffer();
            } catch (IOException e) {
                e.printStackTrace();
                response.setStatus(404);
                e.printStackTrace();
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    // 判断是否可内联显示
    private boolean shouldDisplayInline(String extension) {
        switch (extension) {
            // 图片格式
            case "jpg": case "jpeg": case "png":
            case "gif": case "bmp": case "svg":
            // 文本格式
            case "txt": case "csv":
            // 网页格式
            case "html": case "htm":
                return true;
            // 默认不可内联显示
            default:
                return false;
        }
    }

    private static String extractPathFromPattern(final HttpServletRequest request) {
        String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        String bestMatchPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        return new AntPathMatcher().extractPathWithinPattern(bestMatchPattern, path);
    }

    @PostMapping("/upload")
    public Result<?> uploadFile(HttpServletRequest request, MultipartFile file) throws Exception {
        try {
            String bizPath = request.getParameter("biz");
            String type = request.getParameter("type");

            // 是否为文件名添加时间戳后缀
            boolean needTimestampSuffix = !(bizPath != null && bizPath.contains("upload/3DEditor"));
            if ("Tiles".equals(type)) {
                String originalFilename = file.getOriginalFilename();
                int subIndex = originalFilename.lastIndexOf(".");
                String fileExtension = originalFilename.substring(subIndex);
                if (".zip".equals(fileExtension)) {
                    String zipDirectory = originalFilename.substring(0, subIndex) + "-" + System.currentTimeMillis();
                    if (StringUtils.isNotBlank(bizPath)) {
                        zipDirectory = bizPath + "/" + zipDirectory;
                    }
                    String targetDirectory = AstralConfig.getUploadDir() + "/" + zipDirectory;
                    boolean flag = ZipUtil.extractZipCheckFile(file, targetDirectory, "tileset.json");
                    if (!flag) {
                        CommonUtils.deleteFile(zipDirectory);
                        throw new RuntimeException("文件异常，请检查上传的zip文件是否包含tileset.json文件");
                    }
                    return Result.success("上传成功", zipDirectory);
                } else {
                    String fileName = CommonUtils.upload(bizPath, file, needTimestampSuffix);
                    return Result.success("上传成功",fileName);
                }
            } else {
                // 上传并返回新文件名称
                String fileName = CommonUtils.upload(bizPath, file, needTimestampSuffix);
                return Result.success("上传成功",fileName);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/deleteFile")
    public Result<?> deleteFile(String fileUrl) {
        return Result.toAjax(CommonUtils.deleteFile(fileUrl));
    }

    @PostMapping("/zipUploadExtraction")
    public Result<?> zipUploadExtraction(HttpServletRequest request, MultipartFile file) throws Exception {
        try {
            String bizPath = request.getParameter("biz");
            // 上传并返回新文件名称
            ZipUtil.extractZip(file, bizPath);
            return Result.success("上传成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
    }

}
