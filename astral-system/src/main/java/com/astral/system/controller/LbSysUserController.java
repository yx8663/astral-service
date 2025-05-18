package com.astral.system.controller;

import com.astral.common.result.Result;
import com.astral.system.entity.LbSysUser;
import com.astral.system.service.LbSysUserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * (LbSysUser)表控制层
 *
 * @author makejava
 * @since 2025-03-30 20:26:10
 */
@RestController
@RequestMapping("/user")
public class LbSysUserController {
    /**
     * 服务对象
     */
    @Resource
    private LbSysUserService lbSysUserService;

    /**
     * 新增
     */
    @PostMapping("/createUser")
    public Result<?> post(@RequestBody LbSysUser lbSysUser) {
        return Result.toAjax(lbSysUserService.save(lbSysUser));
    }

    @GetMapping("/getUserInfo/{id}")
    public Result<?> getOne(@PathVariable("id") String id) {
        try {
            return Result.success(lbSysUserService.getById(id));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/getAllUser")
    public Result<?> getAll(HttpServletRequest request) {
        String fieldsStr = request.getParameter("fields");
        List<String> fields = StringUtils.hasLength(fieldsStr) ? Arrays.asList(fieldsStr.split(",")) : new ArrayList<>();
        String limitStr = request.getParameter("limit");
        Integer limit = StringUtils.hasLength(limitStr) ? Integer.parseInt(limitStr) : 10;
        String offsetStr = request.getParameter("offset");
        Integer offset = StringUtils.hasLength(offsetStr) ? Integer.parseInt(offsetStr) : 0;
        String sortbyStr = request.getParameter("sortby");
        List<String> sortby = StringUtils.hasLength(sortbyStr) ? Arrays.asList(sortbyStr.split(",")) : new ArrayList<>();
        String orderStr = request.getParameter("order");
        List<String> order = StringUtils.hasLength(orderStr) ? Arrays.asList(orderStr.split(",")) : new ArrayList<>();
        String queryStr = request.getParameter("query");
        Map<String, String> query = new HashMap<>();
        if (StringUtils.hasLength(queryStr)) {
            try {
                query = Arrays.stream(queryStr.split(",")).map(s -> {
                    String[] split = s.split(":");
                    if (split.length != 2) {
                        throw new IllegalArgumentException("error:无效的查询键/值对");
                    }
                    return split;
                }).collect(Collectors.toMap(s -> s[0], s -> s[1], (v1, v2) -> v1));
            } catch (Exception e) {
                return Result.error(e.getMessage());
            }
        }
        QueryWrapper<LbSysUser> queryWrapper = new QueryWrapper<LbSysUser>();
        query.forEach((k, v) -> {
            String filedName = StringUtils.replace(k, ".", "__");
            if ("isnull".equals(filedName)) {
                queryWrapper.eq(filedName, "true".equals(v) || "1".equals(v));
            } else {
                queryWrapper.eq(filedName, v);
            }
        });
        if (!CollectionUtils.isEmpty(sortby)) {
            if (sortby.size() == order.size()) {
                for (int i = 0; i < sortby.size(); i++) {
                    if ("desc".equals(order.get(i))) {
                        queryWrapper.orderByDesc(sortby.get(i));
                    } else if ("asc".equals(order.get(i))) {
                        queryWrapper.orderByAsc(sortby.get(i));
                    } else {
                        return Result.error("Error: Invalid order. Must be either [asc|desc]");
                    }
                }

            } else if (order.size() == 1) {
                if ("desc".equals(order.get(0))) {
                    queryWrapper.orderByDesc(sortby);
                } else if ("asc".equals(order.get(0))) {
                    queryWrapper.orderByAsc(sortby);
                }else {
                    return Result.error("Error: Invalid order. Must be either [asc|desc]");
                }
            } else {
                return Result.error("Error: 'sortby', 'order' sizes mismatch or 'order' size is not 1");
            }
        } else {
            if (!CollectionUtils.isEmpty(order)) {
                return Result.error("Error: unused 'order' fields");
            }
        }
        Page<LbSysUser> page = new Page<>();
        page.setSize(limit);
        page.setCurrent(offset / limit + 1);
        if (!CollectionUtils.isEmpty(fields)) {
            queryWrapper.select(fields);
        }
        try {
            return Result.success(lbSysUserService.page(page, queryWrapper));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/updateUser/{id}")
    public Result<?> put(@PathVariable("id") Long id, @RequestBody LbSysUser lbSysUser) {
        lbSysUser.setId(id);
        return Result.toAjax(lbSysUserService.updateById(lbSysUser));
    }

    @DeleteMapping("/delUser/{id}")
    public Result<?> delete(@PathVariable("id") Long id) {
        return Result.toAjax(lbSysUserService.removeById(id));
    }

    @PostMapping("/login")
    public Result<?> login(@RequestBody LbSysUser lbSysUser) {
        return lbSysUserService.doLogin(lbSysUser);
    }

    @PostMapping("/register")
    public void register() {

    }

}

