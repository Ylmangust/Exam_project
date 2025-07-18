/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model.databaseEntities;

import Model.enums.Status;
import Model.enums.PriorityLevel;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Регина
 */
public class ProjectTask {

    private int taskID;
    private String name;
    private String description;
    private User executor;
    private PriorityLevel priority;
    private Status status;
    private LocalDateTime createdAt;
    private LocalDate deadline;
    private List<Comment> comments = new ArrayList<>();
    private List<TaskHistory> history = new ArrayList<>();
    private String projectName;
    private boolean isRated;

    public ProjectTask(int id, String name, String description, User executor, PriorityLevel priority, Status status, LocalDateTime createdAt, LocalDate deadline, boolean isRated) {
        this.taskID = id;
        this.name = name;
        this.description = description;
        this.executor = executor;
        this.priority = priority;
        this.status = status;
        this.createdAt = createdAt;
        this.deadline = deadline;
        this.isRated = isRated;
    }

    public String getProject() {
        return projectName;
    }

    public void setProject(String project) {
        this.projectName = project;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public void setHistory(List<TaskHistory> history) {
        this.history = history;
    }

    public List<TaskHistory> getHistory() {
        return history;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }


    public User getExecutor() {
        return executor;
    }

    public PriorityLevel getPriority() {
        return priority;
    }

    public Status getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public int getTaskID() {
        return taskID;
    }

    public boolean getIsRated() {
        return isRated;
    }

    public void setIsRated(boolean isRated) {
        this.isRated = isRated;
    }

    public void addComment(Comment comment) {
        comments.add(comment);
    }

    public List<Comment> getComments() {
        return comments;
    }

    @Override
    public String toString() {
        return name;
    }

}
