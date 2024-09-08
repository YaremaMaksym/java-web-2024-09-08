package org.yaremax.javaweb20240908.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.yaremax.javaweb20240908.entity.User;
import org.yaremax.javaweb20240908.service.UserService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@WebServlet("/users/*")
public class UserServlet extends HttpServlet {
    private UserService userService;

    @Override
    public void init() throws ServletException {
        userService = UserService.getInstance();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = parseRequestBody(request, User.class);
        User createdUser = userService.createUser(user);
        writeResponse(response, HttpServletResponse.SC_OK, createdUser);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getPathInfo();
        if (path == null || path.equals("/")) {
            List<User> users = userService.getAllUsers();
            writeResponse(response, HttpServletResponse.SC_OK, users);
        } else {
            Long userId = Long.parseLong(path.substring(1)); // extract the user ID
            Optional<User> user = userService.getUserById(userId);
            if (user.isPresent()) {
                writeResponse(response, HttpServletResponse.SC_OK, user.get());
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
            }
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getPathInfo();
        if (path != null && path.startsWith("/")) {
            Long userId = Long.parseLong(path.substring(1));
            User updatedUser = parseRequestBody(request, User.class);
            Optional<User> user = userService.updateUser(updatedUser, userId);
            if (user.isPresent()) {
                writeResponse(response, HttpServletResponse.SC_OK, user.get());
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
            }
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid user ID");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getPathInfo();
        if (path != null && path.startsWith("/")) {
            Long userId = Long.parseLong(path.substring(1));
            boolean deleted = userService.deleteUser(userId);
            if (deleted) {
                writeResponse(response, HttpServletResponse.SC_OK, "Deleted user with id " + userId);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
            }
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid user ID");
        }
    }

    private <T> T parseRequestBody(HttpServletRequest request, Class<T> clazz) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(request.getInputStream(), clazz);
    }

    private void writeResponse(HttpServletResponse response, int statusCode, Object data) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        response.setContentType("application/json");
        response.setStatus(statusCode);
        mapper.writeValue(response.getOutputStream(), data);
    }
}

