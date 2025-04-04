package com.murali.placify.event;

import com.murali.placify.entity.User;
import com.murali.placify.entity.VerificationToken;
import com.murali.placify.enums.TokenType;
import com.murali.placify.repository.VerificationTokenRepository;
import com.murali.placify.service.UserService;
import com.murali.placify.util.EmailSender;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;


@Component
public class UserRegistrationEventListener implements ApplicationListener<UserRegistrationEvent> {

    private final EmailSender emailSender;
    private final VerificationTokenRepository verificationTokenRepo;

    public UserRegistrationEventListener(EmailSender emailSender, VerificationTokenRepository verificationTokenRepo) {
        this.emailSender = emailSender;
        this.verificationTokenRepo = verificationTokenRepo;
    }

    @Override
    public void onApplicationEvent(UserRegistrationEvent event) {
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        String toEmail = user.getMailID();
        String subject = "Account verification";

        String mailBody = "Please verify your account by clicking on the link below\n" + event.getApplicationUrl() + "/api/v0/auth/verify-user?token=" + token;

        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setUser(user);
        verificationToken.setToken(token);
        verificationToken.setCreatedAt(LocalDateTime.now());
        verificationToken.setTokenType(TokenType.EMAIL_VERIFY);

        verificationTokenRepo.save(verificationToken);

        emailSender.sendEmail(toEmail, mailBody, subject);
    }
}

