/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mephi.b22901.exam_project;

import java.util.Date;

/**
 *
 * @author Регина
 */
public class TaskHistory {

    private User user;
    private TaskStatus oldStatus;
    private TaskStatus newStatus;
    private Date changed_at;

    public TaskHistory(User user, TaskStatus oldStatus, TaskStatus newStatus, Date changed_at) {
        this.user = user;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.changed_at = changed_at;
    }

    public User getUser() {
        return user;
    }

    public TaskStatus getOldStatus() {
        return oldStatus;
    }

    public TaskStatus getNewStatus() {
        return newStatus;
    }

    public Date getChanged_at() {
        return changed_at;
    }
    
    

}
