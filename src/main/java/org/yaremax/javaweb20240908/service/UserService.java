package org.yaremax.javaweb20240908.service;

import org.yaremax.javaweb20240908.entity.User;
import org.yaremax.javaweb20240908.jdbc.UserDao;

import java.util.List;
import java.util.Optional;

public class UserService {
    private UserDao userDao;
    private static UserService instance;

    private UserService() {
        userDao = UserDao.getInstance();
    }

    public static synchronized UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }

    public List<User> getAllUsers() {
        return userDao.getAllUsers();
    }

    public Optional<User> getUserById(Long id) {
        return userDao.getUserById(id);
    }

    public User createUser(User user) {
        return userDao.createUser(user);
    }

    public Optional<User> updateUser(User user, Long id) {
        return userDao.updateUser(user, id);
    }

    public boolean deleteUser(Long id) {
        return userDao.deleteUser(id);
    }
}
