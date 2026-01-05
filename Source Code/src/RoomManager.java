import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RoomManager {
    private List<Room> rooms;
    private List<Rectangle> doors;
    private static final int GAME_START_Y = 50;

    public RoomManager() {
        rooms = new ArrayList<>();
        doors = new ArrayList<>();
        initializeRooms();
        initializeDoors();
    }

    private void initializeRooms() {
        PowerUp.PowerUpType[] powerUpTypes = PowerUp.PowerUpType.values();
        Random random = new Random();

        Room toilet = new Room("Toilet", 10, GAME_START_Y + 10, 200, 200,
                new Color(173, 216, 230), Guard.RiskLevel.LOW);
        toilet.setGuard(new Guard(Guard.GuardType.CAT, 20, GAME_START_Y + 180, 180, GAME_START_Y + 70));
        toilet.setCucumber(new Room.Cucumber(100, GAME_START_Y + 180));
        toilet.addObstacle(new Obstacle(Obstacle.ObstacleType.TABLE, 120, GAME_START_Y + 80));
        toilet.setPowerUp(new PowerUp(powerUpTypes[random.nextInt(powerUpTypes.length)], 50, GAME_START_Y + 120));
        rooms.add(toilet);

        Room playerBedroom = new Room("My Bedroom", 220, GAME_START_Y + 10, 260, 200,
                new Color(144, 238, 144), Guard.RiskLevel.LOW);
        playerBedroom.setGuard(new Guard(Guard.GuardType.BROTHER, 440, GAME_START_Y + 30, 250, GAME_START_Y + 170));
        playerBedroom.setCucumber(new Room.Cucumber(350, GAME_START_Y + 180));
        playerBedroom.addObstacle(new Obstacle(Obstacle.ObstacleType.BED, 240, GAME_START_Y + 120));
        playerBedroom.addObstacle(new Obstacle(Obstacle.ObstacleType.CHAIR, 420, GAME_START_Y + 50));
        playerBedroom
                .setPowerUp(new PowerUp(powerUpTypes[random.nextInt(powerUpTypes.length)], 320, GAME_START_Y + 80));
        rooms.add(playerBedroom);

        Room parentsBedroom = new Room("Parents Room", 490, GAME_START_Y + 10, 280, 200,
                new Color(255, 182, 193), Guard.RiskLevel.HIGH);
        parentsBedroom.setGuard(new Guard(Guard.GuardType.DAD, 720, GAME_START_Y + 30, 510, GAME_START_Y + 170));
        parentsBedroom.setCucumber(new Room.Cucumber(650, GAME_START_Y + 180));
        parentsBedroom.addObstacle(new Obstacle(Obstacle.ObstacleType.BED, 580, GAME_START_Y + 100, 80, 50));
        parentsBedroom.addObstacle(new Obstacle(Obstacle.ObstacleType.DRESSER, 510, GAME_START_Y + 40));
        parentsBedroom
                .setPowerUp(new PowerUp(powerUpTypes[random.nextInt(powerUpTypes.length)], 700, GAME_START_Y + 80));
        rooms.add(parentsBedroom);

        Room livingRoom = new Room("Living Room", 10, GAME_START_Y + 220, 370, 280,
                new Color(255, 218, 185), Guard.RiskLevel.HIGH);
        livingRoom.setGuard(new Guard(Guard.GuardType.MOM, 20, GAME_START_Y + 460, 340, GAME_START_Y + 250));
        livingRoom.setCucumber(new Room.Cucumber(200, GAME_START_Y + 400));
        livingRoom.addObstacle(new Obstacle(Obstacle.ObstacleType.BOOKSHELF, 30, GAME_START_Y + 280));
        livingRoom.addObstacle(new Obstacle(Obstacle.ObstacleType.TABLE, 180, GAME_START_Y + 350));
        livingRoom.addObstacle(new Obstacle(Obstacle.ObstacleType.CHAIR, 280, GAME_START_Y + 420));
        livingRoom.setPowerUp(new PowerUp(powerUpTypes[random.nextInt(powerUpTypes.length)], 100, GAME_START_Y + 320));
        rooms.add(livingRoom);

        Room garage = new Room("Garage", 390, GAME_START_Y + 220, 380, 280,
                new Color(192, 192, 192), Guard.RiskLevel.MEDIUM);
        garage.setGuard(new Guard(Guard.GuardType.SISTER, 720, GAME_START_Y + 460, 420, GAME_START_Y + 250));
        garage.setCucumber(new Room.Cucumber(550, GAME_START_Y + 400));
        garage.addObstacle(new Obstacle(Obstacle.ObstacleType.WORKBENCH, 420, GAME_START_Y + 280));
        garage.addObstacle(new Obstacle(Obstacle.ObstacleType.BOOKSHELF, 600, GAME_START_Y + 350));
        garage.setPowerUp(new PowerUp(powerUpTypes[random.nextInt(powerUpTypes.length)], 500, GAME_START_Y + 320));
        rooms.add(garage);
    }

    private void initializeDoors() {
        doors.add(new Rectangle(205, GAME_START_Y + 100, 20, 40));

        doors.add(new Rectangle(475, GAME_START_Y + 100, 20, 40));

        doors.add(new Rectangle(220, GAME_START_Y + 205, 60, 20));

        doors.add(new Rectangle(550, GAME_START_Y + 205, 60, 20));

        doors.add(new Rectangle(375, GAME_START_Y + 350, 20, 60));
    }

    public List<Rectangle> getDoors() {
        return doors;
    }

    public void update() {
        for (Room room : rooms) {
            room.update();
        }
    }

    public void draw(Graphics2D g2d) {
        for (Room room : rooms) {
            room.draw(g2d);
        }
        g2d.setColor(new Color(139, 90, 43));

        g2d.fillRect(205, GAME_START_Y + 100, 20, 40);

        g2d.fillRect(475, GAME_START_Y + 100, 20, 40);

        g2d.fillRect(220, GAME_START_Y + 205, 60, 20);

        g2d.fillRect(550, GAME_START_Y + 205, 60, 20);

        g2d.fillRect(375, GAME_START_Y + 350, 20, 60);
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public List<Guard> getAllGuards() {
        List<Guard> guards = new ArrayList<>();
        for (Room room : rooms) {
            if (room.getGuard() != null) {
                guards.add(room.getGuard());
            }
        }
        return guards;
    }

    public List<Room.Cucumber> getAllCucumbers() {
        List<Room.Cucumber> cucumbers = new ArrayList<>();
        for (Room room : rooms) {
            if (room.getCucumber() != null) {
                cucumbers.add(room.getCucumber());
            }
        }
        return cucumbers;
    }

    public List<Obstacle> getAllObstacles() {
        List<Obstacle> allObstacles = new ArrayList<>();
        for (Room room : rooms) {
            allObstacles.addAll(room.getObstacles());
        }
        return allObstacles;
    }

    public List<PowerUp> getAllPowerUps() {
        List<PowerUp> powerUps = new ArrayList<>();
        for (Room room : rooms) {
            if (room.getPowerUp() != null) {
                powerUps.add(room.getPowerUp());
            }
        }
        return powerUps;
    }

    public void resetAll() {
        PowerUp.PowerUpType[] powerUpTypes = PowerUp.PowerUpType.values();
        Random random = new Random();

        for (Room room : rooms) {
            if (room.getCucumber() != null) {
                room.getCucumber().reset();
            }
            if (room.getGuard() != null) {
                room.getGuard().reset();
            }

            PowerUp.PowerUpType randomType = powerUpTypes[random.nextInt(powerUpTypes.length)];
            int randomX, randomY;
            boolean validPosition;
            int attempts = 0;

            do {
                randomX = room.getX() + 30 + random.nextInt(Math.max(1, room.getWidth() - 80));
                randomY = room.getY() + 30 + random.nextInt(Math.max(1, room.getHeight() - 60));
                validPosition = true;

                Rectangle powerUpBounds = new Rectangle(randomX, randomY, 20, 20);
                for (Obstacle obstacle : room.getObstacles()) {
                    if (powerUpBounds.intersects(obstacle.getBounds())) {
                        validPosition = false;
                        break;
                    }
                }
                attempts++;
            } while (!validPosition && attempts < 20);

            room.setPowerUp(new PowerUp(randomType, randomX, randomY));
        }
    }

    public boolean[] getRoomUnlockStates() {
        boolean[] states = new boolean[rooms.size()];
        for (int i = 0; i < rooms.size(); i++) {
            states[i] = rooms.get(i).isUnlocked();
        }
        return states;
    }

    public void setRoomUnlockStates(boolean[] states) {
        for (int i = 0; i < Math.min(states.length, rooms.size()); i++) {
            if (states[i] && rooms.get(i).getCucumber() != null) {
                rooms.get(i).getCucumber().collect();
            }
        }
    }
}
