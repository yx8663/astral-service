package com.astral.business.assets.controller;

import com.astral.business.assets.entity.AssetsCategory;
import com.astral.business.assets.service.AssetsCategoryService;
import com.astral.common.result.Result;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 资产分类
 */
@RestController
@RequestMapping("/assets/assetsCategory")
public class AssetsCategoryController {

    @Autowired
    private AssetsCategoryService assetsCategoryService;

    /**
     * 条件查询资产分类
     */
    @GetMapping("/list")
    public Result<?> list(@RequestBody AssetsCategory assetsCategory) {
        QueryWrapper<AssetsCategory> queryWrapper = new QueryWrapper<>(assetsCategory);
        List<AssetsCategory> list = assetsCategoryService.list(queryWrapper);
        return Result.success(list);
    }

    /**
     * 查询树型结构数据
     * @return
     */
    @GetMapping("/treeList")
    public Result<?> treeList(@RequestParam(required = false, value = "type") String type) {
        return Result.success(assetsCategoryService.treeList(type));
    }

    /**
     * 根据ID查询资产分类
     */
    @GetMapping("/{id}")
    public Result<AssetsCategory> getById(@PathVariable Long id) {
        AssetsCategory assetsCategory = assetsCategoryService.getById(id);
        return assetsCategory != null ? Result.success(assetsCategory) : Result.error("未找到该资产");
    }

    /**
     * 新增资产分类
     */
    @PostMapping
    public Result<?> add(@RequestBody AssetsCategory assetsCategory) {
        boolean success = assetsCategoryService.add(assetsCategory);
        return success ? Result.success("添加成功") : Result.error("添加失败");
    }

    /**
     * 更新资产分类
     */
    @PutMapping
    public Result<?> update(@RequestBody AssetsCategory assetsCategory) {
        boolean success = assetsCategoryService.edit(assetsCategory);
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }

    /**
     * 根据ID删除资产分类
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        boolean success = assetsCategoryService.delete(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }

}

