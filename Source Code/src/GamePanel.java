import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class GamePanel extends JPanel implements ActionListener {
    private static final int PANEL_WIDTH = 780;
    private static final int PANEL_HEIGHT = 510;
    private static final int FPS = 60;
    private static final int DELAY = 1000 / FPS;

    private Player player;
    private RoomManager roomManager;
    private GameState gameState;
    private InputHandler inputHandler;
    private CollisionManager collisionManager;
    private GameWindow.ProgressBar progressBar;
    private Timer gameTimer;
    private DatabaseManager databaseManager;
    private AudioManager audioManager;

    private String statusMessage = "";
    private long statusMessageTime = 0;
    private static final long STATUS_DISPLAY_DURATION = 2000;
    private boolean highScoreSaved = false;
    private boolean showHighScores = false;
    private int currentDifficulty = 2;
    private static final String[] DIFFICULTY_NAMES = { "", "Easy", "Normal", "Hard" };
    private static final double[] DIFFICULTY_SPEED_MULTIPLIERS = { 0, 0.5, 1.0, 1.5 };

    private int menuSelectedIndex = 0;
    private static final String[] MENU_OPTIONS = { "Start Game", "High Scores", "Difficulty", "Exit" };

    private static final double START_X = 250;
    private static final double START_Y = 100;

    private PowerUp.PowerUpType activePowerUp = null;
    private long powerUpEndTime = 0;
    private static final double NORMAL_SPEED = 4.0;
    private static final double BOOST_SPEED = 8.0;

    public GamePanel(GameWindow.ProgressBar progressBar) {
        this.progressBar = progressBar;
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setBackground(new Color(40, 40, 40));
        setFocusable(true);

        initializeGame();
    }

    private void initializeGame() {
        player = new Player(START_X, START_Y);
        roomManager = new RoomManager();
        gameState = new GameState();
        collisionManager = new CollisionManager();
        inputHandler = new InputHandler(player);

        try {
            databaseManager = DatabaseManager.getInstance();
        } catch (Exception e) {
            System.err.println("Failed to initialize database: " + e.getMessage());
            databaseManager = null;
        }

        audioManager = AudioManager.getInstance();
        audioManager.playBackgroundMusic();

        addKeyListener(inputHandler);

        gameTimer = new Timer(DELAY, this);
        gameTimer.start();

        progressBar.setProgress(0, 5);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameState.isMenu()) {
            handleMenuInput();
            repaint();
            return;
        }

        if (gameState.isPlaying() && !showHighScores) {
            update();
        }

        if (inputHandler.isEscapeRequested()) {
            showHighScores = false;
            gameState.returnToMenu();
            menuSelectedIndex = 0;
        }

        if (inputHandler.isRestartRequested()) {
            showHighScores = false;
            restartGame();
        }

        if (inputHandler.isSaveRequested()) {
            saveGame();
        }

        if (inputHandler.isLoadRequested()) {
            loadGame();
        }

        if (inputHandler.isHighScoresRequested()) {
            showHighScores = !showHighScores;
        }

        int diffChange = inputHandler.getDifficultyChangeRequested();
        if (diffChange > 0 && diffChange <= 3) {
            changeDifficulty(diffChange);
        }

        repaint();
    }

    private void handleMenuInput() {
        if (inputHandler.isMenuUpRequested()) {
            menuSelectedIndex = (menuSelectedIndex - 1 + MENU_OPTIONS.length) % MENU_OPTIONS.length;
        }
        if (inputHandler.isMenuDownRequested()) {
            menuSelectedIndex = (menuSelectedIndex + 1) % MENU_OPTIONS.length;
        }

        if (inputHandler.isMenuSelectRequested()) {
            switch (menuSelectedIndex) {
                case 0:
                    gameState.startGame();
                    player.reset(START_X, START_Y);
                    roomManager.resetAll();
                    progressBar.reset();
                    highScoreSaved = false;
                    requestFocusInWindow();
                    break;
                case 1:
                    showHighScores = true;
                    gameState.startGame();
                    break;
                case 2:
                    currentDifficulty = (currentDifficulty % 3) + 1;
                    changeDifficulty(currentDifficulty);
                    break;
                case 3:
                    System.exit(0);
                    break;
            }
        }

        inputHandler.isRestartRequested();
        inputHandler.isSaveRequested();
        inputHandler.isLoadRequested();
        inputHandler.isHighScoresRequested();
        inputHandler.getDifficultyChangeRequested();
        inputHandler.isEscapeRequested();
    }

    private void update() {
        Room currentRoom = collisionManager.getCurrentRoom(player, roomManager.getRooms());

        player.update();

        if (collisionManager.checkPlayerObstacleCollision(player, roomManager.getAllObstacles())) {
            player.restorePosition();
        }

        if (currentRoom != null && !collisionManager.isPlayerInsideRoom(player, currentRoom)) {
            if (!collisionManager.isPlayerOnDoor(player, roomManager.getDoors())) {
                player.restorePosition();
            }
        }

        Room newRoom = collisionManager.getCurrentRoom(player, roomManager.getRooms());

        if (currentRoom != newRoom) {
            if (currentRoom != null && !currentRoom.isUnlocked()) {
                player.restorePosition();
            } else if (!collisionManager.isPlayerOnDoor(player, roomManager.getDoors())) {
                player.restorePosition();
            }
        }

        if (activePowerUp != null && System.currentTimeMillis() > powerUpEndTime) {
            if (activePowerUp == PowerUp.PowerUpType.SPEED_BOOST) {
                player.setSpeed(NORMAL_SPEED);
            }
            activePowerUp = null;
        }

        boolean guardsAreFrozen = (activePowerUp == PowerUp.PowerUpType.FREEZE_GUARDS);
        if (!guardsAreFrozen) {
            roomManager.update();
        }

        PowerUp collectedPowerUp = collisionManager.checkPlayerPowerUpCollision(
                player, roomManager.getAllPowerUps());
        if (collectedPowerUp != null) {
            collectedPowerUp.collect();
            activePowerUp = collectedPowerUp.getType();
            powerUpEndTime = System.currentTimeMillis() + collectedPowerUp.getType().getDurationMs();
            audioManager.playCollectSound();

            if (activePowerUp == PowerUp.PowerUpType.SPEED_BOOST) {
                player.setSpeed(BOOST_SPEED);
            }
        }

        Room.Cucumber collected = collisionManager.checkPlayerCucumberCollision(
                player, roomManager.getAllCucumbers());
        if (collected != null) {
            collected.collect();
            gameState.collectCucumber();
            audioManager.playCollectSound();
            progressBar.setProgress(
                    gameState.getCucumbersCollected(),
                    gameState.getTotalCucumbers());
        }

        boolean hasImmunity = (activePowerUp == PowerUp.PowerUpType.IMMUNITY);
        if (!hasImmunity) {
            Guard caughtBy = collisionManager.checkPlayerGuardCollision(
                    player, roomManager.getRooms());
            if (caughtBy != null) {
                gameState.caughtByGuard(caughtBy.getType().getDisplayName());
                audioManager.playCaughtSound();
            }
        }

        if (gameState.isWon() && !highScoreSaved) {
            audioManager.playWinSound();
            saveHighScore();
            highScoreSaved = true;
        }
    }

    private void restartGame() {
        player.reset(START_X, START_Y);
        roomManager.resetAll();
        gameState.reset();
        progressBar.reset();
        highScoreSaved = false;
        activePowerUp = null;
        player.setSpeed(NORMAL_SPEED);
        requestFocusInWindow();
    }

    private void changeDifficulty(int difficulty) {
        if (difficulty == currentDifficulty)
            return;

        currentDifficulty = difficulty;
        double speedMultiplier = DIFFICULTY_SPEED_MULTIPLIERS[difficulty];

        for (Guard guard : roomManager.getAllGuards()) {
            double baseSpeed = guard.getType().getRiskLevel().getGuardSpeed();
            guard.setSpeed(baseSpeed * speedMultiplier);
        }

        showStatusMessage("Difficulty: " + DIFFICULTY_NAMES[difficulty]);
    }

    private void saveHighScore() {
        if (databaseManager != null) {
            databaseManager.saveHighScore(
                    "Player",
                    gameState.getCucumbersCollected(),
                    gameState.getCompletionTime(),
                    true,
                    DIFFICULTY_NAMES[currentDifficulty]);
        }
    }

    private void saveGame() {
        if (databaseManager == null) {
            showStatusMessage("Database not available!");
            return;
        }

        if (!gameState.isPlaying()) {
            showStatusMessage("Cannot save - game not in progress!");
            return;
        }

        DatabaseManager.SaveData saveData = new DatabaseManager.SaveData(
                "Player",
                player.getX(),
                player.getY(),
                gameState.getCucumbersCollected(),
                roomManager.getRoomUnlockStates());

        if (databaseManager.saveGame(saveData)) {
            showStatusMessage("Game Saved! (F9 to load)");
        } else {
            showStatusMessage("Save failed!");
        }
    }

    private void loadGame() {
        if (databaseManager == null) {
            showStatusMessage("Database not available!");
            return;
        }

        DatabaseManager.SaveData saveData = databaseManager.loadGame();

        if (saveData == null) {
            showStatusMessage("No save found!");
            return;
        }

        player.setPosition(saveData.getPlayerX(), saveData.getPlayerY());

        roomManager.resetAll();
        roomManager.setRoomUnlockStates(saveData.getRoomUnlockStates());

        gameState.reset();
        gameState.setCucumbersCollected(saveData.getCucumbersCollected());

        progressBar.setProgress(
                gameState.getCucumbersCollected(),
                gameState.getTotalCucumbers());

        showStatusMessage("Game Loaded!");
        requestFocusInWindow();
    }

    private void showStatusMessage(String message) {
        this.statusMessage = message;
        this.statusMessageTime = System.currentTimeMillis();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (gameState.isMenu()) {
            drawMainMenu(g2d);
            return;
        }

        roomManager.draw(g2d);

        player.draw(g2d);

        g2d.setColor(new Color(255, 255, 255, 200));
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        String timeStr = "Time: " + gameState.formatTime(gameState.getElapsedTime());
        g2d.drawString(timeStr, PANEL_WIDTH - 120, 20);

        g2d.setColor(new Color(200, 200, 200));
        g2d.setFont(new Font("Arial", Font.PLAIN, 10));
        g2d.drawString("WASD: Move | R: Restart | F5: Save | F9: Load | H: High Scores | ESC: Menu", 10,
                PANEL_HEIGHT - 10);

        g2d.setColor(new Color(100, 100, 100, 180));
        g2d.fillRoundRect(5, 5, 75, 20, 5, 5);
        g2d.setColor(currentDifficulty == 1 ? Color.GREEN : currentDifficulty == 2 ? Color.YELLOW : Color.RED);
        g2d.setFont(new Font("Arial", Font.BOLD, 11));
        g2d.drawString(DIFFICULTY_NAMES[currentDifficulty], 15, 19);

        if (activePowerUp != null) {
            long remaining = powerUpEndTime - System.currentTimeMillis();
            if (remaining > 0) {
                int barWidth = 100;
                int barHeight = 18;
                int barX = PANEL_WIDTH / 2 - barWidth / 2;
                int barY = 5;

                g2d.setColor(new Color(0, 0, 0, 150));
                g2d.fillRoundRect(barX - 5, barY - 2, barWidth + 10, barHeight + 4, 8, 8);

                double progress = (double) remaining / activePowerUp.getDurationMs();
                int fillWidth = (int) (barWidth * progress);
                g2d.setColor(activePowerUp.getColor());
                g2d.fillRoundRect(barX, barY, fillWidth, barHeight, 5, 5);

                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 11));
                FontMetrics fm = g2d.getFontMetrics();
                String powerUpName = activePowerUp.getDisplayName();
                int textX = barX + (barWidth - fm.stringWidth(powerUpName)) / 2;
                g2d.drawString(powerUpName, textX, barY + 13);
            }
        }

        if (!statusMessage.isEmpty()) {
            long elapsed = System.currentTimeMillis() - statusMessageTime;
            if (elapsed < STATUS_DISPLAY_DURATION) {
                int alpha = (int) (255 * (1 - (double) elapsed / STATUS_DISPLAY_DURATION));
                g2d.setColor(new Color(50, 50, 50, Math.min(200, alpha + 50)));
                g2d.fillRoundRect(PANEL_WIDTH / 2 - 100, 20, 200, 30, 10, 10);
                g2d.setColor(new Color(255, 255, 100, alpha));
                g2d.setFont(new Font("Arial", Font.BOLD, 14));
                FontMetrics fm = g2d.getFontMetrics();
                int x = (PANEL_WIDTH - fm.stringWidth(statusMessage)) / 2;
                g2d.drawString(statusMessage, x, 40);
            } else {
                statusMessage = "";
            }
        }

        if (showHighScores) {
            drawHighScoresOverlay(g2d);
        } else if (gameState.isGameOver()) {
            drawGameOver(g2d);
        } else if (gameState.isWon()) {
            drawWinScreen(g2d);
        }
    }

    private void drawGameOver(Graphics2D g2d) {
        g2d.setColor(new Color(0, 0, 0, 180));
        g2d.fillRect(0, 0, PANEL_WIDTH, PANEL_HEIGHT);

        g2d.setColor(Color.RED);
        g2d.setFont(new Font("Arial", Font.BOLD, 48));
        String gameOver = "GAME OVER!";
        FontMetrics fm = g2d.getFontMetrics();
        int x = (PANEL_WIDTH - fm.stringWidth(gameOver)) / 2;
        g2d.drawString(gameOver, x, PANEL_HEIGHT / 2 - 30);

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.PLAIN, 24));
        String message = gameState.getGameOverMessage();
        fm = g2d.getFontMetrics();
        x = (PANEL_WIDTH - fm.stringWidth(message)) / 2;
        g2d.drawString(message, x, PANEL_HEIGHT / 2 + 20);

        g2d.setColor(Color.YELLOW);
        g2d.setFont(new Font("Arial", Font.BOLD, 18));
        String restart = "Press R to Restart";
        fm = g2d.getFontMetrics();
        x = (PANEL_WIDTH - fm.stringWidth(restart)) / 2;
        g2d.drawString(restart, x, PANEL_HEIGHT / 2 + 70);
    }

    private void drawWinScreen(Graphics2D g2d) {
        g2d.setColor(new Color(0, 50, 0, 180));
        g2d.fillRect(0, 0, PANEL_WIDTH, PANEL_HEIGHT);

        g2d.setColor(new Color(50, 255, 50));
        g2d.setFont(new Font("Arial", Font.BOLD, 48));
        String win = "YOU WIN!";
        FontMetrics fm = g2d.getFontMetrics();
        int x = (PANEL_WIDTH - fm.stringWidth(win)) / 2;
        g2d.drawString(win, x, PANEL_HEIGHT / 2 - 60);

        g2d.setColor(new Color(255, 215, 0));
        g2d.setFont(new Font("Arial", Font.BOLD, 28));
        String timeMsg = "Completion Time: " + gameState.formatTime(gameState.getCompletionTime());
        fm = g2d.getFontMetrics();
        x = (PANEL_WIDTH - fm.stringWidth(timeMsg)) / 2;
        g2d.drawString(timeMsg, x, PANEL_HEIGHT / 2 - 10);

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.PLAIN, 20));
        String message = "All cucumbers collected! High score saved!";
        fm = g2d.getFontMetrics();
        x = (PANEL_WIDTH - fm.stringWidth(message)) / 2;
        g2d.drawString(message, x, PANEL_HEIGHT / 2 + 30);

        g2d.setColor(Color.YELLOW);
        g2d.setFont(new Font("Arial", Font.BOLD, 18));
        String restart = "Press R to Play Again";
        fm = g2d.getFontMetrics();
        x = (PANEL_WIDTH - fm.stringWidth(restart)) / 2;
        g2d.drawString(restart, x, PANEL_HEIGHT / 2 + 70);
    }

    private void drawHighScoresOverlay(Graphics2D g2d) {
        g2d.setColor(new Color(0, 0, 50, 220));
        g2d.fillRect(0, 0, PANEL_WIDTH, PANEL_HEIGHT);

        g2d.setColor(new Color(255, 215, 0));
        g2d.setFont(new Font("Arial", Font.BOLD, 36));
        String title = "HIGH SCORES";
        FontMetrics fm = g2d.getFontMetrics();
        int x = (PANEL_WIDTH - fm.stringWidth(title)) / 2;
        g2d.drawString(title, x, 60);

        drawMenuCucumber(g2d, x - 55, 45);
        drawMenuCucumber(g2d, x + fm.stringWidth(title) + 15, 45);

        g2d.setColor(new Color(255, 215, 0, 150));
        g2d.fillRect(PANEL_WIDTH / 4, 75, PANEL_WIDTH / 2, 3);

        String[] scores;
        if (databaseManager != null) {
            scores = databaseManager.getHighScoresList(10);
        } else {
            scores = new String[] { "Database not available" };
        }

        g2d.setFont(new Font("Consolas", Font.PLAIN, 18));
        int startY = 110;
        int lineHeight = 28;

        for (int i = 0; i < scores.length; i++) {
            if (i % 2 == 0) {
                g2d.setColor(new Color(255, 255, 255, 30));
                g2d.fillRect(PANEL_WIDTH / 4 - 10, startY + i * lineHeight - 18, PANEL_WIDTH / 2 + 20, lineHeight);
            }

            if (i == 0 && !scores[i].startsWith("No") && !scores[i].startsWith("Database")
                    && !scores[i].startsWith("Error")) {
                g2d.setColor(new Color(255, 215, 0));
            } else if (i == 1 && scores.length > 1) {
                g2d.setColor(new Color(192, 192, 192));
            } else if (i == 2 && scores.length > 2) {
                g2d.setColor(new Color(205, 127, 50));
            } else {
                g2d.setColor(Color.WHITE);
            }

            fm = g2d.getFontMetrics();
            x = (PANEL_WIDTH - fm.stringWidth(scores[i])) / 2;
            g2d.drawString(scores[i], x, startY + i * lineHeight);
        }

        g2d.setColor(new Color(150, 150, 150));
        g2d.setFont(new Font("Arial", Font.PLAIN, 14));
        String closeMsg = "Press H to close | Press R to restart";
        fm = g2d.getFontMetrics();
        x = (PANEL_WIDTH - fm.stringWidth(closeMsg)) / 2;
        g2d.drawString(closeMsg, x, PANEL_HEIGHT - 30);
    }

    private void drawMainMenu(Graphics2D g2d) {
        g2d.setColor(new Color(20, 30, 50));
        g2d.fillRect(0, 0, PANEL_WIDTH, PANEL_HEIGHT);

        g2d.setColor(new Color(30, 45, 70));
        for (int i = 0; i < PANEL_WIDTH; i += 30) {
            for (int j = 0; j < PANEL_HEIGHT; j += 30) {
                if ((i + j) % 60 == 0) {
                    g2d.fillRect(i, j, 20, 20);
                }
            }
        }

        g2d.setFont(new Font("Arial", Font.BOLD, 48));
        String title = "HIDE MY CUCUMBER";
        FontMetrics fm = g2d.getFontMetrics();
        int x = (PANEL_WIDTH - fm.stringWidth(title)) / 2;

        g2d.setColor(new Color(0, 0, 0, 100));
        g2d.drawString(title, x + 3, 103);

        g2d.setColor(new Color(50, 200, 50));
        g2d.drawString(title, x, 100);

        drawMenuCucumber(g2d, x - 55, 75);
        drawMenuCucumber(g2d, x + fm.stringWidth(title) + 20, 75);

        g2d.setColor(new Color(150, 150, 150));
        g2d.setFont(new Font("Arial", Font.ITALIC, 18));
        String subtitle = "Collect all cucumbers without getting caught!";
        fm = g2d.getFontMetrics();
        x = (PANEL_WIDTH - fm.stringWidth(subtitle)) / 2;
        g2d.drawString(subtitle, x, 140);

        int startY = 200;
        int lineHeight = 50;

        for (int i = 0; i < MENU_OPTIONS.length; i++) {
            String option = MENU_OPTIONS[i];

            if (i == 2) {
                option = "Difficulty: " + DIFFICULTY_NAMES[currentDifficulty];
            }

            if (i == menuSelectedIndex) {
                g2d.setColor(new Color(50, 150, 50, 100));
                fm = g2d.getFontMetrics(new Font("Arial", Font.BOLD, 24));
                int boxWidth = fm.stringWidth(option) + 40;
                g2d.fillRoundRect((PANEL_WIDTH - boxWidth) / 2, startY + i * lineHeight - 25, boxWidth, 40, 10, 10);

                g2d.setColor(new Color(100, 255, 100));
                g2d.setStroke(new java.awt.BasicStroke(2));
                g2d.drawRoundRect((PANEL_WIDTH - boxWidth) / 2, startY + i * lineHeight - 25, boxWidth, 40, 10, 10);

                g2d.setFont(new Font("Arial", Font.BOLD, 24));
                g2d.drawString("►", (PANEL_WIDTH - boxWidth) / 2 - 30, startY + i * lineHeight);

                g2d.setColor(Color.WHITE);
            } else {
                g2d.setColor(new Color(180, 180, 180));
            }

            g2d.setFont(new Font("Arial", Font.BOLD, 24));
            fm = g2d.getFontMetrics();
            x = (PANEL_WIDTH - fm.stringWidth(option)) / 2;
            g2d.drawString(option, x, startY + i * lineHeight);
        }

        g2d.setColor(new Color(100, 100, 100));
        g2d.setFont(new Font("Arial", Font.PLAIN, 14));
        String controls = "↑↓: Navigate | Enter/Space: Select";
        fm = g2d.getFontMetrics();
        x = (PANEL_WIDTH - fm.stringWidth(controls)) / 2;
        g2d.drawString(controls, x, PANEL_HEIGHT - 40);

        g2d.setFont(new Font("Arial", Font.PLAIN, 11));
        g2d.drawString("Hide My Cucumber v1.0", 10, PANEL_HEIGHT - 10);
    }

    private void drawMenuCucumber(Graphics2D g2d, int x, int y) {
        g2d.setColor(new Color(34, 139, 34));
        g2d.fillOval(x, y, 35, 15);

        g2d.setColor(new Color(50, 180, 50));
        g2d.fillOval(x + 5, y + 4, 25, 7);

        g2d.setColor(new Color(34, 139, 34));
        for (int i = 0; i < 4; i++) {
            g2d.fillOval(x + 5 + i * 8, y + 2, 4, 4);
        }

        g2d.setColor(new Color(100, 180, 100));
        g2d.fillRect(x + 32, y + 5, 6, 4);

        g2d.setColor(new Color(20, 80, 20));
        g2d.drawOval(x, y, 35, 15);
    }

    public class CollisionManager {

        public Guard checkPlayerGuardCollision(Player player, java.util.List<Room> rooms) {
            Room playerRoom = getCurrentRoom(player, rooms);
            if (playerRoom == null) {
                return null;
            }

            Guard guard = playerRoom.getGuard();
            if (guard != null && guard.canDetectPlayer(player)) {
                return guard;
            }
            return null;
        }

        public Room.Cucumber checkPlayerCucumberCollision(Player player, java.util.List<Room.Cucumber> cucumbers) {
            for (Room.Cucumber cucumber : cucumbers) {
                if (!cucumber.isCollected() && player.getBounds().intersects(cucumber.getBounds())) {
                    return cucumber;
                }
            }
            return null;
        }

        public boolean isPlayerInRoom(Player player, Room room) {
            double centerX = player.getX() + player.getWidth() / 2.0;
            double centerY = player.getY() + player.getHeight() / 2.0;
            return room.containsPoint(centerX, centerY);
        }

        public Room getCurrentRoom(Player player, java.util.List<Room> rooms) {
            for (Room room : rooms) {
                if (isPlayerInRoom(player, room)) {
                    return room;
                }
            }
            return null;
        }

        public boolean isPlayerInsideRoom(Player player, Room room) {
            Rectangle playerBounds = player.getBounds();
            Rectangle roomBounds = room.getBounds();
            return roomBounds.contains(playerBounds);
        }

        public boolean isPlayerOnDoor(Player player, java.util.List<Rectangle> doors) {
            Rectangle playerBounds = player.getBounds();
            for (Rectangle door : doors) {
                if (playerBounds.intersects(door)) {
                    return true;
                }
            }
            return false;
        }

        public boolean canPlayerMoveToNewRoom(Room oldRoom, Room newRoom, Player player,
                java.util.List<Rectangle> doors) {
            if (oldRoom == newRoom) {
                return true;
            }

            return isPlayerOnDoor(player, doors);
        }

        public boolean checkPlayerObstacleCollision(Player player, java.util.List<Obstacle> obstacles) {
            Rectangle playerBounds = player.getBounds();
            for (Obstacle obstacle : obstacles) {
                if (playerBounds.intersects(obstacle.getBounds())) {
                    return true;
                }
            }
            return false;
        }

        public PowerUp checkPlayerPowerUpCollision(Player player, java.util.List<PowerUp> powerUps) {
            for (PowerUp powerUp : powerUps) {
                if (!powerUp.isCollected() && player.getBounds().intersects(powerUp.getBounds())) {
                    return powerUp;
                }
            }
            return null;
        }
    }

    public class InputHandler implements KeyListener {
        private Player player;
        private boolean restartRequested;
        private boolean saveRequested;
        private boolean loadRequested;
        private boolean highScoresRequested;
        private int difficultyChangeRequested;

        private boolean menuSelectRequested;
        private boolean menuUpRequested;
        private boolean menuDownRequested;
        private boolean escapeRequested;

        public InputHandler(Player player) {
            this.player = player;
            this.restartRequested = false;
            this.saveRequested = false;
            this.loadRequested = false;
            this.highScoresRequested = false;
            this.difficultyChangeRequested = 0;
            this.menuSelectRequested = false;
            this.menuUpRequested = false;
            this.menuDownRequested = false;
            this.escapeRequested = false;
        }

        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();

            switch (key) {
                case KeyEvent.VK_W:
                case KeyEvent.VK_UP:
                    player.setMovingUp(true);
                    break;
                case KeyEvent.VK_S:
                case KeyEvent.VK_DOWN:
                    player.setMovingDown(true);
                    break;
                case KeyEvent.VK_A:
                case KeyEvent.VK_LEFT:
                    player.setMovingLeft(true);
                    break;
                case KeyEvent.VK_D:
                case KeyEvent.VK_RIGHT:
                    player.setMovingRight(true);
                    break;
                case KeyEvent.VK_R:
                    restartRequested = true;
                    break;
                case KeyEvent.VK_F5:
                    saveRequested = true;
                    break;
                case KeyEvent.VK_F9:
                    loadRequested = true;
                    break;
                case KeyEvent.VK_H:
                    highScoresRequested = true;
                    break;
                case KeyEvent.VK_1:
                    difficultyChangeRequested = 1;
                    break;
                case KeyEvent.VK_2:
                    difficultyChangeRequested = 2;
                    break;
                case KeyEvent.VK_3:
                    difficultyChangeRequested = 3;
                    break;
                case KeyEvent.VK_ENTER:
                case KeyEvent.VK_SPACE:
                    menuSelectRequested = true;
                    break;
                case KeyEvent.VK_ESCAPE:
                    escapeRequested = true;
                    break;
            }

            if (key == KeyEvent.VK_UP) {
                menuUpRequested = true;
            } else if (key == KeyEvent.VK_DOWN) {
                menuDownRequested = true;
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            int key = e.getKeyCode();

            switch (key) {
                case KeyEvent.VK_W:
                case KeyEvent.VK_UP:
                    player.setMovingUp(false);
                    break;
                case KeyEvent.VK_S:
                case KeyEvent.VK_DOWN:
                    player.setMovingDown(false);
                    break;
                case KeyEvent.VK_A:
                case KeyEvent.VK_LEFT:
                    player.setMovingLeft(false);
                    break;
                case KeyEvent.VK_D:
                case KeyEvent.VK_RIGHT:
                    player.setMovingRight(false);
                    break;
            }
        }

        @Override
        public void keyTyped(KeyEvent e) {
        }

        public boolean isRestartRequested() {
            boolean result = restartRequested;
            restartRequested = false;
            return result;
        }

        public boolean isSaveRequested() {
            boolean result = saveRequested;
            saveRequested = false;
            return result;
        }

        public boolean isLoadRequested() {
            boolean result = loadRequested;
            loadRequested = false;
            return result;
        }

        public boolean isHighScoresRequested() {
            boolean result = highScoresRequested;
            highScoresRequested = false;
            return result;
        }

        public int getDifficultyChangeRequested() {
            int result = difficultyChangeRequested;
            difficultyChangeRequested = 0;
            return result;
        }

        public boolean isMenuSelectRequested() {
            boolean result = menuSelectRequested;
            menuSelectRequested = false;
            return result;
        }

        public boolean isMenuUpRequested() {
            boolean result = menuUpRequested;
            menuUpRequested = false;
            return result;
        }

        public boolean isMenuDownRequested() {
            boolean result = menuDownRequested;
            menuDownRequested = false;
            return result;
        }

        public boolean isEscapeRequested() {
            boolean result = escapeRequested;
            escapeRequested = false;
            return result;
        }

        public void setPlayer(Player player) {
            this.player = player;
        }
    }
}
