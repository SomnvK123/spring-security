package com.example.security.config;

import com.example.security.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    private final Key key; 
    private int expiration = 3600000; // 1 giờ

    public JwtUtil() {
        this.key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        this.expiration = 3600000;
    }

    public JwtUtil(int expiration) {
        this.key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        this.expiration = expiration;
    }

    // Tạo JWT token
    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("roles", user.getRoles()) // đảm bảo roles phù hợp với kiểu dữ liệu của bạn
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key)
                .compact();
    }

    // Xác thực token
    public boolean validateToken(String token, String username) {
        String tokenUsername = extractUsername(token);
        return tokenUsername != null && tokenUsername.equals(username) && !isTokenExpired(token);
    }

    // Lấy username từ token
    public String extractUsername(String token) {
        try {
            Claims claims = getClaims(token);
            return claims.getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    // Kiểm tra token hết hạn
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = getClaims(token);
            Date expiration = claims.getExpiration();
            return expiration.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    // Phương thức lấy Claims
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}