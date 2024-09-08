package org.yaremax.javaweb20240908.jdbc;

import org.yaremax.javaweb20240908.entity.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDao {
    private DBConnectionManager dbConnectionManager;
    private static UserDao instance;

    private UserDao() {
        dbConnectionManager = DBConnectionManager.getInstance();
    }

    public static synchronized UserDao getInstance() {
        if (instance == null) {
            instance = new UserDao();
        }
        return instance;
    }

    // CRUD methods

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (Connection connection = dbConnectionManager.openConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                User user = User.builder()
                        .id(rs.getLong("id"))
                        .firstName(rs.getString("first_name"))
                        .lastName(rs.getString("last_name"))
                        .age(rs.getInt("age"))
                        .build();
                users.add(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching all users", e);
        }
        return users;
    }

    public Optional<User> getUserById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?";

        try (Connection connection = dbConnectionManager.openConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setLong(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return Optional.of(User.builder()
                        .id(rs.getLong("id"))
                        .firstName(rs.getString("first_name"))
                        .lastName(rs.getString("last_name"))
                        .age(rs.getInt("age"))
                        .build());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching user by ID", e);
        }
        return Optional.empty();
    }

    public User createUser(User user) {
        String sql = "INSERT INTO users (first_name, last_name, age) VALUES (?, ?, ?)";

        try (Connection connection = dbConnectionManager.openConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setInt(3, user.getAge());
            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    user.setId(rs.getLong(1));
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error creating user", e);
        }
        return user;
    }

    public Optional<User> updateUser(User user, Long id) {
        String sql = "UPDATE users SET first_name = ?, last_name = ?, age = ? WHERE id = ?";

        try (Connection connection = dbConnectionManager.openConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setInt(3, user.getAge());
            ps.setLong(4, id);

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                user.setId(id);
                return Optional.of(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error updating user", e);
        }
        return Optional.empty();
    }

    public boolean deleteUser(Long id) {
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection connection = dbConnectionManager.openConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, id);
            int affectedRows = ps.executeUpdate();

            return affectedRows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting user", e);
        }
    }

}
