package com.alexquasar.rest_service.dto;

import com.alexquasar.rest_service.entity.Customer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CustomerDTO {

    public CustomerDTO(Customer customer) {
        this.id = customer.getId();
        this.name = customer.getName();
        this.shortName = customer.getShortName();
        this.address = customer.getAddress();
        this.legalInformation = customer.getLegalInformation();
    }

    private Long id;
    private String name;
    private String shortName;
    private String address;
    private FormType legalInformation;
}
