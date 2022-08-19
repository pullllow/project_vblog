package com.example.business.shiro;
/*
 *  @author changqi
 *  @date 2022/8/19 21:59
 *  @description
 *  @Version V1.0
 */

import cn.hutool.core.bean.BeanUtil;
import com.example.business.shiro.data.AccountProfile;
import com.example.business.shiro.utils.JwtUtils;
import com.example.entity.User;
import com.example.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AccountRealm extends AuthorizingRealm {

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    UserService userService;

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        return null;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        JwtToken jwt = (JwtToken) token;
        log.info("jwt--------------------->{}",jwt);
        String userId = jwtUtils.getClaimByToken((String)jwt.getPrincipal()).getSubject();
        User user = userService.getById(userId);
        if(user==null) {
            throw new UnknownAccountException("账户不存在！");
        }
        if(user.getPassword()==null) {
            throw new UnknownAccountException("账户未激活！");
        }
        AccountProfile profile = new AccountProfile();
        BeanUtil.copyProperties(user, profile);
        log.info("profile----------------->{}",profile.toString());
        return new SimpleAuthenticationInfo(profile, jwt.getCredentials(), getName());

    }
}
