import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;

public class Guard extends Entity {
    private GuardType type;
    private double patrolStartX, patrolStartY;
    private double patrolEndX, patrolEndY;
    private boolean movingToEnd;
    private int detectionRadius;

    public Guard(GuardType type, double startX, double startY, double endX, double endY) {
        super(startX, startY, 35, 35);
        this.type = type;
        this.patrolStartX = startX;
        this.patrolStartY = startY;
        this.patrolEndX = endX;
        this.patrolEndY = endY;
        this.movingToEnd = true;
        this.speed = type.getRiskLevel().getGuardSpeed();
        this.detectionRadius = type.getRiskLevel().getDetectionRadius();
    }

    @Override
    public void update() {
        double targetX = movingToEnd ? patrolEndX : patrolStartX;
        double targetY = movingToEnd ? patrolEndY : patrolStartY;

        double dx = targetX - x;
        double dy = targetY - y;
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance < speed) {
            movingToEnd = !movingToEnd;
        } else {
            x += (dx / distance) * speed;
            y += (dy / distance) * speed;
        }
    }

    @Override
    public void draw(Graphics2D g2d) {
        draw(g2d, null);
    }

    public void draw(Graphics2D g2d, Rectangle roomBounds) {
        Color radiusColor = new Color(
                type.getColor().getRed(),
                type.getColor().getGreen(),
                type.getColor().getBlue(),
                50);
        g2d.setColor(radiusColor);

        if (roomBounds != null) {
            Shape oldClip = g2d.getClip();
            g2d.setClip(roomBounds);
            g2d.fillOval(
                    (int) (x + width / 2 - detectionRadius),
                    (int) (y + height / 2 - detectionRadius),
                    detectionRadius * 2,
                    detectionRadius * 2);
            g2d.setClip(oldClip);
        } else {
            g2d.fillOval(
                    (int) (x + width / 2 - detectionRadius),
                    (int) (y + height / 2 - detectionRadius),
                    detectionRadius * 2,
                    detectionRadius * 2);
        }

        switch (type) {
            case CAT:
                drawCat(g2d);
                break;
            case BROTHER:
                drawBrother(g2d);
                break;
            case MOM:
                drawMom(g2d);
                break;
            case DAD:
                drawDad(g2d);
                break;
            case SISTER:
                drawSister(g2d);
                break;
        }
    }

    private void drawCat(Graphics2D g2d) {
        int px = (int) x;
        int py = (int) y;

        g2d.setColor(type.getColor());
        g2d.fillOval(px, py + 10, width, height - 10);

        g2d.fillPolygon(new int[] { px + 5, px + 12, px + 2 }, new int[] { py + 12, py + 5, py }, 3);
        g2d.fillPolygon(new int[] { px + width - 5, px + width - 12, px + width - 2 },
                new int[] { py + 12, py + 5, py }, 3);

        g2d.setColor(new Color(255, 182, 193));
        g2d.fillPolygon(new int[] { px + 6, px + 10, px + 4 }, new int[] { py + 10, py + 6, py + 3 }, 3);
        g2d.fillPolygon(new int[] { px + width - 6, px + width - 10, px + width - 4 },
                new int[] { py + 10, py + 6, py + 3 }, 3);

        g2d.setColor(Color.WHITE);
        g2d.fillOval(px + 8, py + 15, 8, 8);
        g2d.fillOval(px + 19, py + 15, 8, 8);
        g2d.setColor(Color.BLACK);
        g2d.fillOval(px + 10, py + 17, 4, 4);
        g2d.fillOval(px + 21, py + 17, 4, 4);

        g2d.setColor(new Color(255, 150, 150));
        g2d.fillOval(px + 14, py + 23, 6, 4);

        g2d.setColor(Color.BLACK);
        g2d.drawLine(px + 5, py + 24, px - 2, py + 22);
        g2d.drawLine(px + 5, py + 26, px - 2, py + 26);
        g2d.drawLine(px + width - 5, py + 24, px + width + 2, py + 22);
        g2d.drawLine(px + width - 5, py + 26, px + width + 2, py + 26);
    }

    private void drawBrother(Graphics2D g2d) {
        int px = (int) x;
        int py = (int) y;

        g2d.setColor(new Color(255, 220, 180));
        g2d.fillOval(px + 5, py + 10, width - 10, height - 12);

        g2d.setColor(type.getColor());
        g2d.fillArc(px + 3, py + 5, width - 6, 20, 0, 180);
        g2d.fillRect(px + 3, py + 14, width - 6, 5);
        g2d.fillRect(px + width - 8, py + 14, 12, 4);

        g2d.setColor(Color.WHITE);
        g2d.fillOval(px + 10, py + 18, 6, 6);
        g2d.fillOval(px + 19, py + 18, 6, 6);
        g2d.setColor(Color.BLACK);
        g2d.fillOval(px + 12, py + 20, 3, 3);
        g2d.fillOval(px + 21, py + 20, 3, 3);

        g2d.drawArc(px + 12, py + 24, 10, 6, 180, 180);
    }

    private void drawMom(Graphics2D g2d) {
        int px = (int) x;
        int py = (int) y;

        g2d.setColor(new Color(139, 69, 19));
        g2d.fillOval(px + 3, py + 2, width - 6, 20);

        g2d.setColor(new Color(255, 220, 180));
        g2d.fillOval(px + 5, py + 8, width - 10, height - 18);

        g2d.setColor(new Color(139, 69, 19));
        g2d.fillOval(px + 2, py + 8, 8, 15);
        g2d.fillOval(px + width - 10, py + 8, 8, 15);

        g2d.setColor(Color.WHITE);
        g2d.fillOval(px + 10, py + 14, 6, 6);
        g2d.fillOval(px + 19, py + 14, 6, 6);
        g2d.setColor(Color.BLACK);
        g2d.fillOval(px + 12, py + 16, 3, 3);
        g2d.fillOval(px + 21, py + 16, 3, 3);

        g2d.setColor(Color.RED);
        g2d.fillOval(px + 13, py + 22, 8, 4);

        g2d.setColor(type.getColor());
        g2d.fillRect(px + 8, py + 28, width - 16, 8);
    }

    private void drawDad(Graphics2D g2d) {
        int px = (int) x;
        int py = (int) y;

        g2d.setColor(new Color(255, 220, 180));
        g2d.fillOval(px + 5, py + 5, width - 10, height - 15);

        g2d.setColor(new Color(60, 60, 60));
        g2d.fillArc(px + 5, py + 2, width - 10, 15, 0, 180);

        g2d.setColor(Color.WHITE);
        g2d.fillOval(px + 10, py + 12, 6, 6);
        g2d.fillOval(px + 19, py + 12, 6, 6);
        g2d.setColor(Color.BLACK);
        g2d.fillOval(px + 12, py + 14, 3, 3);
        g2d.fillOval(px + 21, py + 14, 3, 3);

        g2d.setColor(new Color(60, 60, 60));
        g2d.fillRect(px + 10, py + 20, 14, 3);

        g2d.setColor(type.getColor());
        g2d.fillPolygon(new int[] { px + 15, px + 20, px + 17 }, new int[] { py + 26, py + 26, py + 35 }, 3);
        g2d.fillRect(px + 15, py + 24, 5, 4);
    }

    private void drawSister(Graphics2D g2d) {
        int px = (int) x;
        int py = (int) y;

        g2d.setColor(new Color(100, 50, 20));
        g2d.fillOval(px + width - 5, py, 12, 15);

        g2d.setColor(new Color(255, 220, 180));
        g2d.fillOval(px + 5, py + 5, width - 10, height - 15);

        g2d.setColor(new Color(100, 50, 20));
        g2d.fillArc(px + 5, py + 2, width - 10, 18, 0, 180);

        g2d.setColor(Color.WHITE);
        g2d.fillOval(px + 10, py + 14, 6, 6);
        g2d.fillOval(px + 19, py + 14, 6, 6);
        g2d.setColor(Color.BLACK);
        g2d.fillOval(px + 12, py + 16, 3, 3);
        g2d.fillOval(px + 21, py + 16, 3, 3);

        g2d.setColor(type.getColor());
        g2d.drawArc(px + 12, py + 22, 10, 6, 180, 180);

        g2d.setColor(type.getColor());
        g2d.fillOval(px + width - 3, py + 5, 6, 6);
    }

    public boolean canDetectPlayer(Player player) {
        double centerX = x + width / 2.0;
        double centerY = y + height / 2.0;
        double playerCenterX = player.getX() + player.getWidth() / 2.0;
        double playerCenterY = player.getY() + player.getHeight() / 2.0;

        double distance = Math.sqrt(
                Math.pow(centerX - playerCenterX, 2) +
                        Math.pow(centerY - playerCenterY, 2));

        return distance < detectionRadius;
    }

    public GuardType getType() {
        return type;
    }

    public int getDetectionRadius() {
        return detectionRadius;
    }

    public void reset() {
        this.x = patrolStartX;
        this.y = patrolStartY;
        this.movingToEnd = true;
    }

    public enum RiskLevel {
        LOW(1.5, 50),
        MEDIUM(2.5, 75),
        HIGH(3.5, 100);

        private final double guardSpeed;
        private final int detectionRadius;

        RiskLevel(double guardSpeed, int detectionRadius) {
            this.guardSpeed = guardSpeed;
            this.detectionRadius = detectionRadius;
        }

        public double getGuardSpeed() {
            return guardSpeed;
        }

        public int getDetectionRadius() {
            return detectionRadius;
        }
    }

    public enum GuardType {
        CAT("Cat", new Color(255, 165, 0), RiskLevel.LOW),
        BROTHER("Little Brother", new Color(100, 149, 237), RiskLevel.LOW),
        MOM("Mom", new Color(255, 105, 180), RiskLevel.HIGH),
        DAD("Dad", new Color(139, 69, 19), RiskLevel.HIGH),
        SISTER("Sister", new Color(186, 85, 211), RiskLevel.MEDIUM);

        private final String displayName;
        private final Color color;
        private final RiskLevel riskLevel;

        GuardType(String displayName, Color color, RiskLevel riskLevel) {
            this.displayName = displayName;
            this.color = color;
            this.riskLevel = riskLevel;
        }

        public String getDisplayName() {
            return displayName;
        }

        public Color getColor() {
            return color;
        }

        public RiskLevel getRiskLevel() {
            return riskLevel;
        }
    }
}
