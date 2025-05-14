package com.example.security.config;

import com.example.security.dao.UserDAO;
import com.example.security.model.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class jwtFilter  {
    @Autowired
    private jwtUtil jwtUtil;

    @Autowired
    private UserDAO userDAO;

    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain){
        String authHeader = request.getHeader("Authorization");
        if(authHeader != null && authHeader.startsWith("Bearer ")){
            String token = authHeader.substring(7);
            try{
                String username =
            }
        }

    }
}
