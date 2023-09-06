package com.terron.services.user;

import com.terron.dto.*;
import com.terron.exceptions.UserAlreadyExistException;
import com.terron.models.user.Users;
import com.terron.models.user.UserRole;
import com.terron.repository.user.UserRepository;
import com.terron.services.email.EmailServiceImpl;
import javassist.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder encoder;

    @Autowired
    ModelMapper modelMapper;

    private final EmailServiceImpl emailServiceImpl;

    @Override
    public void registerUser(UserRegistrationDto userRegistrationDto) throws UserAlreadyExistException, MessagingException {
        Users user = new Users();
        userRegistrationDto.setEmailAddress(userRegistrationDto.getEmailAddress().toLowerCase());
        boolean userExists = userRepository
                .existsByEmailAddress(userRegistrationDto.getEmailAddress());

        if (userExists) {
            throw new UserAlreadyExistException(String.format("User with email address: %s already exists", userRegistrationDto.getEmailAddress()));

        }
        userRegistrationDto.setPassword(encoder.encode(userRegistrationDto.getPassword()));
        user = modelMapper.map(userRegistrationDto, Users.class);
        user.setRole(UserRole.COMPANY_OWNER);
        user.setVerificationToken(UUID.randomUUID().toString());
        userRepository.save(user);

        emailServiceImpl.sendConfirmationMail(userRegistrationDto.getEmailAddress(), user.getVerificationToken());
    }

    @Override
    public void confirmUser(String token) throws NotFoundException {
        Users user = userRepository.findByVerificationToken(token).orElseThrow(() -> new NotFoundException(String.format("Invalid token: %s", token)));
        user.setActive(true);
        user.setVerificationToken(null);
        userRepository.save(user);
    }

    @Override
    public void confirmResetPassword(String token, UpdatePasswordDto updatePasswordDto) throws NotFoundException {
        Users user = userRepository.findByVerificationToken(token).orElseThrow(() -> new NotFoundException(String.format("Invalid token: %s", token)));
        user.setPassword(encoder.encode(updatePasswordDto.getNewPassword()));
        user.setVerificationToken(null);
        userRepository.save(user);
    }

    @Override
    public void resetPassword(UpdatePasswordDto passwordDto) throws MessagingException, NotFoundException {
        Users user = userRepository.findByEmailAddress(passwordDto.getEmailAddress()).orElseThrow(() -> new NotFoundException(String.format("User with this email: %s does not exist", passwordDto.getEmailAddress())));
        String token = UUID.randomUUID().toString();
        user.setVerificationToken(token);
        userRepository.save(user);
        emailServiceImpl.sendResetPasswordMail(passwordDto.getEmailAddress(), token);
    }

}
