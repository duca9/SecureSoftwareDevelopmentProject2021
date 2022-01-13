package com.zuehlke.securesoftwaredevelopment.repository;

import com.zuehlke.securesoftwaredevelopment.config.AuditLogger;
import com.zuehlke.securesoftwaredevelopment.config.EntityChanged;
import com.zuehlke.securesoftwaredevelopment.domain.Restaurant;
import com.zuehlke.securesoftwaredevelopment.domain.RestaurantUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


@Repository
public class RestaurantRepository {
    private static final Logger LOG = LoggerFactory.getLogger(RestaurantRepository.class);
    private static final AuditLogger auditLogger = AuditLogger.getAuditLogger(RestaurantRepository.class);
    private final DataSource dataSource;

    public RestaurantRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Restaurant> getRestaurants() {
        List<Restaurant> restaurants = new ArrayList<>();
        String query = "SELECT r.id, r.name, r.address, rt.name  FROM restaurant AS r JOIN restaurant_type AS rt ON r.typeId = rt.id ";
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(query)) {
            while (rs.next()) {
                tryToAddNewRestaurant(restaurants, rs);
            }
        } catch (SQLException e) {
            LOG.warn("Getting restaurants failed");
        }
        return restaurants;
    }

    private void tryToAddNewRestaurant(List<Restaurant> restaurants, ResultSet rs) {
        try {
            restaurants.add(createRestaurant(rs));
        } catch (SQLException e) {
            LOG.warn("Creating restaurant from result set failed");
        }
    }

    private Restaurant createRestaurant(ResultSet rs) throws SQLException {
        int id = rs.getInt(1);
        String name = rs.getString(2);
        String address = rs.getString(3);
        String type = rs.getString(4);
        return new Restaurant(id, name, address, type);
    }

    public Restaurant getRestaurant(String id) {
        String query = "SELECT r.id, r.name, r.address, rt.name  FROM restaurant AS r JOIN restaurant_type AS rt ON r.typeId = rt.id WHERE r.id=" + id;
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(query)) {

            if (rs.next()) {
                return createRestaurant(rs);
            }
        } catch (SQLException e) {
            LOG.warn(String.format("Getting restaurant with id %s failed", id));
        }
        return null;
    }

    public void deleteRestaurant(int id) {
        String query = "DELETE FROM restaurant WHERE id=" + id;
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()
        ) {
            Restaurant oldRestaurant = getRestaurant(String.valueOf(id));
            statement.executeUpdate(query);
            LOG.info("Restaurant with id {} successfully deleted", id);
            auditLogger.auditChange(
                    new EntityChanged("restaurant.Delete",
                            String.valueOf(oldRestaurant),
                            "--deleted--"));
        } catch (SQLException e) {
            LOG.warn(String.format("Deleting restaurant with id %s failed", id));
        }
    }

    public void updateRestaurant(RestaurantUpdate restaurantUpdate) {
        String query = "UPDATE restaurant SET name = '" + restaurantUpdate.getName() + "', address='" + restaurantUpdate.getAddress() + "', typeId =" + restaurantUpdate.getRestaurantType() + " WHERE id =" + restaurantUpdate.getId();
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()
        ) {
            Restaurant restaurant = getRestaurant(String.valueOf(restaurantUpdate.getId()));
            statement.executeUpdate(query);
            LOG.info("Restaurant with id {} successfully updated", restaurantUpdate.getId());
            auditLogger.auditChange(
                    new EntityChanged("restaurant.update",
                            String.valueOf(restaurant),
                            String.valueOf(restaurantUpdate)));
        } catch (SQLException e) {
            LOG.warn(String.format("Updating restaurant with id %s failed", restaurantUpdate.getId()));
        }
    }
}
