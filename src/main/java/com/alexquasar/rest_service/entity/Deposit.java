package com.alexquasar.rest_service.entity;

import com.alexquasar.rest_service.dto.DepositDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "deposit")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Deposit {

    public Deposit(DepositDTO deposit) {
        this.customer = new Customer(deposit.getCustomer());
        this.bank = new Bank(deposit.getBank());
        this.date = deposit.getDate();
        this.percent = deposit.getPercent();
        this.dateMonths = deposit.getDateMonths();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    private Customer customer;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "bank_id", referencedColumnName = "id")
    private Bank bank;

    @Column(name = "open_date")
    private LocalDate date;
    private Integer percent;

    @Column(name = "date_months")
    private Integer dateMonths;
}
