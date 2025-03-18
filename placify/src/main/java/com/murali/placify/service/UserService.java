package com.murali.placify.service;

import com.murali.placify.Mapper.UserMapper;
import com.murali.placify.entity.User;
import com.murali.placify.entity.VerificationToken;
import com.murali.placify.enums.TokenType;
import com.murali.placify.event.UserRegistrationEvent;
import com.murali.placify.exception.*;
import com.murali.placify.model.RegistrationDTO;
import com.murali.placify.repository.UserRepository;
import com.murali.placify.repository.VerificationTokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final VerificationTokenRepository verificationTokenRepository;

    @Value("${dev.domain-url}")
    private String appURL;

    public UserService(UserRepository userRepository, UserMapper userMapper, ApplicationEventPublisher applicationEventPublisher, VerificationTokenRepository verificationTokenRepository) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.applicationEventPublisher = applicationEventPublisher;
        this.verificationTokenRepository = verificationTokenRepository;
    }

    public User getUserById(UUID id) throws UserNotFoundException {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent())
            return optionalUser.get();
        else
            throw new UserNotFoundException("No such user exists");
    }

    @Transactional
    public void registerUser(RegistrationDTO registrationDTO, HttpServletRequest request) throws UserAlreadyExistsException {
        if (userRepository.existsByMailID(registrationDTO.getMailID()))
            throw new UserAlreadyExistsException("User with this mail-id already exists");

        User user = userRepository.saveAndFlush(userMapper.registerDtoToUserMapper(registrationDTO));
        applicationEventPublisher.publishEvent(new UserRegistrationEvent(user, appURL, TokenType.EMAIL_VERIFY));
    }

    @Transactional(noRollbackFor = TokenGenerationException.class)
    public String saveVerificationToken(User user, TokenType type) {
        LocalDateTime NOW = LocalDateTime.now();
        String TOKEN = UUID.randomUUID().toString();

        VerificationToken token = verificationTokenRepository.findByUser(user);

        if (token == null) {
            token = new VerificationToken();
            token.setUser(user);
            token.setToken(TOKEN);
            token.setAttempts(1);
            token.setType(type);
            token.setLastRequestTime(NOW);
            token.setBanStartTime(null);
            return verificationTokenRepository.save(token).getToken();
        }

        if (token.getBanStartTime() != null) {
            long minutesSinceBan = Duration.between(token.getBanStartTime(), NOW).toMinutes();
            if (minutesSinceBan < 60) {
                throw new TokenGenerationException("Ban not over. Try again after " + (60 - minutesSinceBan) + " minutes.");
            } else {
                token.setBanStartTime(null);
                token.setAttempts(0);
            }
        }

        long minutesSinceLastRequest = Duration.between(token.getLastRequestTime(), NOW).toMinutes();
        if (minutesSinceLastRequest <= 60) {
            if (token.getAttempts() >= 5) {
                token.setBanStartTime(NOW);
                verificationTokenRepository.saveAndFlush(token);
                throw new TokenGenerationException("Too many attempts. You are banned for 1 hour.");
            } else {
                token.setAttempts(token.getAttempts() + 1);
            }
        } else {
            token.setAttempts(1);
        }

        token.setToken(TOKEN);
        token.setLastRequestTime(NOW);

        return verificationTokenRepository.save(token).getToken();
    }


    @Transactional
    public void verifyUserRegistration(String token) {
        if(Objects.equals(token, ""))
            throw new InvalidTokenException("Invalid Token");

        Optional<VerificationToken> optional = verificationTokenRepository.findByToken(token);

        if(optional.isEmpty())
            throw new InvalidTokenException("Invalid Token");

        VerificationToken verificationToken = optional.get();

        if(Duration.between(verificationToken.getExpireTime(), LocalDateTime.now()).toMinutes() > 5)
            throw new TokenExpiredException("Token Expired");

        User user = verificationToken.getUser();
        user.setEnabled(true);

        userRepository.save(user);

        verificationTokenRepository.delete(verificationToken);
    }


    public void resendVerificationToken(String token) {
        Optional<User> optionalUser = verificationTokenRepository.findUserByToken(token);
        if (optionalUser.isEmpty())
            throw new TokenGenerationException("Something went wrong try again");
        User user = optionalUser.get();

        applicationEventPublisher.publishEvent(new UserRegistrationEvent(user, appURL, TokenType.EMAIL_VERIFY));
    }

    public List<User> getUserByBatch(List<String> assignToBatches) {

        Optional<List<User>> optionalUsers = userRepository.findByBatch_BatchNameIn(assignToBatches);

        if (optionalUsers.isEmpty())
            throw new IllegalArgumentException("Invalid batches");
        return optionalUsers.get();
    }
}
