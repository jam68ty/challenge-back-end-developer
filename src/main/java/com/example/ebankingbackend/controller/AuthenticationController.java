package com.example.ebankingbackend.controller;

import com.example.ebankingbackend.service.AuthenticationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Api(tags = "Authentication 權限")
public class AuthenticationController {
    private final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/login")
    @ApiOperation(value = "登入", notes = "login")
    public ResponseEntity<?> loginUser(@ApiParam(value = "username", required = true) @RequestParam("username") String username,
                                       @ApiParam(value = "password", required = true) @RequestParam("password") String password) {
        return authenticationService.loginUser(username, password);
    }


    @PostMapping("/register")
    @ApiOperation(value = "註冊", notes = "register")
    public ResponseEntity<?> saveUser(@ApiParam(value = "username", required = true) @RequestParam("username") String username,
                                      @ApiParam(value = "password", required = true) @RequestParam("password") String password,
                                      @ApiParam(value = "email", required = true) @RequestParam("email") String email) {
        return authenticationService.saveUser(username, password, email);
    }

    @GetMapping("/user")
    @ApiOperation(value = "查看使用者名稱", notes = "get username")
    public Map<String, Object> getUserName() {
        return authenticationService.getUserName();
    }
}
