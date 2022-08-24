package com.example.business.shiro.data;
/*
 *  @author changqi
 *  @date 2022/8/19 22:31
 *  @description
 *  @Version V1.0
 */

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class AccountProfile implements Serializable {
    private Long id;
    private String username;
    private String email;

    private String avatar;
    private String sign;

    private Date created;
}
