package com.example.common.dto;

import com.example.entity.User;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/*
 *  @author changqi
 *  @date 2022/8/22 21:24
 *  @description
 *  @Version V1.0
 */
@Data
public class RegisterDto extends User {

    @NotBlank(message = "密码不能为空")
    public String repass;

    @NotBlank(message = "验证码不能为空")
    public String vercode;
}
