import java.awt.Graphics2D;
import java.awt.Rectangle;

public abstract class Entity {
    protected double x;
    protected double y;
    protected int width;
    protected int height;
    protected double speed;

    public Entity(double x, double y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.speed = 3.0;
    }

    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, width, height);
    }

    public boolean collidesWith(Entity other) {
        return getBounds().intersects(other.getBounds());
    }

    public abstract void draw(Graphics2D g2d);

    public abstract void update();

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }
}
