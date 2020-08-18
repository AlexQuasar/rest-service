package com.alexquasar.rest_service.service;

import com.alexquasar.rest_service.dto.BankDTO;
import com.alexquasar.rest_service.dto.CustomerDTO;
import com.alexquasar.rest_service.dto.DepositDTO;
import com.alexquasar.rest_service.dto.DepositFilter;
import com.alexquasar.rest_service.dto.FormType;
import com.alexquasar.rest_service.entity.Deposit;
import com.alexquasar.rest_service.repository.DepositRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DepositServiceTest {

    @Mock
    DepositRepository depositRepository;

    @InjectMocks
    DepositService depositService;

    @Test
    public void addDeposit() {
        DepositDTO deposit = getDeposit();
        assertTrue(depositService.addDeposit(deposit));
        verify(depositRepository).save(any(Deposit.class));
    }

    @Test
    public void changeDeposit() {
        DepositDTO deposit = getDeposit();

        when(depositRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertFalse(depositService.changeDeposit(1L, deposit));

        when(depositRepository.findById(anyLong())).thenReturn(Optional.of(new Deposit(deposit)));
        assertTrue(depositService.changeDeposit(1L, deposit));
        verify(depositRepository).save(any(Deposit.class));
    }

    @Test
    public void deleteDeposit() {
        DepositDTO deposit = getDeposit();
        assertTrue(depositService.addDeposit(deposit));

        when(depositRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertFalse(depositService.deleteDeposit(1L));

        when(depositRepository.findById(anyLong())).thenReturn(Optional.of(new Deposit(deposit)));
        assertTrue(depositService.deleteDeposit(1L));
        verify(depositRepository).delete(any(Deposit.class));
    }

    @Test
    public void getFilteredAndSortDeposit() {
        try {
            Field field = depositService.getClass().getDeclaredField("inHibernate");
            field.setAccessible(true);
            field.set(depositService, true);
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            ex.printStackTrace();
            return;
        }

        DepositFilter depositFilter = getFilter();

        Set<Deposit> deposits = new HashSet<>();
        for (int i = 0; i < 5; i++) {
            DepositDTO deposit = getDeposit();
            deposit.setDate(deposit.getDate().plusDays(i));
            deposits.add(new Deposit(deposit));
        }

        when(depositRepository.findAllByCustomer(anyLong())).thenReturn(deposits);

        Set<DepositDTO> findDeposits = depositService.getFilteredAndSortDeposit(depositFilter);
        assertEquals(2, findDeposits.size());
    }

    private DepositDTO getDeposit() {
        CustomerDTO customer = new CustomerDTO();
        customer.setName("Name1");
        customer.setShortName("Nm1");
        customer.setAddress("st. Foo");
        customer.setLegalInformation(FormType.FIRST_TYPE);

        BankDTO bank = new BankDTO();
        bank.setName("Bank1");
        bank.setIdentificationCode(111222333);

        DepositDTO deposit = new DepositDTO();
        deposit.setCustomer(customer);
        deposit.setBank(bank);
        deposit.setDate(LocalDate.now());
        deposit.setPercent(5);
        deposit.setDateMonths(12);

        return deposit;
    }

    private DepositFilter getFilter() {
        DepositFilter depositFilter = new DepositFilter();
        depositFilter.setCustomerId(1L);
        LocalDate now = LocalDate.now();
        depositFilter.setDateFrom(now.minusDays(1));
        depositFilter.setDateTo(now.plusDays(1));
        depositFilter.setDate(now);
        Integer percent = 5;
        depositFilter.setPercentFrom(percent - 2);
        depositFilter.setPercentTo(percent + 2);
        depositFilter.setPercent(percent);
        Integer dateMoths = 12;
        depositFilter.setDateMonthsFrom(dateMoths - 5);
        depositFilter.setDateMonthsTo(dateMoths + 5);
        depositFilter.setDateMonths(dateMoths);

        return depositFilter;
    }
}