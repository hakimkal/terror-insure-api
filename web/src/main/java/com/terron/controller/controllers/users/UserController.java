package com.terron.controller.controllers.users;

import com.terron.dto.*;
import com.terron.models.user.Users;
import com.terron.response.ResponseDetails;
import com.terron.response.ResponseDetailsWithObject;
import com.terron.services.user.UserServiceImpl;
import com.terron.services.utils.UserDetailsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Objects;

import static com.terron.utils.utility.decodeToken;


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

    @PostMapping ("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegistrationDto userRegistrationDto, @RequestHeader(name = "Authorization") String token) throws Exception {
        String role = decodeToken(token);
        if (!Objects.equals(role, "ROLE_COMPANY_OWNER") && !Objects.equals(role, "ROLE_ADMIN")) {
            ResponseDetails responseDetails = new ResponseDetails(LocalDateTime.now(), "Access is denied", "error");
            return new ResponseEntity<>(responseDetails, HttpStatus.FORBIDDEN);
        }

        Users user = userServiceImpl.registerUser(userRegistrationDto);
        ResponseDetailsWithObject responseDetails = new ResponseDetailsWithObject(LocalDateTime.now(), "Registration successful",user, "success");
        return new ResponseEntity<>(responseDetails, HttpStatus.CREATED);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> confirmResetPasswordToken(@RequestParam("token") String token, @RequestBody UpdatePasswordDto updatePasswordDto) throws Exception {
        userServiceImpl.confirmResetPassword(token, updatePasswordDto);
        ResponseDetails responseDetails = new ResponseDetails(LocalDateTime.now(), "Password rest successful", "success");
        return new ResponseEntity<>(responseDetails, HttpStatus.OK);
    }

    @GetMapping ("/{userId}")
    public ResponseEntity<?> getSingleUser(@PathVariable Long userId) throws Exception {
        UserDetailsDto user = userServiceImpl.getUserById(userId);
        ResponseDetailsWithObject responseDetails = new ResponseDetailsWithObject(LocalDateTime.now(), "User gotten successfully",user, "success");
        return new ResponseEntity<>(responseDetails, HttpStatus.OK);
    }

    @GetMapping ("/profile")
    public ResponseEntity<?> getUser(@RequestHeader(name = "Authorization") String token) throws Exception {
        UserDetailsDto user = userServiceImpl.getUserByToken(token);
        ResponseDetailsWithObject responseDetails = new ResponseDetailsWithObject(LocalDateTime.now(), "User gotten successfully", user, "success");
        return new ResponseEntity<>(responseDetails, HttpStatus.OK);
    }

    @PatchMapping ("/{userId}")
    public ResponseEntity<?> updateUser(@RequestBody UpdateProfileDto updateProfileDto, @PathVariable Long userId,@RequestParam(value = "companyId", defaultValue = "0", required = false) Long companyId) throws Exception {
        Users user = userServiceImpl.updateUser(updateProfileDto, userId, companyId);
        ResponseDetailsWithObject responseDetails = new ResponseDetailsWithObject(LocalDateTime.now(), "User updated successfully", user, "success");
        return new ResponseEntity<>(responseDetails, HttpStatus.OK);
    }
}