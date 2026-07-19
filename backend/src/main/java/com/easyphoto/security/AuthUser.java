package com.easyphoto.security;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 认证后的当前用户信息，存入 SecurityContext 的 principal。
 */
@Data
@AllArgsConstructor
public class AuthUser {
    private Long userId;
    private String username;
    private String role;
}
