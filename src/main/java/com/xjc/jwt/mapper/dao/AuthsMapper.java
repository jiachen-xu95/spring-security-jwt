package com.xjc.jwt.mapper.dao;

import com.xjc.jwt.mapper.dataobject.AuthsDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Version 1.0
 * @ClassName AuthsMapper
 * @Author jiachenXu
 * @Date 2020/11/2
 * @Description
 */
@Mapper
public interface AuthsMapper {

    AuthsDO queryByUserName(String userName);

    int insert(AuthsDO authsDO);

}
