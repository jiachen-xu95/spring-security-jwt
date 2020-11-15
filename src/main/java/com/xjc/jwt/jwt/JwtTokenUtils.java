package com.xjc.jwt.jwt;

import com.xjc.jwt.constants.SecurityConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.xml.bind.DatatypeConverter;
import java.util.Date;
import java.util.Set;

/**
 * @Version 1.0
 * @ClassName JwtTokenUtils
 * @Author jiachenXu
 * @Date 2020/11/4
 * @Description
 */
@Component
public class JwtTokenUtils {

    /**
     * 生成足够的安全随机密钥，以适合符合规范的签名
     */
    private final static byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(SecurityConstants.JWT_SECRET_KEY);

    private final static SecretKey secretKey = Keys.hmacShaKeyFor(apiKeySecretBytes);

    /**
     * 创建token
     *
     * @param username
     * @param roles
     * @param isRememberMe
     * @return
     */
    public String createToken(String username, Set<String> roles, boolean isRememberMe) {
        long expiration = getTokenExpiration(isRememberMe);
        Date createdDate = new Date( );
        Date expirationDate = new Date(createdDate.getTime( ) + expiration * 1000);
        String tokenPrefix = Jwts.builder( )
                .setHeaderParam("type", SecurityConstants.TOKEN_TYPE)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .claim(SecurityConstants.ROLE_CLAIMS, String.join(",", roles))
                .setIssuer("SnailClimb")
                .setIssuedAt(createdDate)
                .setSubject(username)
                .setExpiration(expirationDate)
                .compact( );
        return SecurityConstants.TOKEN_PREFIX + tokenPrefix;
    }

    /**
     * token是否过期
     *
     * @param token
     * @return
     */
    public boolean isTokenExpired(String token) {
        Date expiredDate = getTokenBody(token).getExpiration( );
        return expiredDate.before(new Date( ));
    }

    public String getUsernameByToken(String token) {
        return getTokenBody(token).getSubject( );
    }

    /**
     * 获取token
     *
     * @param token
     * @return
     */
    private Claims getTokenBody(String token) {
        return Jwts.parser( )
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody( );
    }

    /**
     * 根据是否保留七天决定过期时间
     *
     * @param isRememberMe
     * @return
     */
    private long getTokenExpiration(boolean isRememberMe) {
        return isRememberMe ? SecurityConstants.EXPIRATION_REMEMBER : SecurityConstants.EXPIRATION;
    }


}
