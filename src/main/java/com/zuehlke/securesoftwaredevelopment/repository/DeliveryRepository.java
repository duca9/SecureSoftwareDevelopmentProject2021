package com.zuehlke.securesoftwaredevelopment.repository;

import com.zuehlke.securesoftwaredevelopment.domain.DeliveryDetail;
import com.zuehlke.securesoftwaredevelopment.domain.ViewableDelivery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class DeliveryRepository {

    private static final Logger LOG = LoggerFactory.getLogger(DeliveryRepository.class);
    private final DataSource dataSource;

    public DeliveryRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<ViewableDelivery> getAllDeliveries() {
        List<ViewableDelivery> deliveries = new ArrayList<>();
        String sqlQuery = "SELECT d.id, d.isDone, d.date, d.comment, u.username, r.name, rt.name, a.name FROM delivery AS d JOIN users AS u ON d.userId = u.id JOIN restaurant as r ON d.restaurantId = r.id JOIN address AS a ON d.addressId = a.id JOIN restaurant_type AS rt ON r.typeId= rt.id";

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sqlQuery)) {

            while (rs.next()) {
                deliveries.add(createDelivery(rs));
            }

        } catch (SQLException e) {
            LOG.warn("Getting all deliveries failed");
        }
        return deliveries;
    }


    private ViewableDelivery createDelivery(ResultSet rs) throws SQLException {
        int id = rs.getInt(1);
        boolean isDone = rs.getBoolean(2);
        Date date = rs.getDate(3);
        String comment = rs.getString(4);
        String username = rs.getString(5);
        String restaurantName = rs.getString(6);
        String restaurantType = rs.getString(7);
        String address = rs.getString(8);
        return new ViewableDelivery(id, isDone, date, comment, username, address, restaurantName, restaurantType);
    }

    public ViewableDelivery getDelivery(String id) {
        String sqlQuery = "SELECT d.id, d.isDone, d.date, d.comment, u.username, r.name, rt.name, a.name FROM delivery AS d JOIN users AS u ON d.userId = u.id JOIN restaurant as r ON d.restaurantId = r.id JOIN address AS a ON d.addressId = a.id JOIN restaurant_type AS rt ON r.typeId= rt.id WHERE d.id = " + id;

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sqlQuery)) {

            if (rs.next()) {
                return createDelivery(rs);
            }

        } catch (SQLException e) {
            LOG.warn(String.format("Getting delivery failed for delivery with id %s", id));
        }
        return null;
    }

    public List<DeliveryDetail> getDeliveryDetails(String id) {
        List<DeliveryDetail> details = new ArrayList<>();
        String sqlQuery = "SELECT di.id, di.amount, f.name, f.price FROM delivery_item AS di JOIN food AS f ON di.foodId = f.id WHERE deliveryId = " + id;

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sqlQuery)) {

            while (rs.next()) {
                tryToAddNewDeliveryDetails(details, rs);
            }
        } catch (SQLException e) {
            LOG.warn(String.format("Getting delivery details failed for delivery with id %s", id));
        }
        return details;
    }

    private void tryToAddNewDeliveryDetails(List<DeliveryDetail> deliveryDetails, ResultSet rs) {
        try {
            deliveryDetails.add(createDetail(rs));
        } catch (SQLException e) {
            LOG.warn("Creating delivery detail from result set failed");
        }
    }

    private DeliveryDetail createDetail(ResultSet rs) throws SQLException {
        int id = rs.getInt(1);
        int amount = rs.getInt(2);
        String foodName = rs.getString(3);
        int price = rs.getInt(4);

        return new DeliveryDetail(id, amount, foodName, price);

    }

    public int calculateSum(List<DeliveryDetail> details) {
        int sum = 0;
        for (DeliveryDetail detail : details) {
            sum += detail.getPrice() * detail.getAmount();
        }
        return sum;
    }


    public List<ViewableDelivery> search(String searchQuery) {
        List<ViewableDelivery> deliveries = new ArrayList<>();
        String sqlQuery =
                "SELECT d.id, d.isDone, d.date, d.comment, u.username, r.name, rt.name, a.name FROM delivery AS d JOIN users AS u ON d.userId = u.id JOIN restaurant as r ON d.restaurantId = r.id JOIN address AS a ON d.addressId = a.id JOIN restaurant_type AS rt ON r.typeId= rt.id" +
                        " WHERE UPPER(d.comment) LIKE UPPER('%" + searchQuery + "%')"
                        + "OR UPPER(u.username) LIKE UPPER('%" + searchQuery + "%')"
                        + "OR UPPER(r.name) LIKE UPPER('%" + searchQuery + "%')"
                        + "OR UPPER(rt.name) LIKE UPPER('%" + searchQuery + "%')"
                        + "OR UPPER(a.name) LIKE UPPER('%" + searchQuery + "%')";
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sqlQuery)) {
            while (rs.next()) {
                tryToAddNewDelivery(deliveries, rs);
            }
            LOG.info("Searched successfully for {}", searchQuery);
        } catch (SQLException exception) {
            LOG.warn("Search delivery failed");
        }
        return deliveries;
    }

    private void tryToAddNewDelivery(List<ViewableDelivery> viewableDeliveries, ResultSet rs) {
        try {
            viewableDeliveries.add(createDelivery(rs));
        } catch (SQLException e) {
            LOG.warn("Creating viewable delivery from result set failed");
        }
    }


}
