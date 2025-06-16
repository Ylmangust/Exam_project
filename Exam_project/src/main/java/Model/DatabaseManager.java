/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import Model.databaseEntities.Comment;
import Model.databaseEntities.Project;
import Model.databaseEntities.User;
import Model.databaseEntities.ProjectTask;
import Model.databaseEntities.TaskHistory;
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
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JOptionPane;

/**
 *
 * @author Регина
 */
public class DatabaseManager {

    private String password = "UGfn%f@ziWFBwl";
    private String user = "postgres.oghifeiboocdhvyuzkuo";
    private String url = "jdbc:postgresql://aws-0-eu-north-1.pooler.supabase.com:5432/postgres";
    private Connection connection;
    User currentUser;
    Timer timer;

    public DatabaseManager() {
        try {
            connection = DriverManager.getConnection(url, user, password);
            if (connection == null) {
                connection.close();
                System.exit(0);
            }
        } catch (SQLException e) {
            System.out.println("Connection error: " + e.getMessage());
            try {
                connection.close();
            } catch (SQLException ex) {
            }
            System.exit(0);
        }
    }

    public User autorization(String username, String password) {
        if (timer != null) {
            timer.cancel();
        }
        String query = "SELECT * FROM users where username = ? and password = ?";
        PreparedStatement statement = null;
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
                getProjectsForCurrentUser();
                startDeadlineTimer(connection);
            }
            if (currentUser.getRole() == Role.ADMIN){
                readUsers();
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

    public List<User> readUsers() {
        List<User> users = new ArrayList<>();
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
            if (currentUser.getRole() == Role.ADMIN){
                ExcelOperator.exportUsers(users);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public List<Project> getProjectsForCurrentUser() {

        List<Project> projects = new ArrayList<>();
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
                getProjectExecutors(project);
                getProjectTasks(project);
                projects.add(project);
            }
            projects.sort(Comparator.comparing((Project project) -> project.getEndDate()));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return projects;
    }

    public List<ProjectTask> getProjectTasks(Project project) {
        List<ProjectTask> tasks = new ArrayList<>();
        String taskQuery = "SELECT * FROM tasks WHERE project_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(taskQuery)) {
            statement.setInt(1, project.getProjectID());
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String priorityStr = resultSet.getString("priority");
                int executorId = resultSet.getInt("executor_id");
                User executor = getTaskExecutor(executorId, connection);
                PriorityLevel priority = PriorityLevel.valueOf(priorityStr);
                String statusStr = resultSet.getString("status");
                Status status = Status.valueOf(statusStr);
                boolean isRated = resultSet.getBoolean("is_rated");
                ProjectTask task = new ProjectTask(resultSet.getInt("task_id"),
                        resultSet.getString("task_title"),
                        resultSet.getString("task_description"),
                        executor,
                        priority,
                        status,
                        resultSet.getTimestamp("created_at").toLocalDateTime(),
                        resultSet.getDate("deadline").toLocalDate(),
                        isRated
                );
                tasks.add(task);
            }
            project.setTasks(tasks);
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return tasks;
    }

    public ProjectTask getTaskByID(int taskID) {
        ProjectTask task = null;
        String taskQuery = "SELECT * FROM tasks WHERE task_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(taskQuery)) {
            statement.setInt(1, taskID);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String priorityStr = resultSet.getString("priority");
                int executorId = resultSet.getInt("executor_id");
                User executor = getTaskExecutor(executorId, connection);
                PriorityLevel priority = PriorityLevel.valueOf(priorityStr);
                String statusStr = resultSet.getString("status");
                Status status = Status.valueOf(statusStr);
                boolean isRated = resultSet.getBoolean("is_rated");
                task = new ProjectTask(resultSet.getInt("task_id"),
                        resultSet.getString("task_title"),
                        resultSet.getString("task_description"),
                        executor,
                        priority,
                        status,
                        resultSet.getTimestamp("created_at").toLocalDateTime(),
                        resultSet.getDate("deadline").toLocalDate(),
                        isRated
                );
                getTaskHistory(task);
                getComments(task);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return task;
    }

    public List<ProjectTask> getTasksForCurrentUser() {
        List<ProjectTask> userTasks = new ArrayList<>();
        String userTasksQuery = "SELECT t.*, p.project_name FROM tasks t "
                + "JOIN projects p ON t.project_id = p.project_id "
                + "WHERE t.executor_id = ? "
                + "ORDER BY t.deadline";
        try (PreparedStatement statement = connection.prepareStatement(userTasksQuery)) {
            statement.setInt(1, currentUser.getUserId());
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String priorityStr = resultSet.getString("priority");
                PriorityLevel priority = PriorityLevel.valueOf(priorityStr);
                String statusStr = resultSet.getString("status");
                Status status = Status.valueOf(statusStr);
                boolean isRated = resultSet.getBoolean("is_rated");
                String projectName = resultSet.getString("project_name");
                ProjectTask task = new ProjectTask(resultSet.getInt("task_id"),
                        resultSet.getString("task_title"),
                        resultSet.getString("task_description"),
                        currentUser,
                        priority,
                        status,
                        resultSet.getTimestamp("created_at").toLocalDateTime(),
                        resultSet.getDate("deadline").toLocalDate(),
                        isRated
                );
                task.setProject(projectName);
                userTasks.add(task);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return userTasks;
    }

    private List<TaskHistory> getTaskHistory(ProjectTask task) {
        List<TaskHistory> taskHistory = new ArrayList<>();
        String historyQuery = "SELECT * FROM task_history WHERE task_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(historyQuery)) {
            statement.setInt(1, task.getTaskID());
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String statusStr = resultSet.getString("new_status");
                Status status = Status.valueOf(statusStr);
                LocalDateTime date = resultSet.getTimestamp("changed_at").toLocalDateTime();
                int creatorID = resultSet.getInt("changed_by");
                User creator = getCreatorByID(creatorID, connection);
                TaskHistory record = new TaskHistory(creator, status, date);
                taskHistory.add(record);
            }
            task.setHistory(taskHistory);
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return taskHistory;
    }

    private List<Comment> getComments(ProjectTask task) {
        List<Comment> taskComments = new ArrayList<>();
        String historyQuery = "SELECT * FROM comments WHERE task_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(historyQuery)) {
            statement.setInt(1, task.getTaskID());
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                LocalDateTime date = resultSet.getTimestamp("created_at").toLocalDateTime();
                int creatorID = resultSet.getInt("user_id");
                User creator = getCreatorByID(creatorID, connection);
                String message = resultSet.getString("message");
                Comment record = new Comment(creator, message, date);
                taskComments.add(record);
            }
            task.setComments(taskComments);
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return taskComments;
    }

    private List<User> getProjectExecutors(Project project) {
        List<User> executors = new ArrayList<>();
        String executorQuery = "SELECT u.* FROM users u INNER JOIN project_executors pe ON pe.user_id = u.user_id WHERE pe.project_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(executorQuery)) {
            statement.setInt(1, project.getProjectID());
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
            project.setExecutors(executors);
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
                executorsStmt = connection.prepareStatement(insertExecutors);
                for (User user : executors) {
                    executorsStmt.setInt(1, projectId);
                    executorsStmt.setInt(2, user.getUserId());
                    executorsStmt.addBatch(); //добавили в пакет запросов
                }
                executorsStmt.executeBatch(); //выполнили весь пакет целиком
            }
            connection.commit();
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
        String insertTask = "INSERT INTO tasks (task_title, task_description, project_id, creator_id, executor_id, priority, created_at, deadline) VALUES (?,?,?,?,?,?,?,?) returning task_id";
        String insertTaskHistory = "INSERT INTO task_history (task_id, changed_by, new_status, changed_at) VALUES (?,?,?,?)";
        String changeProjectStatus = "UPDATE projects SET status = 'IN_PROGRESS' WHERE project_id = ? AND status = 'NEW'";
        boolean result = false;
        try {
            connection.setAutoCommit(false);
            try (PreparedStatement taskStmt = connection.prepareStatement(insertTask)) {
                taskStmt.setString(1, name);
                taskStmt.setString(2, description);
                taskStmt.setInt(3, project.getProjectID());
                taskStmt.setInt(4, currentUser.getUserId());
                taskStmt.setInt(5, executor.getUserId());
                taskStmt.setObject(6, priority.name(), java.sql.Types.OTHER);
                taskStmt.setTimestamp(7, java.sql.Timestamp.valueOf(LocalDateTime.now()));
                taskStmt.setDate(8, java.sql.Date.valueOf(deadline));

                try (ResultSet rs = taskStmt.executeQuery()) {
                    if (rs.next()) {
                        int newTaskId = rs.getInt(1);
                        try (PreparedStatement historyStmt = connection.prepareStatement(insertTaskHistory); PreparedStatement projectStmt = connection.prepareStatement(changeProjectStatus)) {
                            historyStmt.setInt(1, newTaskId);
                            historyStmt.setInt(2, currentUser.getUserId());
                            historyStmt.setObject(3, Status.NEW.name(), java.sql.Types.OTHER);
                            taskStmt.setTimestamp(4, java.sql.Timestamp.valueOf(LocalDateTime.now()));
                            historyStmt.executeUpdate();
                            projectStmt.setInt(1, project.getProjectID());
                            projectStmt.executeUpdate();
                        }
                        result = true;
                        getProjectsForCurrentUser();
                    }
                }
            }
            connection.commit();
        } catch (SQLException ex) {
            try {
                connection.rollback();
            } catch (SQLException ignore) {
            }
            if ("23505".equals(ex.getSQLState())) {
                return false;
            } else {
                ex.printStackTrace();
            }
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ignore) {
            }
        }
        return result;
    }

    public boolean changeStatus(int taskId, Status newStatus) {
        String updateTaskSql = "UPDATE tasks SET status = ? WHERE task_id = ?";
        String insertTaskHistorySql = "INSERT INTO task_history (task_id, changed_by, new_status, changed_at) VALUES (?, ?, ?,?)";
        boolean result = false;
        try {
            connection.setAutoCommit(false);
            try (PreparedStatement updateTaskStmt = connection.prepareStatement(updateTaskSql)) {
                updateTaskStmt.setObject(1, newStatus.name(), java.sql.Types.OTHER);
                updateTaskStmt.setInt(2, taskId);
                int rowsUpdated = updateTaskStmt.executeUpdate();
                if (rowsUpdated != 1) {
                    connection.rollback();
                    return false;
                }
            }

            try (PreparedStatement insertHistoryStmt = connection.prepareStatement(insertTaskHistorySql)) {
                insertHistoryStmt.setInt(1, taskId);
                insertHistoryStmt.setInt(2, currentUser.getUserId());
                insertHistoryStmt.setObject(3, newStatus.name(), java.sql.Types.OTHER);
                insertHistoryStmt.setTimestamp(4, java.sql.Timestamp.valueOf(LocalDateTime.now()));
                insertHistoryStmt.executeUpdate();
            }
            connection.commit();
            result = true;
        } catch (SQLException ex) {
            try {
                connection.rollback();
            } catch (SQLException ignore) {
            }
            ex.printStackTrace();
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ignore) {
            }
        }
        return result;
    }

    public boolean endProject(int projectId) {
        boolean result = false;
        String updateProjectSql = "UPDATE projects SET status = 'DONE' WHERE project_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(updateProjectSql)) {
            statement.setInt(1, projectId);
            statement.executeUpdate();
            result = true;
        } catch (SQLException ex) {
            ex.printStackTrace();
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
            readUsers();
        } catch (SQLException ex) {
            if ("23505".equals(ex.getSQLState())) {
                return false;
            } else {
                ex.printStackTrace();
            }
        }
        return result;
    }

    public boolean createComments(int taskID, String message, LocalDateTime date) {
        String insertUser = "INSERT INTO comments (task_id, user_id, message, created_at) VALUES (?, ?, ?, ?)";
        boolean result = false;
        try (PreparedStatement statement = connection.prepareStatement(insertUser)) {
            statement.setInt(1, taskID);
            statement.setInt(2, currentUser.getUserId());
            statement.setString(3, message);
            statement.setObject(4, java.sql.Timestamp.valueOf(date));
            statement.executeUpdate();
            result = true;
        } catch (SQLException ex) {
            if ("23505".equals(ex.getSQLState())) {
                return false;
            } else {
                ex.printStackTrace();
            }
        }
        return result;
    }

    public boolean addPointsForTask(int userId, Integer points, int taskId) {
        boolean result = false;
        if (points == null) {
            return false;
        }
        String addPointsQuery = "UPDATE users SET rate = rate + ? WHERE user_id = ?";
        String updateTaskSql = "UPDATE tasks SET is_rated = TRUE WHERE task_id = ?";
        try {
            connection.setAutoCommit(false);
            try (PreparedStatement updateTaskStmt = connection.prepareStatement(addPointsQuery)) {
                updateTaskStmt.setInt(1, points);
                updateTaskStmt.setInt(2, userId);
                int rowsUpdated = updateTaskStmt.executeUpdate();
                if (rowsUpdated != 1) {
                    connection.rollback();
                    return false;
                }
            }

            try (PreparedStatement updateTaskRated = connection.prepareStatement(updateTaskSql)) {
                updateTaskRated.setInt(1, taskId);
                updateTaskRated.executeUpdate();
            }
            connection.commit();
            result = true;
        } catch (SQLException ex) {
            try {
                connection.rollback();
            } catch (SQLException ignore) {
            }
            ex.printStackTrace();
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ignore) {
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

    private void startDeadlineTimer(Connection conn) {
        timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    findOverdueTasks();
                    findOverdueProjects();
                    notifyDeadlines();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }, 60000, 60000);
    }

    private void findOverdueTasks() {
        String overdueTasksQuery = "UPDATE tasks SET status = 'OVERDUE'  WHERE deadline < ? AND status NOT IN ('DONE', 'OVERDUE')";
        try (PreparedStatement statement = connection.prepareStatement(overdueTasksQuery)) {
            statement.setDate(1, Date.valueOf(LocalDate.now()));
            statement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void findOverdueProjects() {
        String endedProjectQuery = "UPDATE projects SET status = 'OVERDUE'  WHERE end_date < ? AND status NOT IN ('DONE', 'OVERDUE')";
        try (PreparedStatement statement = connection.prepareStatement(endedProjectQuery)) {
            statement.setDate(1, Date.valueOf(LocalDate.now()));
            statement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void notifyDeadlines() {
        String deadlinesUpcomingQuery = "SELECT task_title, deadline FROM tasks "
                + "WHERE deadline - ? <= 2 "
                + "AND deadline >= ? "
                + "AND status NOT IN ('DONE', 'OVERDUE') "
                + "AND executor_id = ?";
        String message = "Внимание! Приближается дедлайн следующих задач: \n";
        try (PreparedStatement ps = connection.prepareStatement(deadlinesUpcomingQuery)) {
            LocalDate today = LocalDate.now();
            ps.setDate(1, Date.valueOf(today));
            ps.setDate(2, Date.valueOf(today));
            ps.setInt(3, currentUser.getUserId());

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String title = rs.getString("task_title");
                String deadline = String.valueOf(rs.getDate("deadline"));
                message += title + " имеет дедлайн " + deadline + "\n";
            }
            if (!message.equals("Внимание! Приближается дедлайн следующих задач: \n")) {
                JOptionPane.showMessageDialog(null, message, null, JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public List<Project> getProjects() {
        return getProjectsForCurrentUser();
    }

}
