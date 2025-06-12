/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package Model.enums;

/**
 *
 * @author Регина
 */
public enum Role {
    ADMIN("Администратор"),
    MANAGER("Менеджер"),
    EXECUTOR("Исполнитель");

    private String text;

    private Role(String text) {
        this.text = text;
    }

    public String getRole() {
        return text;
    }

    @Override
    public String toString() {
        return this.text;
    }

}
