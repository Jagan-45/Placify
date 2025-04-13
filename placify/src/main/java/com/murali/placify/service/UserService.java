package com.murali.placify.service;

import com.mchange.util.AlreadyExistsException;
import com.murali.placify.Mapper.UserMapper;
import com.murali.placify.entity.Leaderboard;
import com.murali.placify.entity.User;
import com.murali.placify.entity.VerificationToken;
import com.murali.placify.enums.Level;
import com.murali.placify.enums.TokenType;
import com.murali.placify.event.UserRegistrationEvent;
import com.murali.placify.exception.*;
import com.murali.placify.model.RegistrationDTO;
import com.murali.placify.repository.LeaderboardRepo;
import com.murali.placify.repository.UserRepository;
import com.murali.placify.repository.VerificationTokenRepository;
import com.murali.placify.util.UrlCreator;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final UserMapper userMapper;
    private final UrlCreator urlCreator;
    private final LeaderBoardService leaderBoardService;
    private final LeetcodeApiService leetcodeApiService;


    public UserService(UserRepository userRepository, VerificationTokenRepository verificationTokenRepository, ApplicationEventPublisher eventPublisher, UserMapper userMapper, UrlCreator urlCreator, LeaderBoardService leaderBoardService, LeetcodeApiService leetcodeApiService) {
        this.userRepository = userRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.eventPublisher = eventPublisher;
        this.userMapper = userMapper;
        this.urlCreator = urlCreator;
        this.leaderBoardService = leaderBoardService;
        this.leetcodeApiService = leetcodeApiService;
    }

    public User getUserById(UUID id) throws UserNotFoundException {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent())
            return optionalUser.get();
        else
            throw new UserNotFoundException("No such user exists");
    }

    public List<User> getUserByBatch(List<String> assignToBatches) {

        Optional<List<User>> optionalUsers = userRepository.findByBatch_BatchNameIn(assignToBatches);

        if (optionalUsers.isEmpty())
            throw new IllegalArgumentException("Invalid batches");
        System.out.println("----users list---" + optionalUsers.get());
        return optionalUsers.get();
    }

    public String registerUser(RegistrationDTO dto, HttpServletRequest request) {
        Optional<User> optionalUser = userRepository.findByMailID(dto.getMailID());

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (!user.isEnabled()) {
                eventPublisher.publishEvent(new UserRegistrationEvent(user, urlCreator.createApplicationUrl(request)));
                return "This Email is already registered, we have sent a verification link to the mail Id";
            } else throw new UserAlreadyExistsException("Account with this email already exists");
        } else if(leetcodeApiService.doesUserExist(dto.getUsername())){
            User user = saveUser(userMapper.registerDtoToUserMapper(dto));
            Leaderboard leaderboard = new Leaderboard();
            leaderboard.setUser(user);
            leaderboard.setOverAllRating(0);
            leaderboard.setContestRating(0);
            leaderboard.setLevel(Level.NEWBIE);
            leaderboard.setTaskStreak(0);
            leaderboard.setContestRating(0);
            leaderBoardService.saveRecord(leaderboard);
            eventPublisher.publishEvent(new UserRegistrationEvent(user, urlCreator.createApplicationUrl(request)));
            return "Please verify your account, verification link has been sent to email";
        }
        else throw new IllegalArgumentException("your username and your leetcode username should be same");

    }


    @Transactional
    public String VerifyUser(String token) {
        Optional<VerificationToken> optionalToken = verificationTokenRepository.findByToken(token);

        if (optionalToken.isEmpty() || optionalToken.get().getTokenType() != TokenType.EMAIL_VERIFY)
            throw new IllegalArgumentException("Invalid Verification token, please hit re-send");

        if (Duration.between(optionalToken.get().getCreatedAt(), LocalDateTime.now())
                .compareTo(Duration.ofMinutes(5)) > 0)
            throw new IllegalArgumentException("Token Expired, please hit re-send");

        User user = optionalToken.get().getUser();

        if (user.isEnabled()) {
            throw new IllegalArgumentException("Account already verified, please login");
        }

        user.setEnabled(true);
        User user1 = userRepository.save(user);



        return "Account verification successful";
    }

    public void resendVerificationToken(String token, HttpServletRequest request) {
        Optional<VerificationToken> optionalToken = verificationTokenRepository.findByToken(token);

        if (optionalToken.isEmpty())
            throw new TokenGenerationException("Something went wrong try again, try again");
        User user = optionalToken.get().getUser();

        eventPublisher.publishEvent(new UserRegistrationEvent(user, urlCreator.createApplicationUrl(request)));
    }

    public UUID getUserIdByEmail(String mailId) {
        Optional<User> optionalUser =  userRepository.findByMailID(mailId);
        if (optionalUser.isEmpty())
            throw new IllegalArgumentException("No user exits for emailId" + mailId);

        return optionalUser.get().getUserID();
    }

    @Transactional
    public void saveUsers(List<User> users) {
        userRepository.saveAll(users);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }
}
