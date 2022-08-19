package com.example.business.shiro;
/*
 *  @author changqi
 *  @date 2022/8/19 22:03
 *  @description
 *  @Version V1.0
 */

import lombok.AllArgsConstructor;
import org.apache.shiro.authc.AuthenticationToken;

@AllArgsConstructor
public class JwtToken implements AuthenticationToken {

    private String token;


    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }
}
