package com.astral.system.entity;


import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * (LbSysUser)表实体类
 *
 * @author makejava
 * @since 2025-03-30 20:26:12
 */
@TableName("lb_sys_user")
@Data
public class LbSysUser extends Model<LbSysUser> {

    @TableId
    private Long id;
    //用户名
    private String username;
    //用户昵称
    private String nickname;
    //用户手机号
    private String mobile;
    //用户密码
    private String password;
    //性别， 0 表示女， 1 表示男
    private Integer sex;
    //头像
    private String avatar;
    //邮箱
    private String email;
    //删除标记，0 未删除 1 已删除
    private Integer deltag;
    //jwt 鉴权 SALT值
    private String salt;
    //最后登录时间
    private Date lastlogintime;
    //最后登录ip
    private String lastloginip;
    //注册时的ip地址
    private String registerip;

    private Date createtime;

    private Date updatetime;

    private Date deltime;

}

