import javax.swing.*;
import java.awt.*;

public class ColorIcon implements Icon {
    private static final int ICON_WIDTH = 16;
    private static final int ICON_HEIGHT = 16;
    private Color color;

    public ColorIcon(Color color) {
        this.color = color;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        g.setColor(color);
        g.fillOval(x, y, ICON_WIDTH, ICON_HEIGHT);
    }

    @Override
    public int getIconWidth() {
        return ICON_WIDTH;
    }

    @Override
    public int getIconHeight() {
        return ICON_HEIGHT;
    }
}
