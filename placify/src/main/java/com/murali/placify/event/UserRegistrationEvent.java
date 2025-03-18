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
    private TokenType type;

    public UserRegistrationEvent(User user, String applicationUrl, TokenType type) {
        super(user);
        this.type = type;
        this.user=user;
        this.applicationUrl=applicationUrl;
    }
}