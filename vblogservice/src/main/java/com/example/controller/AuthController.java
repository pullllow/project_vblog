package com.example.controller;

import cn.hutool.core.map.MapUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.example.business.shiro.utils.JwtUtils;
import com.example.common.dto.LoginDto;
import com.example.common.dto.RegisterDto;
import com.example.common.lang.RestResponse;
import com.example.common.util.ValidationUtil;
import com.example.entity.User;
import com.example.service.UserService;
import com.google.code.kaptcha.Producer;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @author Chang Qi
 * @date 2022/8/20 14:39
 * @description
 * @Version V1.0
 */

@RestController
public class AuthController {

    private static final String KAPTCHA_SESSION_KEY = "KAPTCHA_SESSION_KEY";

    @Autowired
    Producer producer;

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

    @PostMapping("/register")
    public RestResponse register(@Validated @RequestBody(required = false)RegisterDto registerDto, HttpServletRequest request) {
        RestResponse restResponse = new RestResponse();
        ValidationUtil.ValidResult validResult = ValidationUtil.validateBean(registerDto);
        if(validResult.hasErrors()) {
            restResponse = RestResponse.fail(validResult.getErrors());
            return restResponse;
        }


        if(!registerDto.getPassword().equals(registerDto.getRepass())) {
            restResponse =  RestResponse.fail("两次输入密码不一致");
            return restResponse;
        }
        // 获取验证码
        String kaptcha = (String) request.getSession().getAttribute(KAPTCHA_SESSION_KEY);
        if(!StringUtils.hasLength(registerDto.getVercode()) || !registerDto.getVercode().equalsIgnoreCase(kaptcha)) {
            restResponse =  RestResponse.fail("验证码输入不正确");
            return restResponse;
        }
        User user;

        try {
            user = new User();
            user.setUsername(registerDto.getUsername());
            user.setPassword(registerDto.getPassword());
            user.setEmail(registerDto.getEmail());
            //注册
            restResponse = userService.register(user);
        } catch (Exception e) {

        } finally {
            return restResponse;
        }

    }

    @GetMapping("/kaptcha.jpg")
    public void kaptcha(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //验证码
        String text = producer.createText();
        BufferedImage image = producer.createImage(text);
        request.getSession().setAttribute(KAPTCHA_SESSION_KEY, text);

        response.setHeader("Cache-Control", "no-store, no-cache");
        response.setContentType("image/jpeg");
        ServletOutputStream outputStream = response.getOutputStream();
        ImageIO.write(image, "jpg", outputStream);
    }

}
