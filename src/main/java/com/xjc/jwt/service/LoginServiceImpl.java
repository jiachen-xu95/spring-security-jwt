package com.xjc.jwt.service;

import com.xjc.jwt.enums.ErrorEnum;
import com.xjc.jwt.enums.RoleEnum;
import com.xjc.jwt.exception.BaseException;
import com.xjc.jwt.mapper.dao.AuthsMapper;
import com.xjc.jwt.mapper.dao.UsersMapper;
import com.xjc.jwt.mapper.dataobject.AuthsDO;
import com.xjc.jwt.mapper.dataobject.UsersDO;
import com.xjc.jwt.web.UserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @Version 1.0
 * @ClassName LoginServiceImpl
 * @Author jiachenXu
 * @Date 2020/11/8
 * @Description
 */
@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private AuthsMapper authsMapper;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public boolean signUp(UserRequest userRequest) {
        String userName = userRequest.getUserName( );
        if (usersMapper.queryByUserName(userName) != null) {
            throw new BaseException(ErrorEnum.USER_NAME_ALREADY_EXIST, ErrorEnum.USER_NAME_ALREADY_EXIST.getMessage( ));
        }

        usersMapper.insert(new UsersDO(userName, bCryptPasswordEncoder.encode(userRequest.getPassWord( ))));

        // 分配权限
        authsMapper.insert(new AuthsDO(userName, RoleEnum.USER.name( )));

        return true;
    }

    /**
     * 获取全部用户
     *
     * @return
     */
    @Override
    public List<UserRequest> queryAll() {
        List<UsersDO> usersDOS = usersMapper.queryAll( );
        List<UserRequest> result = new ArrayList<>( );
        usersDOS.forEach(x -> {
            UserRequest userRequest = new UserRequest( );
            userRequest.setUserName(x.getUserName( ));
            result.add(userRequest);
        });

        return result;
    }
}
