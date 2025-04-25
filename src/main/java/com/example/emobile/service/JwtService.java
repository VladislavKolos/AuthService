package com.example.emobile.service;

import com.example.emobile.util.AuthenticationServiceConstantUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Getter
@Setter
@Service
public class JwtService {
    @Value("${jwt.access.secret}")
    private String accessSecretKey;

    @Value("${jwt.verification.secret}")
    private String verificationSecretKey;

    public String generateAccessToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails, accessSecretKey);
    }

    public boolean isAccessTokenValid(String token) {
        return isTokenValid(token, accessSecretKey);
    }

    public String extractUsername(String token, String key) {
        return extractClaim(token, Claims::getSubject, key);
    }

    public boolean isTokenValid(String token, String key) {
        return !isTokenExpired(token, key);
    }

    public boolean isTokenExpired(String token, String key) {
        try {
            return extractExpiration(token, key).before(new Date());
        } catch (ExpiredJwtException ex) {
            return true;
        }
    }

    public Date extractExpiration(String token, String key) {
        return extractClaim(token, Claims::getExpiration, key);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver, String key) {
        var claims = extractAllClaims(token, key);
        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(String token, String key) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey(key))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String generateVerificationToken(String email) {
        var now = new Date();
        var expiration = Date.from(now.toInstant().plus(AuthenticationServiceConstantUtil.VERIFICATION_TOKEN_EXPIRATION));

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(getSignInKey(verificationSecretKey), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSignInKey(String key) {
        byte[] keyBytes = Decoders.BASE64.decode(key);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails, String key) {
        var now = new Date();
        var expiration = Date.from(now.toInstant().plus(AuthenticationServiceConstantUtil.ACCESS_TOKEN_EXPIRATION));

        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(getSignInKey(key), SignatureAlgorithm.HS256)
                .compact();
    }
}