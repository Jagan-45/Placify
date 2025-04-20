package com.murali.placify.service;

import com.murali.placify.entity.RefreshToken;
import com.murali.placify.entity.User;
import com.murali.placify.exception.InvalidRefreshTokenException;
import com.murali.placify.exception.TokenExpiredException;
import com.murali.placify.repository.RefreshTokenRepository;
import com.murali.placify.repository.UserRepository;
import com.murali.placify.security.JwtService;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Component
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenRepository tokenRepository;

    public AuthService(UserRepository userRepository, JwtService jwtService, RefreshTokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.tokenRepository = tokenRepository;
    }

    @Transactional
    public String generateJwt(String mailID, Date date) {
        Optional<User> user = userRepository.findByMailID(mailID);
        String jwt = jwtService.generateToken(user.get().getUserID(), user.get().getRole(), date);

        if(tokenRepository.existsByUser(user.get())){
            tokenRepository.updateJwtToken(LocalDateTime.now().plusDays(7), jwt, user.get(), true);
        }

        else tokenRepository.save(new RefreshToken(user.get(),jwt, LocalDateTime.now().plusDays(7), true));
        return jwt;
    }

    @Transactional
    public String generateNewToken(String token) {

        Optional<RefreshToken> optional = tokenRepository.findByToken(token);

        if (optional.isPresent() && optional.get().getRefreshTokenExpiryTime().isAfter(LocalDateTime.now()) && optional.get().isLoggedIn()) {
            RefreshToken refreshToken = optional.get();
            Optional<User> user = userRepository.findById(refreshToken.getUser().getUserID());
            String jwt = jwtService.generateToken(user.get().getUserID(), user.get().getRole(), new Date(System.currentTimeMillis() + 1000 * 60 * 15));

            tokenRepository.updateJwtToken(LocalDateTime.now().plusDays(7), jwt, user.get(), true);
            return jwt;
        }

        throw new InvalidRefreshTokenException("Invalid JWT or refresh token expired, please login again");
    }

}
