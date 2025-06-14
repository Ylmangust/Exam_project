/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import Model.databaseEntities.Project;
import Model.databaseEntities.Task;
import Model.databaseEntities.User;
import Model.enums.Status;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Регина
 */
public class StatisticsOperator {

    public static Map<User, UserStats> getUserStats(List<User> users, List<Project> projects) {
        Map<User, UserStats> stats = new HashMap<>();
        for (User user : users) {
            UserStats personStats = new UserStats();
            for (Project project : projects) {
                List<Task> tasks = project.getTasks();
                if (tasks == null || tasks.isEmpty()) {
                    continue;
                }
                for (Task task : tasks) {
                    if (task.getExecutor().getUserId() == user.getUserId()) {
                        System.out.println(user);
                        if (task.getStatus() == Status.DONE) {
                            personStats.addCompletedeCount();
                        } else if (task.getStatus() == Status.OVERDUE) {
                            personStats.addOverdueCount();
                        }
                    }
                }
            }
            stats.put(user, personStats);
        }
        return stats;
    }
}
