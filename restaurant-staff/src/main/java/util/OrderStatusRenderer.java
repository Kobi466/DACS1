package util;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class OrderStatusRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        String status = model.getValueAt(row, 4).toString();

        if (!isSelected) {
            switch (status) {
                case "CONFIRMED" -> c.setBackground(new Color(0xD4EFDF));
                case "CANCELLED" -> c.setBackground(new Color(0xFADBD8));
                case "COMPLETED" -> c.setBackground(new Color(0xD6EAF8));
                default -> c.setBackground(Color.WHITE);
            }
        } else {
            c.setBackground(table.getSelectionBackground());
        }

        return c;
    }
}
