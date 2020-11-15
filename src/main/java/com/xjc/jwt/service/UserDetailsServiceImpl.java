package com.xjc.jwt.service;

import com.xjc.jwt.mapper.dao.AuthsMapper;
import com.xjc.jwt.mapper.dao.UsersMapper;
import com.xjc.jwt.mapper.dataobject.AuthsDO;
import com.xjc.jwt.mapper.dataobject.UsersDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Version 1.0
 * @ClassName UserDetailsServiceImpl
 * @Author jiachenXu
 * @Date 2020/11/2
 * @Description
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private AuthsMapper authsMapper;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        UsersDO usersDO = usersMapper.queryByUserName(userName);
        AuthsDO authsDO = authsMapper.queryByUserName(userName);
        List<GrantedAuthority> authorityList = new ArrayList<>( );
        String role = authsDO.getAuths( );
        String[] authsArray = role.split(",");
        for (int i = 0; i < authsArray.length; i++) {
            authorityList.add(new SimpleGrantedAuthority(authsArray[i]));
        }
        usersDO.setAuthorityList(authorityList);
        return usersDO;
    }

}
