package com.easyphoto.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.easyphoto.common.BusinessException;
import com.easyphoto.dto.AuthResponse;
import com.easyphoto.dto.LoginRequest;
import com.easyphoto.dto.RegisterRequest;
import com.easyphoto.entity.User;
import com.easyphoto.mapper.UserMapper;
import com.easyphoto.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserMapper userMapper, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse register(RegisterRequest req) {
        // 用户名唯一校验（逻辑删除自动过滤）
        Long count = userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, req.getUsername()));
        if (count != null && count > 0) {
            throw new BusinessException("用户名已存在");
        }
        User user = new User();
        user.setUsername(req.getUsername());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setRole("user");
        userMapper.insert(user);
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        return new AuthResponse(token, user.getId(), user.getUsername(), user.getRole());
    }

    public AuthResponse login(LoginRequest req) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, req.getUsername()));
        if (user == null || !passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new BusinessException(401, "用户名或密码错误");
        }
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        return new AuthResponse(token, user.getId(), user.getUsername(), user.getRole());
    }
}
