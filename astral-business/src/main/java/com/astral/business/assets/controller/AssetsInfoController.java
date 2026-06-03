package com.astral.business.assets.controller;

import com.alibaba.fastjson2.JSONObject;
import com.astral.business.assets.entity.AssetsInfo;
import com.astral.business.assets.service.AssetsCategoryService;
import com.astral.business.assets.service.AssetsInfoService;
import com.astral.common.result.Result;
import com.astral.common.utils.CommonUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 资产信息
 */
@RestController
@RequestMapping("/assets/assetsInfo")
public class AssetsInfoController {

    @Autowired
    private AssetsInfoService assetsInfoService;

    @Autowired
    private AssetsCategoryService assetsCategoryService;

    /**
     * 分页查询资产信息
     */
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
        String searchStr = request.getParameter("search");
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
        QueryWrapper<AssetsInfo> queryWrapper = new QueryWrapper<AssetsInfo>();
        query.forEach((k, v) -> {
            String filedName = StringUtils.replace(k, ".", "__");
            if ("isnull".equals(filedName)) {
                queryWrapper.eq(filedName, "true".equals(v) || "1".equals(v));
            } else {
                queryWrapper.eq(filedName, v);
            }
        });
        Map<String, String> search = new HashMap<>();
        if (StringUtils.hasLength(searchStr)) {
            try {
                search = Arrays.stream(searchStr.split(";")).map(s -> {
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
        search.forEach((k, v) -> {
            String filedName = StringUtils.replace(k, ".", "__");
            if ("name".equals(k)) {
                queryWrapper.like("name", v);
            } else if ("tags".equals(k)) {
                String[] tags = v.split(",");
                String regex = Arrays.stream(tags)
                        .map(Pattern::quote)
                        .collect(Collectors.joining("|", "(^|,)", "(,|$)"));
                queryWrapper.apply("tags REGEXP {0}", regex);
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
        Page<AssetsInfo> page = new Page<>();
        page.setSize(limit);
        page.setCurrent(offset / limit + 1);
        if (!CollectionUtils.isEmpty(fields)) {
            queryWrapper.select(fields);
        }
        try {
            Page<AssetsInfo> resultPage = assetsInfoService.page(page, queryWrapper);
            long count = assetsInfoService.count(queryWrapper);
            JSONObject result = new JSONObject();
            List<AssetsInfo> records = resultPage.getRecords();
            if (!CollectionUtils.isEmpty(records)) {
                Map<String, Map<String, String>> typeMap = assetsCategoryService.selectCategoryNameMap();
                for (AssetsInfo record : records) {
                    Map<String, String> categoryMap = typeMap.get(record.getType());
                    if (!CollectionUtils.isEmpty(categoryMap)) {
                        record.setCategoryName(categoryMap.get(record.getCategory()));
                    }
                }
            }
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

    /**
     * 条件查询资产信息
     */
    @GetMapping("/list")
    public Result<?> list(AssetsInfo assetsInfo) {
        QueryWrapper<AssetsInfo> queryWrapper = new QueryWrapper<>(assetsInfo);
        List<AssetsInfo> list = assetsInfoService.list(queryWrapper);
        return Result.success(list);
    }

    /**
     * 根据ID查询资产信息
     */
    @GetMapping("/{id}")
    public Result<AssetsInfo> getById(@PathVariable Long id) {
        AssetsInfo assetsInfo = assetsInfoService.getById(id);
        return assetsInfo != null ? Result.success(assetsInfo) : Result.error("未找到该资产");
    }

    /**
     * 新增资产信息
     */
    @PostMapping
    public Result<?> add(@RequestBody AssetsInfo assetsInfo) {
        boolean success = assetsInfoService.save(assetsInfo);
        return success ? Result.success("添加成功",assetsInfo) : Result.error("添加失败");
    }

    /**
     * 更新资产信息
     */
    @PutMapping
    public Result<?> update(@RequestBody AssetsInfo assetsInfo) {
        AssetsInfo metaAssetsInfo = assetsInfoService.getById(assetsInfo.getId());
        String thumbnail = metaAssetsInfo.getThumbnail();

        boolean success = assetsInfoService.updateById(assetsInfo);

        if(success){
            if(!Objects.equals(thumbnail, assetsInfo.getThumbnail())){
                CommonUtils.deleteFile(thumbnail);
            }

            return Result.success("更新成功",assetsInfo);
        }

        return Result.error("更新失败");
    }

    /**
     * 根据ID删除资产信息
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        AssetsInfo assetsInfo = assetsInfoService.getById(id);
        if (assetsInfo == null) {
            return Result.error("未找到该资产");
        }
        String thumbnail = assetsInfo.getThumbnail();
        if (org.apache.commons.lang3.StringUtils.isNotBlank(thumbnail)) {
            CommonUtils.deleteFile(thumbnail);
        }
        String file = assetsInfo.getFile();
        if (org.apache.commons.lang3.StringUtils.isNotBlank(file)) {
            CommonUtils.deleteFile(file);
        }
        boolean success = assetsInfoService.removeById(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }

    /**
     * 根据分类查询标签
     * @param category
     * @return
     */
    @GetMapping("/selectTags")
    public Result<?> selectTages(@RequestParam("category") String category, @RequestParam("type") String type) {
        List<String> list = assetsInfoService.selectTages(category, type);
        return Result.success(list);
    }

}
