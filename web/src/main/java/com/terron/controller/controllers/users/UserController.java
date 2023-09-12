package com.terron.controller.controllers.users;

import com.terron.dto.ChangePasswordDto;
import com.terron.dto.RequestResetPasswordDto;
import com.terron.dto.UpdatePasswordDto;
import com.terron.dto.UserRegistrationDto;
import com.terron.models.user.Users;
import com.terron.response.ResponseDetails;
import com.terron.response.ResponseDetailsWithObject;
import com.terron.services.user.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.time.LocalDateTime;


@RestController
@RequestMapping("/v1/users")
public class UserController {

    @Autowired
    UserServiceImpl userServiceImpl;



    @PatchMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordDto changePasswordDto) throws Exception {
        userServiceImpl.changePassword(changePasswordDto);
        ResponseDetails responseDetails = new ResponseDetails(LocalDateTime.now(), "You have successfully changed your password", "success");

        return ResponseEntity.status(200).body(responseDetails);
    }

    @GetMapping("/confirm")
    public ResponseEntity<?> confirmMail(@RequestParam("token") String token) throws Exception {
        if(token == null){
            throw new IllegalStateException("Token can not be null");
        }else {
            userServiceImpl.confirmUser(token);
        }

        ResponseDetails responseDetails = new ResponseDetails(LocalDateTime.now(), "Verification successful", "success");

        return ResponseEntity.status(200).body(responseDetails);
    }

    @PostMapping ("/request-password-reset")
    public ResponseEntity<?> RequestPasswordReset(@Valid @RequestBody RequestResetPasswordDto resetPasswordDto) throws Exception {
        Users user = userServiceImpl.resetPassword(resetPasswordDto);
        ResponseDetailsWithObject responseDetails = new ResponseDetailsWithObject(LocalDateTime.now(), "An email has been sent to you , reset your password",user.getVerificationToken(), "success");
        return new ResponseEntity<>(responseDetails, HttpStatus.OK);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> confirmResetPasswordToken(@RequestParam("token") String token, @RequestBody UpdatePasswordDto updatePasswordDto) throws Exception {
        userServiceImpl.confirmResetPassword(token, updatePasswordDto);
        ResponseDetails responseDetails = new ResponseDetails(LocalDateTime.now(), "Password rest successful", "success");
        return new ResponseEntity<>(responseDetails, HttpStatus.OK);
    }
}