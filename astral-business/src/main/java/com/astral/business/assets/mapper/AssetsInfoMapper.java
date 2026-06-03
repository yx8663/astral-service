package com.astral.business.assets.mapper;

import com.astral.business.assets.entity.AssetsInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
*/
public interface AssetsInfoMapper extends BaseMapper<AssetsInfo> {

    @Select({"<script>",
            "SELECT DISTINCT tags FROM astral_3d_assets_info WHERE category IN ",
            "<foreach item='item' collection='categoryCodeList' open='(' separator=',' close=')'>",
            "#{item}",
            "</foreach>",
            "</script>"})
    public List<String> selectTagsByCategory(@Param("categoryCodeList") List<String> categoryCodeList);

}




