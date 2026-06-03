package com.astral.business.assets.service.impl;

import com.astral.business.assets.entity.AssetsCategory;
import com.astral.business.assets.entity.AssetsInfo;
import com.astral.business.assets.mapper.AssetsInfoMapper;
import com.astral.business.assets.service.AssetsCategoryService;
import com.astral.business.assets.service.AssetsInfoService;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
* @author 13623
* @description 针对表【assets_info】的数据库操作Service实现
* @createDate 2025-07-03 11:03:38
*/
@Service
public class AssetsInfoServiceImpl extends ServiceImpl<AssetsInfoMapper, AssetsInfo> implements AssetsInfoService {

    @Autowired
    @Lazy
    private AssetsCategoryService assetsCategoryService;

    /**
     * 校验分类下是否存在资产
     * 存在则返回true，不存在则返回false
     * @param category
     */
    @Override
    public boolean checkCategoryExistAssets(String type, String category) {
        long count = this.count(new LambdaQueryWrapper<AssetsInfo>().eq(AssetsInfo::getType, type).likeRight(AssetsInfo::getCategory, category));
        if (count > 0) {
            return true;
        }
        return false;
    }

    @Override
    public List<String> selectTages(String category, String type) {
        List<AssetsCategory> categoryList = assetsCategoryService.selectCategoryAndChildren(category, "");
        if (CollectionUtils.isEmpty(categoryList)) {
            return new ArrayList<>();
        }
        List<String> categoryCodeList = categoryList.stream().map(AssetsCategory::getCode).collect(Collectors.toList());
        List<AssetsInfo> list = this.list(new LambdaQueryWrapper<AssetsInfo>().eq(AssetsInfo::getType, type).in(AssetsInfo::getCategory, categoryCodeList));
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        Set<String> tagsSet = new HashSet<>();
        for (AssetsInfo assetsInfo : list) {
            String tags = assetsInfo.getTags();
            if (StringUtils.isBlank(tags)) {
                continue;
            }
            String[] split = tags.split(",");
            tagsSet.addAll(Arrays.asList(split));
        }
        return new ArrayList<>(tagsSet);
    }

}




