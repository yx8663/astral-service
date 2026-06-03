package com.astral.common.utils;

import com.astral.common.base.TreeNode;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 树构造工具类
 * @Author yx
 */
public class TreeBuilderUtil {

//    /**
//     * 构建树形结构
//     * @param nodes 所有节点列表
//     * @return 树形结构列表
//     */
//    public static <T extends TreeNode<T>> List<T> buildTree(List<T> nodes) {
//        // 获取所有根节点
//        List<T> rootNodes = nodes.stream()
//                .filter(node -> StringUtils.isBlank(node.getPkey()))
//                .sorted(Comparator.comparingInt(T::getSortNum))
//                .collect(Collectors.toList());
//
//        // 为每个根节点构建子树
//        for (T rootNode : rootNodes) {
//            buildChildNodes(rootNode, nodes);
//        }
//
//        return rootNodes;
//    }
//
//    /**
//     * 递归构建子节点
//     * @param parentNode 父节点
//     * @param nodes 所有节点列表
//     */
//    private static <T extends TreeNode<T>> void buildChildNodes(T parentNode, List<T> nodes) {
//        // 获取当前节点的所有子节点
//        List<T> children = nodes.stream()
//                .filter(node -> parentNode.getKey().equals(node.getPkey()))
//                .sorted(Comparator.comparingInt(T::getSortNum))
//                .collect(Collectors.toList());
//
//        // 递归构建每个子节点的子节点
//        for (T child : children) {
//            buildChildNodes(child, nodes);
//        }
//
//        // 设置子节点列表
//        parentNode.setChildren(children);
//    }

//    /**
//     * 构建树形结构
//     * @param nodes 所有节点列表
//     * @return 树形结构列表
//     */
//    public static <T extends TreeNode<T>> List<TreeNode> build(List<T> nodes) {
//        // 按父节点ID分组
//        Map<String, List<T>> pCodeMap = nodes.stream()
//                .filter(node -> StringUtils.isNotBlank(node.getPkey()))
//                .collect(Collectors.groupingBy(T::getPkey));
//
//        // 设置子节点
//        nodes.forEach(node -> {
//            List<T> children = pCodeMap.get(node.getKey());
//            if (children != null && !children.isEmpty()) {
//                // 排序(如果有sort字段)
//                children.sort(Comparator.comparingInt(T::getSortNum));
//                node.setChildren(children);
//            }
//        });
//
//        // 获取所有根节点
//        return nodes.stream()
//                .filter(node -> StringUtils.isBlank(node.getPkey()))
//                .sorted(Comparator.comparingInt(T::getSortNum))
//                .collect(Collectors.toList());
//    }

    public static List<TreeNode> buildTree(List<TreeNode> nodes) {
        // 按父节点ID分组
        Map<String, List<TreeNode>> pCodeMap = nodes.stream()
                .filter(node -> StringUtils.isNotBlank(node.getPkey()))
                .collect(Collectors.groupingBy(treeNode -> treeNode.getPkey() + "_" + treeNode.getType()));

        // 设置子节点
        nodes.forEach(node -> {
            List<TreeNode> children = pCodeMap.get(node.getKey() + "_" + node.getType());
            if (children != null && !children.isEmpty()) {
                // 排序(如果有sort字段)
                children.sort(Comparator.comparing(TreeNode::getSortNum, Comparator.nullsLast(Comparator.naturalOrder())));
                node.setChildren(children);
            }
        });

        // 获取所有根节点
        return nodes.stream()
                .filter(node -> StringUtils.isBlank(node.getPkey()))
                .sorted(Comparator.comparing(TreeNode::getSortNum, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
    }


}
