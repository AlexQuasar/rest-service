package com.alexquasar.rest_service.entity;

import com.alexquasar.rest_service.dto.CustomerDTO;
import com.alexquasar.rest_service.dto.FormType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "customer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    public Customer(CustomerDTO customer) {
        this.name = customer.getName();
        this.shortName = customer.getShortName();
        this.address = customer.getAddress();
        this.legalInformation = customer.getLegalInformation();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "short_name")
    private String shortName;

    private String address;

    @Enumerated(EnumType.STRING)
    @Column(name = "legal_information")
    private FormType legalInformation;
}
