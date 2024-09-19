package org.yaremax.javaweb20240908.jdbc;

import org.yaremax.javaweb20240908.entity.Activity;
import org.yaremax.javaweb20240908.entity.User;
import org.yaremax.javaweb20240908.jdbc.strategy.ReadCommittedStrategy;
import org.yaremax.javaweb20240908.jdbc.strategy.RepeatableReadStrategy;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ActivityDao {
    private DBConnectionManager dbConnectionManager;
    private static ActivityDao instance;

    private ActivityDao() {
        dbConnectionManager = DBConnectionManager.getInstance();
    }

    public static synchronized ActivityDao getInstance() {
        if (instance == null) {
            instance = new ActivityDao();
        }
        return instance;
    }

    // CRUD methods for Activity

    public List<Activity> getAllActivities() {
        List<Activity> activities = new ArrayList<>();
        String sql = "SELECT * FROM activities";

        try (Connection connection = dbConnectionManager.openConnection(new ReadCommittedStrategy())) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Activity activity = Activity.builder()
                        .id(rs.getLong("id"))
                        .user(fetchUserById(rs.getLong("user_id"))) // Fetch the full User object
                        .description(rs.getString("description"))
                        .timestamp(rs.getTimestamp("timestamp").toLocalDateTime())
                        .build();
                activities.add(activity);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching all activities", e);
        }
        return activities;
    }

    public Optional<Activity> getActivityById(Long id) {
        String sql = "SELECT * FROM activities WHERE id = ?";

        try (Connection connection = dbConnectionManager.openConnection(new ReadCommittedStrategy())) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setLong(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return Optional.of(Activity.builder()
                        .id(rs.getLong("id"))
                        .user(fetchUserById(rs.getLong("user_id")))
                        .description(rs.getString("description"))
                        .timestamp(rs.getTimestamp("timestamp").toLocalDateTime())
                        .build());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching activity by ID", e);
        }
        return Optional.empty();
    }

    public List<Activity> getActivitiesByUserAndDate(Long userId, Date date) {
        List<Activity> activities = new ArrayList<>();
        String sql = "SELECT * FROM activities WHERE user_id = ? AND DATE(timestamp) = ?";

        try (Connection connection = dbConnectionManager.openConnection(new ReadCommittedStrategy())) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setLong(1, userId);
            ps.setDate(2, date);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Activity activity = Activity.builder()
                        .id(rs.getLong("id"))
                        .user(fetchUserById(rs.getLong("user_id")))
                        .description(rs.getString("description"))
                        .timestamp(rs.getTimestamp("timestamp").toLocalDateTime())
                        .build();
                activities.add(activity);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching activities for user by date", e);
        }
        return activities;
    }

    public Activity createActivity(Activity activity) {
        String sql = "INSERT INTO activities (user_id, description, timestamp) VALUES (?, ?, ?)";

        try (Connection connection = dbConnectionManager.openConnection(new ReadCommittedStrategy());
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, activity.getUser().getId());
            ps.setString(2, activity.getDescription());
            ps.setTimestamp(3, Timestamp.valueOf(activity.getTimestamp()));

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating activity failed, no rows affected.");
            }

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    activity.setId(rs.getLong(1));
                } else {
                    throw new SQLException("Creating activity failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error creating activity", e);
        }
        return activity;
    }

    public Optional<Activity> updateActivity(Activity activity, Long id) {
        String sql = "UPDATE activities SET description = ?, timestamp = ? WHERE id = ?";

        try (Connection connection = dbConnectionManager.openConnection(new RepeatableReadStrategy());
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, activity.getDescription());
            ps.setTimestamp(2, Timestamp.valueOf(activity.getTimestamp()));
            ps.setLong(3, id);

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                activity.setId(id);
                return Optional.of(activity);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error updating activity", e);
        }
        return Optional.empty();
    }

    public boolean deleteActivity(Long id) {
        String sql = "DELETE FROM activities WHERE id = ?";

        try (Connection connection = dbConnectionManager.openConnection(new RepeatableReadStrategy());
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, id);
            int affectedRows = ps.executeUpdate();

            return affectedRows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting activity", e);
        }
    }

    private User fetchUserById(Long userId) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection connection = dbConnectionManager.openConnection(new ReadCommittedStrategy())) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setLong(1, userId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return User.builder()
                        .id(rs.getLong("id"))
                        .firstName(rs.getString("first_name"))
                        .lastName(rs.getString("last_name"))
                        .age(rs.getInt("age"))
                        .build();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching user by ID", e);
        }
        throw new RuntimeException("User not found with ID: " + userId);
    }
}
