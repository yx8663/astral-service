package com.astral.business.scenes.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 场景zip压缩包信息表(Lb3dEditorScenes)表实体类
 */
@Data
@TableName("lb_3d_editor_scenes")
public class Lb3dEditorScenes {

    //主键ID,UUID
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    //场景类型
    @TableField("sceneType")
    private String sceneType;

    //场景名称
    @TableField("sceneName")
    private String sceneName;

    //场景版本
    @TableField("sceneVersion")
    private Integer sceneVersion;

    //场景描述
    @TableField("sceneIntroduction")
    private String sceneIntroduction;

    //保存场景时自动生成的封面图url
    @TableField("coverPicture")
    private String coverPicture;

    //场景是否包含图纸 0:false  1:true
    @TableField("hasDrawing")
    private Integer hasDrawing;

    //场景zip包
    private String zip;

    //场景zip包大小
    @TableField("zipSize")
    private String zipSize;

    //创建项目时来源于哪一个示例模板项目，null代表从空项目创建。（fk）
    @TableField("exampleSceneId")
    private String exampleSceneId;

    //项目类型。0：Web3D-THREE  1：WebGIS-Cesium
    @TableField("projectType")
    private Integer projectType;

    //WebGIS-Cesium 类型项目的基础Cesium配置
    @TableField("cesiumConfig")
    private String cesiumConfig;

    //删除标记，0 未删除 1 已删除
    @TableField("delTag")
    private Integer delTag;

    @TableField("createTime")
    private Date createTime;

    @TableField("updateTime")
    private Date updateTime;

    @TableField("delTime")
    private Date delTime;

}

