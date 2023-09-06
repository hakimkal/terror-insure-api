package com.terron.repository.user;

import com.terron.models.user.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {

    Optional<Users> findByEmailAddress(String email);

    boolean existsByEmailAddress(String email);

    Optional<Users> findByVerificationToken(String token);

}
