package com.terron.security;

import com.terron.models.user.UserRole;
import com.terron.models.user.Users;
import com.terron.repository.user.UserRepository;
import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class AppUserDetailService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Users user = null;
        try {
            user = userRepository.findByEmailAddress(s).orElseThrow(() -> new NotFoundException(String.format("User with this email: %s does not exist", s)));
        } catch (NotFoundException e) {
            throw new UsernameNotFoundException("Invalid credentials");
        }
        Object test = new User(user.getEmailAddress(), user.getPassword(), getAuthorities(user.getRole()));
        log.info("User -> {}",test );
        return new User(user.getEmailAddress(), user.getPassword(), getAuthorities(user.getRole()));

    }

    private Collection<? extends GrantedAuthority> getAuthorities(UserRole role) {
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_" + role.toString()));
    }
}