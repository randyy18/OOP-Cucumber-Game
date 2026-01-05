import java.awt.Color;
import java.awt.Graphics2D;

public class Player extends Entity {
    private boolean movingUp, movingDown, movingLeft, movingRight;

    private int cucumbersCollected;
    private double prevX, prevY;

    public Player(double x, double y) {
        super(x, y, 30, 30);
        this.speed = 4.0;
        this.cucumbersCollected = 0;
        this.prevX = x;
        this.prevY = y;
    }

    @Override
    public void update() {
        prevX = x;
        prevY = y;

        if (movingUp)
            y -= speed;
        if (movingDown)
            y += speed;
        if (movingLeft)
            x -= speed;
        if (movingRight)
            x += speed;

        if (x < 10)
            x = 10;
        if (y < 60)
            y = 60;
        if (x > 750 - width)
            x = 750 - width;
        if (y > 500 - height)
            y = 500 - height;
    }

    public void restorePosition() {
        this.x = prevX;
        this.y = prevY;
    }

    @Override
    public void draw(Graphics2D g2d) {
        int px = (int) x;
        int py = (int) y;

        g2d.setColor(new Color(70, 130, 180));
        g2d.fillRoundRect(px + 6, py + 22, 6, 8, 2, 2);
        g2d.fillRoundRect(px + 18, py + 22, 6, 8, 2, 2);

        g2d.setColor(new Color(139, 69, 19));
        g2d.fillRoundRect(px + 5, py + 27, 8, 4, 2, 2);
        g2d.fillRoundRect(px + 17, py + 27, 8, 4, 2, 2);

        g2d.setColor(new Color(220, 60, 60));
        g2d.fillRoundRect(px + 5, py + 10, 20, 14, 4, 4);

        g2d.setColor(new Color(255, 218, 185));
        g2d.fillRoundRect(px + 1, py + 11, 5, 10, 3, 3);
        g2d.fillRoundRect(px + 24, py + 11, 5, 10, 3, 3);

        g2d.setColor(new Color(255, 218, 185));
        g2d.fillOval(px + 7, py, 16, 14);

        g2d.setColor(new Color(101, 67, 33));
        g2d.fillArc(px + 6, py - 2, 18, 10, 0, 180);
        g2d.fillOval(px + 5, py + 1, 5, 6);
        g2d.fillOval(px + 20, py + 1, 5, 6);

        g2d.setColor(Color.WHITE);
        g2d.fillOval(px + 10, py + 4, 5, 5);
        g2d.fillOval(px + 16, py + 4, 5, 5);

        g2d.setColor(new Color(50, 50, 50));
        g2d.fillOval(px + 12, py + 5, 2, 3);
        g2d.fillOval(px + 18, py + 5, 2, 3);

        g2d.setColor(new Color(255, 182, 193, 150));
        g2d.fillOval(px + 8, py + 8, 4, 3);
        g2d.fillOval(px + 19, py + 8, 4, 3);

        g2d.setColor(new Color(180, 80, 80));
        g2d.drawArc(px + 11, py + 8, 8, 5, 200, 140);
    }

    public void setMovingUp(boolean moving) {
        this.movingUp = moving;
    }

    public void setMovingDown(boolean moving) {
        this.movingDown = moving;
    }

    public void setMovingLeft(boolean moving) {
        this.movingLeft = moving;
    }

    public void setMovingRight(boolean moving) {
        this.movingRight = moving;
    }

    public void collectCucumber() {
        cucumbersCollected++;
    }

    public int getCucumbersCollected() {
        return cucumbersCollected;
    }

    public void reset(double startX, double startY) {
        this.x = startX;
        this.y = startY;
        this.cucumbersCollected = 0;
        this.movingUp = false;
        this.movingDown = false;
        this.movingLeft = false;
        this.movingRight = false;
    }

    public boolean isMoving() {
        return movingUp || movingDown || movingLeft || movingRight;
    }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
        this.prevX = x;
        this.prevY = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
