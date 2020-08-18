package com.alexquasar.rest_service.component;

import com.alexquasar.rest_service.dto.FormType;
import com.alexquasar.rest_service.entity.Bank;
import com.alexquasar.rest_service.entity.Customer;
import com.alexquasar.rest_service.entity.Deposit;
import com.alexquasar.rest_service.repository.DepositRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

@Component
public class JPARunner implements CommandLineRunner {

    private DepositRepository depositRepository;

    public JPARunner(DepositRepository depositRepository) {
        this.depositRepository = depositRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        LocalDate date = LocalDate.of(2020, 8, 1);
        addDeposit(date);
        date = LocalDate.of(2020, 8, 2);
        addDeposit(date);
        date = LocalDate.of(2020, 8, 3);
        addDeposit(date);
    }

    private void addDeposit(LocalDate date) {
        Optional<Deposit> optionalDeposit = depositRepository.findByDate(date);
        if (!optionalDeposit.isPresent()) {
            Deposit deposit = new Deposit();
            deposit.setCustomer(getCustomer());
            deposit.setBank(getBank());
            deposit.setDate(date);
            deposit.setPercent(10);
            deposit.setDateMonths(24);

            depositRepository.save(deposit);
        }
    }

    private Customer getCustomer() {
        Customer customer = new Customer();
        customer.setName("TestName");
        customer.setShortName("TestShortName");
        customer.setAddress("st. Test");
        customer.setLegalInformation(FormType.FIRST_TYPE);

        return customer;
    }

    private Bank getBank() {
        Bank bank = new Bank();
        bank.setName("TestBank");
        bank.setIdentificationCode(999999999);

        return bank;
    }
}
