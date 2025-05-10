package util;

import javax.swing.border.AbstractBorder;
import java.awt.*;

public class RoundedBorder extends AbstractBorder {
    private final int radius;
    private final boolean isSender;

    public RoundedBorder(int radius, boolean isSender) {
        this.radius = radius;
        this.isSender = isSender;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(Color.GRAY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int arc = radius * 2;
        int shadowOffset = 1;

        // Shadow
        g2d.setColor(new Color(0, 0, 0, 30));
        g2d.fillRoundRect(x + shadowOffset, y + shadowOffset, width - 2, height - 2, arc, arc);

        // Border
        g2d.setColor(c.getBackground());
        g2d.fillRoundRect(x, y, width - 2, height - 2, arc, arc);

        g2d.dispose();
    }
}
