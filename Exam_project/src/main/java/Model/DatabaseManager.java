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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static Model.enums.Role.ADMIN;
import static Model.enums.Role.EXECUTOR;
import static Model.enums.Role.MANAGER;
import java.security.SecureRandom;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Comparator;

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
        if (!users.isEmpty()) {
            users.clear();
        }
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
        if (!projects.isEmpty() || !userTasks.isEmpty()) {
            projects.clear();
            userTasks.clear();
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
            projects.sort(Comparator.comparing((Project project) -> project.getEndDate()));
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

    public int createProject(String projectName, String description, LocalDate startDate, LocalDate endDate, List<User> executors) {
        String insertProject = "INSERT INTO projects (project_name, description, created_by, start_date, end_date) "
                + "VALUES (?, ?, ?, ?, ?) RETURNING project_id";
        String insertExecutors = "INSERT INTO project_executors (project_id, user_id) VALUES (?, ?)";

        PreparedStatement projectStmt = null;
        PreparedStatement executorsStmt = null;
        ResultSet rs = null;
        int projectId = -1;
        try {
            connection.setAutoCommit(false); // Начинаем транзакцию

            projectStmt = connection.prepareStatement(insertProject);
            projectStmt.setString(1, projectName);
            projectStmt.setString(2, description);
            projectStmt.setLong(3, currentUser.getUserId());
            projectStmt.setDate(4, java.sql.Date.valueOf(startDate));
            projectStmt.setDate(5, java.sql.Date.valueOf(endDate));

            rs = projectStmt.executeQuery(); // есть RETURNING
            if (rs.next()) {
                projectId = rs.getInt(1);
            } else {
                throw new SQLException("Не удалось получить project_id после вставки проекта.");
            }

            // 2. Вставляем исполнителей
            executorsStmt = connection.prepareStatement(insertExecutors);
            for (User user : executors) {
                executorsStmt.setInt(1, projectId);
                executorsStmt.setInt(2, user.getUserId());
                executorsStmt.addBatch(); //добавили в пакет запросов
            }
            executorsStmt.executeBatch(); //выполнили весь пакет целиком

            connection.commit();
            getProjectsForCurrentUser(connection);
            return projectId;
        } catch (SQLException ex) {
            if (connection != null) try {
                connection.rollback();
            } catch (SQLException e2) {
            }
            // 23505 — нарушение уникальности project_name
            if ("23505".equals(ex.getSQLState())) {
                return -1;
            } else {
                ex.printStackTrace();
            }
        } finally {
            if (rs != null) try {
                rs.close();
            } catch (SQLException e) {
            }
            if (projectStmt != null) try {
                projectStmt.close();
            } catch (SQLException e) {
            }
            if (executorsStmt != null) try {
                executorsStmt.close();
            } catch (SQLException e) {
            }
            if (connection != null) try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
            }
        }
        return projectId;
    }

    public boolean createTask(String name, String description, Project project, User executor, PriorityLevel priority, LocalDate deadline) {
        String insertTask = "INSERT INTO tasks (task_title, task_description, project_id, creator_id, executor_id, priority, deadline) VALUES (?,?,?,?,?,?,?)";
        boolean result = false;
        try (PreparedStatement taskStmt = connection.prepareStatement(insertTask)) {
            taskStmt.setString(1, name);
            taskStmt.setString(2, description);
            taskStmt.setInt(3, project.getProjectID());
            taskStmt.setInt(4, currentUser.getUserId());
            taskStmt.setInt(5, executor.getUserId());
            taskStmt.setObject(6, priority.name(), java.sql.Types.OTHER);
            taskStmt.setDate(7, java.sql.Date.valueOf(deadline));
            taskStmt.executeUpdate();
            result = true;
            getProjectsForCurrentUser(connection);
        } catch (SQLException ex) {
            if ("23505".equals(ex.getSQLState())) {
                return false;
            } else {
                ex.printStackTrace();
            }
        }
        return result;
    }

    public boolean createUser(String fullname, String username, Role role) {
        String insertUser = "INSERT INTO users (username, password, full_name, role) VALUES (?, ?, ?, ?)";
        String newPass = generatePassword();
        boolean result = false;
        try (PreparedStatement statement = connection.prepareStatement(insertUser)) {
            statement.setString(1, username);
            statement.setString(2, newPass);
            statement.setString(3, fullname);
            statement.setObject(4, role.name(), java.sql.Types.OTHER);
            statement.executeUpdate();
            result = true;
            readUsers(connection);
        } catch (SQLException ex) {
            if ("23505".equals(ex.getSQLState())) {
                return false;
            } else {
                ex.printStackTrace();
            }
        }
        return result;
    }

    private String generatePassword() {
        SecureRandom random = new SecureRandom();
        String simbols = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#*()_+-?";
        StringBuilder newPass = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            int index = random.nextInt(simbols.length());
            newPass.append(simbols.charAt(index));
        }
        return newPass.toString();
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
