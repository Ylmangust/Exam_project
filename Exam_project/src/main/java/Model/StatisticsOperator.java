/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import Model.databaseEntities.Project;
import Model.databaseEntities.ProjectTask;
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

    public static Map<User, UserStats> getUserStats(List<User> users, List<ProjectTask> projectTasks) {
        Map<User, UserStats> stats = new HashMap<>();
        for (User user : users) {
            UserStats personStats = new UserStats();
            if (projectTasks == null || projectTasks.isEmpty()) {
                continue;
            }
            for (ProjectTask task : projectTasks) {
                if (task.getExecutor().getUserId() == user.getUserId()) {
                    if (task.getStatus() == Status.DONE) {
                        personStats.addCompletedeCount();
                    } else if (task.getStatus() == Status.OVERDUE) {
                        personStats.addOverdueCount();
                    }
                }
            }
            stats.put(user, personStats);
        }
        return stats;
    }
}
