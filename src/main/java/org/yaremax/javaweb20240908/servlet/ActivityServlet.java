package org.yaremax.javaweb20240908.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.yaremax.javaweb20240908.dto.ActivityDto;
import org.yaremax.javaweb20240908.entity.Activity;
import org.yaremax.javaweb20240908.service.ActivityService;
import org.yaremax.javaweb20240908.service.mapper.ActivityMapper;
import org.yaremax.javaweb20240908.service.util.JsonUtil;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@WebServlet("/activities/*")
public class ActivityServlet extends HttpServlet {
    private ActivityMapper activityMapper = ActivityMapper.INSTANCE;
    private ActivityService activityService;
    private JsonUtil jsonUtil;

    @Override
    public void init() throws ServletException {
        activityService = ActivityService.getInstance();
        jsonUtil = JsonUtil.getInstance();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ActivityDto activityDTO = jsonUtil.parseRequestBody(request, ActivityDto.class);

        Activity createdActivity = activityService.createActivity(activityDTO);

        ActivityDto createdActivityDto = activityMapper.toDto(createdActivity);

        jsonUtil.writeResponse(response, HttpServletResponse.SC_OK, createdActivityDto);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getPathInfo();
        if (path == null || path.equals("/")) {
            List<Activity> activities = activityService.getAllActivities();

            List<ActivityDto> activityDtoList = activityMapper.toDtoList(activities);

            jsonUtil.writeResponse(response, HttpServletResponse.SC_OK, activityDtoList);
        } else {
            Long activityId = Long.parseLong(path.substring(1));
            Optional<Activity> activity = activityService.getActivityById(activityId);
            if (activity.isPresent()) {
                ActivityDto activityDTO = activityMapper.toDto(activity.get());

                jsonUtil.writeResponse(response, HttpServletResponse.SC_OK, activityDTO);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Activity not found");
            }
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getPathInfo();
        if (path != null && path.startsWith("/")) {
            Long activityId = Long.parseLong(path.substring(1));

            ActivityDto activityDTO = jsonUtil.parseRequestBody(request, ActivityDto.class);

            Activity updatedActivity = activityMapper.toEntity(activityDTO);

            Optional<Activity> activity = activityService.updateActivity(updatedActivity, activityId);
            if (activity.isPresent()) {
                ActivityDto updatedActivityDto = activityMapper.toDto(activity.get());

                jsonUtil.writeResponse(response, HttpServletResponse.SC_OK, updatedActivityDto);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Activity not found");
            }
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid activity ID");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getPathInfo();
        if (path != null && path.startsWith("/")) {
            Long activityId = Long.parseLong(path.substring(1));
            boolean deleted = activityService.deleteActivity(activityId);
            if (deleted) {
                jsonUtil.writeResponse(response, HttpServletResponse.SC_OK, "Deleted activity with id " + activityId);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Activity not found");
            }
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid activity ID");
        }
    }
}
