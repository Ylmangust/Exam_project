package mephi.b22901.exam_project;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */

/**
 *
 * @author Регина
 */
public enum PriorityLevel {
    HIGH("Высокий"),
    MEDIUM("Средний"),
    LOW("Низкий");
    
    private String text;
    
    private PriorityLevel(String text){
        this.text = text;
    }
    
    public String getPriority(){
        return text;
    }
}
