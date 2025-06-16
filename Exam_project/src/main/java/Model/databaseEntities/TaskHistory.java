/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model.databaseEntities;

import Model.enums.Status;
import java.time.LocalDateTime;

/**
 *
 * @author Регина
 */
public class TaskHistory {

    private User user;
    private Status statusSet;
    private LocalDateTime changed_at;

    public TaskHistory(User user, Status statusSet, LocalDateTime changed_at) {
        this.user = user;
        this.statusSet = statusSet;
        this.changed_at = changed_at;
    }

    public User getUser() {
        return user;
    }

    public Status getStatusSet() {
        return statusSet;
    }

    public LocalDateTime getChanged_at() {
        return changed_at;
    }
    
    

}
