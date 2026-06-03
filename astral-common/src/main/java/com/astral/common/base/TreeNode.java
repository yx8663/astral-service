package com.astral.common.base;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 树节点
 */
@Data
public class TreeNode {

    private String id;

    private String key;

    private String pkey;

    private String label;

    private Integer sortNum;

    private String type;

    @TableField(exist = false)
    private List<TreeNode> children = new ArrayList<>();

}
