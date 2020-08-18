package com.alexquasar.rest_service.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class DepositFilter {

    private Long customerId;
    private boolean sortCustomer;
    private Long bankId;
    private boolean sortBank;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private LocalDate date;
    private boolean sortDate;
    private Integer percentFrom;
    private Integer percentTo;
    private Integer percent;
    private boolean sortPercent;
    private Integer dateMonthsFrom;
    private Integer dateMonthsTo;
    private Integer dateMonths;
    private boolean sortDateMonths;
}
