package com.murali.placify.controller;

import com.murali.placify.model.LoginDTO;
import com.murali.placify.model.RegistrationDTO;
import com.murali.placify.response.ApiResponse;
import com.murali.placify.service.AuthService;
import com.murali.placify.service.UserService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping("api/v0/auth")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    public final AuthService authService;


    public AuthController(UserService userService, AuthenticationManager authenticationManager, AuthService authService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.authService = authService;
    }

    @PostMapping("/sign-up")
    public ResponseEntity<ApiResponse> signUp(@RequestBody RegistrationDTO dto, HttpServletRequest req) {
        userService.registerUser(dto, req);
        return new ResponseEntity<>(new ApiResponse("Verification link has be sent to the mail", null), HttpStatus.OK);
    }

    @GetMapping("/verify-user")
    public ResponseEntity<ApiResponse> verifyAccount(@RequestParam String token) {
        return new ResponseEntity<>(new ApiResponse(null, userService.VerifyUser(token)), HttpStatus.OK);
    }

    @GetMapping("/resend-token")
    public ResponseEntity<ApiResponse> resendVerificationToken(@RequestParam(name = "token") String token, HttpServletRequest req) {
        userService.resendVerificationToken(token, req);
        return new ResponseEntity<>(new ApiResponse("verification token sent", null), HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> loginController(@RequestBody LoginDTO loginDTO) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDTO.getMailID(), loginDTO.getPassword()));
        if (authentication.isAuthenticated()) {
            String jwt = authService.generateJwt(loginDTO.getMailID(), new Date(System.currentTimeMillis() + 1000 * 60 * 15));
            return new ResponseEntity<>(new ApiResponse("login success", jwt), HttpStatus.OK);
        }

        return new ResponseEntity<>(new ApiResponse("login failed"), HttpStatus.UNAUTHORIZED);
    }

    @GetMapping()
    public ResponseEntity<ApiResponse> refreshJwtToken(HttpServletRequest request) {
        String header= request.getHeader("Authorization");
        System.out.println(header);
        if(header == null || !header.startsWith("Bearer "))
            throw new JwtException("Invalid Authorization header");

        String jwt = header.substring(7);

        String newJwt = authService.generateNewToken(jwt);

        return new ResponseEntity<>(new ApiResponse("", newJwt), HttpStatus.OK);
    }

    @PostMapping("/dummy")
    public void dummy() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        System.out.println(username);
    }
}
