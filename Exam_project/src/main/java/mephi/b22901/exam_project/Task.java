/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mephi.b22901.exam_project;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Регина
 */
public class Task {

    private String name;
    private String description;
    private User creator;
    private List<User> executors;
    private PriorityLevel priority;
    private TaskStatus status;
    private Date createdAt;
    private Date deadline;
    private Date completedAt;
    private List <Comment> comments = new ArrayList<>();

    public Task(String name, String description, User creator, List<User> executors, PriorityLevel priority, TaskStatus status, Date createdAt, Date deadline, Date completedAt, List <Comment> comments) {
        this.name = name;
        this.description = description;
        this.creator = creator;
        this.executors = executors;
        this.priority = priority;
        this.status = status;
        this.createdAt = createdAt;
        this.deadline = deadline;
        this.completedAt = completedAt;
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

    public List<User> getExecutors() {
        return executors;
    }

    public PriorityLevel getPriority() {
        return priority;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getDeadline() {
        return deadline;
    }

    public Date getCompletedAt() {
        return completedAt;
    }
    
    public void addComment(Comment comment){
        comments.add(comment);
    }
     
    @Override
    public String toString(){
        return name;
    }

}
