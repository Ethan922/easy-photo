package com.easyphoto.config;

import com.easyphoto.common.Result;
import com.easyphoto.security.JwtAuthenticationFilter;
import com.easyphoto.security.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 配置：无状态 JWT 鉴权。
 * - 放行注册/登录、public 教程浏览、图片访问等接口。
 * - 其余接口需认证；管理端接口通过方法级 @PreAuthorize 限制角色。
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public SecurityConfig(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .cors().and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                // 认证相关
                .antMatchers("/api/auth/register", "/api/auth/login").permitAll()
                // 图片静态访问
                .antMatchers("/uploads/**").permitAll()
                // 分类维度、教程浏览：GET 放行（可见性在业务层控制），其余需登录
                .antMatchers(org.springframework.http.HttpMethod.GET,
                        "/api/categories/**", "/api/tutorials/**").permitAll()
                .anyRequest().authenticated()
                .and()
                // 未认证返回 401
                .exceptionHandling()
                .authenticationEntryPoint((request, response, ex) -> {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write(objectMapper.writeValueAsString(
                            Result.error(HttpStatus.UNAUTHORIZED.value(), "未授权或令牌无效")));
                })
                // 已认证但权限不足返回 403
                .accessDeniedHandler((request, response, ex) -> {
                    response.setStatus(HttpStatus.FORBIDDEN.value());
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write(objectMapper.writeValueAsString(
                            Result.error(HttpStatus.FORBIDDEN.value(), "禁止访问")));
                });

        http.addFilterBefore(new JwtAuthenticationFilter(jwtUtil),
                UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
