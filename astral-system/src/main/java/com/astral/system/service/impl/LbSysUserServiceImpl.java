package com.astral.system.service.impl;

import com.astral.common.result.Result;
import com.astral.common.utils.JwtUtil;
import com.astral.system.entity.vo.LoginResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.astral.system.mapper.LbSysUserMapper;
import com.astral.system.entity.LbSysUser;
import com.astral.system.service.LbSysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * (LbSysUser)表服务实现类
 */
@Service("lbSysUserService")
public class LbSysUserServiceImpl extends ServiceImpl<LbSysUserMapper, LbSysUser> implements LbSysUserService {

    @Override
    public Result<?> doLogin(LbSysUser lbSysUser) {
        String username = lbSysUser.getUsername();
        String password = lbSysUser.getPassword();
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            return Result.error("用户名或密码不能为空！");
        }
        LbSysUser user = this.getOne(new LambdaQueryWrapper<LbSysUser>().eq(LbSysUser::getUsername, username));
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

