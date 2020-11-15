package com.xjc.jwt.filter;

import com.xjc.jwt.constants.SecurityConstants;
import com.xjc.jwt.jwt.JwtTokenUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * @Version 1.0
 * @ClassName JwtAuthorizationFilter
 * @Author jiachenXu
 * @Date 2020/11/4
 * @Description 处理所有HTTP请求，并检查是否存在带有正确的令牌
 */
@Slf4j
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private JwtTokenUtils jwtTokenUtils;

    private UserDetailsService userDetailsService;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, UserDetailsService userDetailsService, JwtTokenUtils jwtTokenUtils) {
        super(authenticationManager);
        this.userDetailsService = userDetailsService;
        this.jwtTokenUtils = jwtTokenUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String token = request.getHeader(SecurityConstants.TOKEN_HEADER);
        if (token == null || !token.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            SecurityContextHolder.clearContext( );
        } else {
            UsernamePasswordAuthenticationToken authentication = getAuthentication(Objects.requireNonNull(token));
            SecurityContextHolder.getContext( ).setAuthentication(authentication);
        }
        chain.doFilter(request, response);
    }

    /**
     * 获取用户认证信息 Authentication
     *
     * @param authorization
     * @return
     */
    private UsernamePasswordAuthenticationToken getAuthentication(String authorization) {
        String token = authorization.replace(SecurityConstants.TOKEN_PREFIX, "");
        try {
            String username = jwtTokenUtils.getUsernameByToken(token);
            if (!StringUtils.isEmpty(username)) {
                // 这里我们是又从数据库拿了一遍,避免用户的角色信息有变
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(username, null, userDetails.getAuthorities( ));
                return userDetails.isEnabled( ) ? usernamePasswordAuthenticationToken : null;
            }
        } catch (ExpiredJwtException | MalformedJwtException | IllegalArgumentException exception) {
            log.warn("Request to parse JWT with invalid signature . Detail : " + exception.getMessage( ));
        }
        return null;
    }


}
