package com.terron.services.company;

import com.terron.dto.FixedVirtualAccountRequest;
import com.terron.dto.OnboardCompanyDto;
import com.terron.dto.VirtualAccountResponse;
import com.terron.exceptions.UserAlreadyExistException;
import com.terron.models.company.Company;
import com.terron.models.company.CompanyType;
import com.terron.models.company.VirtualAccount;
import com.terron.models.payment.Payment;
import com.terron.models.user.UserRole;
import com.terron.models.user.Users;
import com.terron.repository.company.CompanyRepository;
import com.terron.repository.company.VirtualAccountRepository;
import com.terron.repository.hotels.GuestInsuranceRepository;
import com.terron.repository.payment.PaymentRepository;
import com.terron.repository.user.UserRepository;
import com.terron.services.email.EmailServiceImpl;
import com.terron.services.payment.PaymentService;
import com.terron.services.utils.GuestInsuranceResponseDto;
import com.terron.services.utils.HotelPaginationModel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.naming.ServiceUnavailableException;
import java.io.Closeable;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
@AllArgsConstructor
public class CompanyServiceImpl implements CompanyService{

    private final UserRepository userRepository;

    private final CompanyRepository companyRepository;

    private final PaymentService paymentService;

    private final VirtualAccountRepository virtualAccountRepository;

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    GuestInsuranceRepository guestInsuranceRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    TemplateEngine templateEngine;

    @Autowired
    Random random;

    @Override
    @Transactional
    public Company onboardCompany(OnboardCompanyDto onboardCompanyDto) throws Exception {
        Boolean companyExists = companyRepository.existsByOfficialEmailAddress(onboardCompanyDto.getOfficialEmailAddress());
        if (companyExists) {
            throw new UserAlreadyExistException(String.format("Company with email address: %s already exists", onboardCompanyDto.getOfficialEmailAddress()));

        }
        Boolean companyExistsByCac = companyRepository.existsByCacNumber(onboardCompanyDto.getCacNumber());
        if (companyExistsByCac) {
            throw new UserAlreadyExistException(String.format("Company with cac number: %s already exists", onboardCompanyDto.getCacNumber()));

        }

        Company company = modelMapper.map(onboardCompanyDto, Company.class);
        company.setRegisteredDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss")));
        company = companyRepository.save(company);
        String verificationToken = String.format("%04d", random.nextInt(10000));
        Users user = Users.builder()
                .emailAddress(onboardCompanyDto.getOfficialEmailAddress())
                .registeredDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss")))
                .firstName(onboardCompanyDto.getContactPersonFirstname())
                .lastName(onboardCompanyDto.getContactPersonLastname())
                .phoneNumber(company.getContactPersonPhoneNumber())
                .companyId(company.getId())
                .verificationToken(verificationToken)
                .modifiedDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss")))
                .build();
        if(onboardCompanyDto.getCompanyType() == CompanyType.hotel){
            user.setRole(UserRole.COMPANY_OWNER);
        }
        else {
            user.setRole(UserRole.INSURANCE_USER);
        }
        userRepository.save(user);

        if(onboardCompanyDto.getCompanyType() == CompanyType.insurance){
           createVirtualAccount(onboardCompanyDto, company);
        }
        sendConfirmationMail(user, "localhost:3000");
        return company;
    }

    public void createVirtualAccount(OnboardCompanyDto onboardCompanyDto, Company company) throws ServiceUnavailableException {
        FixedVirtualAccountRequest fixedVirtualAccountRequest = FixedVirtualAccountRequest.builder()
                .customerEmail(onboardCompanyDto.getOfficialEmailAddress())
                .customerMobile(onboardCompanyDto.getContactPersonPhoneNumber())
                .customerName(onboardCompanyDto.getCompanyName())
                .bvn(onboardCompanyDto.getBvn())
                .build();

        VirtualAccountResponse virtualAccountResponse = paymentService.createFixedVirtualAccount(fixedVirtualAccountRequest);
        VirtualAccount virtualAccount = VirtualAccount.builder()
                .accountName(virtualAccountResponse.getData().getAccountName())
                .accountNumber(virtualAccountResponse.getData().getAccountNumber())
                .accountType(virtualAccountResponse.getData().getAccountType())
                .active(virtualAccountResponse.getData().isActive())
                .bankName(virtualAccountResponse.getData().getBankName())
                .companyId(company.getId())
                .creationDate(virtualAccountResponse.getData().getCreationDate())
                .deleted(virtualAccountResponse.getData().isDeleted())
                .email(virtualAccountResponse.getData().getEmail())
                .gateway(virtualAccountResponse.getData().getGateway())
                .phone(virtualAccountResponse.getData().getPhone())
                .build();
        virtualAccountRepository.save(virtualAccount);
    }

    public VirtualAccount getCompanyAccountDetails(Long companyId) {
        VirtualAccount virtualAccount = virtualAccountRepository.findByCompanyId(companyId);
        return virtualAccount;
    }

    @Override
    public Company addHotel(OnboardCompanyDto onboardCompanyDto) throws Exception {
       Company company = onboardCompany(onboardCompanyDto);
       return company;
    }

    public HotelPaginationModel getAllHotels(Integer page, Integer pageSize, String searchField, String state){
        Page<Company> companies = null;
        Pageable pagination = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.ASC, "registeredDate"));
        try {
            companies = searchField.length() > 0
                    ? companyRepository.findByCompanyTypeAndCompanyNameContainingOrCacNumberContaining(CompanyType.hotel, searchField, searchField, pagination)
                    : state.length() > 0
                    ? companyRepository.findByCompanyTypeAndState(CompanyType.hotel,state, pagination)
                    : companyRepository.findAllByCompanyType(CompanyType.hotel, pagination);

            HotelPaginationModel hotelPaginationModel = new HotelPaginationModel();
            hotelPaginationModel.setTotalCount(companies.getTotalElements());
            hotelPaginationModel.setData(companies.getContent());
            Long hotelCount = companyRepository.countAllByCompanyType(CompanyType.hotel);
            hotelPaginationModel.setTotalHotels(hotelCount);
            Long usersCount = userRepository.count();
            hotelPaginationModel.setTotalUsers(usersCount);

            return hotelPaginationModel;
        } finally {
            if (companies != null && companies instanceof Closeable) {
                try {
                    ((Closeable) companies).close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public List<Company> getAllInsuranceCompanies(){
        List<Company> companies = companyRepository.findAllByCompanyType(CompanyType.insurance);
        return companies;
    }

    private void sendConfirmationMail(Users applicationUser, String url) throws Exception {
        String toAddress = applicationUser.getEmailAddress();
        String fromAddress = "o.ifeoluwah@gmail.com";
        String senderName = "Terror Insure";
        String subject = "Welcome to Terror insure";
        String verifyURL = url + "/verify?token=" + applicationUser.getVerificationToken();

        Context context = new Context();
        context.setVariable("name", applicationUser.getFirstName() + " " + applicationUser.getLastName());
        context.setVariable("link", verifyURL);

        String content = templateEngine.process("confirmationEmail", context);

        new EmailServiceImpl().sendNotification(fromAddress, senderName, toAddress, subject, verifyURL, content);
    }

    public GuestInsuranceResponseDto insuranceCompanyGeneralReport(Long companyId) throws UserAlreadyExistException {
        Company company = companyRepository.findById(companyId).orElseThrow( () -> new UserAlreadyExistException(String.format("Company with id: %s does not exist", companyId)));
        long users = userRepository.count();
        List<Company> companies = companyRepository.getCompaniesByInsuranceCompany(company.getCompanyName());
        long ntdcUsersCount = 0L;
        long hotelUsersCount = 0L;
        long insuranceUsersCount = 0L;
        long guestCount = 0L;
        for(Company company1 : companies){
            Long totalHotelUsers = userRepository.countAllByRoleAndCompanyId(UserRole.COMPANY_OWNER,company1.getId());
            Long totalInsuranceUsers = userRepository.countAllByRoleAndCompanyId(UserRole.INSURANCE_USER,company1.getId());
            Long totalNtdcUsers = userRepository.countAllByRoleAndCompanyId(UserRole.NTDC,company1.getId());
            Long totalGuest = guestInsuranceRepository.countAllByCompanyId(company1.getId());
            hotelUsersCount = hotelUsersCount + totalHotelUsers;
            ntdcUsersCount = ntdcUsersCount + totalNtdcUsers;
            insuranceUsersCount = insuranceUsersCount + totalInsuranceUsers;
            guestCount = guestCount + totalGuest;
        }
        Long companiesCount = companyRepository.countAllByInsuranceCompany(company.getCompanyName());

        List<Payment> payments = paymentRepository.findAllByInsuranceCompany(company.getCompanyName());
        double totalAmountPaid = payments.stream()
                .mapToDouble(Payment::getAmountPaid)
                .sum();

        double totalAmountInsured = guestCount * 690.0;
        GuestInsuranceResponseDto guestInsuranceResponseDto = new GuestInsuranceResponseDto();
        guestInsuranceResponseDto.setTotalAmountInsured(totalAmountInsured);
        guestInsuranceResponseDto.setAmountPaid(totalAmountPaid);
        guestInsuranceResponseDto.setOutstandingAmount(totalAmountInsured - totalAmountPaid);
        guestInsuranceResponseDto.setUsersCount(users);
        guestInsuranceResponseDto.setInsuredVolume(guestCount);
        guestInsuranceResponseDto.setCompanyCount(companiesCount);
        guestInsuranceResponseDto.setInsuranceUsers((float) ((insuranceUsersCount / users) * 100.0));
        guestInsuranceResponseDto.setHotelUser((float) ((hotelUsersCount / users) * 100.0));
        guestInsuranceResponseDto.setNtdcUsers((float) ((ntdcUsersCount / users) * 100.0));
        return guestInsuranceResponseDto;
    }

    public void revenueTrend(Long companyId, Date startDate, Date endDate) throws UserAlreadyExistException {
        Company company = companyRepository.findById(companyId).orElseThrow( () -> new UserAlreadyExistException(String.format("Company with id: %s does not exist", companyId)));
        List<Company> companies = companyRepository.getCompaniesByInsuranceCompany(company.getCompanyName());
        long guestCount = 0L;
        for(Company company1 : companies){
            Long totalGuest = guestInsuranceRepository.countAllByCompanyId(company1.getId());
            guestCount = guestCount + totalGuest;
        }

        List<Payment> payments = paymentRepository.findAllByInsuranceCompany(company.getCompanyName());

        double totalAmountInsured = guestCount * 690.0;
    }
}
