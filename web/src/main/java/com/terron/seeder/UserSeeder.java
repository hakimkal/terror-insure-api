package com.terron.seeder;

import com.terron.dto.UserRegistrationDto;
import com.terron.models.company.Company;
import com.terron.models.user.UserRole;
import com.terron.repository.company.CompanyRepository;
import com.terron.services.user.UserServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
@Order(2) // Run after CompanySeeder
public class UserSeeder implements CommandLineRunner {

    private final UserServiceImpl userService;
    private final CompanyRepository companyRepository;

    @Override
    public void run(String... args) throws Exception {
        // Get company IDs first (created by CompanySeeder)
        Long hiltonId = getCompanyIdByEmail("hilton.abuja@hilton.com");
        Long ekoHotelId = getCompanyIdByEmail("reservations@ekohotels.com");
        Long axaMansardId = getCompanyIdByEmail("info@axamansard.com");
        Long leadwayId = getCompanyIdByEmail("info@leadway.com");
        
        seedAdminUser();
        seedCompanyOwner(hiltonId);
        seedInsuranceUser(axaMansardId);
        seedSecurityUsers();
        seedSpecificUsers();
    }

    private void seedAdminUser() {
        try {
            UserRegistrationDto admin = new UserRegistrationDto();
            admin.setEmailAddress("admin@terron.com");
            admin.setPassword("Admin123!");
            admin.setFirstName("System");
            admin.setLastName("Admin");
            admin.setRole(UserRole.ADMIN);
            admin.setPhoneNumber("08000000000");
            
            userService.registerUser(admin);
            log.info("✅ Admin user created: admin@terron.com / Admin123!");
        } catch (Exception e) {
            log.info("Admin user already exists or error: {}", e.getMessage());
        }
    }

    private void seedCompanyOwner(Long companyId) {
        try {
            UserRegistrationDto owner = new UserRegistrationDto();
            owner.setEmailAddress("owner@company.com");
            owner.setPassword("Owner123!");
            owner.setFirstName("Company");
            owner.setLastName("Owner");
            owner.setRole(UserRole.COMPANY_OWNER);
            owner.setPhoneNumber("08111111111");
            owner.setCompanyId(companyId);
            
            userService.registerUser(owner);
            log.info("✅ Company owner created: owner@company.com / Owner123! (companyId: {})", companyId);
        } catch (Exception e) {
            log.info("Company owner already exists or error: {}", e.getMessage());
        }
    }

    private void seedInsuranceUser(Long companyId) {
        try {
            UserRegistrationDto insurance = new UserRegistrationDto();
            insurance.setEmailAddress("insurance@terron.com");
            insurance.setPassword("Insurance123!");
            insurance.setFirstName("Insurance");
            insurance.setLastName("User");
            insurance.setRole(UserRole.INSURANCE_USER);
            insurance.setPhoneNumber("08222222222");
            insurance.setCompanyId(companyId);
            
            userService.registerUser(insurance);
            log.info("✅ Insurance user created: insurance@terron.com / Insurance123! (companyId: {})", companyId);
        } catch (Exception e) {
            log.info("Insurance user already exists or error: {}", e.getMessage());
        }
    }

    private void seedSecurityUsers() {
        // NTDA User
        seedSecurityUser("ntda@terron.com", "NTDA123!", "NTDA", "Officer", UserRole.NTDA, "08333333333");
        
        // DSS User
        seedSecurityUser("dss@terron.com", "DSS123!", "DSS", "Agent", UserRole.DSS, "08444444444");
        
        // INTERPOL User
        seedSecurityUser("interpol@terron.com", "Interpol123!", "Interpol", "Officer", UserRole.INTERPOL, "08555555555");
        
        // NSA User
        seedSecurityUser("nsa@terron.com", "NSA123!", "NSA", "Agent", UserRole.NSA, "08666666666");
    }

    private void seedSecurityUser(String email, String password, String firstName, String lastName, UserRole role, String phone) {
        try {
            UserRegistrationDto user = new UserRegistrationDto();
            user.setEmailAddress(email);
            user.setPassword(password);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setRole(role);
            user.setPhoneNumber(phone);
            
            userService.registerUser(user);
            log.info("✅ {} user created: {} / {}", role, email, password);
        } catch (Exception e) {
            log.info("{} user already exists or error: {}", role, e.getMessage());
        }
    }

    private void seedSpecificUsers() {
        // Company IDs are fetched in run() method
        Long hiltonId = getCompanyIdByEmail("hilton.abuja@hilton.com");
        Long axaMansardId = getCompanyIdByEmail("info@axamansard.com");
        
        // Admin User (no company association needed)
        seedUser("admin@terror.insure", "demo.admin", "Admin", "Terror", UserRole.ADMIN, "08012345678", null);
        
        // Hilton Hotel User - associated with Hilton Abuja hotel
        seedUser("hilton.abuja@hilton.com", "demo.password", "Hilton", "Abuja", UserRole.COMPANY_OWNER, "08023456789", hiltonId);
        
        // Insurance User - associated with AXA Mansard
        seedUser("maryam.a@axamansard.com", "demo.password", "Maryam", "A", UserRole.INSURANCE_USER, "08034567890", axaMansardId);
        
        // NTDC User (no company association needed)
        seedUser("bello.y@ntdc.gov.ng", "demo.password", "Bello", "Y", UserRole.NTDA, "08045678901", null);
        
        // DSS User (no company association needed)
        seedUser("dss.admin@dss.gov.ng", "demo.password", "DSS", "Admin", UserRole.DSS, "08056789012", null);
    }
    
    private Long getCompanyIdByEmail(String email) {
        try {
            return companyRepository.findByOfficialEmailAddress(email).getId();
        } catch (Exception e) {
            log.warn("Company not found for email: {}", email);
            return null;
        }
    }

    private void seedUser(String email, String password, String firstName, String lastName, UserRole role, String phone, Long companyId) {
        try {
            UserRegistrationDto user = new UserRegistrationDto();
            user.setEmailAddress(email);
            user.setPassword(password);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setRole(role);
            user.setPhoneNumber(phone);
            user.setCompanyId(companyId);
            
            userService.registerUser(user);
            log.info("✅ {} user created: {} / {} (companyId: {})", role, email, password, companyId);
        } catch (Exception e) {
            log.info("User already exists or error for {}: {}", email, e.getMessage());
        }
    }
}
