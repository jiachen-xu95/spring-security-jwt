package com.xjc.jwt.service;

import com.xjc.jwt.web.UserRequest;

import java.util.List;

/**
 * @Version 1.0
 * @ClassName LoginService
 * @Author jiachenXu
 * @Date 2020/11/8
 * @Description
 */
public interface LoginService {

    /**
     * 注册用户
     *
     * @param userRequest
     * @return
     */
    boolean signUp(UserRequest userRequest);

    /**
     * 获取全部用户
     *
     * @return
     */
    List<UserRequest> queryAll();

}
