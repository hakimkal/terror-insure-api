package com.terron.services.company;

import com.terron.dto.FixedVirtualAccountRequest;
import com.terron.dto.OnboardCompanyDto;
import com.terron.dto.VirtualAccountResponse;
import com.terron.exceptions.UserAlreadyExistException;
import com.terron.models.company.Company;
import com.terron.models.company.CompanyType;
import com.terron.models.company.VirtualAccount;
import com.terron.models.hotels.GuestInsurance;
import com.terron.models.payment.DailyTransactions;
import com.terron.models.payment.Payment;
import com.terron.models.user.UserRole;
import com.terron.models.user.Users;
import com.terron.repository.company.CompanyRepository;
import com.terron.repository.company.VirtualAccountRepository;
import com.terron.repository.hotels.GuestInsuranceRepository;
import com.terron.repository.hotels.ReservationsRepository;
import com.terron.repository.payment.DailyTransactionRepository;
import com.terron.repository.payment.PaymentRepository;
import com.terron.repository.user.UserRepository;
import com.terron.services.email.EmailServiceImpl;
import com.terron.services.payment.PaymentService;
import com.terron.services.utils.*;
import javassist.NotFoundException;
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
import java.util.*;

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
    DailyTransactionRepository dailyTransactionRepository;

    @Autowired
    GuestInsuranceRepository guestInsuranceRepository;

    @Autowired
    ReservationsRepository reservationsRepository;

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
        Company company = new Company();
        if(onboardCompanyDto.getInsuranceCompany() != null){
             companyRepository.findById(onboardCompanyDto.getInsuranceCompany()).
                    orElseThrow( () -> new UserAlreadyExistException(String.format("Insurance company with id: %s does not exist", onboardCompanyDto.getInsuranceCompany())));
            company.setInsuranceCompanyId(onboardCompanyDto.getInsuranceCompany());
        }

        company.setCompanyLogo(onboardCompanyDto.getCompanyLogo());
        company.setCompanyName(onboardCompanyDto.getCompanyName());
        company.setCompanyType(onboardCompanyDto.getCompanyType());
        company.setAddress(onboardCompanyDto.getAddress());
        company.setBranch(onboardCompanyDto.getBranch());
        company.setCacNumber(onboardCompanyDto.getCacNumber());
        company.setCacRegistrationDate(onboardCompanyDto.getCacRegistrationDate());
        company.setContactPersonCountryCode(onboardCompanyDto.getContactPersonCountryCode());
        company.setContactPersonFirstname(onboardCompanyDto.getContactPersonFirstname());
        company.setContactPersonLastname(onboardCompanyDto.getContactPersonLastname());
        company.setGpsCoordinate(onboardCompanyDto.getGpsCoordinate());
        company.setOfficialEmailAddress(onboardCompanyDto.getOfficialEmailAddress());
        company.setLga(onboardCompanyDto.getLga());
        company.setState(onboardCompanyDto.getState());
        company.setContactPersonPhoneNumber(onboardCompanyDto.getContactPersonPhoneNumber());
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

        if(onboardCompanyDto.getCompanyType() == CompanyType.hotel){
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
                .bvn("00000000000")
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

    public VirtualAccount getCompanyAccountDetails(Long companyId) throws UserAlreadyExistException, ServiceUnavailableException {
        VirtualAccount virtualAccount = virtualAccountRepository.findByCompanyId(companyId);
        Company company = companyRepository.findById(companyId).
                orElseThrow( () -> new UserAlreadyExistException(String.format("Company with id: %s does not exist", companyId)));
        if(virtualAccount == null){
            OnboardCompanyDto onboardCompanyDto = OnboardCompanyDto.builder()
                    .contactPersonFirstname(company.getContactPersonFirstname())
                    .contactPersonPhoneNumber(company.getContactPersonPhoneNumber())
                    .companyName(company.getCompanyName())
                    .officialEmailAddress(company.getOfficialEmailAddress())
                    .build();
            if(company.getCompanyType() == CompanyType.hotel){
               createVirtualAccount(onboardCompanyDto, company);
            }
            virtualAccount = virtualAccountRepository.findByCompanyId(companyId);
        }
        return virtualAccount;
    }

    @Override
    public Company addHotel(OnboardCompanyDto onboardCompanyDto) throws Exception {
       Company company = onboardCompany(onboardCompanyDto);
       return company;
    }

    public PaginationModel getAllHotelPayments(Long companyId, String startDate, String endDate, Integer page, Integer pageSize){
        Page<Payment> payments = null;
        Pageable pagination = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "datePaid"));

        try {
            payments = startDate.length() > 0 && endDate.length() > 0
                    ? paymentRepository.findAllByCompanyIdAndDatePaidBetween(companyId, startDate, endDate, pagination)
                    : paymentRepository.findAllByCompanyId(companyId, pagination);

            PaginationModel paginationModel = new PaginationModel();
            paginationModel.setTotalCount(payments.getTotalElements());
            paginationModel.setData(payments.getContent());

            return paginationModel;
        } finally {
            if (payments != null && payments instanceof Closeable) {
                try {
                    ((Closeable) payments).close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public PaginationModel getAllPayments(String startDate, String endDate, Integer page, Integer pageSize){
        Page<Payment> payments = null;
        Pageable pagination = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "datePaid"));

        try {
            payments = startDate.length() > 0 && endDate.length() > 0
                    ? paymentRepository.findAllByDatePaidBetween(startDate, endDate, pagination)
                    : paymentRepository.findAll(pagination);

            PaginationModel paginationModel = new PaginationModel();
            paginationModel.setTotalCount(payments.getTotalElements());
            paginationModel.setData(payments.getContent());

            return paginationModel;
        } finally {
            if (payments != null && payments instanceof Closeable) {
                try {
                    ((Closeable) payments).close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public PaginationModel getAllInsuranceCompanyPayments(Long insuranceCompanyId,String startDate, String endDate, Integer page, Integer pageSize) throws NotFoundException {
        Page<Payment> payments = null;
        Pageable pagination = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "datePaid"));

        Company company = companyRepository.findById(insuranceCompanyId).orElseThrow(() -> new NotFoundException(String.format("Company with this id: %s does not exist", insuranceCompanyId)));
        try {
            payments = startDate.length() > 0 && endDate.length() > 0
                    ? paymentRepository.findAllByInsuranceCompanyAndDatePaidBetween(company.getCompanyName(), startDate, endDate, pagination)
                    : paymentRepository.findAllByInsuranceCompany(company.getCompanyName(),pagination);

            PaginationModel paginationModel = new PaginationModel();
            paginationModel.setTotalCount(payments.getTotalElements());
            paginationModel.setData(payments.getContent());

            return paginationModel;
        } finally {
            if (payments != null && payments instanceof Closeable) {
                try {
                    ((Closeable) payments).close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public InsuranceCompanyPaginatedModel getAllInsuranceCompany(Integer page, Integer pageSize, String searchField, String state){
        Page<Company> companies = null;
        Long totalGuest = guestInsuranceRepository.count();
        Pageable pagination = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "registeredDate"));
        try {
            companies = searchField.length() > 0
                    ? companyRepository.findAllByCompanyTypeAndCompanyNameContainingOrCacNumberContaining(CompanyType.insurance, searchField, searchField, pagination)
                    : state.length() > 0
                    ? companyRepository.findAllByCompanyTypeAndState(CompanyType.insurance,state, pagination)
                    : companyRepository.findAllByCompanyType(CompanyType.insurance, pagination);

            InsuranceCompanyPaginatedModel insuranceCompanyPaginatedModel = new InsuranceCompanyPaginatedModel();
            insuranceCompanyPaginatedModel.setTotalCount(companies.getTotalElements());
            insuranceCompanyPaginatedModel.setData(companies.getContent());
            Long insuranceCompaniesCount = companyRepository.countAllByCompanyType(CompanyType.insurance);
            insuranceCompanyPaginatedModel.setTotalInsuranceCompanies(insuranceCompaniesCount);
            List<Payment> payments = paymentRepository.findAll();
            double totalAmountPaid = payments.stream()
                    .mapToDouble(Payment::getAmountPaid)
                    .sum();
            double totalAmountInsured = totalGuest * 690.0;
            insuranceCompanyPaginatedModel.setTotalAmountInsured(totalAmountInsured);
            insuranceCompanyPaginatedModel.setAmountPaid(totalAmountPaid);
            insuranceCompanyPaginatedModel.setOutstandingAmount(totalAmountInsured - totalAmountPaid);
            return insuranceCompanyPaginatedModel;
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


    public PaginationModel getAllHotelTransactions(Long companyId, String startDate, String endDate, Integer page, Integer pageSize){
        Page<DailyTransactions> transactions = null;
        Pageable pagination = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "transactionDate"));

        try {
            transactions = startDate.length() > 0 && endDate.length() > 0
                    ? dailyTransactionRepository.findAllByCompanyIdAndTransactionDateBetween(companyId, startDate, endDate, pagination)
                    : dailyTransactionRepository.findAllByCompanyId(companyId, pagination);

            PaginationModel paginationModel = new PaginationModel();
            paginationModel.setTotalCount(transactions.getTotalElements());
            paginationModel.setData(transactions.getContent());

            return paginationModel;
        } finally {
            if (transactions != null && transactions instanceof Closeable) {
                try {
                    ((Closeable) transactions).close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public PaginationModel getAllTransactions(String startDate, String endDate, Integer page, Integer pageSize){
        Page<DailyTransactions> transactions = null;
        Pageable pagination = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "transactionDate"));

        try {
            transactions = startDate.length() > 0 && endDate.length() > 0
                    ? dailyTransactionRepository.findAllByTransactionDateBetween(startDate, endDate, pagination)
                    : dailyTransactionRepository.findAll(pagination);

            PaginationModel paginationModel = new PaginationModel();
            paginationModel.setTotalCount(transactions.getTotalElements());
            paginationModel.setData(transactions.getContent());

            return paginationModel;
        } finally {
            if (transactions != null && transactions instanceof Closeable) {
                try {
                    ((Closeable) transactions).close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public PaginationModel getAllInsuranceCompanyTransactions(Long insuranceCompanyId,String startDate, String endDate, Integer page, Integer pageSize) throws NotFoundException {
        Page<DailyTransactions> transactions = null;
        Pageable pagination = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "transactionDate"));

        Company company = companyRepository.findById(insuranceCompanyId).orElseThrow(() -> new NotFoundException(String.format("Company with this id: %s does not exist", insuranceCompanyId)));
        try {
            transactions = startDate.length() > 0 && endDate.length() > 0
                    ? dailyTransactionRepository.findAllByInsuranceCompanyAndTransactionDateBetween(company.getCompanyName(), startDate, endDate, pagination)
                    : dailyTransactionRepository.findAllByInsuranceCompany(company.getCompanyName(),pagination);

            PaginationModel paginationModel = new PaginationModel();
            paginationModel.setTotalCount(transactions.getTotalElements());
            paginationModel.setData(transactions.getContent());

            return paginationModel;
        } finally {
            if (transactions != null && transactions instanceof Closeable) {
                try {
                    ((Closeable) transactions).close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public PaginationModel getAllInsuranceCompanyGuestInsurance(Integer page, Integer pageSize, String searchField, Long companyId) throws UserAlreadyExistException {
        boolean companyExists = companyRepository.existsById(companyId);
        if (!companyExists) {
            throw new UserAlreadyExistException(String.format("Company with id: %s does not exist exists", companyId));

        }

        Page<GuestInsurance> guestInsurances = null;
        Pageable pagination = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "createdDate"));
        try {
            guestInsurances = searchField.length() > 0
                    ? guestInsuranceRepository.findByInsuranceCompanyIdAndFirstNameContainingOrLastNameContainingOrCertificateNumberContaining(companyId,searchField, searchField, searchField, pagination)
                    : guestInsuranceRepository.findAllByInsuranceCompanyId(companyId, pagination);

            PaginationModel paginationModel = new PaginationModel();
            paginationModel.setTotalCount(guestInsurances.getTotalElements());
            paginationModel.setData(guestInsurances.getContent());

            return paginationModel;
        } finally {
            if (guestInsurances != null && guestInsurances instanceof Closeable) {
                try {
                    ((Closeable) guestInsurances).close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public HotelPaginationModel getAllHotels(Integer page, Integer pageSize, String searchField, String state){
        Page<Company> companies = null;
        Pageable pagination = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "registeredDate"));
        try {
            companies = searchField.length() > 0
                    ? companyRepository.findAllByCompanyTypeAndCompanyNameContainingOrCacNumberContaining(CompanyType.hotel, searchField, searchField, pagination)
                    : state.length() > 0
                    ? companyRepository.findAllByCompanyTypeAndState(CompanyType.hotel,state, pagination)
                    : companyRepository.findAllByCompanyType(CompanyType.hotel, pagination);

            HotelPaginationModel hotelPaginationModel = new HotelPaginationModel();
            hotelPaginationModel.setTotalCount(companies.getTotalElements());
            hotelPaginationModel.setData(companies.getContent());
            Long hotelCount = companyRepository.countAllByCompanyType(CompanyType.hotel);

            hotelPaginationModel.setTotalReservations(reservationsRepository.count());
            hotelPaginationModel.setTotalGuest(guestInsuranceRepository.count());
            List<GuestInsurance> guestInsurances = guestInsuranceRepository.findAll();
            Map<String, Integer> emailCounts = new HashMap<>();

            for (GuestInsurance guestInsurance : guestInsurances) {
                String email = guestInsurance.getEmailAddress();
                if (emailCounts.containsKey(email)) {
                    emailCounts.put(email, emailCounts.get(email) + 1);
                } else {
                    emailCounts.put(email, 1);
                }
            }

            int uniqueEmailCount = 0;
            int duplicateEmailCount = 0;
            for (Map.Entry<String, Integer> entry : emailCounts.entrySet()) {
                if (entry.getValue() == 1) {
                    uniqueEmailCount++;
                } else {
                    duplicateEmailCount++;
                }
            }
            hotelPaginationModel.setTotalHotels(hotelCount);
            hotelPaginationModel.setNewGuests(uniqueEmailCount);
            hotelPaginationModel.setReturnGuest(duplicateEmailCount);
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

    public GuestInsuranceResponseDto dashboard() {
        long users = userRepository.count();

        long hotelUsersCount = userRepository.countAllByRole(UserRole.COMPANY_OWNER);
        long insuranceUsersCount = userRepository.countAllByRole(UserRole.INSURANCE_USER);
        long ntdcUsersCount = userRepository.countAllByRole(UserRole.NTDC);
        long guestCount = guestInsuranceRepository.count();
        long companiesCount = companyRepository.count();

        List<Payment> payments = paymentRepository.findAll();
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
        guestInsuranceResponseDto.setInsuranceUsers(insuranceUsersCount);
        guestInsuranceResponseDto.setHotelUser(hotelUsersCount);
        guestInsuranceResponseDto.setNtdcUsers(ntdcUsersCount);
        return guestInsuranceResponseDto;
    }

    public GuestInsuranceResponseDto companyDashboard(Long companyId) throws UserAlreadyExistException {
        Company company = companyRepository.findById(companyId).orElseThrow( () -> new UserAlreadyExistException(String.format("Company with id: %s does not exist", companyId)));
        long users = userRepository.count();

        long hotelUsersCount = userRepository.countAllByRoleAndCompanyId(UserRole.COMPANY_OWNER,companyId);
        long insuranceUsersCount = userRepository.countAllByRoleAndCompanyId(UserRole.INSURANCE_USER,companyId);
        long ntdcUsersCount = userRepository.countAllByRoleAndCompanyId(UserRole.NTDC,companyId);
        long guestCount = guestInsuranceRepository.countAllByCompanyId(companyId);

        Long companiesCount = companyRepository.countAllByInsuranceCompanyId(company.getId());

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

//    public void revenueTrend(Long companyId, Date startDate, Date endDate) throws UserAlreadyExistException {
//        Company company = companyRepository.findById(companyId).orElseThrow( () -> new UserAlreadyExistException(String.format("Company with id: %s does not exist", companyId)));
//        List<Company> companies = companyRepository.getCompaniesByInsuranceCompany(company.getCompanyName());
//        long guestCount = 0L;
//        for(Company company1 : companies){
//            Long totalGuest = guestInsuranceRepository.countAllByCompanyId(company1.getId());
//            guestCount = guestCount + totalGuest;
//        }
//
//        List<Payment> payments = paymentRepository.findAllByInsuranceCompany(company.getCompanyName());
//
//        double totalAmountInsured = guestCount * 690.0;
//    }
}
