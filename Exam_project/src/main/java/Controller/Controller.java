/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import View.GUI;
import java.util.List;
import javax.swing.JOptionPane;
import Model.DatabaseManager;
import Model.databaseEntities.Project;
import Model.databaseEntities.Task;
import Model.databaseEntities.User;
import Model.enums.PriorityLevel;
import Model.enums.Role;
import java.sql.Date;
import java.time.LocalDate;

/**
 *
 * @author Регина
 */
public class Controller {

    private GUI gui;
    private DatabaseManager dataManager;

    public Controller() {
        this.dataManager = new DatabaseManager();
        this.gui = new GUI(this);

    }

    public User authorization(String username, String password) {
        User user = null;
        if (this.dataManager != null) {
            user = dataManager.autorization(username, password);
        } else {
            JOptionPane.showMessageDialog(null, "data manager is null");
        }
        return user;
    }

    public User getCurrentUser() {
        return dataManager.getCurrentUser();
    }

    public List<Project> getProjectsForCurrentUser() {
        return dataManager.getProjects();
    }

    public List<Task> getTasksForCurrentUser() {
        return dataManager.getUserTasks();
    }

    public List<User> getAllUsers() {
        return dataManager.getUsers();
    }

    public List<String> getProjectsNames() {
        return dataManager.getProjectNames();
    }

    public int createNewProject(String projectName, String description, LocalDate startDate, LocalDate endDate, List<User> executors) {
        return dataManager.createProject(projectName, description, startDate, endDate, executors);
    }
    
    public boolean createNewUser(String fullName, String name, Role role){
        return dataManager.createUser(fullName, name, role);
    }
    
    public boolean createNewTask(String name, String description, Project project, User executor, PriorityLevel priority, LocalDate deadline){
        return dataManager.createTask(name, description, project, executor, priority, deadline);
    }
}
