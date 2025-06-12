/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model.databaseEntities;

import Model.enums.Role;

/**
 *
 * @author Регина
 */
public class User {

    private int userId;
    private String username;
    private String password;
    private String fullName;
    private Role role;
    private int rate;

    public User(int id, String userName, String pass, String fullName, Role role, int rate) {     
        this.userId = id;
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

    public Role getRole() {
        return role;
    }

    public int getRate() {
        return rate;
    }
    
    public void setRate(int rate){
        this.rate = rate;
    }

    public int getUserId() {
        return userId;
    }
    
    
    
    @Override
    public String toString(){
        return fullName;
    }

}
