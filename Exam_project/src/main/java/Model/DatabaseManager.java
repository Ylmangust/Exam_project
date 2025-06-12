/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import Model.databaseEntities.Project;
import Model.databaseEntities.User;
import Model.databaseEntities.Task;
import Model.enums.Role;
import Model.enums.Status;
import Model.enums.PriorityLevel;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static Model.enums.Role.ADMIN;
import static Model.enums.Role.EXECUTOR;
import static Model.enums.Role.MANAGER;

/**
 *
 * @author Регина
 */
public class DatabaseManager {

    private String password = "UGfn%f@ziWFBwl";
    private String user = "postgres.oghifeiboocdhvyuzkuo";
    private String url = "jdbc:postgresql://aws-0-eu-north-1.pooler.supabase.com:5432/postgres";
    private Connection connection;
    private User currentUser;
    private List<User> users = new ArrayList<>();
    private List<Project> projects = new ArrayList<>();
    private List<Task> userTasks = new ArrayList<>();
    private List<String> projectNames = new ArrayList<>();

    public DatabaseManager() {
        try {
            connection = DriverManager.getConnection(url, user, password);
            if (connection != null) {

            }
        } catch (SQLException e) {
            System.out.println("Connection error: " + e.getMessage());
            System.exit(0);
        }
    }

    public User autorization(String username, String password) {
        String query = "SELECT * FROM users where username = ? and password = ?";
        PreparedStatement statement = null;
        clearCache();
        try {
            statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int id = resultSet.getInt("user_id");
                String name = resultSet.getString("full_name");
                String roleStr = resultSet.getString("role");
                Role role = Role.valueOf(roleStr);
                int rate = resultSet.getInt("rate");
                currentUser = new User(id, username, password, name, role, rate);
                getProjectsForCurrentUser(connection);
                if (currentUser.getRole() != Role.EXECUTOR) {
                    readUsers(connection);
                    getAllProjectsNames(connection);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return currentUser;
    }

    private void readUsers(Connection connection) {
        String query = "SELECT * FROM users";
        try (PreparedStatement statement = connection.prepareStatement(query); ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                int id = resultSet.getInt("user_id");
                String username = resultSet.getString("username");
                String pass = resultSet.getString("password");
                String name = resultSet.getString("full_name");
                String roleStr = resultSet.getString("role");
                Role role = Role.valueOf(roleStr);
                int rate = resultSet.getInt("rate");
                users.add(new User(id, username, pass, name, role, rate));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void getProjectsForCurrentUser(Connection connection) {
        if (!projects.isEmpty()) {
            projects.clear();
        }
        String projectsQuery = buildQueryByRole();
        try (PreparedStatement projectsStmt = connection.prepareStatement(projectsQuery)) {
            switch (currentUser.getRole()) {
                case MANAGER:
                    projectsStmt.setInt(1, currentUser.getUserId());
                    projectsStmt.setInt(2, currentUser.getUserId());
                    break;
                case EXECUTOR:
                    projectsStmt.setInt(1, currentUser.getUserId());
                    break;
            }
            ResultSet resultSet = projectsStmt.executeQuery();
            while (resultSet.next()) {
                int creatorId = resultSet.getInt("created_by");
                User creator = getCreatorByID(creatorId, connection);
                String statusStr = resultSet.getString("status");
                Status status = Status.valueOf(statusStr);
                Project project = new Project(resultSet.getInt("project_id"),
                        resultSet.getString("project_name"),
                        resultSet.getString("description"),
                        creator,
                        status,
                        resultSet.getDate("start_date").toLocalDate(),
                        resultSet.getDate("end_date").toLocalDate());
                project.setExecutors(getProjectExecutors(project.getProjectID(), connection));
                project.setTasks(getProjectTasks(project.getProjectID(), project, connection));
                projects.add(project);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

    private List<Task> getProjectTasks(int projectID, Project project, Connection connection) {
        List<Task> tasks = new ArrayList<>();
        String taskQuery = "SELECT * FROM tasks WHERE project_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(taskQuery)) {
            statement.setInt(1, projectID);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int creatorId = resultSet.getInt("creator_id");
                User creator = getCreatorByID(creatorId, connection);
                String priorityStr = resultSet.getString("priority");
                int executorId = resultSet.getInt("executor_id");
                User executor = getTaskExecutor(executorId, connection);
                PriorityLevel priority = PriorityLevel.valueOf(priorityStr);
                String statusStr = resultSet.getString("status");
                Status status = Status.valueOf(statusStr);
                Task task = new Task(resultSet.getInt("task_id"),
                        resultSet.getString("task_title"),
                        resultSet.getString("task_description"),
                        creator,
                        executor,
                        priority,
                        status,
                        resultSet.getTimestamp("created_at").toLocalDateTime().toLocalDate(),
                        resultSet.getDate("deadline").toLocalDate()
                );
                if (resultSet.getDate("completed_at") != null) {
                    task.setCompletedAt(resultSet.getDate("completed_at").toLocalDate());
                }
                task.setProject(project);
                if (task.getExecutor().getUserId() == currentUser.getUserId()) {
                    userTasks.add(task);
                }
                tasks.add(task);

            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return tasks;
    }

    private List<User> getProjectExecutors(int projectID, Connection connection) {
        List<User> executors = new ArrayList<>();
        String executorQuery = "SELECT u.* FROM users u INNER JOIN project_executors pe ON pe.user_id = u.user_id WHERE pe.project_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(executorQuery)) {
            statement.setInt(1, projectID);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String roleStr = resultSet.getString("role");
                Role role = Role.valueOf(roleStr);
                User executor = new User(resultSet.getInt("user_id"),
                        resultSet.getString("username"),
                        resultSet.getString("password"),
                        resultSet.getString("full_name"),
                        role,
                        resultSet.getInt("rate")
                );
                executors.add(executor);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return executors;
    }

    private User getTaskExecutor(int userID, Connection connection) {
        User executor = null;
        String executorQuery = "SELECT * FROM users WHERE user_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(executorQuery)) {
            statement.setInt(1, userID);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String roleStr = resultSet.getString("role");
                Role role = Role.valueOf(roleStr);
                executor = new User(resultSet.getInt("user_id"),
                        resultSet.getString("username"),
                        resultSet.getString("password"),
                        resultSet.getString("full_name"),
                        role,
                        resultSet.getInt("rate")
                );
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return executor;
    }

    private User getCreatorByID(int userID, Connection connection) {
        String userQuery = "SELECT * FROM users WHERE user_id = ?";
        User creator = null;
        try (PreparedStatement statement = connection.prepareStatement(userQuery)) {
            statement.setInt(1, userID);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int id = resultSet.getInt("user_id");
                String username = resultSet.getString("username");
                String pass = resultSet.getString("password");
                String name = resultSet.getString("full_name");
                String roleStr = resultSet.getString("role");
                Role role = Role.valueOf(roleStr);
                int rate = resultSet.getInt("rate");
                creator = new User(id, username, pass, name, role, rate);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return creator;
    }

    private void getAllProjectsNames(Connection connection) {
        String userQuery = "SELECT project_name FROM projects";
        try (PreparedStatement statement = connection.prepareStatement(userQuery); ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                projectNames.add(resultSet.getString("project_name"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String buildQueryByRole() {
        switch (currentUser.getRole()) {
            case ADMIN -> {
                return "SELECT * FROM projects";
            }
            case MANAGER -> {
                return "SELECT DISTINCT p.* FROM projects p "
                        + "LEFT JOIN project_executors pe ON pe.project_id = p.project_id "
                        + "WHERE created_by = ? OR pe.user_id = ?";
            }
            case EXECUTOR -> {
                return "SELECT p.* FROM projects p "
                        + "INNER JOIN project_executors pe ON pe.project_id = p.project_id "
                        + "WHERE pe.user_id = ?";
            }
            default ->
                throw new IllegalArgumentException("Unknown role: " + currentUser.getRole());
        }
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public List<User> getUsers() {
        return users;
    }

    public List<Project> getProjects() {
        return projects;
    }

    public List<Task> getUserTasks() {
        return userTasks;
    }

    public List<String> getProjectNames() {
        return projectNames;
    }

    private void clearCache() {
        projects.clear();
        userTasks.clear();
        users.clear();
        currentUser = null;
    }

}
