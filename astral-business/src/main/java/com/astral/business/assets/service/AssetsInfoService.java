package com.astral.business.assets.service;

import com.astral.business.assets.entity.AssetsInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface AssetsInfoService extends IService<AssetsInfo> {

    public boolean checkCategoryExistAssets(String type, String category);

    public List<String> selectTages(String category, String type);

}
