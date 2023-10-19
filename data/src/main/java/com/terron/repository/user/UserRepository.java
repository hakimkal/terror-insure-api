package com.terron.repository.user;

import com.terron.models.hotels.GuestInsurance;
import com.terron.models.user.UserRole;
import com.terron.models.user.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {

    Optional<Users> findByEmailAddress(String email);

    boolean existsByEmailAddress(String email);

    Optional<Users> findByVerificationToken(String token);

    Page<Users> findByCompanyIdAndFirstNameContainingOrLastNameContainingOrPhoneNumberContaining(Long companyId, String firstName, String lastName, String phoneNumber, Pageable pagination);

    Page<Users> findByRoleAndFirstNameContainingOrLastNameContainingOrPhoneNumberContaining(UserRole role, String firstName, String lastName, String phoneNumber, Pageable pagination);

    Page<Users> findAllByCompanyId(Long companyId, Pageable pagination);

    Page<Users> findAllByRole(UserRole role, Pageable pagination);

    Long countAllByRoleAndCompanyId(UserRole role,Long companyId);

}
