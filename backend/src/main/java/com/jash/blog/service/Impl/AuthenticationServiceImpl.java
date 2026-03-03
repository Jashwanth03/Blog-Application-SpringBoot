package com.jash.blog.service.Impl;

import com.jash.blog.service.AuthenticationService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    //only job is to take credentials and verify them
    private final AuthenticationManager  authenticationManager;
    private final UserDetailsService userDetailsService;

    @Value("${jwt.secret}")
    private  String secretKey;
    private final Long jwtExpiryMs = 8400000L;

    @Override
    public UserDetails authenticate(String email, String password) {

        authenticationManager.authenticate(
                // wraps the credentials into an object Spring Security understands
                new UsernamePasswordAuthenticationToken(email, password)
        );
        return userDetailsService.loadUserByUsername(email);
    }

    //token are used to carry identity of the user ,
    // so that we don't need to validate the user everytime looking up in the DB / per request
    @Override
    public String generateToken(UserDetails userDetails) {
        Map<String,Object> claims = new HashMap<>();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername()) //identifies who this token belongs to
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date((System.currentTimeMillis() + jwtExpiryMs)))
                .signWith(getSigninngKey() , SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public UserDetails validateToken(String token) {
        String userName = extractUsername(token);

        return userDetailsService.loadUserByUsername(userName);
    }
    //token comes in → signature is verified → username is extracted → full user object is loaded and returned.

    private String extractUsername(String token){
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigninngKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    private Key getSigninngKey(){
        byte[] keyBytes = secretKey.getBytes();

        return Keys.hmacShaKeyFor(keyBytes);
    }
}
