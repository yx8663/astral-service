package com.astral.business.cad.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * (Lb3dEditorCad)表实体类
 */
@Data
@TableName("lb_3d_editor_cad")
public class Lb3dEditorCad {

    @TableId(type = IdType.AUTO)
    private Long id;
    //文件名
    private String fileName;
    //缩略图
    private String thumbnail;
    //源文件路径
    private String filePath;
    //转换后的文件路径
    private String converterFilePath;
    //0 转换中 1 转换完成 2 转换失败
    private Integer conversionStatus;
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

