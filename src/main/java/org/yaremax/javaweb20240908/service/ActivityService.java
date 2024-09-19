package org.yaremax.javaweb20240908.service;

import org.yaremax.javaweb20240908.dto.ActivityDto;
import org.yaremax.javaweb20240908.entity.Activity;
import org.yaremax.javaweb20240908.jdbc.ActivityDao;
import org.yaremax.javaweb20240908.service.mapper.ActivityMapper;

import java.util.List;
import java.util.Optional;

public class ActivityService {
    private ActivityDao activityDao;
    private ReferenceService referenceService;
    private ActivityMapper activityMapper;
    private static ActivityService instance;

    private ActivityService() {
        activityDao = ActivityDao.getInstance();
        referenceService = ReferenceService.getInstance();
        activityMapper = ActivityMapper.INSTANCE;
    }

    public static synchronized ActivityService getInstance() {
        if (instance == null) {
            instance = new ActivityService();
        }
        return instance;
    }

    public List<Activity> getAllActivities() {
        return activityDao.getAllActivities();
    }

    public Optional<Activity> getActivityById(Long id) {
        return activityDao.getActivityById(id);
    }

    public Activity createActivity(ActivityDto activityDTO) {
        Activity activity = activityMapper.toEntity(activityDTO);
        activity.setUser(referenceService.getExistingUserReference(activityDTO.getUserId()));
        return activityDao.createActivity(activity);
    }

    public Optional<Activity> updateActivity(Activity activity, Long id) {
        return activityDao.updateActivity(activity, id);
    }

    public boolean deleteActivity(Long id) {
        return activityDao.deleteActivity(id);
    }

    public List<Activity> getActivitiesByUserAndDate(Long userId, java.sql.Date date) {
        return activityDao.getActivitiesByUserAndDate(userId, date);
    }
}
