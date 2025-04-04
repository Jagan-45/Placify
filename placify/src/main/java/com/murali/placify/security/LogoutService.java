package com.murali.placify.security;

import com.murali.placify.entity.RefreshToken;
import com.murali.placify.repository.RefreshTokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
public class LogoutService implements LogoutHandler {

    private final RefreshTokenRepository repository;

    public LogoutService(RefreshTokenRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String authHeader = request.getHeader("Authorization");
        String jwt;
        if(authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);

            Optional<RefreshToken> optionalJwtToken = repository.findByToken(jwt);
            if(optionalJwtToken.isPresent()){
                RefreshToken token = optionalJwtToken.get();
                token.setLoggedIn(false);
                repository.save(token);
            }
        }
    }
}
