package com.xjc.jwt.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Version 1.0
 * @ClassName UserRequest
 * @Author jiachenXu
 * @Date 2020/11/8 16:43
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {

    private String userName;

    private String passWord;

    private Boolean rememberMe;

}
