package org.yaremax.javaweb20240908.service;

import org.yaremax.javaweb20240908.entity.User;
import org.yaremax.javaweb20240908.jdbc.UserDao;

public class ReferenceService {
    private final UserDao userDao;
    private static ReferenceService instance;

    private ReferenceService() {
        userDao = UserDao.getInstance();
    }

    public static synchronized ReferenceService getInstance() {
        if (instance == null) {
            instance = new ReferenceService();
        }
        return instance;
    }

    public User getExistingUserReference(Long id) {
        return userDao.getUserById(id)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + id + " not found"));
    }
}
