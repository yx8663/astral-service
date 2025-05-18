package com.astral.common.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtil {

    private static final String SECRET_KEY = "JWT-Secret-Key";
    private static final int DEFAULT_EXPIRE_SECONDS = 120 * 60;
    private static final int PASSWORD_HASH_BYTES = 16;

    private static final SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    public static String generateToken(String userName, Long userId, int expiredSeconds) {
        if (expiredSeconds == 0) {
            expiredSeconds = DEFAULT_EXPIRE_SECONDS;
        }

        Date expireAt = Date.from(Instant.now().plusSeconds(expiredSeconds));

        Map<String, Object> claims = new HashMap<>();
        claims.put("UserID", userId);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuer(userName)
                .setIssuedAt(new Date())
                .setExpiration(expireAt)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public static JwtPayload validateToken(String tokenString) {
        try {
            Jws<Claims> claimsJws = Jwts.parser()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(tokenString);

            Claims claims = claimsJws.getBody();

            return new JwtPayload(
                    claims.getIssuer(),
                    ((Number) claims.get("UserID")).intValue(),
                    claims.getIssuedAt().getTime(),
                    claims.getExpiration().getTime()
            );
        } catch (JwtException e) {
            throw new RuntimeException("Error: Unable to validate token");
        }
    }

    public static String refreshToken(String tokenString) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(tokenString)
                    .getBody();

            Date expireAt = Date.from(Instant.now().plusSeconds(DEFAULT_EXPIRE_SECONDS));

            return Jwts.builder()
                    .setClaims(claims)
                    .setIssuedAt(new Date())
                    .setExpiration(expireAt)
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();
        } catch (JwtException e) {
            throw new RuntimeException("Error: Failed to generate new fresh JWT");
        }
    }

    public static String generateSalt() {
        byte[] buf = new byte[PASSWORD_HASH_BYTES];
        new SecureRandom().nextBytes(buf);
        return Hex.encodeHexString(buf);
    }

    public static String generatePassHash(String password, String salt) {
        try {
            return Hex.encodeHexString(
                    javax.crypto.SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                            .generateSecret(new javax.crypto.spec.PBEKeySpec(
                                    password.toCharArray(),
                                    salt.getBytes(),
                                    16384,
                                    PASSWORD_HASH_BYTES * 8))
                            .getEncoded()
            );
        } catch (Exception e) {
            throw new RuntimeException("Error: failed to generate password hash");
        }
    }

    public static TokenStatus checkStatus(String tokenString) {
        try {
            JwtPayload jp = validateToken(tokenString);

            long timeDiff = (jp.getExpiresAt() - System.currentTimeMillis()) / 1000;

            if (timeDiff <= 30) {
                String newToken = refreshToken(tokenString);
                return new TokenStatus(newToken, timeDiff);
            }

            return new TokenStatus(tokenString, timeDiff);
        } catch (Exception e) {
            return new TokenStatus("", -1L);
        }
    }

    public static class TokenStatus {
        private final String token;
        private final long timeDiff;

        public TokenStatus(String token, long timeDiff) {
            this.token = token;
            this.timeDiff = timeDiff;
        }

        public String getToken() {
            return token;
        }

        public long getTimeDiff() {
            return timeDiff;
        }
    }

    public static class JwtPayload {
        private final String username;
        private final int userId;
        private final long issuedAt;
        private final long expiresAt;

        public JwtPayload(String username, int userId, long issuedAt, long expiresAt) {
            this.username = username;
            this.userId = userId;
            this.issuedAt = issuedAt;
            this.expiresAt = expiresAt;
        }

        public String getUsername() {
            return username;
        }

        public int getUserId() {
            return userId;
        }

        public long getIssuedAt() {
            return issuedAt;
        }

        public long getExpiresAt() {
            return expiresAt;
        }
    }
}