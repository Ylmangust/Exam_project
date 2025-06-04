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
public class Comment {
    private User creator;
    private String message;
    private Date createdAt;
    private List <Attachment> attachments = new ArrayList<>();

    public Comment(User creator, String message, Date createdAt) {
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

    public Date getCreatedAt() {
        return createdAt;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }
    
    
    
}
