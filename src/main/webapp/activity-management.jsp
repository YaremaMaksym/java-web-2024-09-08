<%--
  Created by IntelliJ IDEA.
  User: macsy
  Date: 2024-09-19
  Time: 13:16
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Activity Management</title>
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
<h1>Activity Management</h1>

<h2>Activity List</h2>
<table id="activityTable">
    <thead>
    <tr>
        <th>ID</th>
        <th>User ID</th>
        <th>Description</th>
        <th>Timestamp</th>
        <th>Actions</th>
    </tr>
    </thead>
    <tbody>
    <!-- Activity data will be inserted here by JavaScript -->
    </tbody>
</table>

<h2>Add/Edit Activity</h2>
<form id="activityForm">
    <input type="hidden" id="activityId" name="id">
    <label for="userId">User ID:</label>
    <input type="number" id="userId" name="userId" required><br>
    <label for="description">Description:</label>
    <input type="text" id="description" name="description" required><br>
    <label for="timestamp">Timestamp:</label>
    <input type="datetime-local" id="timestamp" name="timestamp" required><br>
    <button type="submit" id="submitBtn">Add Activity</button>
</form>

<script>
    // Отримуємо базовий URL з поточної сторінки
    var baseUrl = window.location.pathname.replace(/\/[^\/]*$/, '');

    $(document).ready(function() {
        loadActivities();

        $('#activityForm').submit(function(e) {
            e.preventDefault();
            var activityId = $('#activityId').val();
            var activityData = {
                userId: parseInt($('#userId').val()),
                description: $('#description').val(),
                timestamp: $('#timestamp').val()
            };

            if (activityId) {
                updateActivity(activityId, activityData);
            } else {
                createActivity(activityData);
            }
        });
    });

    function loadActivities() {
        $.ajax({
            url: baseUrl + '/activities',
            type: 'GET',
            dataType: 'json',
            success: function(activities) {
                console.log("Received activities:", activities);
                var tbody = $('#activityTable tbody');
                tbody.empty();
                if (Array.isArray(activities)) {
                    activities.forEach(function(activity) {
                        var rowHtml = '<tr>' +
                            '<td>' + activity.id + '</td>' +
                            '<td>' + activity.userId + '</td>' +
                            '<td>' + activity.description + '</td>' +
                            '<td>' + new Date(activity.timestamp).toLocaleString() + '</td>' +
                            '<td>' +
                            '<button onclick="editActivity(' + activity.id + ')">Edit</button>' +
                            '<button onclick="deleteActivity(' + activity.id + ')">Delete</button>' +
                            '</td>' +
                            '</tr>';
                        tbody.append(rowHtml);
                    });
                } else {
                    console.error("Received data is not an array:", activities);
                }
            },
            error: function(xhr, status, error) {
                console.error("Error loading activities:", status, error);
                console.log("Response text:", xhr.responseText);
                alert("Failed to load activities. Please check the console for details.");
            }
        });
    }

    function createActivity(activityData) {
        $.ajax({
            url: baseUrl + '/activities',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(activityData),
            success: function(response) {
                loadActivities();
                clearForm();
            },
            error: function(xhr, status, error) {
                console.error("Error creating activity:", status, error);
                alert("Failed to create activity. Please check the console for details.");
            }
        });
    }

    function editActivity(id) {
        $.ajax({
            url: baseUrl + '/activities/' + id,
            type: 'GET',
            success: function(activity) {
                $('#activityId').val(activity.id);
                $('#userId').val(activity.userId);
                $('#description').val(activity.description);
                $('#timestamp').val(activity.timestamp.replace(" ", "T"));
                $('#submitBtn').text('Update Activity');
            },
            error: function(xhr, status, error) {
                console.error("Error fetching activity:", status, error);
                alert("Failed to fetch activity details. Please check the console for details.");
            }
        });
    }

    function updateActivity(id, activityData) {
        $.ajax({
            url: baseUrl + '/activities/' + id,
            type: 'PUT',
            contentType: 'application/json',
            data: JSON.stringify(activityData),
            success: function(response) {
                loadActivities();
                clearForm();
            },
            error: function(xhr, status, error) {
                console.error("Error updating activity:", status, error);
                alert("Failed to update activity. Please check the console for details.");
            }
        });
    }

    function deleteActivity(id) {
        if (confirm('Are you sure you want to delete this activity?')) {
            $.ajax({
                url: baseUrl + '/activities/' + id,
                type: 'DELETE',
                success: function(response) {
                    loadActivities();
                },
                error: function(xhr, status, error) {
                    console.error("Error deleting activity:", status, error);
                    alert("Failed to delete activity. Please check the console for details.");
                }
            });
        }
    }

    function clearForm() {
        $('#activityForm')[0].reset();
        $('#activityId').val('');
        $('#submitBtn').text('Add Activity');
    }
</script>
</body>
</html>

