package com.zuehlke.securesoftwaredevelopment.repository;

import com.zuehlke.securesoftwaredevelopment.config.AuditLogger;
import com.zuehlke.securesoftwaredevelopment.config.EntityChanged;
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

    private static final AuditLogger auditLogger = AuditLogger.getAuditLogger(OrderRepository.class);
    private static final Logger LOG = LoggerFactory.getLogger(OrderRepository.class);
    private final DataSource dataSource;

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
                tryToAddNewFood(menu, rs);
            }
        } catch (SQLException e) {
            LOG.warn(String.format("Getting menu for restaurant with id %s failed", id));
        }
        return menu;
    }

    private void tryToAddNewFood(List<Food> foods, ResultSet rs) {
        try {
            foods.add(createFood(rs));
        } catch (SQLException e) {
            LOG.warn("Creating food from result set failed");
        }
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
                    PreparedStatement statement = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS)
            ) {
                statement.setInt(1, userId);
                statement.setInt(2, newOrder.getRestaurantId());
                statement.setInt(3, newOrder.getAddress());
                statement.setDate(4, Date.valueOf(date));
                statement.setString(5, newOrder.getComment());
                statement.executeUpdate();
                try (ResultSet rs = statement.getGeneratedKeys()) {
                    if (rs.next()) {
                        newOrder.setId(rs.getInt(1));
                        NewOrder orderWithoutFood = new NewOrder(newOrder);
                        orderWithoutFood.setItems(null); // remove items as that is not inserted yet
                        LOG.info("New order for user with id {} inserted successfully", userId);
                        auditLogger.auditChange(
                                new EntityChanged("order.Insert",
                                        "--not exist--",
                                        String.valueOf(orderWithoutFood)));
                    } else {
                        LOG.error("Error retrieving generated keys");
                    }
                }
                insertOrderFood(newOrder);
            } catch (SQLException e) {
                LOG.warn(String.format("Creating new order for restaurant with id %s and user id %s failed", newOrder.getRestaurantId(), userId));
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
            LOG.warn(String.format("Getting addresses for user with id %s failed", userId));
        }
        return addresses;
    }

    private Address createAddress(ResultSet rs) throws SQLException {
        int id = rs.getInt(1);
        String name = rs.getString(2);
        return new Address(id, name);
    }

    private String createInsertFoodQuery(NewOrder newOrder, List<Object> paramToSet) {
        String retQuery = "";
        if (newOrder.getItems() != null) {
            StringBuilder foodSqlQuery = new StringBuilder("INSERT INTO delivery_item (amount, foodId, deliveryId)" +
                    "values");
            String separator = "";
            for (FoodItem foodItem : newOrder.getItems()) {
                String deliveryItem = separator + "(?, ?, ?)";
                foodSqlQuery.append(deliveryItem);
                separator = ",";
                paramToSet.add(foodItem.getAmount());
                paramToSet.add(foodItem.getFoodId());
                paramToSet.add(foodItem.getDeliveryId());
            }
            retQuery = foodSqlQuery.toString();
        }
        return retQuery;
    }

    private boolean isThereFoodToInsert(NewOrder newOrder) {
        return newOrder != null && newOrder.getItems() != null && newOrder.getItems().length != 0;
    }

    private void insertOrderFood(NewOrder newOrder) {
        if (isThereFoodToInsert(newOrder)) {
            List<Object> paramToSet = new ArrayList<>();
            String query = createInsertFoodQuery(newOrder, paramToSet);
            try (
                    Connection connection = dataSource.getConnection();
                    PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)
            ) {
                int paramIndex = 1;
                for (Object oneParam : paramToSet) {
                    statement.setObject(paramIndex++, oneParam);
                }
                statement.executeUpdate();
                try (ResultSet rs = statement.getGeneratedKeys()) {
                    LOG.info("New foods inserted successfully in order with id {}", newOrder.getId());
                    auditInsertFood(rs, newOrder.getItems());
                }
            } catch (SQLException exception) {
                LOG.warn("Inserting food in last order with id {} failed", newOrder.getId());
            }
        } else {
            LOG.error("There is no food to insert in current order");
        }
    }

    private void auditInsertFood(ResultSet rs, FoodItem[] foodItems) throws SQLException {
        for (FoodItem foodItem : foodItems) {
            if (rs.next()) {
                foodItem.setId(rs.getInt(1));
                auditLogger.auditChange(
                        new EntityChanged(
                                "food.Insert",
                                "--no exist--",
                                String.valueOf(foodItem)));
            } else {
                LOG.error("No more generated id's");
            }
        }
    }


}
