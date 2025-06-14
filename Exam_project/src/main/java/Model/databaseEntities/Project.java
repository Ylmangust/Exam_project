/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model.databaseEntities;

import Model.enums.Status;
import java.time.LocalDate;
import java.util.List;

/**
 *
 * @author Регина
 */
public class Project {

    private int projectID;
    private String projectName;
    private String description;
    private User creator;
    private List<User> executors;
    private List<Task> tasks;
    private Status status;
    private LocalDate startDate;
    private LocalDate endDate;

    public Project(int id, String projectName, String description, User creator, Status status, LocalDate startDate, LocalDate endDate) {
        this.projectID = id;
        this.projectName = projectName;
        this.description = description;
        this.creator = creator;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public void setExecutors(List<User> executors) {
        this.executors = executors;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public int getProjectID() {
        return projectID;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getDescription() {
        return description;
    }

    public User getCreator() {
        return creator;
    }

    public List<User> getExecutors() {
        return executors;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public Status getStatus() {
        return status;
    }
    
    

    @Override
    public String toString() {
        return projectName;
    }
}
