/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mephi.b22901.exam_project;

import java.util.Date;
import java.util.List;

/**
 *
 * @author Регина
 */
public class Project {

    private String projectName;
    private String description;
    private User creator;
    private List<User> executors;
    private List<Task> tasks;
    private Date startDate;
    private Date endDate;

    public Project(String projectName, String description, User creator, List<User> executors, List<Task> tasks, Date startDate, Date endDate) {
        this.projectName = projectName;
        this.description = description;
        this.creator = creator;
        this.executors = executors;
        this.tasks = tasks;
        this.startDate = startDate;
        this.endDate = endDate;
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

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    @Override
    public String toString() {
        return projectName;
    }
}
