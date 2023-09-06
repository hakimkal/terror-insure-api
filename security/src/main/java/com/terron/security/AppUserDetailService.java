package com.terron.security;

import com.terron.models.user.UserRole;
import com.terron.models.user.Users;
import com.terron.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class AppUserDetailService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Users> user = userRepository.findByEmailAddress(email);

        if (user.isEmpty()) {
            throw new UsernameNotFoundException("user does not exist");
        }

        if(user.get().isActive())
            return new org.springframework.security.core.userdetails.User(user.get().getEmailAddress(), user.get().getPassword(), getAuthorities(user.get().getRole()));

        return null;

    }

    private Collection<GrantedAuthority> getGrantedAuthorities(UserRole roles) {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
            grantedAuthorities.add(new SimpleGrantedAuthority(String.valueOf(roles)));

            return grantedAuthorities;
    }


    public Collection<? extends GrantedAuthority> getAuthorities(UserRole authorities) {
        return getGrantedAuthorities(authorities);
    }
}
