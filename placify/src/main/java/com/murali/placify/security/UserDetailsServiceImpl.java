package com.murali.placify.security;

import com.murali.placify.entity.User;
import com.murali.placify.exception.UserNotFoundException;
import com.murali.placify.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepository.findByMailID(username);
        if (optionalUser.isEmpty())
            throw new UserNotFoundException("Invalid username");

        return new UserInfoDetails(optionalUser.get());
    }
}
