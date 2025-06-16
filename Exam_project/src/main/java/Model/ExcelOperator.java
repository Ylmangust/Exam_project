/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import Model.databaseEntities.User;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import mephi.b22901.exam_project.Exam_project;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author Регина
 */
public class ExcelOperator {

    public static void exportReport(String path, Map<User, UserStats> stats) {
        String[] colNames = {"Пользователь", "Выполненные задачи", "Просроченные задачи", "Общий балл"};
        int columns = colNames.length;

        if (!path.endsWith(".xlsx")) {
            path = path + ".xlsx";
        }
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Статистика по участникам");

        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        Row header1 = sheet.createRow(0);
        for (int i = 0; i < columns; i++) {
            Cell cell = header1.createCell(i);
            cell.setCellValue(colNames[i]);
            cell.setCellStyle(style);
        }

        int count = 1;
        for (Map.Entry<User, UserStats> entry : stats.entrySet()) {
            User user = entry.getKey();
            UserStats userStats = entry.getValue();
            Row row = sheet.createRow(count);
            row.createCell(0).setCellValue(user.getFullName());
            row.createCell(1).setCellValue(userStats.getCompletedCount());
            row.createCell(2).setCellValue(userStats.getOverdueCount());
            row.createCell(3).setCellValue(user.getRate());
            count++;
        }

        for (int i = 0; i < columns; i++) {
            sheet.autoSizeColumn(i);
        }

        try {
            FileOutputStream out = new FileOutputStream(new File(path));
            workbook.write(out);
            JOptionPane.showMessageDialog(null, "Отчет успешно экспортирован!\nРасположение:  " + path, null, JOptionPane.INFORMATION_MESSAGE);
            workbook.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void exportUsers(List<User> users) {
        String[] colNames = {"Имя пользователя", "Логин", "Пароль", "Роль"};
        int columns = colNames.length;

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Логины и пароли");

        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        Row header1 = sheet.createRow(0);
        for (int i = 0; i < columns; i++) {
            Cell cell = header1.createCell(i);
            cell.setCellValue(colNames[i]);
            cell.setCellStyle(style);
        }

        int count = 1;
        for (User user : users) {
            Row row = sheet.createRow(count);
            row.createCell(0).setCellValue(user.getFullName());
            row.createCell(1).setCellValue(user.getUsername());
            row.createCell(2).setCellValue(user.getPassword());
            row.createCell(3).setCellValue(user.getRole().toString());
            count++;
        }

        for (int i = 0; i < columns; i++) {
            sheet.autoSizeColumn(i);
        }

        try {
            String dir = new File(Exam_project.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent();
            String pathToSave = dir + File.separator + "Информация о пользователях.xlsx";
            FileOutputStream out = new FileOutputStream(new File(pathToSave));
            workbook.write(out);
            workbook.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException ex) {
            Logger.getLogger(ExcelOperator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
