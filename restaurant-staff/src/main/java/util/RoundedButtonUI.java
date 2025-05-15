package util;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;

public class RoundedButtonUI extends BasicButtonUI {
    @Override
    public void paint(Graphics g, JComponent c) {
        AbstractButton b = (AbstractButton) c;
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Vẽ nền tròn
        g2.setColor(b.getBackground());
        g2.fillOval(0, 0, b.getWidth(), b.getHeight());

        // Vẽ text
        g2.setColor(b.getForeground());
        FontMetrics fm = g2.getFontMetrics();
        int x = (b.getWidth() - fm.stringWidth(b.getText())) / 2;
        int y = (b.getHeight() + fm.getAscent()) / 2 - 4;

        g2.drawString(b.getText(), x, y);
        g2.dispose();
    }

    @Override
    public Dimension getPreferredSize(JComponent c) {
        return new Dimension(100, 100);
    }
}
