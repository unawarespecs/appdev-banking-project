package io.github.unawarespecs.bankdb.serviceimpl;

import io.github.unawarespecs.bankapp.model.*;
import io.github.unawarespecs.bankapp.entity.*;
import io.github.unawarespecs.bankapp.repo.*;
import io.github.unawarespecs.bankapp.service.BankInterface;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Stream;

import static java.time.LocalDateTime.now;

@Service
public class BankServiceImpl implements BankInterface {

    Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private final AdminDataRepository adminDataRepository;
    @Autowired
    private final CustDataRepository custDataRepository;
    @Autowired
    private final LoanDataRepository loanDataRepository;
    @Autowired
    private final LoanPlanDataRepository loanPlanDataRepository;

    private Customer currentlyLoggedInCustomer;
    private Administrator currentlyLoggedInAdmin;

    public BankServiceImpl(AdminDataRepository adminDataRepository,
                       CustDataRepository custDataRepository,
                       LoanDataRepository loanDataRepository,
                       LoanPlanDataRepository loanPlanDataRepository) {
        this.adminDataRepository = adminDataRepository;
        this.custDataRepository = custDataRepository;
        this.loanDataRepository = loanDataRepository;
        this.loanPlanDataRepository = loanPlanDataRepository;
    }

    @Override
    public int getAccountNumber(Customer cust) throws NumberFormatException {
        return custDataRepository.getCustomerId(cust.getUsername(), cust.getPassword());
    }

    @Override
    public String getOwner(Customer cust) throws Exception {
        return "";
    }

    @Override
    public Customer getCurrentlyLoggedInCustomer() {
        return this.currentlyLoggedInCustomer;
    }

    @Override
    public void setCurrentlyLoggedInCustomer(Customer cust) {
        this.currentlyLoggedInCustomer = cust;
    }

    @Override
    public Administrator getCurrentlyLoggedInAdmin() {
        return this.currentlyLoggedInAdmin;
    }

    @Override
    public void setCurrentlyLoggedInAdmin(Administrator admin) {
        this.currentlyLoggedInAdmin = admin;
    }

    @Override
    public User[] getAllUsers() throws Exception {
        List<CustomerData> customerList = custDataRepository.findAll();
        List<AdministratorData> adminList = adminDataRepository.findAll();


        Stream<User> customerStream = customerList.stream().map(cust -> new Customer(
                cust.getUuid(),
                cust.getUsername(),
                cust.getPassword(),
                "Customer",
                false,
                cust.getId(),
                cust.getBalance(),
                cust.getPin(),
                cust.isAccountFrozen(),
                cust.getCreditScore()
        ));


        Stream<User> adminStream = adminList.stream().map(admin -> new Administrator(
                admin.getUuid(),
                "Admin",
                true,
                admin.getUsername(),
                admin.getPassword(),
                admin.getId()
        ));


        return Stream.concat(customerStream, adminStream)
                .toArray(User[]::new);
    }

    @Override
    public Customer[] getCustomers() throws Exception {
        List<CustomerData> data = custDataRepository.findAll();

        Customer[] customers = data.stream().map(
                cust -> new Customer(
                        cust.getUuid(),
                        cust.getUsername(),
                        cust.getPassword(),
                        "Customer",
                        false,
                        cust.getId(),
                        cust.getBalance(),
                        cust.getPin(),
                        cust.isAccountFrozen(),
                        cust.getCreditScore()
                )).toArray(Customer[]::new);
        return customers;
    }

    @Override
    public void createAccount(User user, String type) throws Exception {
        if (type.equals("customer")) {
            CustomerData customerData = new CustomerData();
            customerData.setUuid(user.getUuid());
            customerData.setUsername(user.getUsername());
            customerData.setPassword(user.getPassword());
            customerData.setBalance(0.0);
            customerData.setPin(1234);
            customerData.setAccountFrozen(false);
            customerData.setCreditScore(1000);
            if (user.getId() != 0) {
                customerData.setId(user.getId());
            }
            log.info("Customer {} created", user);
            custDataRepository.save(customerData);
        } else if (type.equals("admin")) {
            AdministratorData administratorData = new AdministratorData();
            administratorData.setUuid(user.getUuid());
            administratorData.setUsername(user.getUsername());
            administratorData.setPassword(user.getPassword());
            log.info("Admin {} created", user);
            adminDataRepository.save(administratorData);
        }


    }

    @Override
    public User updateAccount(User user) throws Exception {
        Optional<CustomerData> existingData = custDataRepository.findByUuid(user.getUuid());
        Optional<AdministratorData> adminData = adminDataRepository.findById(user.getId());
        if (existingData.isPresent()) {
            CustomerData data = existingData.get();
            Customer customerUser = (Customer) user;

            data.setBalance(customerUser.getBalance());
            data.setPin(customerUser.getPin());
            data.setAccountFrozen(customerUser.isAccountFrozen());
            data.setCreditScore(customerUser.getCreditScore());
            data.setId(customerUser.getId());
            data.setUsername(customerUser.getUsername());
            data.setPassword(customerUser.getPassword());
            data.setLastUpdated(now());
            custDataRepository.save(data);
            log.info("Customer {} info updated.", user);
        } else if (adminData.isPresent()){
            AdministratorData data = adminData.get();
            Administrator admin = (Administrator) user;

            data.setLastUpdated(now());
            data.setPassword(admin.getPassword());
            data.setUsername(admin.getUsername());
            adminDataRepository.save(data);
            log.info("Admin {} info updated.", user);
        }
        return user;
    }

    @Override
    public void deleteAccount(User user) throws Exception {
        Optional<CustomerData> existingData = custDataRepository.findByUuid(user.getUuid());
        Optional<AdministratorData> adminData = adminDataRepository.findByUuid(user.getUuid());

        if (adminData.isEmpty() && existingData.isEmpty()){
            log.error("Cannot delete: Users not found.");
            throw new Exception("Cannot delete: Users not found.");
        }

        log.info("User {} deleted.", user);
        existingData.ifPresent(customerData -> custDataRepository.deleteById(customerData.getId()));
        adminData.ifPresent(administratorData -> adminDataRepository.deleteById(administratorData.getId()));
    }

    @Override
    public Administrator getAdminAccount(Integer id) throws Exception {
        Optional<AdministratorData> existingData = adminDataRepository.findById(id);
        if (existingData.isEmpty()) {
            log.error("Cannot get: Admin not found.");
            throw new Exception("Cannot get: Admin not found.");
        }

        Administrator admin = new Administrator(existingData.get().getUuid(),
                "Admin",
                true,
                existingData.get().getUsername(),
                existingData.get().getPassword(),
                existingData.get().getId());
        return admin;
    }

    @Override
    public Customer getAccount(Integer id) throws Exception {
        Optional<CustomerData> existingData = custDataRepository.findById(id);
        if (existingData.isEmpty()) {
            log.error("Cannot get: Customer not found.");
            throw new Exception("Cannot get: Customer not found.");
        }

        Customer customer  = new Customer(
                existingData.get().getUuid(),
                existingData.get().getUsername(),
                existingData.get().getPassword(),
                "Customer", false,
                existingData.get().getId(),
                existingData.get().getBalance(),
                existingData.get().getPin(),
                existingData.get().isAccountFrozen(),
                existingData.get().getCreditScore()
        );
        return customer;
    }

    @Override
    public Administrator[] getAdmins() throws Exception {
        List<AdministratorData> data = adminDataRepository.findAll();

        Administrator[] admins = data.stream().map(
                cust -> new Administrator(
                        cust.getUuid(),
                        "Admin",
                        true,
                        cust.getUsername(),
                        cust.getPassword(),
                        cust.getId()

                )).toArray(Administrator[]::new);
        return admins;
    }

    @Override
    public Administrator createAdminAccount(Administrator admin) throws Exception {
        AdministratorData admina = new AdministratorData();
        admina.setUuid(admin.getUuid());
        admina.setUsername(admin.getUsername());
        admina.setPassword(admin.getPassword());

        adminDataRepository.save(admina);
        return admin;
    }

    @Override
    public Administrator updateAdminAccount(Administrator admin) throws Exception {
        Optional<AdministratorData> existingData = adminDataRepository.findByUuid(admin.getUuid());

        if (existingData.isEmpty()) {
            log.error("Cannot update: Admin not found.");
            throw new Exception("Cannot update: Admin not found.");
        }

        existingData.get().setId(admin.getId());
        existingData.get().setUsername(admin.getUsername());
        existingData.get().setPassword(admin.getPassword());
        adminDataRepository.save(existingData.get());
        return admin;
    }

    @Override
    public double checkBalance(Customer cust) throws Exception {
        Optional<CustomerData> existingData = custDataRepository.findByUuid(cust.getUuid());
        if (existingData.isEmpty()) {
            log.error("Cannot get: Customer not found.");
            throw new Exception("Cannot get: Customer not found.");
        }

        return existingData.get().getBalance();
    }

    @Override
    public void depositMoney(Customer cust, double amount) throws Exception {
        Optional<CustomerData> existingData = custDataRepository.findByUuid(cust.getUuid());

        if (existingData.isEmpty()) {
            log.error("Cannot update: Customer not found.");
            throw new Exception("Cannot update: Customer not found.");
        }

        if (existingData.get().isAccountFrozen()) {
            log.error("Operation denied: Account is frozen.");
            throw new Exception("Operation denied: Account is frozen.");
        }

        existingData.get().setBalance(cust.getBalance() + amount);
        custDataRepository.save(existingData.get());
        log.info("Php {} deposited to account {}.", amount,
                existingData.get().getUsername());
    }

    @Override
    public void withdrawMoney(Customer cust, double amount) throws Exception {
        Optional<CustomerData> existingData = custDataRepository.findByUuid(cust.getUuid());

        if (existingData.isEmpty()) {
            log.error("Cannot update: Customer not found.");
            throw new Exception("Cannot update: Customer not found.");
        }

        if (existingData.get().isAccountFrozen()) {
            log.error("Operation denied: Account is frozen.");
            throw new Exception("Operation denied: Account is frozen.");
        }

        existingData.get().setBalance(cust.getBalance() - amount);
        custDataRepository.save(existingData.get());

        log.info("Php {} withdrawn from account {}.", amount,
                existingData.get().getUsername());
    }

    @Override
    public void transferMoney(Customer source, Customer destination, double amt) throws Exception {
        Optional<CustomerData> sourceData = custDataRepository.findByUuid(source.getUuid());
        Optional<CustomerData> destData = custDataRepository.findByUuid(destination.getUuid());
        if (sourceData.isEmpty() || destData.isEmpty()) {
            log.error("Cannot update: Customer not found.");
            throw new Exception("Cannot update: Customer not found.");
        }

        if (sourceData.get().isAccountFrozen()) {
            log.error("Operation denied: The source account {} is frozen.", sourceData.get().getUsername());
            throw new Exception(
                    String.format("Operation denied: The source account %s is frozen.", sourceData.get().getUsername())
            );
        }

        if (destData.get().isAccountFrozen()) {
            log.error("Operation denied: The destination account {} is frozen.", destData.get().getUsername());
            throw new Exception(
                    String.format("Operation denied: The destination account %s is frozen.", destData.get().getUsername())
            );
        }

        destData.get().setBalance(destData.get().getBalance() + amt);
        custDataRepository.save(destData.get());
        sourceData.get().setBalance(source.getBalance() - amt);
        custDataRepository.save(sourceData.get());

        log.info("Php {} transferred from account {} to account {}.", amt,
                sourceData.get().getUsername(), destData.get().getUsername());
    }

    @Override
    public List<LoanPlan> getLoanPlans() throws Exception {
        List<LoanPlan> plans = new ArrayList<>();
        for (LoanPlanData data : loanPlanDataRepository.findAll()) {
            plans.add(new LoanPlan(
                    data.getId(),
                    data.getName(),
                    data.getDuration(),
                    data.getMaxAmount(),
                    data.getInterestRate()
            ));
        }
        return plans;
    }

    @Override
    public void createLoanPlan(LoanPlan plan) throws Exception {
        LoanPlanData data = new LoanPlanData();
        data.setName(plan.getName());
        data.setDuration(plan.getDuration());
        data.setMaxAmount(plan.getMaxAmount());
        data.setInterestRate(plan.getInterestRate());
        loanPlanDataRepository.save(data);
    }

    @Override
    public void deleteLoanPlan(int planId) throws Exception {
        loanPlanDataRepository.deleteById(planId);
    }

    @Override
    public List<Loan> getLoans(Customer cust) throws Exception {
        List<Loan> loans = new ArrayList<>();
        Optional<CustomerData> custData = custDataRepository.findByUuid(cust.getUuid());
        if (custData.isEmpty()) {
            log.error("Customer not found.");
            throw new Exception("Customer not found.");
        }
        Iterable<LoanData> allLoans = loanDataRepository.findAll();
        for (LoanData data : allLoans) {
            if (data.getUserID() == custData.get().getId()) {
                Loan l = new Loan();
                l.setId(data.getId());
                l.setUserID(data.getUserID());
                l.setLoanMoney(data.getLoanMoney());
                l.setMoneyLeftToRepay(data.getMoneyLeftToRepay());
                l.setDuration(data.getDuration());
                l.setInterestRate(data.getInterestRate());
                l.setInstallmentRate(data.getInstallmentRate());
                loans.add(l);
            }
        }
        return loans;
    }

    @Override
    public void applyForLoan(Customer cust, LoanPlan plan, double amount) throws Exception {
        Optional<CustomerData> existingCust = custDataRepository.findByUuid(cust.getUuid());
        if (existingCust.isEmpty()) {
            log.error("Customer not found.");
            throw new Exception("Customer not found.");
        }
        CustomerData customer = existingCust.get();
        if (customer.isAccountFrozen()) {
            log.error("Operation denied: Account is frozen.");
            throw new Exception("Operation denied: Account is frozen.");
        }
        if (amount <= 0) {
            log.error("Invalid loan amount: Must be greater than 0.");
            throw new Exception("Invalid loan amount: Must be greater than 0.");
        }
        if (amount > plan.getMaxAmount()) {
            log.error("Invalid loan amount: Exceeds the plan limit of {}", plan.getMaxAmount());
            throw new Exception("Invalid loan amount: Exceeds the plan limit of " + plan.getMaxAmount());
        }

        // BigDecimal precision calculations
        BigDecimal principal = BigDecimal.valueOf(amount);
        BigDecimal rate = BigDecimal.valueOf(plan.getInterestRate()).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
        BigDecimal totalRepayable = principal.multiply(BigDecimal.valueOf(1).add(rate)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal installment = totalRepayable.divide(BigDecimal.valueOf(plan.getDuration()), 2, RoundingMode.HALF_UP);

        // Update customer balance
        BigDecimal currentBalance = BigDecimal.valueOf(customer.getBalance());
        customer.setBalance(currentBalance.add(principal).setScale(2, RoundingMode.HALF_UP).doubleValue());
        custDataRepository.save(customer);

        // Save loan data
        LoanData loan = new LoanData();
        loan.setUserID(customer.getId());
        loan.setLoanMoney(amount);
        loan.setMoneyLeftToRepay(totalRepayable.doubleValue());
        loan.setDuration(plan.getDuration());
        loan.setInterestRate(plan.getInterestRate());
        loan.setInstallmentRate(installment.doubleValue());
        loanDataRepository.save(loan);
    }

    @Override
    public void payLoan(Customer cust, Loan loan, double amount) throws Exception {
        Optional<CustomerData> existingCust = custDataRepository.findByUuid(cust.getUuid());
        if (existingCust.isEmpty()) {
            log.error("Customer not found.");
            throw new Exception("Customer not found.");
        }
        CustomerData customer = existingCust.get();
        if (customer.isAccountFrozen()) {
            log.error("Operation denied: Account is frozen.");
            throw new Exception("Operation denied: Account is frozen.");
        }
        if (amount <= 0) {
            log.error("Payment amount must be positive.");
            throw new Exception("Payment amount must be positive.");
        }
        Optional<LoanData> existingLoan = loanDataRepository.findById(loan.getId());
        if (existingLoan.isEmpty()) {
            log.error("Loan not found.");
            throw new Exception("Loan not found.");
        }
        LoanData loanData = existingLoan.get();
        if (loanData.getMoneyLeftToRepay() <= 0) {
            log.error("This loan has already been fully repaid.");
            throw new Exception("This loan has already been fully repaid.");
        }

        BigDecimal payAmount = BigDecimal.valueOf(amount);
        BigDecimal leftToRepay = BigDecimal.valueOf(loanData.getMoneyLeftToRepay());
        if (payAmount.compareTo(leftToRepay) > 0) {
            payAmount = leftToRepay;
        }

        BigDecimal balance = BigDecimal.valueOf(customer.getBalance());
        if (balance.compareTo(payAmount) < 0) {
            log.error("Insufficient funds to make this loan payment.");
            throw new Exception("Insufficient funds to make this loan payment.");
        }

        // Deduct from customer balance
        customer.setBalance(balance.subtract(payAmount).setScale(2, RoundingMode.HALF_UP).doubleValue());
        custDataRepository.save(customer);

        // Deduct from loan outstanding amount
        BigDecimal newLeftToRepay = leftToRepay.subtract(payAmount).setScale(2, RoundingMode.HALF_UP);
        loanData.setMoneyLeftToRepay(newLeftToRepay.doubleValue());
        loanDataRepository.save(loanData);
    }

    @Override
    public int getCreditScore(Customer cust) throws Exception {
        Optional<CustomerData> existingCust = custDataRepository.findByUuid(cust.getUuid());
        if (existingCust.isEmpty()) {
            log.error("Customer not found.");
            throw new Exception("Customer not found.");
        }
        return existingCust.get().getCreditScore();
    }

    @Override
    public void updateCreditScore(Customer cust, int score) throws Exception {
        Optional<CustomerData> existingCust = custDataRepository.findByUuid(cust.getUuid());
        if (existingCust.isEmpty()) {
            log.error("Customer not found.");
            throw new Exception("Customer not found.");
        }
        existingCust.get().setCreditScore(score);
        custDataRepository.save(existingCust.get());
    }

    @Override
    public List<Loan> getAllActiveLoans() throws Exception {
        List<Loan> loans = new ArrayList<>();
        for (LoanData data : loanDataRepository.findAll()) {
            if (data.getMoneyLeftToRepay() > 0) {
                Loan l = new Loan();
                l.setId(data.getId());
                l.setUserID(data.getUserID());
                l.setLoanMoney(data.getLoanMoney());
                l.setMoneyLeftToRepay(data.getMoneyLeftToRepay());
                l.setDuration(data.getDuration());
                l.setInterestRate(data.getInterestRate());
                l.setInstallmentRate(data.getInstallmentRate());
                loans.add(l);
            }
        }
        return loans;
    }

    @Override
    public List<Loan> searchActiveLoans(String query) throws Exception {
        List<Loan> activeLoans = getAllActiveLoans();
        if (query == null || query.trim().isEmpty()) {
            return activeLoans;
        }
        String q = query.trim().toLowerCase();
        List<Loan> results = new ArrayList<>();
        for (Loan loan : activeLoans) {
            // Find customer details to check ID/username match
            Optional<CustomerData> custData = custDataRepository.findById(loan.getUserID());
            if (custData.isPresent()) {
                CustomerData customer = custData.get();
                String userIdStr = String.valueOf(customer.getId());
                String username = customer.getUsername().toLowerCase();
                if (userIdStr.contains(q) || username.contains(q)) {
                    results.add(loan);
                }
            }
        }
        return results;
    }
}
