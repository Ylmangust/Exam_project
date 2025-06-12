/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model.databaseEntities;

import Model.enums.Status;
import Model.enums.PriorityLevel;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Регина
 */
public class Task {

    private int taskID;
    private String name;
    private String description;
    private User creator;
    private User executor;
    private PriorityLevel priority;
    private Status status;
    private LocalDate createdAt;
    private LocalDate deadline;
    private LocalDate completedAt;
    private List<Comment> comments = new ArrayList<>();
    private Project project;

    public Task(int id, String name, String description, User creator, User executor, PriorityLevel priority, Status status, LocalDate createdAt, LocalDate deadline) {
        this.taskID = id;
        this.name = name;
        this.description = description;
        this.creator = creator;
        this.executor = executor;
        this.priority = priority;
        this.status = status;
        this.createdAt = createdAt;
        this.deadline = deadline;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public User getCreator() {
        return creator;
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

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public LocalDate getCompletedAt() {
        return completedAt;
    }

    public void addComment(Comment comment) {
        comments.add(comment);
    }

    public void setCompletedAt(LocalDate completedAt) {
        this.completedAt = completedAt;
    }

    @Override
    public String toString() {
        return name + project.getProjectName();
    }

}
