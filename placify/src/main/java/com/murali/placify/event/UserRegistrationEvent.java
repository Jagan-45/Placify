package com.murali.placify.event;

import com.murali.placify.entity.User;
import com.murali.placify.enums.TokenType;
import lombok.Getter;
import lombok.Setter;

import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class UserRegistrationEvent extends ApplicationEvent {
    private User user;
    private String applicationUrl;

    public UserRegistrationEvent(User user, String applicationUrl) {
        super(user);
        this.user = user;
        this.applicationUrl = applicationUrl;
    }
}


