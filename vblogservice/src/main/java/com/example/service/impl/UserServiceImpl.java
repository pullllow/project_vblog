package com.example.service.impl;

import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.common.lang.RestResponse;
import com.example.entity.User;
import com.example.mapper.UserMapper;
import com.example.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author ChangQi
 * @since 2022-08-18
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public RestResponse register(User user) {

        int count = this.count(new QueryWrapper<User>()
                .eq("email", user.getEmail())
                .or()
                .eq("username",user.getUsername())
        );

        if(count > 0) {
            return RestResponse.fail("用户名或邮箱已被占用！");
        }
        User temp;

        try {
            temp = new User();
            temp.setUsername(user.getUsername());
            temp.setPassword(SecureUtil.md5(user.getPassword()));
            temp.setEmail(user.getEmail());

            temp.setCreated(new Date());
            temp.setPoint(0);
            temp.setGender("0");
            temp.setVipLevel(0);
            temp.setCommentCount(0);
            temp.setPostCount(0);
            temp.setAvatar("/res/images/avatar/default.png");

            this.save(temp);

        } catch (Exception e) {

        } finally {
            return RestResponse.success("注册成功");
        }

    }
}
