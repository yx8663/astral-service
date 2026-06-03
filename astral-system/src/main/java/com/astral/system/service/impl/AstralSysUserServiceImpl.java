package com.astral.system.service.impl;

import com.astral.common.result.Result;
import com.astral.common.utils.JwtUtil;
import com.astral.system.entity.vo.LoginResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.astral.system.mapper.AstralSysUserMapper;
import com.astral.system.entity.AstralSysUser;
import com.astral.system.service.AstralSysUserService;
import lombok.val;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * (LbSysUser)表服务实现类
 */
@Service("lbSysUserService")
public class AstralSysUserServiceImpl extends ServiceImpl<AstralSysUserMapper, AstralSysUser> implements AstralSysUserService {

    @Override
    public Result<?> doLogin(AstralSysUser lbSysUser) {
        String username = lbSysUser.getUsername();
        String password = lbSysUser.getPassword();
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            return Result.error("用户名或密码不能为空！");
        }
        val user = this.getOne(new LambdaQueryWrapper<AstralSysUser>().eq(AstralSysUser::getUsername, username));
        if (user == null) {
            return Result.error("错误: 用户名不存在！");
        }
        String hash = JwtUtil.generatePassHash(password, user.getSalt());
        if (!Objects.equals(hash, user.getPassword())) {
            return Result.error("错误: 密码错误！");
        }
        String tokenString = JwtUtil.generateToken(lbSysUser.getUsername(), user.getId(), 0);
        if (tokenString == null) {
            return Result.error("Token generation failed");
        }
        return Result.success(new LoginResponse(user.getUsername(), user.getId(), tokenString));
    }

    @Override
    public void register(String username, String password) {

    }

}

