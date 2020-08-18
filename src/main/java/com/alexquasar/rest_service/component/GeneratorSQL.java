package com.alexquasar.rest_service.component;

import com.alexquasar.rest_service.dto.DepositFilter;
import com.alexquasar.rest_service.dto.FormType;
import com.alexquasar.rest_service.entity.Bank;
import com.alexquasar.rest_service.entity.Customer;
import com.alexquasar.rest_service.entity.Deposit;
import com.alexquasar.rest_service.exception.ServiceException;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Component
public class GeneratorSQL {

    @Value("${datasource.jdbc-url}")
    private String url;

    @Value("${datasource.username}")
    private String user;

    @Value("${datasource.password}")
    private String password;

    private ComboPooledDataSource comboPooledDataSource = new ComboPooledDataSource();

    @PostConstruct
    void init() {
        comboPooledDataSource.setJdbcUrl(url);
        comboPooledDataSource.setUser(user);
        comboPooledDataSource.setPassword(password);
    }

    public Set<Deposit> generateQuery(DepositFilter depositFilter) {
        if (depositFilter == null) {
            throw new ServiceException("no filter set", HttpStatus.NO_CONTENT);
        }

        try {
            return getFilteredDeposit(depositFilter);
        } catch (SQLException ex) {
            throw new ServiceException(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    private Set<Deposit> getFilteredDeposit(DepositFilter depositFilter) throws SQLException {
        Connection connection = null;
        try {
            connection = comboPooledDataSource.getConnection();

            if (connection == null) {
                System.out.println("No database connection!");
                System.exit(0);
            }

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(generateSQL(depositFilter));

            Set<Deposit> deposits = new HashSet<>();
            while (resultSet.next()) {
                addDepositInCollection(deposits, resultSet);
            }

            statement.close();
            return deposits;
        } catch (SQLException ex) {
            throw new ServiceException(ex.getMessage(), HttpStatus.BAD_REQUEST);
        } finally {
            if (connection != null){
                connection.close();
            }
        }
    }

    private String generateSQL(DepositFilter depositFilter) {
        StringBuilder query = new StringBuilder(
                "select d.id, d.customer_id, d.bank_id, d.open_date, d.percent, d.date_months,\n" +
                "c.name c_name, c.short_name, c.address, c.legal_information,\n" +
                "b.name b_name, b.identification_code from deposit d\n" +
                "inner join customer c on d.customer_id = c.id\n" +
                "inner join bank b on d.bank_id = b.id\n");

        generateCondition(query, depositFilter);
        generateSort(query, depositFilter);

        return query.toString();
    }

    private void generateCondition(StringBuilder query, DepositFilter depositFilter) {
        if (depositFilter.getCustomerId() != null) {
            setCustomer(query, depositFilter);
        }
        if (depositFilter.getBankId() != null) {
            setBank(query, depositFilter);
        }
        if (depositFilter.getDateFrom() != null
                || depositFilter.getDateTo() != null
                || depositFilter.getDate() != null) {
            setDate(query, depositFilter);
        }
        if (depositFilter.getPercentFrom() != null
                || depositFilter.getPercentTo() != null
                || depositFilter.getPercent() != null) {
            setPercent(query, depositFilter);
        }
        if (depositFilter.getDateMonthsFrom() != null
                || depositFilter.getDateMonthsTo() != null
                || depositFilter.getDateMonths() != null) {
            setDateMonths(query, depositFilter);
        }
    }

    // сортировка реализована только по возрастанию
    private void generateSort(StringBuilder query, DepositFilter depositFilter) {
        if (depositFilter.isSortCustomer()) {
            addSort(query, "customer");
        }
        if (depositFilter.isSortBank()) {
            addSort(query, "bank");
        }
        if (depositFilter.isSortDate()) {
            addSort(query, "open_date");
        }
        if (depositFilter.isSortPercent()) {
            addSort(query, "percent");
        }
        if (depositFilter.isSortDateMonths()) {
            addSort(query, "date_months");
        }
    }

    private void setCustomer(StringBuilder query, DepositFilter depositFilter) {
        addCondition(query, "d.customer_id = " + depositFilter.getCustomerId());
    }

    private void setBank(StringBuilder query, DepositFilter depositFilter) {
        addCondition(query, "d.bank_id = " + depositFilter.getBankId());
    }

    private void setDate(StringBuilder query, DepositFilter depositFilter) {
        LocalDate dateFrom = depositFilter.getDateFrom();
        LocalDate dateTo = depositFilter.getDateTo();
        LocalDate date = depositFilter.getDate();

        if (dateFrom != null) {
            addCondition(query, "d.open_date >= '" + dateFrom + "'");
        }
        if (dateTo != null) {
            addCondition(query, "d.open_date <= '" + dateTo + "'");
        }
        if (date != null && dateFrom == null && dateTo == null) {
            addCondition(query, "d.open_date = '" + date + "'");
        }
    }

    private void setPercent(StringBuilder query, DepositFilter depositFilter) {
        Integer percentFrom = depositFilter.getPercentFrom();
        Integer percentTo = depositFilter.getPercentTo();
        Integer percent = depositFilter.getPercent();

        if (percentFrom != null) {
            addCondition(query, "d.percent >= " + percentFrom);
        }
        if (percentTo != null) {
            addCondition(query, "d.percent <= " + percentTo);
        }
        if (percent != null && percentFrom == null && percentTo == null) {
            addCondition(query, "d.percent = " + percent);
        }
    }

    private void setDateMonths(StringBuilder query, DepositFilter depositFilter) {
        Integer dateMonthsFrom = depositFilter.getDateMonthsFrom();
        Integer dateMonthsTo = depositFilter.getDateMonthsTo();
        Integer dateMonths = depositFilter.getDateMonths();

        if (dateMonthsFrom != null) {
            addCondition(query, "d.date_months >= " + dateMonthsFrom);
        }
        if (dateMonthsTo != null) {
            addCondition(query, "d.date_months <= " + dateMonthsTo);
        }
        if (dateMonths != null && dateMonthsFrom == null && dateMonthsTo == null) {
            addCondition(query, "d.date_months = " + dateMonths);
        }
    }

    private void addCondition(StringBuilder query, String condition) {
        if (containWhere(query)) {
            query.append("and ");
        } else {
            query.append("where ");
        }
        query.append(condition).append("\n");
    }

    private boolean containWhere(StringBuilder query) {
        return query.indexOf("where") > 0;
    }

    private void addSort(StringBuilder query, String sort) {
        if (containOrderBy(query)) {
            query.append(",\n");
        } else {
            query.append("order by ");
        }
        query.append(sort);
    }

    private boolean containOrderBy(StringBuilder query) {
        return query.indexOf("order by") > 0;
    }

    private void addDepositInCollection(Set<Deposit> deposits, ResultSet res) throws SQLException {
        Customer customer = new Customer(
                Long.parseLong(res.getString("customer_id")),
                res.getString("c_name"),
                res.getString("short_name"),
                res.getString("address"),
                FormType.valueOf(res.getString("legal_information"))
        );
        Bank bank = new Bank(
                Long.parseLong(res.getString("bank_id")),
                res.getString("b_name"),
                Integer.parseInt(res.getString("identification_code"))
        );
        Deposit deposit = new Deposit(
                Long.parseLong(res.getString("id")),
                customer,
                bank,
                LocalDate.parse(res.getString("open_date")),
                Integer.parseInt(res.getString("percent")),
                Integer.parseInt(res.getString("date_months"))
        );

        deposits.add(deposit);
    }
}
