/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model.databaseEntities;

import Model.enums.Status;
import java.util.Date;

/**
 *
 * @author Регина
 */
public class TaskHistory {

    private User user;
    private Status oldStatus;
    private Status newStatus;
    private Date changed_at;

    public TaskHistory(User user, Status oldStatus, Status newStatus, Date changed_at) {
        this.user = user;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.changed_at = changed_at;
    }

    public User getUser() {
        return user;
    }

    public Status getOldStatus() {
        return oldStatus;
    }

    public Status getNewStatus() {
        return newStatus;
    }

    public Date getChanged_at() {
        return changed_at;
    }
    
    

}
