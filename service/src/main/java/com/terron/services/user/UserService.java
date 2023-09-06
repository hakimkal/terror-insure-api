package com.terron.services.user;

import com.terron.dto.UpdatePasswordDto;
import com.terron.dto.UpdateProfileDto;
import com.terron.dto.UserRegistrationDto;
import com.terron.exceptions.UserAlreadyExistException;
import com.terron.exceptions.UserNotFoundException;
import javassist.NotFoundException;


import javax.mail.MessagingException;

public interface UserService {

    void registerUser(UserRegistrationDto userRegistrationDto) throws UserAlreadyExistException, MessagingException;

    void confirmUser(String token) throws NotFoundException;

    void confirmResetPassword(String token, UpdatePasswordDto updatePasswordDto) throws NotFoundException;

    void resetPassword(UpdatePasswordDto passwordDto) throws MessagingException, NotFoundException;
}
