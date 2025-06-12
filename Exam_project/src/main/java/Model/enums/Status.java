/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package Model.enums;

/**
 *
 * @author Регина
 */
public enum Status {
    NEW("Новый"),
    IN_PROGRESS("В процессе"),
    DONE("Завершен"),
    OVERDUE("Просрочен");
    
    private String text;
    
    private Status(String text){
        this.text = text;
    }
    
    public String getStatus(){
        return text;
    }
    
    @Override 
    public String toString(){
        return this.text;
    }
}
