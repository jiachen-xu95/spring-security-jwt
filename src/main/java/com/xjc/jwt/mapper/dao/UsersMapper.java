package com.xjc.jwt.mapper.dao;

import com.xjc.jwt.mapper.dataobject.UsersDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Version 1.0
 * @ClassName UsersMapper
 * @Author jiachenXu
 * @Date 2020/11/2
 * @Description
 */
@Mapper
public interface UsersMapper {

    List<UsersDO> queryAll();

    UsersDO queryByUserName(String userName);

    int insert(UsersDO usersDO);

}
