package com.murali.placify.service;

import com.murali.placify.Mapper.UserMapper;
import com.murali.placify.entity.*;
import com.murali.placify.enums.Level;
import com.murali.placify.enums.TokenType;
import com.murali.placify.event.UserRegistrationEvent;
import com.murali.placify.exception.*;
import com.murali.placify.model.ProfileResDto;
import com.murali.placify.model.RegistrationDTO;
import com.murali.placify.model.StaffProfileResDto;
import com.murali.placify.model.StudentProfileResDto;
import com.murali.placify.repository.TaskRepo;
import com.murali.placify.repository.UserRepository;
import com.murali.placify.repository.VerificationTokenRepository;
import com.murali.placify.util.UrlCreator;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final UserMapper userMapper;
    private final UrlCreator urlCreator;
    private final LeaderBoardService leaderBoardService;
    private final LeetcodeApiService leetcodeApiService;
    private final TaskRepo taskRepo;

    public UserService(UserRepository userRepository, VerificationTokenRepository verificationTokenRepository, ApplicationEventPublisher eventPublisher, UserMapper userMapper, UrlCreator urlCreator, LeaderBoardService leaderBoardService, LeetcodeApiService leetcodeApiService, TaskRepo taskRepo) {
        this.userRepository = userRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.eventPublisher = eventPublisher;
        this.userMapper = userMapper;
        this.urlCreator = urlCreator;
        this.leaderBoardService = leaderBoardService;
        this.leetcodeApiService = leetcodeApiService;
        this.taskRepo = taskRepo;
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
        Optional<User> optional = userRepository.findByUsername(dto.getUsername().toLowerCase());

        if (optional.isPresent() && optional.get().isEnabled())
            throw new UserAlreadyExistsException("This username is already taken");

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

    public UUID getUserIdByEmailForBatch(String mailId) {
        Optional<User> optionalUser =  userRepository.findByMailID(mailId);

        //Add log here to log unavilable mailIds
        if (optionalUser.isEmpty())
            return null;

        return optionalUser.get().getUserID();
    }

    @Transactional
    public void saveUsers(List<User> users) {
        userRepository.saveAll(users);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public ProfileResDto getProfileInfo(UUID userId) {
        User user = getUserById(userId);

        switch (user.getRole()) {
            case ROLE_STUDENT -> {
                return getStudentProfile(user);
            }
            case ROLE_STAFF -> {
                return getStaffProfile(user);
            }
            default -> {
                return null;
            }
        }
    }

    private ProfileResDto getStudentProfile(User user) {
        StudentProfileResDto dto = new StudentProfileResDto();

        dto.setUsername(user.getUsername());
        dto.setDept(user.getDepartment().getDeptName());
        dto.setYear(user.getYear());
        dto.setMailId(user.getMailID());
        dto.setRating(user.getLeaderboard().getOverAllRating());
        dto.setGlobalRank(leaderBoardService.getPosition(user.getLeaderboard()));
        dto.setProblemSolved(getProblemsCompletedCount(user));
        dto.setTaskStreak(user.getLeaderboard().getTaskStreak());

        return dto;
    }

    private ProfileResDto getStaffProfile(User user) {

        StaffProfileResDto dto = new StaffProfileResDto();

        dto.setUsername(user.getUsername());
        dto.setDept(user.getDepartment().getDeptName());
        dto.setEmailId(user.getMailID());
        dto.setContestCreated(user.getCreatedContestList().size());
        dto.setTasksCreated(user.getScheduledTasks().size());
        dto.setBatchesCreated(user.getBatchesCreated().size());

        int count = 0;

        for (Batch b : user.getBatchesCreated()) {
            count += b.getStudents().size();
        }

        dto.setTotalStudentsInAllBatches(count);

        return dto;

        //return userRepository.findStaffProfileByUserId(user.getUserID());
    }

    private int getProblemsCompletedCount(User user) {
        List<Task> tasks = taskRepo.findAllByAssignedBy(user);

        int count = 0;

        for (Task t : tasks) {
            List<ProblemLink> problemLinks = t.getProblemLinks();
            for (ProblemLink pl : problemLinks) {
                if (pl.isSolved())
                    count++;
            }
        }

        return count;
    }
}
