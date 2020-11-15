package com.xjc.jwt.web;

import com.xjc.jwt.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Version 1.0
 * @ClassName AuthController
 * @Author jiachenXu
 * @Date 2020/11/4
 * @Description
 */
@RestController
@RequestMapping("/users")
public class AuthController {

    @Autowired
    private LoginService loginService;

    @PostMapping(value = "/signUp")
    public boolean signUp(@RequestBody UserRequest userRequest) {
        return loginService.signUp(userRequest);
    }

    @GetMapping("/queryAll")
    public List<UserRequest> queryAll() {
        return loginService.queryAll();
    }

}
