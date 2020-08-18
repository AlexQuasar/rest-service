package com.alexquasar.rest_service.dto;

import com.alexquasar.rest_service.entity.Deposit;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class DepositDTO {

    public DepositDTO(Deposit deposit) {
        this.id = deposit.getId();
        this.customer = new CustomerDTO(deposit.getCustomer());
        this.bank = new BankDTO(deposit.getBank());
        this.date = deposit.getDate();
        this.percent = deposit.getPercent();
        this.dateMonths = deposit.getDateMonths();
    }

    private Long id;
    private CustomerDTO customer;
    private BankDTO bank;
    private LocalDate date;
    private Integer percent;
    private Integer dateMonths;
}
