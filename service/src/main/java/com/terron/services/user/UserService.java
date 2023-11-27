package com.terron.services.user;

import com.terron.dto.ChangePasswordDto;
import com.terron.dto.RequestResetPasswordDto;
import com.terron.dto.UpdatePasswordDto;
import com.terron.models.user.Users;

public interface UserService {

    void changePassword(ChangePasswordDto changePasswordDto) throws Exception;

    void confirmUser(String token) throws Exception;

    void confirmResetPassword(String token, UpdatePasswordDto updatePasswordDto) throws Exception;

    Users resetPassword(RequestResetPasswordDto passwordDto) throws Exception;
}
