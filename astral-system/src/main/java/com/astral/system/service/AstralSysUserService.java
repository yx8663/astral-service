package com.astral.system.service;

import com.astral.common.result.Result;
import com.baomidou.mybatisplus.extension.service.IService;
import com.astral.system.entity.AstralSysUser;

/**
 * (LbSysUser)表服务接口
 *
 * @author makejava
 * @since 2025-03-30 20:26:13
 */
public interface AstralSysUserService extends IService<AstralSysUser> {

    Result<?> doLogin(AstralSysUser astralSysUser);

    void register(String username, String password);


}

