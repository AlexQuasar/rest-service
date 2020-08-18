package com.alexquasar.rest_service.repository;

import com.alexquasar.rest_service.entity.Deposit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

public interface DepositRepository extends JpaRepository<Deposit, Long> {

    Optional<Deposit> findByDate(LocalDate date);

    Set<Deposit> findAllByCustomer(Long customerId);
    Set<Deposit> findAllByBank(Long bankId);

    Set<Deposit> findAllByDateBetween(LocalDate dateFrom, LocalDate dateTo);
    Set<Deposit> findAllByDate(LocalDate date);

    Set<Deposit> findAllByPercentBetween(Integer percentFrom, Integer percentTo);
    Set<Deposit> findAllByPercent(Integer percent);

    Set<Deposit> findAllByDateMonthsBetween(Integer dateMonthsFrom, Integer dateMonthsTo);
    Set<Deposit> findAllByDateMonths(Integer dateMonths);
}
