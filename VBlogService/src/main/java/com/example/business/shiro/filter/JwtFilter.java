package com.example.business.shiro.filter;

import cn.hutool.json.JSONUtil;
import com.example.business.shiro.JwtToken;
import com.example.business.shiro.utils.JwtUtils;
import com.example.common.lang.RestResponse;
import io.jsonwebtoken.Claims;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.ExpiredCredentialsException;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/*
 *  @author changqi
 *  @date 2022/8/19 22:49
 *  @description
 *  @Version V1.0
 */

@Component
public class JwtFilter extends AuthenticatingFilter {

    @Autowired
    JwtUtils jwtUtils;

    private static final String HEADER_TOKEN = "x-user-token";


    @Override
    protected AuthenticationToken createToken(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        // 获取token
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String jwt = request.getHeader(HEADER_TOKEN);
        if (StringUtils.hasLength(jwt)) {
            return null;
        }
        return new JwtToken(jwt);
    }

    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String token = request.getHeader(HEADER_TOKEN);
        if (StringUtils.hasLength(token)) {
            return true;
        } else {
            // 判断是否过期
            Claims claim = jwtUtils.getClaimByToken(token);
            if (claim == null || jwtUtils.isTokenExpired(claim.getExpiration())) {
                throw new ExpiredCredentialsException("token已失效，请重新登录");
            }
        }
        // 执行自动登录
        return executeLogin(servletRequest, servletResponse);
    }

    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response) {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        try {
            Throwable throwable = e.getCause() == null ? e : e.getCause();
            RestResponse restResponse = RestResponse.fail(throwable.getMessage());
            String json = JSONUtil.toJsonStr(restResponse);
            httpResponse.getWriter().print(json);
        } catch (Exception ex) {

        }
        return false;
    }

    /**
     * 提供跨域支持
     *
     * @param request
     * @param response
     * @return
     **/
    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = WebUtils.toHttp(request);
        HttpServletResponse httpServletResponse = WebUtils.toHttp(response);
        httpServletResponse.setHeader("Access-Control-Allow-Origin",
                httpServletRequest.getHeader("Origin"));
        httpServletResponse.setHeader("Access-Control-Allow-Methods",
                "GET,POST,OPTIONS,PUT,DELETE");
        httpServletResponse.setHeader("Access-Control-Allow-Headers",
                httpServletRequest.getHeader("Access-Control-Allow-Headers"));

        //跨域校验时，首先发送OPTIONS请求，给OPTIONS请求直接返回正常状态
        if(httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
            httpServletResponse.setStatus(HttpStatus.OK.value());
            return false;
        }
        return super.preHandle(request, response);
    }
}
