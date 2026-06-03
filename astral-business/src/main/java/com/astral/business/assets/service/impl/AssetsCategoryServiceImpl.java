package com.astral.business.assets.service.impl;

import com.astral.business.assets.mapper.AssetsCategoryMapper;
import com.astral.business.assets.service.AssetsCategoryService;
import com.astral.business.assets.service.AssetsInfoService;
import com.astral.common.base.TreeNode;
import com.astral.common.utils.TreeBuilderUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.astral.business.assets.entity.AssetsCategory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 资产分类实现层
*/
@Service
public class AssetsCategoryServiceImpl extends ServiceImpl<AssetsCategoryMapper, AssetsCategory> implements AssetsCategoryService {

    @Autowired
    private AssetsInfoService assetsInfoService;

    @Override
    public List<TreeNode> treeList(String type) {
        LambdaQueryWrapper<AssetsCategory> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(type)) {
            queryWrapper.eq(AssetsCategory::getType, type);
        }
        List<AssetsCategory> list = this.list(queryWrapper);
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        List<TreeNode> treeNodeList = list.stream().map(item -> {
            TreeNode node = new TreeNode();
            node.setId(String.valueOf(item.getId()));
            node.setKey(item.getCode());
            node.setPkey(item.getPcode());
            node.setSortNum(item.getSortNum());
            node.setLabel(item.getName());
            node.setType(item.getType());
            return node;
        }).collect(Collectors.toList());
        List<TreeNode> treeList = TreeBuilderUtil.buildTree(treeNodeList);
        // 先type排序，asc，再sortNum排序，asc 仅排序顶级（处理全部查询时排序混乱问题）
        treeList = treeList.stream().sorted(Comparator.comparing(TreeNode::getType, Comparator.nullsLast(Comparator.naturalOrder())).thenComparing(TreeNode::getSortNum, Comparator.nullsLast(Comparator.naturalOrder()))).collect(Collectors.toList());
        return treeList;
    }

    @Override
    public boolean add(AssetsCategory assetsCategory) {
        checkData(assetsCategory);
        return this.save(assetsCategory);
    }

    /**
     * 数据校验
     * @param assetsCategory
     */
    private void checkData(AssetsCategory assetsCategory) {
        String code = assetsCategory.getCode();
        if (StringUtils.isBlank(assetsCategory.getType()) || StringUtils.isBlank(assetsCategory.getName())
                || StringUtils.isBlank(code)) {
            throw new RuntimeException("分类、编码、名称不能为空");
        }
        if (code.indexOf(".") > 0) {
            throw new RuntimeException("编码中不能含有特殊字符.");
        }
        if (assetsCategory.getId() == null) {
            AssetsCategory oldEntity = this.getOne(new LambdaQueryWrapper<AssetsCategory>().eq(AssetsCategory::getType, assetsCategory.getType()).eq(AssetsCategory::getCode, code));
            if (oldEntity != null) {
                throw new RuntimeException("编码已存在");
            }
        } else {
            AssetsCategory oldEntity = this.getById(assetsCategory.getId());
            if (!oldEntity.getCode().equals(code)) {
                if (assetsInfoService.checkCategoryExistAssets(oldEntity.getType(), oldEntity.getCode())) {
                    throw new RuntimeException("该分类存在资产台账信息，不能修改编码/删除");
                }
                long count = this.count(new LambdaQueryWrapper<AssetsCategory>().eq(AssetsCategory::getType, assetsCategory.getType()).eq(AssetsCategory::getPcode, code));
                if (count > 0) {
                    throw new RuntimeException("存在下级分类，不能修改编码或者删除");
                }
                long exitsCount = this.count(new LambdaQueryWrapper<AssetsCategory>().eq(AssetsCategory::getType, assetsCategory.getType()).eq(AssetsCategory::getCode, code));
                if (exitsCount > 0) {
                    throw new RuntimeException("编码已存在");
                }
            }
        }
    }

    @Override
    public boolean edit(AssetsCategory assetsCategory) {
        checkData(assetsCategory);
        return this.updateById(assetsCategory);
    }

    @Override
    public boolean delete(Long id) {
        AssetsCategory oldEntity = this.getById(id);
        if (oldEntity == null) {
            return true;
        }
        if (assetsInfoService.checkCategoryExistAssets(oldEntity.getType(), oldEntity.getCode())) {
            throw new RuntimeException("该分类存在资产台账信息，不能修改编码/删除");
        }
        long count = this.count(new LambdaQueryWrapper<AssetsCategory>().eq(AssetsCategory::getType, oldEntity.getType()).eq(AssetsCategory::getPcode, oldEntity.getCode()));
        if (count > 0) {
            throw new RuntimeException("存在下级分类，不能修改编码或者删除");
        }
        return this.removeById(id);
    }

    /**
     * 根据code查询分类及其子分类
     * @param code
     * @return
     */
    @Override
    public List<AssetsCategory> selectCategoryAndChildren(String code, String type) {
        LambdaQueryWrapper<AssetsCategory> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(type)) {
            queryWrapper.eq(AssetsCategory::getType, type);
        }
        List<AssetsCategory> list = this.list(queryWrapper);
        if (StringUtils.isBlank(code)) {
            return list;
        }
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        List<AssetsCategory> resultList = selectChildren(list, code);
        return resultList;
    }

    /**
     * 查询子集
     * @return
     */
    private List<AssetsCategory> selectChildren(List<AssetsCategory> list, String code) {
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        List<AssetsCategory> resultList = new ArrayList<>();
        for (AssetsCategory assetsCategory : list) {
            if (code.equals(assetsCategory.getPcode())) {
                selectChildren(list, assetsCategory.getCode());
            }
            if (code.equals(assetsCategory.getCode())) {
                resultList.add(assetsCategory);
            }
        }
        return resultList;
    }

    @Override
    public Map<String, Map<String, String>> selectCategoryNameMap() {
        List<AssetsCategory> list = this.list();
        Map<String, Map<String, String>> result = new HashMap<>();
        for (AssetsCategory category : list) {
            String type = category.getType();
            Map<String, String> map = result.get(type);
            if (map == null) {
                map = new HashMap<>();
            }
            map.put(category.getCode(), category.getName());
            result.put(type, map);
        }
        return result;
    }

}




