package com.terron.services.user;

import com.terron.dto.RequestResetPasswordDto;
import com.terron.dto.UpdatePasswordDto;
import com.terron.dto.UserRegistrationDto;
import com.terron.models.user.Users;

public interface UserService {

    void registerUser(UserRegistrationDto userRegistrationDto) throws Exception;

    void confirmUser(String token) throws Exception;

    void confirmResetPassword(String token, UpdatePasswordDto updatePasswordDto) throws Exception;

    Users resetPassword(RequestResetPasswordDto passwordDto) throws Exception;
}
