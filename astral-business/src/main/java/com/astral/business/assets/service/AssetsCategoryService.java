package com.astral.business.assets.service;

import com.astral.business.assets.entity.AssetsCategory;
import com.astral.common.base.TreeNode;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

public interface AssetsCategoryService extends IService<AssetsCategory> {

    List<TreeNode> treeList(String type);

    boolean add(AssetsCategory assetsCategory);

    boolean edit(AssetsCategory assetsCategory);

    boolean delete(Long id);

    List<AssetsCategory> selectCategoryAndChildren(String code, String type);

    Map<String, Map<String, String>> selectCategoryNameMap();

}
