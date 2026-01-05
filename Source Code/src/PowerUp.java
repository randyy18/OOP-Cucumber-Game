import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class PowerUp {
    private double x, y;
    private int width, height;
    private PowerUpType type;
    private boolean collected;

    public enum PowerUpType {
        SPEED_BOOST("Speed Boost", 5000, new Color(255, 215, 0)),
        IMMUNITY("Immunity", 4000, new Color(100, 149, 237)),
        FREEZE_GUARDS("Freeze", 3000, new Color(200, 230, 255));

        private final String displayName;
        private final int durationMs;
        private final Color color;

        PowerUpType(String displayName, int durationMs, Color color) {
            this.displayName = displayName;
            this.durationMs = durationMs;
            this.color = color;
        }

        public String getDisplayName() {
            return displayName;
        }

        public int getDurationMs() {
            return durationMs;
        }

        public Color getColor() {
            return color;
        }
    }

    public PowerUp(PowerUpType type, double x, double y) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.width = 20;
        this.height = 20;
        this.collected = false;
    }

    public void draw(Graphics2D g2d) {
        if (collected)
            return;

        int px = (int) x;
        int py = (int) y;

        switch (type) {
            case SPEED_BOOST:
                drawShoes(g2d, px, py);
                break;
            case IMMUNITY:
                drawShield(g2d, px, py);
                break;
            case FREEZE_GUARDS:
                drawIce(g2d, px, py);
                break;
        }
    }

    private void drawShoes(Graphics2D g2d, int px, int py) {
        g2d.setColor(type.getColor());
        g2d.fillRoundRect(px + 2, py + 12, 16, 8, 3, 3);

        g2d.setColor(new Color(200, 160, 0));
        g2d.fillRect(px + 2, py + 10, 16, 4);

        g2d.setColor(Color.WHITE);
        g2d.fillOval(px + 5, py + 14, 4, 4);
        g2d.fillOval(px + 11, py + 14, 4, 4);

        g2d.setColor(new Color(255, 255, 255));
        int[] wingX1 = { px + 1, px - 4, px + 1, px + 5 };
        int[] wingY1 = { py + 10, py + 2, py + 6, py + 10 };
        g2d.fillPolygon(wingX1, wingY1, 4);

        int[] wingX2 = { px + 19, px + 24, px + 19, px + 15 };
        int[] wingY2 = { py + 10, py + 2, py + 6, py + 10 };
        g2d.fillPolygon(wingX2, wingY2, 4);

        g2d.setColor(new Color(200, 200, 200));
        g2d.drawLine(px - 2, py + 4, px + 2, py + 8);
        g2d.drawLine(px + 22, py + 4, px + 18, py + 8);
    }

    private void drawShield(Graphics2D g2d, int px, int py) {
        g2d.setColor(type.getColor());
        int[] xPoints = { px + width / 2, px, px, px + width / 4, px + width / 2, px + 3 * width / 4, px + width,
                px + width };
        int[] yPoints = { py, py + height / 4, py + height / 2, py + 3 * height / 4, py + height, py + 3 * height / 4,
                py + height / 2, py + height / 4 };
        g2d.fillPolygon(xPoints, yPoints, 8);

        g2d.setColor(new Color(150, 200, 255));
        g2d.fillOval(px + width / 2 - 4, py + height / 2 - 4, 8, 8);

        g2d.setColor(Color.WHITE);
        g2d.drawLine(px + width / 2, py + height / 4, px + width / 2, py + 3 * height / 4);
        g2d.drawLine(px + width / 4, py + height / 2, px + 3 * width / 4, py + height / 2);
    }

    private void drawIce(Graphics2D g2d, int px, int py) {
        g2d.setColor(new Color(180, 220, 255));
        g2d.fillRect(px + 2, py + 5, 16, 14);

        g2d.setColor(type.getColor());
        int[] topX = { px + 2, px + 10, px + 18, px + 10 };
        int[] topY = { py + 5, py, py + 5, py + 5 };
        g2d.fillPolygon(topX, topY, 4);

        g2d.setColor(new Color(150, 200, 255));
        g2d.drawLine(px + 5, py + 7, px + 5, py + 17);
        g2d.drawLine(px + 10, py + 7, px + 10, py + 17);
        g2d.drawLine(px + 15, py + 7, px + 15, py + 17);

        g2d.setColor(Color.WHITE);
        g2d.fillOval(px + 3, py + 6, 3, 3);
        g2d.fillOval(px + 12, py + 10, 2, 2);

        g2d.setColor(new Color(100, 180, 255));
        g2d.drawRect(px + 2, py + 5, 16, 14);
    }

    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, width, height);
    }

    public boolean isCollected() {
        return collected;
    }

    public void collect() {
        this.collected = true;
    }

    public void reset() {
        this.collected = false;
    }

    public PowerUpType getType() {
        return type;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
