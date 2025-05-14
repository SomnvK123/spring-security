package com.example.security.controller;


import com.example.security.dao.UserDAO;
import com.example.security.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserDAO userDao;

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        if(userDao.findUserByUsername(user.getUsername()) != null){
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        userDao.register(user);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody User user) {
        User foundUser = userDao.findUserByUsername(user.getUsername());
        if (foundUser == null || !foundUser.getPassword().equals(user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(foundUser);
    }

    @GetMapping("/all")
    public ResponseEntity<Iterable<User>> findAll(){
        return ResponseEntity.ok(userDao.findAll());
    }
}
