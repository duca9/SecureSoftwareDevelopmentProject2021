package com.zuehlke.securesoftwaredevelopment.repository;

import com.zuehlke.securesoftwaredevelopment.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class OrderRepository {

    private static final Logger LOG = LoggerFactory.getLogger(OrderRepository.class);
    private DataSource dataSource;

    public OrderRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    public List<Food> getMenu(int id) {
        List<Food> menu = new ArrayList<>();
        String sqlQuery = "SELECT id, name FROM food WHERE restaurantId=" + id;
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sqlQuery)) {
            while (rs.next()) {
                menu.add(createFood(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return menu;
    }

    private Food createFood(ResultSet rs) throws SQLException {
        int id = rs.getInt(1);
        String name = rs.getString(2);
        return new Food(id, name);
    }


    public void insertNewOrder(NewOrder newOrder, int userId) {
        if (newOrder != null && userId >= 0) {
            LocalDate date = LocalDate.now();
            String sqlQuery = "INSERT INTO delivery (isDone, userId, restaurantId, addressId, date, comment)" +
                    "values (FALSE, ?, ?, ?, ?, ?)";
            try (
                    Connection connection = dataSource.getConnection();
                    PreparedStatement statement = connection.prepareStatement(sqlQuery)
            ) {
                statement.setInt(1, userId);
                statement.setInt(2, newOrder.getRestaurantId());
                statement.setInt(3, newOrder.getAddress());
                statement.setDate(4, Date.valueOf(date));
                statement.setString(5, newOrder.getComment());
                statement.executeUpdate();

                insertOrderFood(newOrder);

            } catch (SQLException e) {
                LOG.error("Error while inserting new order");
            }
        } else {
            LOG.error("Input params error");
        }
    }

    public Object getAddresses(int userId) {
        List<Address> addresses = new ArrayList<>();
        String sqlQuery = "SELECT id, name FROM address WHERE userId=" + userId;
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sqlQuery)) {
            while (rs.next()) {
                addresses.add(createAddress(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return addresses;
    }

    private Address createAddress(ResultSet rs) throws SQLException {
        int id = rs.getInt(1);
        String name = rs.getString(2);
        return new Address(id, name);

    }

    private int getCurrentOrderId() {
        String sqlQuery = "SELECT MAX(id) FROM delivery";
        int retVal = -1;
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sqlQuery)
        ) {
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    retVal = rs.getInt(1);
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return retVal;
    }

    private void insertOrderFood(NewOrder newOrder) {
        if (newOrder != null) {
            int nextOrderId = getCurrentOrderId();
            if (nextOrderId != -1) {
                StringBuilder foodSqlQuery = new StringBuilder("INSERT INTO delivery_item (amount, foodId, deliveryId)" +
                        "values");
                List<Object> paramToSet = new ArrayList<>();
                String separator = "";
                for (int i = 0; i < newOrder.getItems().length; i++) {
                    FoodItem item = newOrder.getItems()[i];
                    String deliveryItem = separator + "(?, ?, ?)";
                    foodSqlQuery.append(deliveryItem);
                    separator = ",";
                    paramToSet.add(item.getAmount());
                    paramToSet.add(item.getFoodId());
                    paramToSet.add(nextOrderId);
                }
                String query = foodSqlQuery.toString();
                try (
                        Connection connection = dataSource.getConnection();
                        PreparedStatement statement = connection.prepareStatement(query)
                ) {
                    int paramIndex = 1;
                    for (Object oneParam : paramToSet) {
                        statement.setObject(paramIndex++, oneParam);
                    }
                    statement.executeUpdate();
                } catch (SQLException exception) {
                    LOG.error("Error while inserting food order");
                }
            } else {
                LOG.error("Error while getting current order id");
            }
        } else {
            LOG.error("Order is null");
        }
    }
}
