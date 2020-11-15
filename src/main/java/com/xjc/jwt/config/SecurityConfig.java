package com.xjc.jwt.config;

import com.xjc.jwt.exception.JwtAccessDeniedHandler;
import com.xjc.jwt.exception.JwtAuthenticationEntryPoint;
import com.xjc.jwt.filter.JwtAuthenticationFilter;
import com.xjc.jwt.filter.JwtAuthorizationFilter;
import com.xjc.jwt.jwt.JwtTokenUtils;
import com.xjc.jwt.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * @Version 1.0
 * @ClassName SecurityConfig
 * @Author jiachenXu
 * @Date 2020/11/4
 * @Description
 */
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtTokenUtils jwtTokenUtils;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder( ));
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and()
                // 为了测试禁止CSRF
                .csrf( ).disable( )
                .authorizeRequests( )
                // 登录放开
                .antMatchers(HttpMethod.POST, "/auth/login").permitAll( )
                // 匹配路径下需要验证
                .antMatchers("/api/**").authenticated( )
                // 操作权限控制
                .antMatchers(HttpMethod.DELETE, "/api/users/**").hasRole("ADMIN")
                // 以上接口外均放行
                .anyRequest( ).permitAll( )
                .and( )
                .addFilter(new JwtAuthorizationFilter(authenticationManager( ), userDetailsService, jwtTokenUtils))
                .addFilter(new JwtAuthenticationFilter(authenticationManager(), userDetailsService, jwtTokenUtils))
                // 不创建会话
                .sessionManagement( ).sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                // 授权异常处理
                .exceptionHandling().authenticationEntryPoint(new JwtAuthenticationEntryPoint())
                .accessDeniedHandler(new JwtAccessDeniedHandler());
        // 防止H2 web 页面的Frame 被拦截
        http.headers( ).frameOptions( ).disable( );
    }

    @Bean
    public UserDetailsService createUserDetailsService() {
        return userDetailsService;
    }

    /**
     * 密码加密器
     *
     * @return
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder( );
    }
}
