package com.murali.placify.security;

import com.murali.placify.exception.InvalidTokenException;
import com.murali.placify.exception.TokenExpiredException;
import com.murali.placify.response.ApiResponse;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final String[] whiteListed = new String[]{"/api/v0/auth/login", "/api/v0/auth/sign-up", "/api/v0/auth/verify-user", "/api/v0/auth/resend-token"};

    public JwtFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try{
            if(Arrays.asList(whiteListed).contains(request.getRequestURI())){

                filterChain.doFilter(request, response);
                return;
            }
            System.out.println(request.getRequestURI());
            String header= request.getHeader("Authorization");
            System.out.println(header);
            if(header == null || !header.startsWith("Bearer "))
                throw new InvalidTokenException("Invalid Authorization header");

            String jwt = header.substring(7);

            if(jwtService.isTokenExpired(jwt))
                throw new TokenExpiredException("Jwt expired");

            if (jwtService.verifyToken(jwt)){
                if(SecurityContextHolder.getContext().getAuthentication() == null){
                    UserDetails userDetails = userDetailsService.loadUserByUsername(jwtService.getUsername(jwt));
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }

            doFilter(request, response, filterChain);
        }
        catch (TokenExpiredException te){
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            response.getWriter().write(String.valueOf(new ApiResponse("EXPIRED")));
        }
        catch (JwtException je){
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType("application/json");
            response.getWriter().write(String.valueOf(new ApiResponse("INVALID")));
        }
    }
}

