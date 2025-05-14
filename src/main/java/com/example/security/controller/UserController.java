package com.example.security.controller;


import com.example.security.config.JwtUtil;
import com.example.security.dao.UserDAO;
import com.example.security.model.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if(userDao.findUserByUsername(user.getUsername()) != null){
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        userDao.register(user);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        User foundUser = userDao.findUserByUsername(user.getUsername());
        if (foundUser == null || !foundUser.getPassword().equals(user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String token = jwtUtil.generateToken(foundUser);
        return ResponseEntity.ok(Collections.singletonMap("token", token));
    }

    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers(HttpServletRequest request) {
        List<User> users = userDao.findAll();
        return ResponseEntity.ok(users);
    }}
