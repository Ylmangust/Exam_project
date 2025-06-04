/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package mephi.b22901.exam_project;

/**
 *
 * @author Регина
 */
public enum TaskStatus {
    NEW("Новый"),
    IN_PROGRESS("В процессе"),
    DONE("Завершен"),
    OVERDUE("Просрочен");
    
    private String text;
    
    private TaskStatus(String text){
        this.text = text;
    }
    
    public String getStatus(){
        return text;
    }
}
