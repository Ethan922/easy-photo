package com.easyphoto.controller;

import com.easyphoto.common.Result;
import com.easyphoto.dto.AuthResponse;
import com.easyphoto.dto.LoginRequest;
import com.easyphoto.dto.RegisterRequest;
import com.easyphoto.service.AuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public Result<AuthResponse> register(@Valid @RequestBody RegisterRequest req) {
        return Result.success(authService.register(req));
    }

    @PostMapping("/login")
    public Result<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        return Result.success(authService.login(req));
    }
}
