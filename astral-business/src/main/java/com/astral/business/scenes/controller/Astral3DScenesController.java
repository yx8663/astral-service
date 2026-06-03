package com.astral.business.scenes.controller;

import com.alibaba.fastjson2.JSONObject;
import com.astral.business.scenes.entity.Astral3DScenesExample;
import com.astral.business.scenes.service.Astral3DScenesExampleService;
import com.astral.common.result.Result;
import com.astral.common.utils.CommonUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.astral.business.scenes.entity.Astral3DScenes;
import com.astral.business.scenes.service.Astral3DScenesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 场景zip压缩包信息表(Astral3DScenes)表控制层
 */
@RestController
@RequestMapping("/editor3d/scenes")
public class Astral3DScenesController {
    /**
     * 服务对象
     */
    @Autowired
    private Astral3DScenesService astral3DScenesService;

    @Autowired
    private Astral3DScenesExampleService astral3DScenesExampleService;

    /**
     * 新增
     */
    @PostMapping("/add")
    public Result<?> post(@RequestBody Astral3DScenes lb3dEditorScenes) {
        long count = astral3DScenesService.count();
        if (count > 2000) {
            return Result.error("共享项目场景数量已达上限（2000个），不允许新增");
        }
        if (astral3DScenesService.save(lb3dEditorScenes)) {
            return Result.success(lb3dEditorScenes);
        } else {
            return Result.error("新增失败");
        }
    }

    @GetMapping("/get/{id}")
    public Result<?> getOne(@PathVariable("id") String id) {
        try {
            Astral3DScenes scenes = astral3DScenesService.getById(id);
            if (Objects.isNull(scenes)) {
                return Result.error("场景不存在");
            }
            if ((StringUtils.isEmpty(scenes.getZip()) || StringUtils.isEmpty(scenes.getCoverPicture()))
                    && !StringUtils.isEmpty(scenes.getExampleSceneId())) {
                Astral3DScenesExample scenesExample = astral3DScenesExampleService.getById(scenes.getExampleSceneId());
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
        QueryWrapper<Astral3DScenes> queryWrapper = new QueryWrapper<Astral3DScenes>();
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
        Page<Astral3DScenes> page = new Page<>();
        page.setSize(limit);
        page.setCurrent(offset / limit + 1);
        if (!CollectionUtils.isEmpty(fields)) {
            queryWrapper.select(fields);
        }

        try {
            Page<Astral3DScenes> resultPage = astral3DScenesService.page(page, queryWrapper);

            // 20251104 新增：封面兜底
            List<Astral3DScenes> records = resultPage.getRecords();
            if (!CollectionUtils.isEmpty(records)) {
                // 需要兜底的 exampleSceneId 集合
                Set<String> exampleIdsToFetch = records.stream()
                        .filter(r -> !StringUtils.hasLength(r.getCoverPicture()) && r.getExampleSceneId() != null)
                        .map(Astral3DScenes::getExampleSceneId)
                        .collect(Collectors.toSet());

                if (!CollectionUtils.isEmpty(exampleIdsToFetch)) {
                    // 批量查询示例场景
                    List<Astral3DScenesExample> examples = astral3DScenesExampleService.listByIds(exampleIdsToFetch);
                    Map<String, String> id2Cover = examples.stream()
                            .filter(Objects::nonNull)
                            .filter(e -> StringUtils.hasLength(e.getCoverPicture()))
                            .collect(Collectors.toMap(Astral3DScenesExample::getId, Astral3DScenesExample::getCoverPicture, (a, b) -> a));

                    // 回填 coverPicture
                    for (Astral3DScenes r : records) {
                        if (!StringUtils.hasLength(r.getCoverPicture())) {
                            String exId = r.getExampleSceneId();
                            if (exId != null) {
                                String fallbackCover = id2Cover.get(exId);
                                if (StringUtils.hasLength(fallbackCover)) {
                                    r.setCoverPicture(fallbackCover);
                                }
                            }
                        }
                    }
                }
            }

            long count = astral3DScenesService.count(queryWrapper);
            JSONObject result = new JSONObject();
            result.put("items", resultPage.getRecords());
            result.put("current", offset + 1);
            result.put("pageSize", limit);
            result.put("pages", (count + limit - 1) / limit);
            result.put("total", count);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/update/{id}")
    public Result<?> put(@PathVariable("id") String id, @RequestBody Astral3DScenes lb3dEditorScenes) {
        Astral3DScenes oldScenes = astral3DScenesService.getById(id);
        if (oldScenes != null) {
            String zip = oldScenes.getZip();
            if (StringUtils.hasLength(zip)) {
                // 删除文件父级的文件夹及其下所有文件

                String folder = zip.substring(0, zip.lastIndexOf("/"));
                if (!CommonUtils.deleteFile(folder)) {
                    throw new RuntimeException(folder + " 删除文件失败");
                }
            }
            String oldCoverPicture = oldScenes.getCoverPicture();
            if (StringUtils.hasLength(oldCoverPicture)) {
                // 判断封面图是否变更
                if (!oldCoverPicture.equals(lb3dEditorScenes.getCoverPicture())) {
                    if (!CommonUtils.deleteFile(oldCoverPicture)) {
                        throw new RuntimeException(oldCoverPicture + " 删除文件失败");
                    }
                }
            }
        }
        lb3dEditorScenes.setId(id);
        boolean b = astral3DScenesService.updateById(lb3dEditorScenes);
        if (b) {
            return Result.success(lb3dEditorScenes);
        } else {
            return Result.error("更新失败");
        }
    }

    @DeleteMapping("/del/{id}")
    public Result<?> delete(@PathVariable String id) {
        return Result.toAjax(astral3DScenesService.removeById(id));
    }
}

