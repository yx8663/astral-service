package com.astral.business.scenes.controller;

import com.alibaba.fastjson2.JSONObject;
import com.astral.business.scenes.entity.Lb3dEditorScenesExample;
import com.astral.business.scenes.service.Lb3dEditorScenesExampleService;
import com.astral.common.result.Result;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.astral.business.scenes.entity.Lb3dEditorScenes;
import com.astral.business.scenes.service.Lb3dEditorScenesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 场景zip压缩包信息表(Lb3dEditorScenes)表控制层
 */
@RestController
@RequestMapping("/editor3d/scenes")
public class Lb3dEditorScenesController {
    /**
     * 服务对象
     */
    @Autowired
    private Lb3dEditorScenesService lb3dEditorScenesService;

    @Autowired
    private Lb3dEditorScenesExampleService lb3dEditorScenesExampleService;

    /**
     * 新增
     */
    @PostMapping("/add")
    public Result<?> post(@RequestBody Lb3dEditorScenes lb3dEditorScenes) {
        long count = lb3dEditorScenesService.count();
        if (count > 500) {
            return Result.error("共享项目场景数量已达上限（500个），不允许新增");
        }
        if (lb3dEditorScenesService.save(lb3dEditorScenes)) {
            return Result.success(lb3dEditorScenes);
        } else {
            return Result.error("新增失败");
        }
    }

    @GetMapping("/get/{id}")
    public Result<?> getOne(@PathVariable("id") String id) {
        try {
            Lb3dEditorScenes scenes = lb3dEditorScenesService.getById(id);
            if (Objects.isNull(scenes)) {
                return Result.error("场景不存在");
            }
            if ((StringUtils.isEmpty(scenes.getZip()) || StringUtils.isEmpty(scenes.getCoverPicture()))
                    && !StringUtils.isEmpty(scenes.getExampleSceneId())) {
                Lb3dEditorScenesExample scenesExample = lb3dEditorScenesExampleService.getById(scenes.getExampleSceneId());
                if (Objects.nonNull(scenesExample)) {
                    scenes.setZip(scenesExample.getZip());
                    scenes.setCoverPicture(scenesExample.getCoverPicture());
                }
            }
            return Result.success(scenes);
        } catch (Exception e) {
            return Result.error(e.getMessage());
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
        QueryWrapper<Lb3dEditorScenes> queryWrapper = new QueryWrapper<Lb3dEditorScenes>();
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
        Page<Lb3dEditorScenes> page = new Page<>();
        page.setSize(limit);
        page.setCurrent(offset / limit + 1);
        if (!CollectionUtils.isEmpty(fields)) {
            queryWrapper.select(fields);
        }
        try {
            Page<Lb3dEditorScenes> resultPage = lb3dEditorScenesService.page(page, queryWrapper);
            long count = lb3dEditorScenesService.count(queryWrapper);
            JSONObject result = new JSONObject();
            result.put("items", resultPage.getRecords());
            result.put("current", offset + 1);
            result.put("pageSize", limit);
            result.put("pages", count / limit);
            result.put("total", count);
            return Result.success(result);
//            return Result.success(lb3dEditorScenesService.page(page, queryWrapper));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/update/{id}")
    public Result<?> put(@PathVariable("id") String id, @RequestBody Lb3dEditorScenes lb3dEditorScenes) {
        lb3dEditorScenes.setId(id);
        return Result.toAjax(lb3dEditorScenesService.updateById(lb3dEditorScenes));
    }

    @DeleteMapping("/del/{id}")
    public Result<?> delete(@PathVariable String id) {
        return Result.toAjax(lb3dEditorScenesService.removeById(id));
    }
}

