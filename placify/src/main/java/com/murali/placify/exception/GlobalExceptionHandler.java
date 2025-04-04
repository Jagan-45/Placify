package com.murali.placify.exception;


import com.murali.placify.response.ApiResponse;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleFieldValidationException(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();

        exception.getBindingResult()
                .getAllErrors()
                .forEach(objectError -> errors.put(((FieldError) objectError).getField(),
                        objectError.getDefaultMessage()));

        return new ResponseEntity<>(new ApiResponse("error occurred", errors), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ProblemAlreadyExistsException.class)
    public ResponseEntity<ApiResponse> handleProblemAlreadyException(ProblemAlreadyExistsException pe) {
        return new ResponseEntity<>(new ApiResponse(pe.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ProblemNotFountException.class)
    public ResponseEntity<ApiResponse> handleProblemNotFoundException(ProblemNotFountException pe) {
        return new ResponseEntity<>(new ApiResponse(pe.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FileException.class)
    public ResponseEntity<ApiResponse> handleFileException(FileException fe) {
        return new ResponseEntity<>(new ApiResponse(fe.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse> handleUserNotFoundException(UserNotFoundException une) {
        return new ResponseEntity<>(new ApiResponse(une.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TestcaseException.class)
    public ResponseEntity<ApiResponse> handleTestcaseException(TestcaseException tce) {
        return new ResponseEntity<>(new ApiResponse(tce.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiResponse> handleUserAlreadyExistsException(UserAlreadyExistsException uae) {
        return new ResponseEntity<>(new ApiResponse(uae.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TokenGenerationException.class)
    public ResponseEntity<ApiResponse> handleTokenGenerationException(TokenGenerationException tge) {
        return new ResponseEntity<>(new ApiResponse(tge.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ApiResponse> handleExpiredJwtException(ExpiredJwtException e) {
        return new ResponseEntity<>(new ApiResponse("EXPIRED"), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ApiResponse> handleInvalidTokenException(InvalidTokenException ite) {
        return new ResponseEntity<>(new ApiResponse("INVALID"), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ApiResponse> handleExpiredTokenException(TokenExpiredException tee) {
        return new ResponseEntity<>(new ApiResponse("EXPIRED"), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse> AuthExceptionHandler(AuthenticationException ae){
        return new ResponseEntity<>(new ApiResponse(ae.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> exceptionHandler(Exception e) {
        e.printStackTrace();
        return new ResponseEntity<>(new ApiResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
