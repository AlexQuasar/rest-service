package com.alexquasar.rest_service.controller;

import com.alexquasar.rest_service.dto.BankDTO;
import com.alexquasar.rest_service.dto.CustomerDTO;
import com.alexquasar.rest_service.dto.DepositDTO;
import com.alexquasar.rest_service.dto.DepositFilter;
import com.alexquasar.rest_service.dto.FormType;
import com.alexquasar.rest_service.entity.Deposit;
import com.alexquasar.rest_service.repository.DepositRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.ConfigurableMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(SpringRunner.class)
public class DepositRestControllerTest {

    MockMvc mockMvc;

    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    DepositRepository depositRepository;

    @Autowired
    ObjectMapper mapper;

    Logger log = Logger.getLogger(DepositRestControllerTest.class.getName());
    String deposit = "/deposit";

    @Before
    public void setUp() throws Exception {
        ConfigurableMockMvcBuilder builder =
                MockMvcBuilders.webAppContextSetup(this.webApplicationContext);
        this.mockMvc = builder.build();
    }

    @Test
    @Transactional
    public void addDeposit() throws Exception {
        String addDeposit = deposit + "/addDeposit";

        DepositDTO deposit = getDeposit();
        int expectedVisitsSize = depositRepository.findAll().size() + 1;

        mockMvc.perform(put(addDeposit)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(deposit)))
        .andExpect(status().isOk());

        assertEquals(expectedVisitsSize, depositRepository.findAll().size());
    }

    @Test
    @Transactional
    public void changeDeposit() throws Exception {
        String changeDeposit = deposit + "/changeDeposit";

        DepositDTO depositDTO = getDeposit();
        Deposit deposit = new Deposit(depositDTO);
        depositRepository.save(deposit);

        int expectedVisitsSize = depositRepository.findAll().size();
        depositDTO.setDate(depositDTO.getDate().plusDays(5));
        LocalDate date = depositDTO.getDate();

        mockMvc.perform(put(changeDeposit)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .param("id", deposit.getId().toString())
                .content(mapper.writeValueAsString(depositDTO)))
        .andExpect(status().isOk());

        Optional<Deposit> optionalDeposit = depositRepository.findById(deposit.getId());
        assertTrue(optionalDeposit.isPresent());
        assertEquals(expectedVisitsSize, depositRepository.findAll().size());
        assertEquals(date, optionalDeposit.get().getDate());
    }

    @Test
    @Transactional
    public void deleteDeposit() throws Exception {
        String deleteDeposit = deposit + "/deleteDeposit";

        DepositDTO depositDTO = getDeposit();
        Deposit deposit = new Deposit(depositDTO);
        depositRepository.save(deposit);

        int expectedVisitsSize = depositRepository.findAll().size() - 1;

        mockMvc.perform(delete(deleteDeposit)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .param("id", deposit.getId().toString()))
        .andExpect(status().isOk());

        Optional<Deposit> optionalDeposit = depositRepository.findById(deposit.getId());
        assertFalse(optionalDeposit.isPresent());
        assertEquals(expectedVisitsSize, depositRepository.findAll().size());
    }

    @Test
    public void getFilteredAndSortDeposit() throws Exception {
        String getFilteredAndSortDeposit = deposit + "/getFilteredAndSortDeposit";

        DepositFilter depositFilter = getFilter();

        MvcResult result = mockMvc.perform(get(getFilteredAndSortDeposit)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(depositFilter)))
        .andExpect(status().isOk())
        .andReturn();

        String content = result.getResponse().getContentAsString();
        Set<DepositDTO> deposits = mapper.readValue(content, new TypeReference<Set<DepositDTO>>() {});

        assertEquals(2, deposits.size());
        for (DepositDTO depositDTO : deposits) {
            assertTrue(depositDTO.getDate().isAfter(depositFilter.getDateFrom()));
            assertTrue(depositDTO.getDate().isBefore(depositFilter.getDateTo().plusDays(1)));
        }
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
        LocalDate date = LocalDate.of(2020, 8, 1);
        depositFilter.setDateFrom(date.minusDays(1));
        depositFilter.setDateTo(date.plusDays(1));
        depositFilter.setDate(date);
        Integer percent = 10;
        depositFilter.setPercentFrom(percent - 2);
        depositFilter.setPercentTo(percent + 2);
        depositFilter.setPercent(percent);
        Integer dateMoths = 24;
        depositFilter.setDateMonthsFrom(dateMoths - 5);
        depositFilter.setDateMonthsTo(dateMoths + 5);
        depositFilter.setDateMonths(dateMoths);

        return depositFilter;
    }
}
