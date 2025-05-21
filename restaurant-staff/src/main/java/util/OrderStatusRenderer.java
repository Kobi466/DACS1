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

        // Lấy trạng thái từ bảng
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        String status = model.getValueAt(row, 4).toString(); // Dữ liệu trạng thái luôn ở cột 4

        if (!isSelected) {
            // Thay đổi màu nền theo trạng thái
            switch (status) {
                case "🟡 Chờ xác nhận" -> c.setBackground(new Color(255, 255, 153)); // Màu vàng nhạt
                case "🟢 Đã xác nhận" -> c.setBackground(new Color(204, 255, 204)); // Màu xanh nhạt
                case "🔵 Đang chế biến" -> c.setBackground(new Color(204, 229, 255)); // Màu xanh đậm
                case "✅ Hoàn thành" -> c.setBackground(new Color(204, 255, 204)); // Màu xanh lá
                case "❌ Đã hủy" -> c.setBackground(new Color(255, 204, 204)); // Màu đỏ nhạt
                default -> c.setBackground(Color.WHITE); // Mặc định
            }
        } else {
            c.setBackground(table.getSelectionBackground());
        }

        return c;
    }
}
