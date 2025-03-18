package com.murali.placify.event;

import com.murali.placify.entity.User;
import com.murali.placify.service.UserService;
import com.murali.placify.util.EmailService;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Component;


@Component
public class UserRegistrationEventListener implements ApplicationListener<UserRegistrationEvent> {

    private final UserService userService;

    private final EmailService emailService;

    public UserRegistrationEventListener(UserService userService, EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }

    @Override
    public void onApplicationEvent(UserRegistrationEvent event)throws MailException {
        User user = event.getUser();
        String toEmail = user.getMailID();
        String subject = "Account verification";
        String token = userService.saveVerificationToken(user, event.getType());

        String mailBody = "Please verify your account by clicking the below link\n\n" + event.getApplicationUrl() + "/verify-token?token=" + token;

        emailService.sendEmail(toEmail, mailBody, subject);

    }


}
