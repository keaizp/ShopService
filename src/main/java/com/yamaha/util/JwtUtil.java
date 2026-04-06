package com.yamaha.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.KeyGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expire}")
    private long expire;

    public String generateToken(Long userId, String openid) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + expire * 1000);

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("openid", openid);
        claims.put("type", "user");

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public Claims getClaimsByToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = getClaimsByToken(token);
        if (claims == null) {
            return null;
        }
        return Long.valueOf(claims.get("userId").toString());
    }

    public String getOpenidFromToken(String token) {
        Claims claims = getClaimsByToken(token);
        if (claims == null) {
            return null;
        }
        return claims.get("openid").toString();
    }

    public boolean isUserToken(String token) {
        Claims claims = getClaimsByToken(token);
        if (claims == null) {
            return false;
        }
        return "user".equals(claims.get("type"));
    }

    public String generateAdminToken(Long adminId, String username) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + expire * 1000);

        Map<String, Object> claims = new HashMap<>();
        claims.put("adminId", adminId);
        claims.put("username", username);
        claims.put("type", "admin");

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public Long getAdminIdFromToken(String token) {
        Claims claims = getClaimsByToken(token);
        if (claims == null) {
            return null;
        }
        return Long.valueOf(claims.get("adminId").toString());
    }

    public String getUsernameFromToken(String token) {
        Claims claims = getClaimsByToken(token);
        if (claims == null) {
            return null;
        }
        return claims.get("username").toString();
    }

    public boolean isAdminToken(String token) {
        Claims claims = getClaimsByToken(token);
        if (claims == null) {
            return false;
        }
        return "admin".equals(claims.get("type"));
    }

    public static String generateSecret() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
            keyGen.init(256);  // 使用256位密钥
            byte[] key = keyGen.generateKey().getEncoded();
            return Base64.getEncoder().encodeToString(key);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to generate JWT secret", e);
        }
    }

    public static void main(String[] args) {
        String secret = generateSecret();
        System.out.println("Generated JWT Secret: " + secret);
    }
}
