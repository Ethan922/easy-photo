package com.easyphoto.controller;

import com.easyphoto.common.Result;
import com.easyphoto.dto.PageResult;
import com.easyphoto.dto.RegisterRequest;
import com.easyphoto.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * 管理端用户管理，全部限管理员。
 */
@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('admin')")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Result<PageResult<Map<String, Object>>> list(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "20") long size) {
        return Result.success(userService.list(current, size));
    }

    @PostMapping
    public Result<Map<String, Long>> create(@Valid @RequestBody RegisterRequest req) {
        Long id = userService.create(req);
        Map<String, Long> data = new HashMap<>();
        data.put("id", id);
        return Result.success(data);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return Result.success();
    }
}
