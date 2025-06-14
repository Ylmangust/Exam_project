/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

/**
 *
 * @author Регина
 */
public class UserStats {
    private int overdueCount = 0;
    private int completedCount = 0;
    
    public void addOverdueCount(){
        overdueCount++;
    }
    
    public void addCompletedeCount(){
        completedCount++;
    }

    public int getOverdueCount() {
        return overdueCount;
    }

    public int getCompletedCount() {
        return completedCount;
    }
    
    
    
}
