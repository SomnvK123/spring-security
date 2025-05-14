package com.example.security.dao;

import com.example.security.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public class UserDAO {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public User findUserByUsername(String username) {
        String sql = "SELECT * FROM Users WHERE username = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(User.class), username);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }


    public void register(User user){
        String sql = "INSERT INTO users (username, password, phone, roles) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, user.getUsername(), user.getPassword(), user.getPhone(), user.getRoles());
    }

    public List<User> findAll(){
        String sql = "SELECT * FROM Users";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(User.class));
    }
}