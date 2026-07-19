package com.easyphoto.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.easyphoto.common.BusinessException;
import com.easyphoto.dto.PageResult;
import com.easyphoto.dto.RegisterRequest;
import com.easyphoto.entity.User;
import com.easyphoto.mapper.UserMapper;
import com.easyphoto.security.SecurityUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 管理员用户管理。
 */
@Service
public class UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public PageResult<Map<String, Object>> list(long current, long size) {
        IPage<User> page = userMapper.selectPage(new Page<>(current, size),
                new LambdaQueryWrapper<User>().orderByAsc(User::getId));
        List<Map<String, Object>> records = page.getRecords().stream().map(u -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", u.getId());
            m.put("username", u.getUsername());
            m.put("role", u.getRole());
            m.put("createdAt", u.getCreatedAt());
            return m;
        }).collect(Collectors.toList());
        return new PageResult<>(records, page.getTotal(), current, size);
    }

    public Long create(RegisterRequest req) {
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
        return user.getId();
    }

    public void delete(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        if (id.equals(SecurityUtil.currentUser().getUserId())) {
            throw new BusinessException("不能删除自己");
        }
        userMapper.deleteById(id);
    }
}
