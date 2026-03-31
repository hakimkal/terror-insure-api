package com.terron.seeder;

import com.terron.models.company.Company;
import com.terron.models.company.CompanyType;
import com.terron.repository.company.CompanyRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@AllArgsConstructor
@Slf4j
@Order(1) // Run before UserSeeder
public class CompanySeeder implements CommandLineRunner {

    private final CompanyRepository companyRepository;

    @Override
    public void run(String... args) throws Exception {
        seedInsuranceCompanies();
        seedHotels();
    }

    private void seedInsuranceCompanies() {
        // AXA Mansard Insurance
        createInsuranceCompanyIfNotExists(
                "AXA Mansard Insurance",
                "info@axamansard.com",
                "RC123456",
                "08012345678",
                "Lagos",
                "Ikeja"
        );

        // Leadway Assurance
        createInsuranceCompanyIfNotExists(
                "Leadway Assurance",
                "info@leadway.com",
                "RC234567",
                "08023456789",
                "Lagos",
                "Victoria Island"
        );
    }

    private void seedHotels() {
        // Hilton Abuja - associated with AXA Mansard (ID 1)
        createHotelIfNotExists(
                "Hilton Abuja",
                "hilton.abuja@hilton.com",
                "RC987654",
                "08098765432",
                "FCT",
                "Abuja Municipal",
                "Aguiyi Ironsi Street, Maitama",
                1L // insuranceCompanyId
        );

        // Eko Hotel Lagos
        createHotelIfNotExists(
                "Eko Hotel & Suites",
                "reservations@ekohotels.com",
                "RC876543",
                "08087654321",
                "Lagos",
                "Eti-Osa",
                "1415 Adetokunbo Ademola Street, Victoria Island",
                1L // insuranceCompanyId
        );

        // Transcorp Hilton Abuja
        createHotelIfNotExists(
                "Transcorp Hilton Abuja",
                "info@transcorphilton.com",
                "RC765432",
                "08076543210",
                "FCT",
                "Abuja Municipal",
                "1 Aguiyi Ironsi Street, Maitama",
                2L // insuranceCompanyId
        );
    }

    private void createInsuranceCompanyIfNotExists(String name, String email, String cacNumber, 
                                                    String phone, String state, String lga) {
        try {
            if (!companyRepository.existsByOfficialEmailAddress(email)) {
                Company company = Company.builder()
                        .companyName(name)
                        .officialEmailAddress(email)
                        .cacNumber(cacNumber)
                        .contactPersonPhoneNumber(phone)
                        .companyType(CompanyType.insurance)
                        .state(state)
                        .lga(lga)
                        .contactPersonFirstname("Admin")
                        .contactPersonLastname("User")
                        .registeredDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss")))
                        .build();
                companyRepository.save(company);
                log.info("✅ Insurance company created: {} (ID: {})", name, company.getId());
            } else {
                log.info("Insurance company already exists: {}", name);
            }
        } catch (Exception e) {
            log.error("Error creating insurance company {}: {}", name, e.getMessage());
        }
    }

    private void createHotelIfNotExists(String name, String email, String cacNumber, String phone,
                                        String state, String lga, String address, Long insuranceCompanyId) {
        try {
            if (!companyRepository.existsByOfficialEmailAddress(email)) {
                Company company = Company.builder()
                        .companyName(name)
                        .officialEmailAddress(email)
                        .cacNumber(cacNumber)
                        .contactPersonPhoneNumber(phone)
                        .companyType(CompanyType.hotel)
                        .state(state)
                        .lga(lga)
                        .address(address)
                        .insuranceCompanyId(insuranceCompanyId)
                        .contactPersonFirstname("Manager")
                        .contactPersonLastname("User")
                        .registeredDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss")))
                        .build();
                companyRepository.save(company);
                log.info("✅ Hotel created: {} (ID: {})", name, company.getId());
            } else {
                log.info("Hotel already exists: {}", name);
            }
        } catch (Exception e) {
            log.error("Error creating hotel {}: {}", name, e.getMessage());
        }
    }
}
