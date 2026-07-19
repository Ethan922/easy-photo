package com.easyphoto.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.easyphoto.entity.User;
import com.easyphoto.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 启动时初始化管理员账号（若不存在），密码用 BCrypt 加密。
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final String adminUsername;
    private final String adminPassword;

    public DataInitializer(UserMapper userMapper, PasswordEncoder passwordEncoder,
                           @Value("${app.admin.username}") String adminUsername,
                           @Value("${app.admin.password}") String adminPassword) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
    }

    @Override
    public void run(String... args) {
        Long count = userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, adminUsername));
        if (count == null || count == 0) {
            User admin = new User();
            admin.setUsername(adminUsername);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setRole("admin");
            userMapper.insert(admin);
        }
    }
}
