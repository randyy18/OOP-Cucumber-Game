import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

public class Room {
    private String name;
    private int x, y, width, height;
    private Color floorColor;
    private Color wallColor;
    private Guard.RiskLevel riskLevel;
    private Guard guard;
    private Cucumber cucumber;
    private List<Obstacle> obstacles;
    private PowerUp powerUp;

    public Room(String name, int x, int y, int width, int height,
            Color floorColor, Guard.RiskLevel riskLevel) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.floorColor = floorColor;
        this.wallColor = new Color(100, 100, 100);
        this.riskLevel = riskLevel;
        this.obstacles = new ArrayList<>();
    }

    public void draw(Graphics2D g2d) {
        g2d.setColor(floorColor);
        g2d.fillRect(x, y, width, height);

        g2d.setColor(wallColor);
        g2d.drawRect(x, y, width, height);
        g2d.drawRect(x + 1, y + 1, width - 2, height - 2);

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.drawString(name, x + 5, y + 18);

        Color riskColor;
        switch (riskLevel) {
            case HIGH:
                riskColor = new Color(255, 100, 100);
                break;
            case MEDIUM:
                riskColor = new Color(255, 200, 100);
                break;
            default:
                riskColor = new Color(100, 255, 100);
                break;
        }
        g2d.setColor(riskColor);
        g2d.fillOval(x + width - 20, y + 5, 12, 12);
        g2d.setColor(Color.BLACK);
        g2d.drawOval(x + width - 20, y + 5, 12, 12);

        if (cucumber != null) {
            cucumber.draw(g2d);
        }

        if (powerUp != null) {
            powerUp.draw(g2d);
        }

        for (Obstacle obstacle : obstacles) {
            obstacle.draw(g2d);
        }

        if (guard != null) {
            guard.draw(g2d, new Rectangle(x, y, width, height));
        }
    }

    public void update() {
        if (guard != null) {
            guard.update();
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public boolean containsPoint(double px, double py) {
        return px >= x && px <= x + width && py >= y && py <= y + height;
    }

    public String getName() {
        return name;
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

    public Guard.RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public Guard getGuard() {
        return guard;
    }

    public void setGuard(Guard guard) {
        this.guard = guard;
    }

    public Cucumber getCucumber() {
        return cucumber;
    }

    public void setCucumber(Cucumber cucumber) {
        this.cucumber = cucumber;
    }

    public void addObstacle(Obstacle obstacle) {
        this.obstacles.add(obstacle);
    }

    public List<Obstacle> getObstacles() {
        return obstacles;
    }

    public PowerUp getPowerUp() {
        return powerUp;
    }

    public void setPowerUp(PowerUp powerUp) {
        this.powerUp = powerUp;
    }

    public boolean isUnlocked() {
        return cucumber == null || cucumber.isCollected();
    }

    public static class Cucumber {
        private double x;
        private double y;
        private int width;
        private int height;
        private boolean collected;
        private Color color;

        public Cucumber(double x, double y) {
            this.x = x;
            this.y = y;
            this.width = 25;
            this.height = 12;
            this.collected = false;
            this.color = new Color(50, 205, 50);
        }

        public void draw(Graphics2D g2d) {
            if (collected)
                return;

            g2d.setColor(color);
            g2d.fillOval((int) x, (int) y, width, height);

            g2d.setColor(new Color(34, 139, 34));
            g2d.drawLine((int) x + 5, (int) y + 2, (int) x + 5, (int) y + 10);
            g2d.drawLine((int) x + 12, (int) y + 2, (int) x + 12, (int) y + 10);
            g2d.drawLine((int) x + 19, (int) y + 2, (int) x + 19, (int) y + 10);

            g2d.setColor(new Color(0, 100, 0));
            g2d.drawOval((int) x, (int) y, width, height);

            g2d.setColor(Color.YELLOW);
            g2d.fillOval((int) x - 3, (int) y - 3, 5, 5);
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

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }
    }
}
