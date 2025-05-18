package com.astral.business.cad.controller;

import com.alibaba.fastjson2.JSONObject;
import com.astral.business.cad.entity.ConversionResult;
import com.astral.business.cad.entity.Lb3dEditorCad;
import com.astral.business.cad.service.Lb3dEditorCadService;
import com.astral.common.result.Result;
import com.astral.common.utils.CommonUtils;
import com.astral.core.config.webSocketConfig.WebSocket;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * (Lb3dEditorCad)表控制层
 */
@RestController
@RequestMapping("/editor3d/cad")
public class Lb3dEditorCadController {

    @Autowired
    private Lb3dEditorCadService lb3dEditorCadService;

    @Autowired
    private WebSocket wsocket;

    @Value("${dev.cadDwgConverterAbPath}")
    private String cadDwgConverterAbPath;

    @Value("${dev.currentAbPath}")
    private String currentAbPath;

    @PostMapping("/dwg2dxf")
    public Result<?> dwg2Dxf(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        // 检查文件是否为空
        if (file.isEmpty()) {
            return Result.error("上传文件为空！");
        }
        Integer conversionStatus;
        String converterFilePath = "";
        String dataPath = "";

        // 获取文件扩展名
        String originalFilename = file.getOriginalFilename();
        String ext = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();

        // 检查文件格式
        if (!"dwg".equals(ext) && !"dxf".equals(ext)) {
            return Result.error("上传文件格式不正确！");
        }

        // 去除文件名中的空格
        String sanitizedFilename = originalFilename.replace(" ", "");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String nowDateStr = sdf.format(new Date());
        if (!"dwg".equals(ext)) {
            conversionStatus = 1;
            String dxfUploadDir = "upload/cad/dwg2dxf/" + nowDateStr;
            converterFilePath = dxfUploadDir + "/" + sanitizedFilename;
            dataPath = converterFilePath;
            // 无需转换，直接上传至又拍云
            try {
                CommonUtils.upload(dxfUploadDir, file);
            } catch (Exception e) {
                return Result.error("上传失败，Error：" + e.getMessage());
            }
        } else {
            conversionStatus = 0;
            // 需要转换，保存文件到tmp目录
            dataPath = "static/tmp/cad/" + sanitizedFilename;
            try {
                File tempFile = new File(currentAbPath + "/" + dataPath);
                file.transferTo(tempFile);
            } catch (Exception e) {
                e.printStackTrace();
                return Result.error("cad上传文件保存失败，Error:" + e.getMessage());
            }

        }
        // 更新数据库
        Lb3dEditorCad cad = new Lb3dEditorCad();
        cad.setFilePath(dataPath);
        cad.setThumbnail(request.getParameter("thumbnail"));
        cad.setFileName(request.getParameter("fileName"));
        cad.setConversionStatus(conversionStatus);
        cad.setConverterFilePath(converterFilePath);
        boolean saveResult = lb3dEditorCadService.save(cad);
        if (!saveResult) {
            return Result.error("保存数据失败！");
        }
        String finalDataPath = currentAbPath + "/" + dataPath;
        if (conversionStatus == 0) {
            // 异步转换DWG文件
            new Thread(() -> {
                String uname = request.getParameter("uname");
                String outputFile = currentAbPath + "/static/tmp/cad/" + sanitizedFilename.replace(".dwg", ".dxf");
                String command = "cmd /C " + cadDwgConverterAbPath + "/dwg2dxf.exe " + finalDataPath + " -o " + outputFile;
                System.out.println("[cad] run cmd：" + command);
                try {
                    ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/C", cadDwgConverterAbPath + "/dwg2dxf.exe", finalDataPath, "-o", outputFile);
                    builder.redirectErrorStream(true);
                    Process process = builder.start();
                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(process.getInputStream()))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            // 可以在这里过滤掉错误信息，只处理需要的输出
                            if (!line.startsWith("ERROR:")) {  // 示例过滤条件
                                System.out.println(line);
                            }
                        }
                    }

                    int exitCode = process.waitFor();
                    if (exitCode == 0) {
                        // 转换成功，上传至又拍云
                        try {
                            File dxfFile = new File(outputFile);
                            String dxfUploadDir = "upload/cad/dwg2dxf/" + nowDateStr;
                            String dxfUploadPath = dxfUploadDir + "/" + dxfFile.getName();
                            cad.setConverterFilePath(dxfUploadPath);
                            System.out.println("[cad] 转换成功，准备上传至又拍云: " + dxfFile.getName());
                            try {
                                CommonUtils.upload(dxfUploadDir, dxfFile);
                                cad.setConversionStatus(1);
                            } catch (Exception e) {
                                System.out.println("上传至又拍云失败，Error：" + e.getMessage());
                                cad.setConversionStatus(2);
                                JSONObject webSocketMsg = new JSONObject();
                                webSocketMsg.put("type", "cad");
                                webSocketMsg.put("subscriber", uname);
                                webSocketMsg.put("data", new ConversionResult("failed", cad, "转换成功，但上传结果至又拍云失败！Error：" + e.getMessage()));
                                wsocket.sendMessage(uname, webSocketMsg.toJSONString());
                            }
                            cad.setConversionStatus(1);
                        } catch (Exception e) {
                            System.out.println("failed to open file: " + e.getMessage());
                            cad.setConversionStatus(2);
                            JSONObject webSocketMsg = new JSONObject();
                            webSocketMsg.put("type", "cad");
                            webSocketMsg.put("subscriber", uname);
                            webSocketMsg.put("data", new ConversionResult("failed", cad, "转换成功，但读取结果失败！Error: " + e.getMessage()));
                            wsocket.sendMessage(uname, webSocketMsg.toJSONString());
                        }
                    } else {
                        System.out.println("failed to call cmd.Run(): " + command);
                        // 转换失败
                        cad.setConversionStatus(2);
                        JSONObject webSocketMsg = new JSONObject();
                        webSocketMsg.put("type", "cad");
                        webSocketMsg.put("subscriber", uname);
                        webSocketMsg.put("data", new ConversionResult("failed", cad, "转换失败！Error: " + exitCode));
                        wsocket.sendMessage(uname, webSocketMsg.toJSONString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    cad.setConversionStatus(2);
                    JSONObject webSocketMsg = new JSONObject();
                    webSocketMsg.put("type", "cad");
                    webSocketMsg.put("subscriber", uname);
                    webSocketMsg.put("data", new ConversionResult("failed", cad, e.getMessage()));
                    wsocket.sendMessage(uname, webSocketMsg.toJSONString());
                }

                System.out.println("[cad] 转换完成，准备更新数据库: " + cad.getId());
                boolean updateResult = lb3dEditorCadService.updateById(cad);
                if (cad.getConversionStatus() == 1) {
                    if (updateResult) {
                        System.out.println("[cad] 转换成功，更新数据库成功！发送成功消息");
                        JSONObject webSocketMsg = new JSONObject();
                        webSocketMsg.put("type", "cad");
                        webSocketMsg.put("subscriber", uname);
                        webSocketMsg.put("data", new ConversionResult("completed", cad,  "转换成功！"));
                        wsocket.sendMessage(uname, webSocketMsg.toJSONString());
                    } else {
                        JSONObject webSocketMsg = new JSONObject();
                        webSocketMsg.put("type", "cad");
                        webSocketMsg.put("subscriber", uname);
                        webSocketMsg.put("data", new ConversionResult("failed", cad,  "转换成功，但更新数据库失败！"));
                        wsocket.sendMessage(uname, webSocketMsg.toJSONString());
                    }
                }
                try {
                    // 删除上传的临时文件和转换后的文件
                    Files.deleteIfExists(Paths.get(finalDataPath));
                    Files.deleteIfExists(Paths.get(outputFile));
                } catch (Exception e) {
                    System.out.println("删除临时文件失败:" + e.getMessage());
                    e.printStackTrace();
                }
            }).start();
        }
        return Result.success(cad);
    }

    @PostMapping("/add")
    public Result<?> post(@RequestBody Lb3dEditorCad lb3dEditorCad) {
        if (lb3dEditorCadService.save(lb3dEditorCad)) {
            return Result.success(lb3dEditorCad);
        } else {
            return Result.error("保存失败！");
        }
    }

    @GetMapping("/{id}")
    public Result<?> getOne(@PathVariable("id") Long id) {
        try {
            Lb3dEditorCad lb3dEditorCad = lb3dEditorCadService.getById(id);
            return Result.success(lb3dEditorCad);
        } catch (Exception e) {
            return Result.error("保存失败->" + e.getMessage());
        }

    }

    @GetMapping("/getAll")
    public Result<?> getAll(HttpServletRequest request) {
        String fieldsStr = request.getParameter("fields");
        List<String> fields = StringUtils.hasLength(fieldsStr) ? Arrays.asList(fieldsStr.split(",")) : new ArrayList<>();
        String limitStr = request.getParameter("limit");
        Integer limit = StringUtils.hasLength(limitStr) ? Integer.parseInt(limitStr) : 10;
        String offsetStr = request.getParameter("offset");
        Integer offset = StringUtils.hasLength(offsetStr) ? Integer.parseInt(offsetStr) : 0;
        String sortbyStr = request.getParameter("sortby");
        List<String> sortby = StringUtils.hasLength(sortbyStr) ? Arrays.asList(sortbyStr.split(",")) : new ArrayList<>();
        String orderStr = request.getParameter("order");
        List<String> order = StringUtils.hasLength(orderStr) ? Arrays.asList(orderStr.split(",")) : new ArrayList<>();
        String queryStr = request.getParameter("query");
        Map<String, String> query = new HashMap<>();
        if (StringUtils.hasLength(queryStr)) {
            try {
                query = Arrays.stream(queryStr.split(",")).map(s -> {
                    String[] split = s.split(":");
                    if (split.length != 2) {
                        throw new IllegalArgumentException("error:无效的查询键/值对");
                    }
                    return split;
                }).collect(Collectors.toMap(s -> s[0], s -> s[1], (v1, v2) -> v1));
            } catch (Exception e) {
                return Result.error(e.getMessage());
            }
        }
        QueryWrapper<Lb3dEditorCad> queryWrapper = new QueryWrapper<Lb3dEditorCad>();
        query.forEach((k, v) -> {
            String filedName = StringUtils.replace(k, ".", "__");
            if ("isnull".equals(filedName)) {
                queryWrapper.eq(filedName, "true".equals(v) || "1".equals(v));
            } else {
                queryWrapper.eq(filedName, v);
            }
        });
        if (!CollectionUtils.isEmpty(sortby)) {
            if (sortby.size() == order.size()) {
                for (int i = 0; i < sortby.size(); i++) {
                    if ("desc".equals(order.get(i))) {
                        queryWrapper.orderByDesc(sortby.get(i));
                    } else if ("asc".equals(order.get(i))) {
                        queryWrapper.orderByAsc(sortby.get(i));
                    } else {
                        return Result.error("Error: Invalid order. Must be either [asc|desc]");
                    }
                }
            } else if (order.size() == 1) {
                if ("desc".equals(order.get(0))) {
                    queryWrapper.orderByDesc(sortby);
                } else if ("asc".equals(order.get(0))) {
                    queryWrapper.orderByAsc(sortby);
                }else {
                    return Result.error("Error: Invalid order. Must be either [asc|desc]");
                }
            } else {
                return Result.error("Error: 'sortby', 'order' sizes mismatch or 'order' size is not 1");
            }
        } else {
            if (!CollectionUtils.isEmpty(order)) {
                return Result.error("Error: unused 'order' fields");
            }
        }
        Page<Lb3dEditorCad> page = new Page<>();
        page.setSize(limit);
        page.setCurrent(offset / limit + 1);
        if (!CollectionUtils.isEmpty(fields)) {
            queryWrapper.select(fields);
        }
        try {
            Page<Lb3dEditorCad> resultPage = lb3dEditorCadService.page(page, queryWrapper);
            long count = lb3dEditorCadService.count(queryWrapper);
            JSONObject result = new JSONObject();
            result.put("items", resultPage.getRecords());
            result.put("current", offset + 1);
            result.put("pageSize", limit);
            result.put("pages", count / limit);
            result.put("total", count);
            return Result.success(result);
//            return Result.success(pageResult);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Result<?> put(@PathVariable("id") Long id, @RequestBody Lb3dEditorCad lb3dEditorCad) {
        lb3dEditorCad.setId(id);
        return Result.toAjax(lb3dEditorCadService.updateById(lb3dEditorCad));
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable("id") Long id) {
        return Result.toAjax(lb3dEditorCadService.removeById(id));
    }
}

