package com.alexquasar.rest_service.component;

import com.alexquasar.rest_service.dto.DepositFilter;
import com.alexquasar.rest_service.entity.Deposit;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.ConfigurableMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.Set;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;

@SpringBootTest
@RunWith(SpringRunner.class)
public class GeneratorSQLTest {

    MockMvc mockMvc;

    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    GeneratorSQL generatorSQL;

    @Autowired
    ObjectMapper mapper;

    Logger log = Logger.getLogger(GeneratorSQLTest.class.getName());

    @Before
    public void setUp() throws Exception {
        ConfigurableMockMvcBuilder builder =
                MockMvcBuilders.webAppContextSetup(this.webApplicationContext);
        this.mockMvc = builder.build();
    }

    @Test
    public void generateQueryDate() {
        DepositFilter depositFilter = getFilter();
        Set<Deposit> deposits = generatorSQL.generateQuery(depositFilter);

        assertEquals(2, deposits.size());
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