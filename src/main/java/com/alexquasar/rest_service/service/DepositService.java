package com.alexquasar.rest_service.service;

import com.alexquasar.rest_service.dto.BankDTO;
import com.alexquasar.rest_service.dto.CustomerDTO;
import com.alexquasar.rest_service.dto.DepositDTO;
import com.alexquasar.rest_service.dto.DepositFilter;
import com.alexquasar.rest_service.component.GeneratorSQL;
import com.alexquasar.rest_service.entity.Bank;
import com.alexquasar.rest_service.entity.Customer;
import com.alexquasar.rest_service.entity.Deposit;
import com.alexquasar.rest_service.repository.DepositRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DepositService {

    @Value("${settings.inHibernate}")
    private boolean inHibernate;

    @Value("${settings.maxDateMonths}")
    private Integer maxDateMonths;

    private GeneratorSQL generatorSQL;

    private DepositRepository depositRepository;

    public DepositService(GeneratorSQL generatorSQL, DepositRepository depositRepository) {
        this.generatorSQL = generatorSQL;
        this.depositRepository = depositRepository;
    }

    public boolean addDeposit(DepositDTO deposit) {
        depositRepository.save(new Deposit(deposit));
        return true;
    }

    public boolean changeDeposit(Long id, DepositDTO deposit) {
        Optional<Deposit> optionalDeposit = depositRepository.findById(id);
        if (optionalDeposit.isPresent()) {
            Deposit original = optionalDeposit.get();

            CustomerDTO customerDTO = deposit.getCustomer();
            Customer customer = original.getCustomer();
            customer.setName(customerDTO.getName());
            customer.setShortName(customerDTO.getShortName());
            customer.setAddress(customerDTO.getAddress());
            customer.setLegalInformation(customerDTO.getLegalInformation());

            BankDTO bankDTO = deposit.getBank();
            Bank bank = original.getBank();
            bank.setName(bankDTO.getName());
            bank.setIdentificationCode(bankDTO.getIdentificationCode());

            original.setDate(deposit.getDate());
            original.setPercent(deposit.getPercent());
            original.setDateMonths(deposit.getDateMonths());

            depositRepository.save(original);
            return true;
        }

        return false;
    }

    public boolean deleteDeposit(Long id) {
        Optional<Deposit> optionalDeposit = depositRepository.findById(id);
        if (optionalDeposit.isPresent()) {
            depositRepository.delete(optionalDeposit.get());
            return true;
        }

        return false;
    }

    // реализовал двумя способами:
    // 1. через hibernate и stream коллекций;
    // 2. через JDBC. в таком случае из БД выбирается гораздо меньше данных и сразу все необходимые
    public Set<DepositDTO> getFilteredAndSortDeposit(DepositFilter depositFilter) {
        // 1.
        if (inHibernate) {
            return returnDepositDTO(filterAndSortDeposit(depositFilter));
        }

        // 2.
        return returnDepositDTO(generatorSQL.generateQuery(depositFilter));
    }

    private Set<DepositDTO> returnDepositDTO(Set<Deposit> deposits) {
        Set<DepositDTO> depositsDTO = new HashSet<>();

        for (Deposit deposit : deposits) {
            depositsDTO.add(new DepositDTO(deposit));
        }

        return depositsDTO;
    }

    private Set<Deposit> filterAndSortDeposit(DepositFilter depositFilter) {
        Set<Deposit> deposits = getFilteredDeposit(depositFilter);
        deposits = sortDeposit(deposits, depositFilter);
        return deposits;
    }

    private Set<Deposit> getFilteredDeposit(DepositFilter depositFilter) {
        Set<Deposit> deposits = new HashSet<>();

        if (depositFilter.getCustomerId() != null) {
            deposits = depositRepository.findAllByCustomer(depositFilter.getCustomerId());
        }
        if (depositFilter.getBankId() != null) {
            deposits = getFilteredBank(deposits, depositFilter.getBankId());
        }
        if (depositFilter.getDateFrom() != null
                || depositFilter.getDateTo() != null
                || depositFilter.getDate() != null) {
            deposits = getFilteredDate(deposits, depositFilter.getDateFrom(),
                    depositFilter.getDateTo(), depositFilter.getDate());
        }
        if (depositFilter.getPercentFrom() != null
                || depositFilter.getPercentTo() != null
                || depositFilter.getPercent() != null) {
            deposits = getFilteredPercent(deposits, depositFilter.getPercentFrom(),
                    depositFilter.getPercentTo(), depositFilter.getPercent());
        }
        if (depositFilter.getDateMonthsFrom() != null
                || depositFilter.getDateMonthsTo() != null
                || depositFilter.getDateMonths() != null) {
            deposits = getFilteredDateMonths(deposits, depositFilter.getDateMonthsFrom(),
                    depositFilter.getDateMonthsTo(), depositFilter.getDateMonths());
        }

        return deposits;
    }

    private Set<Deposit> getFilteredBank(Set<Deposit> deposits, Long bankId) {
        if (deposits.isEmpty()) {
            return depositRepository.findAllByBank(bankId);
        }

        return deposits.stream()
                .filter(d -> d.getBank().equals(bankId))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private Set<Deposit> getFilteredDate(Set<Deposit> deposits, LocalDate dateFrom, LocalDate dateTo, LocalDate date) {
        if (date != null && dateFrom == null && dateTo == null) {
            if (deposits.isEmpty()) {
                return depositRepository.findAllByDate(date);
            }

            return deposits.stream()
                    .filter(d -> d.getDate().isEqual(date))
                    .collect(Collectors.toCollection(LinkedHashSet::new));
        }

        if (dateFrom == null) {
            dateFrom = LocalDate.of(1970, 1, 1);
        }
        if (dateTo == null) {
            dateTo = LocalDate.now();
        }

        if (deposits.isEmpty()) {
            return depositRepository.findAllByDateBetween(dateFrom, dateTo);
        }

        LocalDate finalDateFrom = dateFrom;
        LocalDate finalDateTo = dateTo;

        return deposits.stream()
                .filter(d -> d.getDate().isAfter(finalDateFrom) && d.getDate().isBefore(finalDateTo.plusDays(1)))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private Set<Deposit> getFilteredPercent(Set<Deposit> deposits, Integer percentFrom, Integer percentTo, Integer percent) {
        if (percent != null && percentFrom == null && percentTo == null) {
            if (deposits.isEmpty()) {
                return depositRepository.findAllByPercent(percent);
            }

            return deposits.stream()
                    .filter(d -> d.getPercent().equals(percent))
                    .collect(Collectors.toCollection(LinkedHashSet::new));
        }

        if (percentFrom == null) {
            percentFrom = 0;
        }
        if (percentTo == null) {
            percentTo = 100;
        }

        if (deposits.isEmpty()) {
            return depositRepository.findAllByPercentBetween(percentFrom, percentTo);
        }

        Integer finalPercentFrom = percentFrom;
        Integer finalPercentTo = percentTo;

        return deposits.stream()
                .filter(d -> d.getPercent() >= finalPercentFrom && d.getPercent() <= finalPercentTo)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private Set<Deposit> getFilteredDateMonths(Set<Deposit> deposits, Integer dateMonthsFrom, Integer dateMonthsTo, Integer dateMonths) {
        if (dateMonths != null && dateMonthsFrom == null && dateMonthsTo == null) {
            if (deposits.isEmpty()) {
                return depositRepository.findAllByDateMonths(dateMonths);
            }

            return deposits.stream()
                    .filter(d -> d.getDateMonths().equals(dateMonths))
                    .collect(Collectors.toSet());
        }

        if (dateMonthsFrom == null) {
            dateMonthsFrom = 0;
        }
        if (dateMonthsTo == null) {
            dateMonthsTo = maxDateMonths;
        }

        if (deposits.isEmpty()) {
            return depositRepository.findAllByDateMonthsBetween(dateMonthsFrom, dateMonthsTo);
        }

        Integer finalDateMonthsFrom = dateMonthsFrom;
        Integer finalDateMonthsTo = dateMonthsTo;

        return deposits.stream()
                .filter(d -> d.getDateMonths() >= finalDateMonthsFrom && d.getDateMonths() <= finalDateMonthsTo)
                .collect(Collectors.toSet());
    }

    private Set<Deposit> sortDeposit(Set<Deposit> deposits, DepositFilter depositFilter) {
        if (depositFilter.isSortCustomer()) {
            return deposits.stream()
                    .sorted(Comparator.comparing(o -> o.getCustomer().getName()))
                    .collect(Collectors.toCollection(LinkedHashSet::new));
        }
        if (depositFilter.isSortBank()) {
            return deposits.stream()
                    .sorted(Comparator.comparing(o -> o.getBank().getName()))
                    .collect(Collectors.toCollection(LinkedHashSet::new));
        }
        if (depositFilter.isSortDate()) {
            return deposits.stream()
                    .sorted(Comparator.comparing(Deposit::getDate))
                    .collect(Collectors.toCollection(LinkedHashSet::new));
        }
        if (depositFilter.isSortPercent()) {
            return deposits.stream()
                    .sorted(Comparator.comparing(Deposit::getPercent))
                    .collect(Collectors.toCollection(LinkedHashSet::new));
        }
        if (depositFilter.isSortDateMonths()) {
            return deposits.stream()
                    .sorted(Comparator.comparing(Deposit::getDateMonths))
                    .collect(Collectors.toCollection(LinkedHashSet::new));
        }

        return deposits;
    }
}
