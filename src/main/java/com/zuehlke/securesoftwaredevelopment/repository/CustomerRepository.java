package com.zuehlke.securesoftwaredevelopment.repository;

import com.zuehlke.securesoftwaredevelopment.config.AuditLogger;
import com.zuehlke.securesoftwaredevelopment.config.EntityChanged;
import com.zuehlke.securesoftwaredevelopment.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CustomerRepository {

    private static final Logger LOG = LoggerFactory.getLogger(CustomerRepository.class);
    private static final AuditLogger auditLogger = AuditLogger.getAuditLogger(CustomerRepository.class);

    private final DataSource dataSource;

    public CustomerRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Customer> getCustomers() {
        List<com.zuehlke.securesoftwaredevelopment.domain.Customer> customers = new ArrayList<>();
        String query = "SELECT id, username FROM users";
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(query)) {
            while (rs.next()) {
                tryToAddNewCustomer(customers, rs);
            }
        } catch (SQLException e) {
            LOG.warn("Getting customers failed");
        }
        return customers;
    }

    private void tryToAddNewCustomer(List<Customer> customers, ResultSet rs) {
        try {
            customers.add(createCustomer(rs));
        } catch (SQLException e) {
            LOG.warn("Creating customer from result set failed");
        }
    }

    private Customer createCustomer(ResultSet rs) throws SQLException {
        return new Customer(rs.getInt(1), rs.getString(2));
    }


    public Customer getCustomer(String id) {
        String sqlQuery = "SELECT id, username, password FROM users WHERE id=" + id;
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sqlQuery)) {
            if (rs.next()) {
                return createCustomerWithPassword(rs);
            }
        } catch (SQLException e) {
            LOG.warn(String.format("Getting customer failed for customer with id %s", id));

        }
        return null;
    }

    private Customer createCustomerWithPassword(ResultSet rs) {
        Customer retCustomer = null;
        try {
            int id = rs.getInt(1);
            String username = rs.getString(2);
            String password = rs.getString(3);
            retCustomer = new Customer(id, username, password);
        } catch (SQLException ex) {
            LOG.warn("Creation of customer with password from result set failed");
        }
        return retCustomer;
    }


    public void deleteCustomer(String id) {
        String query = "DELETE FROM users WHERE id=" + id;
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()
        ) {
            Customer customer = getCustomer(id);
            statement.executeUpdate(query);
            LOG.info("Customer with id {} successfully deleted", id);
            auditLogger.auditChange(
                    new EntityChanged(
                            "customer.Delete",
                            String.valueOf(customer),
                            "--deleted--"));
        } catch (SQLException e) {
            LOG.warn(String.format("Deleting customer failed for customer with id %s", id));
        }
    }

    public void updateCustomer(CustomerUpdate customerUpdate) {
        String query = "UPDATE users SET username = '" + customerUpdate.getUsername() + "', password='" + customerUpdate.getPassword() + "' WHERE id =" + customerUpdate.getId();
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()
        ) {
            Customer oldCustomer = getCustomer(String.valueOf(customerUpdate.getId()));
            statement.executeUpdate(query);
            LOG.info("Customer with id {} updated successfully", customerUpdate.getId());
            auditLogger.auditChange(
                    new EntityChanged("customer.update",
                            String.valueOf(oldCustomer),
                            String.valueOf(customerUpdate)));
        } catch (SQLException e) {
            LOG.warn(String.format("Updating customer failed for customer with id %s", customerUpdate.getId()));
        }
    }

    public List<Address> getAddresses(String id) {
        String sqlQuery = "SELECT id, name FROM address WHERE userId=" + id;
        List<Address> addresses = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sqlQuery)) {

            while (rs.next()) {
                addresses.add(createAddress(rs));
            }

        } catch (SQLException e) {
            LOG.warn(String.format("Getting addresses failed for user with id %s", id));
        }
        return addresses;
    }


    public Address getAddress(String id) {
        String sqlQuery = "SELECT id, name FROM address WHERE id= ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sqlQuery)
        ) {
            statement.setString(1, id);
            try (
                    ResultSet rs = statement.executeQuery()
            ) {
                if (rs.next()) {
                    return createAddress(rs);
                }
            }
        } catch (SQLException e) {
            LOG.warn("Getting address with if {} failed", id);
        }
        return null;
    }

    private Address createAddress(ResultSet rs) throws SQLException {
        int id = rs.getInt(1);
        String name = rs.getString(2);
        return new Address(id, name);
    }

    public void deleteCustomerAddress(int id) {
        String query = "DELETE FROM address WHERE id=" + id;
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()
        ) {

            Address oldAddress = getAddress(String.valueOf(id));
            statement.executeUpdate(query);
            LOG.info("Address with id {} successfully deleted", id);
            auditLogger.auditChange(
                    new EntityChanged(
                            "customerAddress.Delete",
                            String.valueOf(oldAddress),
                            "--deleted--"));
        } catch (SQLException e) {
            LOG.warn(String.format("Deleting address failed for address with id %s", id));
        }

    }

    public void updateCustomerAddress(Address address) {
        String query = "UPDATE address SET name = '" + address.getName() + "' WHERE id =" + address.getId();
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()
        ) {
            Address oldAddress = getAddress(String.valueOf(address.getId()));
            statement.executeUpdate(query);
            LOG.info("Address with id {} successfully updated", address.getId());
            auditLogger.auditChange(
                    new EntityChanged("customerAddress.Update",
                            String.valueOf(oldAddress),
                            String.valueOf(address)));
        } catch (SQLException e) {
            LOG.warn(String.format("Updating customer address failed for address with id %s", address.getId()));
        }
    }

    public void putCustomerAddress(NewAddress newAddress) {
        String query = "INSERT INTO address (name, userId) VALUES ('" + newAddress.getName() + "' , " + newAddress.getUserId() + ")";
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()
        ) {
            statement.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);

            try (ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next()) {
                    LOG.info("Address for user with id {} successfully created", newAddress.getUserId());
                    newAddress.setId(rs.getInt(1));
                    auditLogger.auditChange(
                            new EntityChanged("customerAddress.Insert",
                                    "--not exist--",
                                    String.valueOf(newAddress)));
                }
            }


        } catch (SQLException e) {
            LOG.warn(String.format("Adding new address failed for user with id %s", newAddress.getUserId()));
        }
    }
}
