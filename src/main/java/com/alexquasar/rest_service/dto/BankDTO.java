package com.alexquasar.rest_service.dto;

import com.alexquasar.rest_service.entity.Bank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BankDTO {

    public BankDTO(Bank bank) {
        this.id = bank.getId();
        this.name = bank.getName();
        this.identificationCode = bank.getIdentificationCode();
    }

    private Long id;
    private String name;
    private Integer identificationCode;
}
