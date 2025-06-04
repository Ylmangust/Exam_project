/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mephi.b22901.exam_project;

/**
 *
 * @author Регина
 */
public class User {

    private String username;
    private String password;
    private String fullName;
    private String role;
    private int rate;

    public User(String userName, String pass, String fullName, String role, int rate) {
        this.username = userName;
        this.password = pass;
        this.fullName = fullName;
        this.role = role;
        this.rate = rate;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFullName() {
        return fullName;
    }

    public String getRole() {
        return role;
    }

    public int getRate() {
        return rate;
    }
    
    public void setRate(int rate){
        this.rate = rate;
    }
    
    @Override
    public String toString(){
        return fullName;
    }

}
