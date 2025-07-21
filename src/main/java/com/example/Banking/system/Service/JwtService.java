package com.example.Banking.system.Service;

import com.example.Banking.system.Model.CustomUserDetails;
import com.example.Banking.system.Model.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Set;


@Service
public class JwtService {


    @Value("${jwt.secret}")
    private String myKey;

    private SecretKey getSignKey() {
        byte[] keyBytes = myKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String getToken(String userName, Set<Role> roles){

        List<String> roleNames = roles.stream()
                .map(Role::getRoleName)
                .toList();

        String token = Jwts.builder()
                .subject(userName)
                .claim("roles",roleNames)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(getSignKey())
                .compact();

        return token;
    }

    public String extractUserName(String token) {
        Claims claims = extractAllClaims(token);
        return claims.getSubject();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token, CustomUserDetails customUserDetails) {
        return extractUserName(token).equals(customUserDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }
}
