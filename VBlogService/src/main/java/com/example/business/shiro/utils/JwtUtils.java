package com.example.business.shiro.utils;

import io.jsonwebtoken.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Date;

/*
 *  @author changqi
 *  @date 2022/8/19 22:18
 *  @description
 *  @Version V1.0
 */
@Data
@Slf4j
@Component
@ConfigurationProperties(prefix = "vblog.jwt")
public class JwtUtils {

    private String secret;
    private long expire;
    private String header;

    /**
     * 生成token
     *
     * @param userId
     * @return
     **/
    public String generateToken(long userId) {
        Date currentDate = new Date();
        //设置过期时间
        Date expireData  = new Date(currentDate.getTime() + expire * 1000);

        return Jwts.builder()
                .setHeaderParam("typ","JWT")
                .setSubject(userId+"")
                .setIssuedAt(currentDate)
                .setExpiration(expireData)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    /**
     * 获取jwt信息
     *
     * @param token
     * @return
     **/
    public Claims getClaimByToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            log.error("validate is token error", e);
            return null;
        }
    }

    /**
     * token 是否过期
     *
     * @param expiration
     * @return
     **/
    public boolean isTokenExpired(Date expiration) {
        return expiration.before(new Date());
    }

}
