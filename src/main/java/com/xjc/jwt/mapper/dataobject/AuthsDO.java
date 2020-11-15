package com.xjc.jwt.mapper.dataobject;

import lombok.Data;

import java.io.Serializable;

/**
 * @Version 1.0
 * @ClassName AuthsDO
 * @Author jiachenXu
 * @Date 2020/11/2
 * @Description
 */
@Data
public class AuthsDO implements Serializable {

    private Integer id;
    
    private String userName;
    
    private String auths;

    public AuthsDO(String userName, String auths) {
        this.userName = userName;
        this.auths = auths;
    }
}
