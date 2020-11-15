package com.xjc.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xjc.jwt.constants.SecurityConstants;
import com.xjc.jwt.enums.ErrorEnum;
import com.xjc.jwt.exception.BaseException;
import com.xjc.jwt.jwt.JwtTokenUtils;
import com.xjc.jwt.mapper.dataobject.UsersDO;
import com.xjc.jwt.web.UserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Version 1.0
 * @ClassName JwtAuthenticationFilter
 * @Author jiachenXu
 * @Date 2020/11/4
 * @Description 用户名和密码正确，那么过滤器将创建一个token 并在Response的header中返回它
 * 格式：token: "Bearer +具体token值
 */
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;

    private UserDetailsService userDetailsService;

    /**
     * token是否保留七天
     */
    private ThreadLocal<Boolean> rememberMe = new ThreadLocal<>( );

    private JwtTokenUtils jwtTokenUtils;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, UserDetailsService userDetailsService, JwtTokenUtils jwtTokenUtils) {
        // 登录的地址 检查是否需要身份验证
        super.setFilterProcessesUrl(SecurityConstants.AUTH_LOGIN_URL);
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtTokenUtils = jwtTokenUtils;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        ObjectMapper objectMapper = new ObjectMapper();
        UserRequest userRequest = null;
        try {
             userRequest = objectMapper.readValue(request.getInputStream(), UserRequest.class);
        } catch (IOException e) {
            e.printStackTrace( );
        }
        String username = userRequest.getUserName();
        String password = userRequest.getPassWord();
        Boolean rememberMe = userRequest.getRememberMe( );
        this.rememberMe.set(rememberMe);

        Collection<? extends GrantedAuthority> authorities = getAuthorities(username);
        try {
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, password, authorities);
            // 校验信息
            return authenticationManager.authenticate(authentication);
        } catch (AuthenticationException e) {
            throw new BaseException(ErrorEnum.USER_NAME_NOT_FOUND, ErrorEnum.USER_NAME_ALREADY_EXIST.getMessage( ));
        }
    }

    /**
     * 如果验证成功，就生成token并返回
     *
     * @param request
     * @param response
     * @param chain
     * @param authResult
     * @throws IOException
     * @throws ServletException
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult) throws IOException, ServletException {
        Object principal = authResult.getPrincipal( );
        if (principal instanceof UsersDO) {
            UsersDO usersDO = (UsersDO) authResult.getPrincipal( );
            String username = usersDO.getUsername( );

            List<GrantedAuthority> authorityList = usersDO.getAuthorityList();
            Set<String> roles = authorityList.stream( ).map(GrantedAuthority::getAuthority).collect(Collectors.toSet( ));
            Collection<? extends GrantedAuthority> authorities = getAuthorities(username);
            Set<String> collect = authorities.stream( ).map(GrantedAuthority::getAuthority).collect(Collectors.toSet( ));
            if (!collect.containsAll(roles)) {
                throw new BaseException(ErrorEnum.Role_NOT_FOUND, ErrorEnum.Role_NOT_FOUND.getMessage());
            }

            // get token
            String token = jwtTokenUtils.createToken(username, roles, rememberMe.get( ));
            if (null != token) {
                usersDO.setRememberMe(rememberMe.get( ));
                response.setHeader(SecurityConstants.TOKEN_HEADER, token);
            }
        }
    }

    /**
     * 失败的处理
     *
     * @param request
     * @param response
     * @param authenticationException
     * @throws IOException
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException authenticationException) throws IOException {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authenticationException.getMessage( ));
    }

    /**
     * 从DB获取角色的角色
     *
     * @param username
     * @return
     */
    private Collection<? extends GrantedAuthority> getAuthorities(String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return userDetails.getAuthorities( );
    }


}
