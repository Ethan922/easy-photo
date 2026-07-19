package com.easyphoto.security;

import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

/**
 * 解析请求头 Authorization: Bearer <token>，校验通过后写入 SecurityContext。
 * 令牌缺失或无效时不设置认证，交由 Security 决定是否放行（匿名可访问 public 接口）。
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                Claims claims = jwtUtil.parse(token);
                Long userId = Long.valueOf(claims.getSubject());
                String username = claims.get("username", String.class);
                String role = claims.get("role", String.class);
                AuthUser authUser = new AuthUser(userId, username, role);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                authUser, null,
                                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role)));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
                // 令牌无效/过期：不设认证，受保护接口将返回 401
                SecurityContextHolder.clearContext();
            }
        }
        filterChain.doFilter(request, response);
    }
}
