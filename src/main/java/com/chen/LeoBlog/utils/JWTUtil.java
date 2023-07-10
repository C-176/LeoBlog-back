package com.chen.LeoBlog.utils;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

public class JWTUtil {
    public static  final Long EXPIRATION_TIME = 3600000L; // 1 hour
    public static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // 生成 JWT
    public static String generateJwt(String subject, long expirationMs) {
        if(expirationMs == 0) expirationMs = EXPIRATION_TIME;
        return Jwts.builder()
                .setSubject(subject)
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 解析 JWT
    public static Jws<Claims> parseJwt(String jwt) {
        Jws<Claims> claimsJws = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(jwt);
        return claimsJws;
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

