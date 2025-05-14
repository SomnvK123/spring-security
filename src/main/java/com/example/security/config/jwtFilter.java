package com.example.security.config;

import com.example.security.dao.UserDAO;
import com.example.security.model.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import java.util.stream.Collectors;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class jwtFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(jwtFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDAO userDAO;

    private static final List<String> EXCLUDED_PATHS = List.of("/users/login", "/users/register");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getServletPath();

        // Nếu là đường dẫn được loại trừ thì bỏ qua filter
        if (shouldNotFilter(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {
                // Xác thực token và lấy username
                String username = jwtUtil.extractUsername(token);
                logger.info("Token validation successful for username: " + username);

                if (username != null && jwtUtil.validateToken(token, username)) {
                    User user = userDAO.findUserByUsername(username);

                    // SimpleGrantedAuthority: tao quyen, role dua tren user
                    if (user != null) {
                        request.setAttribute("user", user);
                        List<SimpleGrantedAuthority> authorities = List.of(
                                new SimpleGrantedAuthority(user.getRoles()));
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(user, null, authorities);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        filterChain.doFilter(request, response);
                        logger.info("User authenticated for username: " + username);
                        return;
                    } else {
                        logger.error("User not found for username: " + username);
                    }
                } else {
                    logger.error("Token validation failed for username: " + username);
                }
            } catch (Exception e) {
                logger.error("Token parsing error", e);
            }
        } else {
            logger.warn("No Authorization header found or missing Bearer token");
        }

        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return EXCLUDED_PATHS.stream().anyMatch(path::startsWith);
    }
}
