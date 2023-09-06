package com.terron.controller.controllers;

import com.terron.dto.UpdatePasswordDto;
import com.terron.dto.UserRegistrationDto;
import com.terron.exceptions.UserAlreadyExistException;
import com.terron.services.user.UserServiceImpl;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;


@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserServiceImpl userServiceImpl;



    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegistrationDto userRegistration) throws MessagingException, UserAlreadyExistException {
        userServiceImpl.registerUser(userRegistration);
        return new ResponseEntity<>("Registration successful. Please check your mail for confirmation", HttpStatus.OK);
    }

    @GetMapping("/confirm")
    public ResponseEntity<?> confirmMail(@RequestParam("token") String token) throws NotFoundException {
        if(token == null){
            throw new IllegalStateException("Token can not be null");
        }else {
            userServiceImpl.confirmUser(token);
        }

        return new ResponseEntity<>( "Verification successful", HttpStatus.OK);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> confirmResetPasswordToken(@RequestParam("token") String token, @RequestBody UpdatePasswordDto updatePasswordDto) throws NotFoundException {
        userServiceImpl.confirmResetPassword(token, updatePasswordDto);
        return new ResponseEntity<>( "Password reset successfully", HttpStatus.OK);
    }
}