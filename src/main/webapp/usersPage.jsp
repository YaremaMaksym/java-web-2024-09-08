<%--
  Created by IntelliJ IDEA.
  User: macsy
  Date: 2024-09-18
  Time: 13:06
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>User Management</title>
    <style>
        table {
            border-collapse: collapse;
            width: 100%;
        }
        th, td {
            border: 1px solid black;
            padding: 8px;
            text-align: left;
        }
        th {
            background-color: #f2f2f2;
        }
    </style>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
</head>
<body>
<h1>User Management</h1>

<h2>User List</h2>
<table id="userTable">
    <thead>
    <tr>
        <th>ID</th>
        <th>First Name</th>
        <th>Last Name</th>
        <th>Age</th>
        <th>Actions</th>
    </tr>
    </thead>
    <tbody>
    <!-- User data will be inserted here by JavaScript -->
    </tbody>
</table>

<h2>Add/Edit User</h2>
<form id="userForm">
    <input type="hidden" id="userId" name="id">
    <label for="firstName">First Name:</label>
    <input type="text" id="firstName" name="firstName" required><br>
    <label for="lastName">Last Name:</label>
    <input type="text" id="lastName" name="lastName" required><br>
    <label for="age">Age:</label>
    <input type="number" id="age" name="age" required><br>
    <button type="submit" id="submitBtn">Add User</button>
</form>

<script>
    // Отримуємо базовий URL з поточної сторінки
    var baseUrl = window.location.pathname.replace(/\/[^\/]*$/, '');

    $(document).ready(function() {
        loadUsers();

        $('#userForm').submit(function(e) {
            e.preventDefault();
            var userId = $('#userId').val();
            var userData = {
                firstName: $('#firstName').val(),
                lastName: $('#lastName').val(),
                age: parseInt($('#age').val())
            };

            if (userId) {
                updateUser(userId, userData);
            } else {
                createUser(userData);
            }
        });
    });

    function loadUsers() {
        $.ajax({
            url: baseUrl + '/users',
            type: 'GET',
            dataType: 'json',
            success: function(users) {
                console.log("Received users:", users);
                var tbody = $('#userTable tbody');
                tbody.empty();
                if (Array.isArray(users)) {
                    users.forEach(function(user) {
                        var rowHtml = '<tr>' +
                            '<td>' + user.id + '</td>' +
                            '<td>' + user.firstName + '</td>' +
                            '<td>' + user.lastName + '</td>' +
                            '<td>' + user.age + '</td>' +
                            '<td>' +
                            '<button onclick="editUser(' + user.id + ')">Edit</button>' +
                            '<button onclick="deleteUser(' + user.id + ')">Delete</button>' +
                            '</td>' +
                            '</tr>';
                        tbody.append(rowHtml);
                    });
                } else {
                    console.error("Received data is not an array:", users);
                }
            },
            error: function(xhr, status, error) {
                console.error("Error loading users:", status, error);
                console.log("Response text:", xhr.responseText);
                alert("Failed to load users. Please check the console for details.");
            }
        });
    }

    function createUser(userData) {
        $.ajax({
            url: baseUrl + '/users',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(userData),
            success: function(response) {
                loadUsers();
                clearForm();
            },
            error: function(xhr, status, error) {
                console.error("Error creating user:", status, error);
                alert("Failed to create user. Please check the console for details.");
            }
        });
    }

    function editUser(id) {
        $.ajax({
            url: baseUrl + '/users/' + id,
            type: 'GET',
            success: function(user) {
                $('#userId').val(user.id);
                $('#firstName').val(user.firstName);
                $('#lastName').val(user.lastName);
                $('#age').val(user.age);
                $('#submitBtn').text('Update User');
            },
            error: function(xhr, status, error) {
                console.error("Error fetching user:", status, error);
                alert("Failed to fetch user details. Please check the console for details.");
            }
        });
    }

    function updateUser(id, userData) {
        $.ajax({
            url: baseUrl + '/users/' + id,
            type: 'PUT',
            contentType: 'application/json',
            data: JSON.stringify(userData),
            success: function(response) {
                loadUsers();
                clearForm();
            },
            error: function(xhr, status, error) {
                console.error("Error updating user:", status, error);
                alert("Failed to update user. Please check the console for details.");
            }
        });
    }

    function deleteUser(id) {
        if (confirm('Are you sure you want to delete this user?')) {
            $.ajax({
                url: baseUrl + '/users/' + id,
                type: 'DELETE',
                success: function(response) {
                    loadUsers();
                },
                error: function(xhr, status, error) {
                    console.error("Error deleting user:", status, error);
                    alert("Failed to delete user. Please check the console for details.");
                }
            });
        }
    }

    function clearForm() {
        $('#userForm')[0].reset();
        $('#userId').val('');
        $('#submitBtn').text('Add User');
    }
</script>
</body>
</html>