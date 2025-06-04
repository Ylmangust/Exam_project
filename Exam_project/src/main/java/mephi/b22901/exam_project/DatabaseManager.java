/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mephi.b22901.exam_project;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 *
 * @author Регина
 */
public class DatabaseManager {

    private String password = "UGfn%f@ziWFBwl";
    private String user = "postgres.oghifeiboocdhvyuzkuo";
    private String url = "jdbc:postgresql://aws-0-eu-north-1.pooler.supabase.com:5432/postgres";
    private Connection connection;

    public DatabaseManager() {
        try {
            connection = DriverManager.getConnection(url, user, password);
            if (connection != null) {
                JOptionPane.showMessageDialog(null, "Вы успешно подключились к базе данных!", null, JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            System.out.println("Connection error: " + e.getMessage());
            System.exit(0);
        }
    }
    
    

}
