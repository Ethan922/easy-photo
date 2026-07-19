package com.easyphoto.security;

import com.easyphoto.common.BusinessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 获取当前登录用户的辅助方法。
 */
public final class SecurityUtil {

    private SecurityUtil() {
    }

    /** 返回当前登录用户，未登录抛 401。 */
    public static AuthUser currentUser() {
        AuthUser user = currentUserOrNull();
        if (user == null) {
            throw new BusinessException(401, "未登录");
        }
        return user;
    }

    /** 返回当前登录用户，未登录返回 null。 */
    public static AuthUser currentUserOrNull() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof AuthUser) {
            return (AuthUser) authentication.getPrincipal();
        }
        return null;
    }

    public static boolean isAdmin() {
        AuthUser user = currentUserOrNull();
        return user != null && "admin".equals(user.getRole());
    }
}
