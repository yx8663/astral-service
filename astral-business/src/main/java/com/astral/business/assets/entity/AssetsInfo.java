package com.astral.business.assets.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName 资产信息
 */
@TableName(value ="astral_3d_assets_info")
@Data
public class AssetsInfo implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 文件名
     */
    private String name;

    /**
     * 类型
     */
    private String type;

    /**
     * 类别
     */
    private String category;

    /**
     * 缩略图
     */
    private String thumbnail;

    /**
     * 文件大小
     */
    private String size;

    /**
     * 标签
     */
    private String tags;

    /**
     * 附件地址
     */
    private String file;

    /**
     * 删除标记，0 未删除 1 已删除
     */
    @TableField("delTag")
    private Integer delTag;

    /**
     * 
     */
    @TableField("createTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 
     */
    @TableField("updateTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    /**
     * 
     */
    @TableField("delTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date delTime;

    /**
     * 分类名称
     */
    @TableField(exist = false)
    private String categoryName;

}