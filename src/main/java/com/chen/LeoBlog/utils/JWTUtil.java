package com.chen.LeoBlog.utils;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.AuthenticationException;

import java.security.Key;
import java.util.Date;

public class JWTUtil {
    public static final Long EXPIRATION_TIME = 7 * 24 * 60 * 60 * 1000L; // 7days
    public static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // 生成 JWT
    public static String generateJwt(String subject, long expirationMs) {
        return Jwts.builder()
                .setSubject(subject)
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 生成 JWT
    public static String generateJwt(String subject) {

        return Jwts.builder()
                .setSubject(subject)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 解析 JWT
    public static Jws<Claims> parseJwt(String jwt) throws AuthenticationException {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(jwt);
    }

    // 解析 JWT中的userId
    public static Long parseJwtUserId(String jwt) throws AuthenticationException {
        Jws<Claims> claimsJws = parseJwt(jwt);
        return Long.parseLong(claimsJws.getBody().getSubject());
    }

    public static Date parseJwtExpiration(String jwt) throws AuthenticationException {
        Jws<Claims> claimsJws = parseJwt(jwt);
        return claimsJws.getBody().getExpiration();
    }

    // 验证 JWT 签名
    public static boolean verifyJwtSignature(String jwt) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(jwt);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean verify(String jwt, String token) {
        Jws<Claims> claimsJws = parseJwt(jwt);
        return claimsJws.getBody().getSubject().equals(token) && verifyJwtSignature(jwt);
    }

    public static void main(String[] args) {
        String subject = "user123";
        long expirationMs = 3600000; // 1 hour


        // 生成 JWT
        String jwt = generateJwt(subject, expirationMs);
        System.out.println("JWT: " + jwt);

        // 解析 JWT
        Jws<Claims> jws = parseJwt(jwt);
        System.out.println("Subject: " + jws.getBody().getSubject());
        System.out.println("Expiration: " + jws.getBody().getExpiration());

        // 验证 JWT 签名
        boolean isValid = verifyJwtSignature(jwt);
        System.out.println("Is valid: " + isValid);
    }
}

