package com.murali.placify.security;

import com.murali.placify.entity.RefreshToken;
import com.murali.placify.entity.User;
import com.murali.placify.enums.Role;
import com.murali.placify.repository.RefreshTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.function.Function;

@Service
public class JwtService{
    @Value("${dev.key}")
    private String KEY;
    private final RefreshTokenRepository refreshTokenRepository;

    public JwtService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public String generateToken(UUID id, Role role, Date expiryTime) {
        Map<String,Object> claims = new HashMap<>();
        return createToken(claims, id, role);
    }

    private String createToken(Map<String, Object> claims, UUID id, Role role, Date expiryTime) {
        claims.put("role", role);

        return Jwts.builder()
                .subject(String.valueOf(id))
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(expiryTime)
                .claims(claims)
                .signWith(getKey())
                .compact();
    }

    private String createToken(Map<String, Object> claims, UUID id, Role role) {
        claims.put("role", role);

        return Jwts.builder()

                .subject(String.valueOf(id))
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis()+1000*60*15))
                .claims(claims)
                .signWith(getKey())
                .compact();

    }

    private SecretKey getKey() {
        byte[] key = Decoders.BASE64.decode(KEY);
        return Keys.hmacShaKeyFor(key);
    }

    public String getUserId(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token){
        Claims claims = null;
        JwtParser jwtParser = Jwts.parser()
                .verifyWith(getKey())
                .build();

        claims =  jwtParser.parseSignedClaims(token).getPayload();
        return claims;
    }

    public boolean isTokenExpired(String token) {
        return getExpirationDateFromToken(token).before(new Date());
    }

    public String getUsername(String jwt) {
        return getRefreshToken(jwt).getUser().getMailID();
    }

    private RefreshToken getRefreshToken(String jwt){
        Optional<RefreshToken> optionalRefreshToken = refreshTokenRepository.findByToken(jwt);
        if(optionalRefreshToken.isEmpty())
            throw new JwtException("Invalid JWT token");
        return optionalRefreshToken.get();
    }

    public boolean verifyToken(String jwt) {
        JwtParser jwtParser = Jwts.parser()
                .verifyWith(getKey())
                .build();
        jwtParser.parse(jwt);
        if(!getRefreshToken(jwt).isLoggedIn())
            throw new JwtException("User not logged in, please login to continue");
        return true;
    }

    public User getUser(String email) {
        return null;
    }
}
