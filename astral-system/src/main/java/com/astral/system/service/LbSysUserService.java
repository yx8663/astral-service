package com.astral.system.service;

import com.astral.common.result.Result;
import com.baomidou.mybatisplus.extension.service.IService;
import com.astral.system.entity.LbSysUser;

/**
 * (LbSysUser)表服务接口
 *
 * @author makejava
 * @since 2025-03-30 20:26:13
 */
public interface LbSysUserService extends IService<LbSysUser> {

    Result<?> doLogin(LbSysUser lbSysUser);

    void register(String username, String password);


}

