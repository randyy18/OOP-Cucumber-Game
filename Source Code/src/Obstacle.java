import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Obstacle {
    private int x, y, width, height;
    private ObstacleType type;

    public enum ObstacleType {
        BED(60, 40, new Color(139, 69, 19)),
        CHAIR(25, 25, new Color(160, 82, 45)),
        BOOKSHELF(50, 30, new Color(101, 67, 33)),
        TABLE(45, 35, new Color(205, 133, 63)),
        DRESSER(40, 30, new Color(139, 90, 43)),
        WORKBENCH(55, 25, new Color(128, 128, 128));

        private final int defaultWidth;
        private final int defaultHeight;
        private final Color color;

        ObstacleType(int defaultWidth, int defaultHeight, Color color) {
            this.defaultWidth = defaultWidth;
            this.defaultHeight = defaultHeight;
            this.color = color;
        }

        public int getDefaultWidth() {
            return defaultWidth;
        }

        public int getDefaultHeight() {
            return defaultHeight;
        }

        public Color getColor() {
            return color;
        }
    }

    public Obstacle(ObstacleType type, int x, int y) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.width = type.getDefaultWidth();
        this.height = type.getDefaultHeight();
    }

    public Obstacle(ObstacleType type, int x, int y, int width, int height) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void draw(Graphics2D g2d) {
        switch (type) {
            case BED:
                drawBed(g2d);
                break;
            case CHAIR:
                drawChair(g2d);
                break;
            case BOOKSHELF:
                drawBookshelf(g2d);
                break;
            case TABLE:
                drawTable(g2d);
                break;
            case DRESSER:
                drawDresser(g2d);
                break;
            case WORKBENCH:
                drawWorkbench(g2d);
                break;
        }
    }

    private void drawBed(Graphics2D g2d) {
        g2d.setColor(type.getColor());
        g2d.fillRect(x, y, width, height);
        g2d.setColor(new Color(70, 35, 10));
        g2d.drawRect(x, y, width, height);

        g2d.setColor(new Color(255, 250, 240));
        g2d.fillRect(x + 2, y + 2, width - 4, height - 4);

        g2d.setColor(new Color(135, 206, 235));
        g2d.fillRect(x + 4, y + 4, width / 3, height - 8);

        g2d.setColor(new Color(200, 200, 200));
        g2d.drawLine(x + width / 3 + 10, y + height / 3, x + width - 5, y + height / 3);
        g2d.drawLine(x + width / 3 + 10, y + 2 * height / 3, x + width - 5, y + 2 * height / 3);
    }

    private void drawChair(Graphics2D g2d) {
        g2d.setColor(type.getColor());
        g2d.fillRect(x, y, width, height * 2 / 3);

        g2d.setColor(new Color(101, 67, 33));
        g2d.fillRect(x, y + height * 2 / 3, width, height / 3);
        g2d.drawRect(x, y, width, height * 2 / 3);

        g2d.setColor(new Color(70, 45, 20));
        g2d.fillRect(x + 2, y + height * 2 / 3 + 2, width - 4, 3);
    }

    private void drawBookshelf(Graphics2D g2d) {
        g2d.setColor(type.getColor());
        g2d.fillRect(x, y, width, height);
        g2d.setColor(new Color(60, 40, 20));
        g2d.drawRect(x, y, width, height);

        g2d.setColor(new Color(80, 50, 30));
        g2d.drawLine(x, y + height / 3, x + width, y + height / 3);
        g2d.drawLine(x, y + 2 * height / 3, x + width, y + 2 * height / 3);

        Color[] bookColors = { Color.RED, Color.BLUE, Color.GREEN, new Color(139, 69, 19), Color.ORANGE };
        int bookWidth = 6;
        for (int row = 0; row < 3; row++) {
            int rowY = y + 2 + row * (height / 3);
            for (int i = 0; i < 6; i++) {
                g2d.setColor(bookColors[i % bookColors.length]);
                g2d.fillRect(x + 3 + i * (bookWidth + 1), rowY, bookWidth, height / 3 - 4);
            }
        }
    }

    private void drawTable(Graphics2D g2d) {
        g2d.setColor(type.getColor());
        g2d.fillRect(x, y, width, height);
        g2d.setColor(new Color(139, 90, 43));
        g2d.drawRect(x, y, width, height);

        g2d.setColor(new Color(180, 120, 60));
        g2d.fillRect(x + 3, y + 3, width - 6, height / 4);

        g2d.setColor(new Color(100, 65, 35));
        g2d.fillRect(x + 3, y + height - 8, 5, 6);
        g2d.fillRect(x + width - 8, y + height - 8, 5, 6);
    }

    private void drawDresser(Graphics2D g2d) {
        g2d.setColor(type.getColor());
        g2d.fillRect(x, y, width, height);
        g2d.setColor(new Color(100, 60, 30));
        g2d.drawRect(x, y, width, height);

        g2d.setColor(new Color(160, 100, 50));
        int drawerHeight = (height - 6) / 3;
        for (int i = 0; i < 3; i++) {
            int drawerY = y + 2 + i * (drawerHeight + 1);
            g2d.fillRect(x + 3, drawerY, width - 6, drawerHeight);
            g2d.setColor(new Color(255, 215, 0));
            g2d.fillOval(x + width / 2 - 3, drawerY + drawerHeight / 2 - 2, 6, 4);
            g2d.setColor(new Color(160, 100, 50));
        }
    }

    private void drawWorkbench(Graphics2D g2d) {
        g2d.setColor(type.getColor());
        g2d.fillRect(x, y, width, height);
        g2d.setColor(new Color(80, 80, 80));
        g2d.drawRect(x, y, width, height);

        g2d.setColor(new Color(139, 69, 19));
        g2d.fillRect(x + 2, y + 2, width - 4, 8);

        g2d.setColor(new Color(192, 192, 192));
        g2d.fillRect(x + 5, y + 12, 8, 6);
        g2d.fillRect(x + 20, y + 12, 12, 8);
        g2d.setColor(new Color(255, 0, 0));
        g2d.fillRect(x + 38, y + 12, 10, 5);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public boolean intersects(Rectangle rect) {
        return getBounds().intersects(rect);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public ObstacleType getType() {
        return type;
    }
}
