package com.example.security.controller;


import com.example.security.config.JwtUtil;
import com.example.security.dao.UserDAO;
import com.example.security.model.User;
import com.example.security.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDAO userDao;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if(userDao.findUserByUsername(user.getUsername()) != null){
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userDao.register(user);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        User foundUser = userDao.findUserByUsername(user.getUsername());
        if (foundUser == null || !passwordEncoder.matches(user.getPassword(), foundUser.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String token = jwtUtil.generateToken(foundUser);
        return ResponseEntity.ok(Collections.singletonMap("token", token));
    }

//    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers(HttpServletRequest request) {
        List<User> users = userDao.findAll();
        return ResponseEntity.ok(users);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return ResponseEntity.ok("Logged out successfully");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/current-user")
    public ResponseEntity<User> getCurrentUser() {
        String username = userService.getCurrentUsername();
        if (username != null) {
            User user = userDao.findUserByUsername(username);
            if (user != null) {
                return ResponseEntity.ok(user);
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}