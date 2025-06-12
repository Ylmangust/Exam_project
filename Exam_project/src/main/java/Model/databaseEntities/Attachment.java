/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model.databaseEntities;

/**
 *
 * @author Регина
 */
public class Attachment {
    private String fileName;
    private String filePath;
    private String fileSize;

    public Attachment(String fileName, String filePath, String fileSize) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileSize = fileSize;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getFileSize() {
        return fileSize;
    }
}
