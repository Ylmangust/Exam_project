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

    public List<Project> getProjectsForCurrentUser(){
        return dataManager.getProjects();
    }
    
    public List<Task> getTasksForCurrentUser(){
        return dataManager.getUserTasks();
    }
    
    public List<User> getAllUsers(){
        return dataManager.getUsers();
    }
    
    public List <String> getProjectsNames(){
        return dataManager.getProjectNames();
    }
}
