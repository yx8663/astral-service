package com.astral.common.controller;

import com.astral.common.config.AstralConfig;
import com.astral.common.config.UpyunConfig;
import com.astral.common.constant.CommonConstant;
import com.astral.common.result.Result;
import com.astral.common.utils.CommonUtils;
import com.astral.common.utils.FileUploadUtils;
import com.astral.common.utils.FileUtils;
import com.astral.common.utils.UpYunUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerMapping;

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
            String downloadPath = localPath + File.separator + fileUrl;
            // 下载名称
            String downloadName = StringUtils.substringAfterLast(downloadPath, "/");
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            FileUtils.setAttachmentResponseHeader(response, downloadName);
            FileUtils.writeBytes(downloadPath, response.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @GetMapping(value = "/static/**")
    public void download(HttpServletRequest request, HttpServletResponse response) {
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
                String filePath = AstralConfig.getUploadDir() + File.separator + imgPath;
                filePath = URLDecoder.decode(filePath, "UTF-8");
                File file = new File(filePath);
                if(!file.exists()){
                    response.setStatus(404);
                    throw new RuntimeException("文件["+imgPath+"]不存在..");
                }
                // 设置强制下载不打开
                response.setContentType("application/force-download");
                response.addHeader("Content-Disposition", "attachment;fileName=" + new String(file.getName().getBytes("UTF-8"),"iso-8859-1"));
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

    private static String extractPathFromPattern(final HttpServletRequest request) {
        String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        String bestMatchPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        return new AntPathMatcher().extractPathWithinPattern(bestMatchPattern, path);
    }

    @PostMapping("/upload")
    public Result<?> uploadFile(HttpServletRequest request, MultipartFile file) throws Exception {
        try {
            String bizPath = request.getParameter("biz");
            // 上传并返回新文件名称
            String fileName = CommonUtils.upload(bizPath, file);
            return Result.success(fileName);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
    }

}
