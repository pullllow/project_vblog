package com.example.controller;

import cn.hutool.core.map.MapUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.additional.query.impl.QueryChainWrapper;
import com.example.business.shiro.utils.JwtUtils;
import com.example.common.dto.LoginDto;
import com.example.common.lang.RestResponse;
import com.example.entity.User;
import com.example.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

/**
 * @author Chang Qi
 * @date 2022/8/20 14:39
 * @description
 * @Version V1.0
 */

@RestController
public class AccountController {

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    UserService userService;


    @PostMapping("/login")
    public RestResponse login(@Validated @RequestBody(required = false) LoginDto loginDto, HttpServletResponse response) {
        User user = userService.getOne(new QueryWrapper<User>().eq("username", loginDto.getUsername()));
        Assert.notNull(user,"用户不存在");
        if(!user.getPassword().equals(SecureUtil.md5(loginDto.getPassword()))) {
            return RestResponse.fail("密码错误！");
        }
        String jwt = jwtUtils.generateToken(user.getId());
        //String jwt = "";
        response.setHeader("x-user-token", jwt);
        response.setHeader("Access-Control-Expose-Headers", "x-user-token");

        return RestResponse.success(MapUtil.builder()
                .put("id",user.getId())
                .put("username",user.getUsername())
                .put("avatar", user.getAvatar())
                .put("email",user.getEmail())
                .map());
    }

    @GetMapping("/logout")
    @RequiresAuthentication
    public RestResponse logout() {
        SecurityUtils.getSubject().logout();
        return RestResponse.success(null);
    }

}
