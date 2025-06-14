/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model.databaseEntities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Регина
 */
public class Comment {
    private User creator;
    private String message;
    private LocalDateTime createdAt;
    private List <Attachment> attachments = new ArrayList<>();

    public Comment(User creator, String message, LocalDateTime createdAt) {
        this.creator = creator;
        this.message = message;
        this.createdAt = createdAt;
    }

    public User getCreator() {
        return creator;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }
    
    
    
}
