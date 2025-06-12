/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package View;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import Model.enums.PriorityLevel;
import Model.enums.Status;

/**
 *
 * @author Регина
 */
public class ColorRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if (value instanceof Status) {
            Status status = (Status) value;
            switch (status) {
                case NEW:
                    setForeground(new Color(33, 150, 243)); // синий
                    break;
                case IN_PROGRESS:
                    setForeground(new Color(255, 152, 0)); // оранжевый
                    break;
                case DONE:
                    setForeground(new Color(46, 125, 50)); // зелёный
                    break;
                case OVERDUE:
                    setForeground(new Color(229, 57, 53)); // красный
                    break;
                default:
                    setForeground(Color.BLACK);
            }
        } else if (value instanceof PriorityLevel) {
            PriorityLevel level = (PriorityLevel) value;
            switch (level) {
                case HIGH:
                    setForeground(new Color(229, 57, 53)); // насыщенный красный
                    break;
                case MEDIUM:
                    setForeground(new Color(255, 152, 0)); // оранжевый
                    break;
                case LOW:
                    setForeground(new Color(46, 125, 50)); // зеленый
                    break;
            }
        } else {
            setForeground(Color.BLACK);
        }

        if (isSelected) {
            setForeground(table.getSelectionForeground());
        }
        return this;
    }
}
