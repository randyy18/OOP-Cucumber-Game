import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:h2:./game_data;AUTO_SERVER=TRUE";
    private static DatabaseManager instance;
    private Connection connection;

    private DatabaseManager() {
        initializeDatabase();
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    private void initializeDatabase() {
        try {
            Class.forName("org.h2.Driver");

            connection = DriverManager.getConnection(DB_URL, "sa", "");

            createTables();

            System.out.println("Database initialized successfully!");
        } catch (ClassNotFoundException e) {
            System.err.println("H2 JDBC driver not found: " + e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    private void createTables() throws SQLException {
        String createSavesTable = """
                    CREATE TABLE IF NOT EXISTS game_saves (
                        id INT PRIMARY KEY AUTO_INCREMENT,
                        player_name VARCHAR(255) DEFAULT 'Player',
                        player_x DOUBLE NOT NULL,
                        player_y DOUBLE NOT NULL,
                        cucumbers_collected INT DEFAULT 0,
                        room_states VARCHAR(255),
                        save_date VARCHAR(255) NOT NULL
                    )
                """;

        String createHighScoresTable = """
                    CREATE TABLE IF NOT EXISTS high_scores (
                        id INT PRIMARY KEY AUTO_INCREMENT,
                        player_name VARCHAR(255) DEFAULT 'Player',
                        cucumbers_collected INT DEFAULT 0,
                        completion_time_ms BIGINT,
                        completed INT DEFAULT 0,
                        difficulty VARCHAR(20) DEFAULT 'Normal',
                        score_date VARCHAR(255) NOT NULL
                    )
                """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createSavesTable);
            stmt.execute(createHighScoresTable);

            try {
                stmt.execute(
                        "ALTER TABLE high_scores ADD COLUMN IF NOT EXISTS difficulty VARCHAR(20) DEFAULT 'Normal'");
            } catch (SQLException e) {
            }
        }
    }

    public boolean saveGame(SaveData saveData) {
        if (!isConnected()) {
            System.err.println("Cannot save: database not connected");
            return false;
        }

        String deleteSql = "DELETE FROM game_saves";
        String insertSql = """
                    INSERT INTO game_saves (player_name, player_x, player_y, cucumbers_collected, room_states, save_date)
                    VALUES (?, ?, ?, ?, ?, ?)
                """;

        try {
            try (Statement stmt = connection.createStatement()) {
                stmt.execute(deleteSql);
            }

            try (PreparedStatement pstmt = connection.prepareStatement(insertSql)) {
                pstmt.setString(1, saveData.getPlayerName() != null ? saveData.getPlayerName() : "Player");
                pstmt.setDouble(2, saveData.getPlayerX());
                pstmt.setDouble(3, saveData.getPlayerY());
                pstmt.setInt(4, saveData.getCucumbersCollected());
                pstmt.setString(5, saveData.roomStatesToString());
                pstmt.setString(6, LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

                pstmt.executeUpdate();
            }

            System.out.println("Game saved successfully!");
            return true;
        } catch (SQLException e) {
            System.err.println("Error saving game: " + e.getMessage());
            return false;
        }
    }

    public SaveData loadGame() {
        if (!isConnected()) {
            System.err.println("Cannot load: database not connected");
            return null;
        }

        String sql = "SELECT * FROM game_saves ORDER BY id DESC LIMIT 1";

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                SaveData saveData = new SaveData();
                saveData.setId(rs.getInt("id"));
                saveData.setPlayerName(rs.getString("player_name"));
                saveData.setPlayerX(rs.getDouble("player_x"));
                saveData.setPlayerY(rs.getDouble("player_y"));
                saveData.setCucumbersCollected(rs.getInt("cucumbers_collected"));
                saveData.setRoomUnlockStates(SaveData.parseRoomStates(rs.getString("room_states")));
                saveData.setSaveDate(rs.getString("save_date"));

                System.out.println("Game loaded successfully!");
                return saveData;
            }
        } catch (SQLException e) {
            System.err.println("Error loading game: " + e.getMessage());
        }

        return null;
    }

    public boolean hasSaveGame() {
        String sql = "SELECT COUNT(*) FROM game_saves";

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking save: " + e.getMessage());
        }
        return false;
    }

    public void saveHighScore(String playerName, int cucumbersCollected, long completionTimeMs, boolean completed,
            String difficulty) {
        String sql = """
                    INSERT INTO high_scores (player_name, cucumbers_collected, completion_time_ms, completed, difficulty, score_date)
                    VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, playerName);
            pstmt.setInt(2, cucumbersCollected);
            pstmt.setLong(3, completionTimeMs);
            pstmt.setInt(4, completed ? 1 : 0);
            pstmt.setString(5, difficulty);
            pstmt.setString(6, LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

            pstmt.executeUpdate();
            System.out.println("High score saved! Difficulty: " + difficulty);
        } catch (SQLException e) {
            System.err.println("Error saving high score: " + e.getMessage());
        }
    }

    public ResultSet getTopHighScores(int limit) {
        String sql = """
                    SELECT player_name, cucumbers_collected, completion_time_ms, completed, score_date
                    FROM high_scores
                    WHERE completed = 1
                    ORDER BY completion_time_ms ASC
                    LIMIT ?
                """;

        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, limit);
            return pstmt.executeQuery();
        } catch (SQLException e) {
            System.err.println("Error getting high scores: " + e.getMessage());
            return null;
        }
    }

    public String[] getHighScoresList(int limit) {
        if (!isConnected()) {
            return new String[] { "Database not connected" };
        }

        String sql = """
                    SELECT player_name, completion_time_ms, difficulty, score_date
                    FROM high_scores
                    WHERE completed = 1
                    ORDER BY completion_time_ms ASC
                    LIMIT ?
                """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();

            java.util.List<String> scores = new java.util.ArrayList<>();
            int rank = 1;
            while (rs.next()) {
                String name = rs.getString("player_name");
                long timeMs = rs.getLong("completion_time_ms");
                String difficulty = rs.getString("difficulty");
                if (difficulty == null)
                    difficulty = "Normal";
                String formattedTime = formatTime(timeMs);
                scores.add(String.format("%d. %s - %s [%s]", rank, name, formattedTime, difficulty));
                rank++;
            }

            if (scores.isEmpty()) {
                return new String[] { "No high scores yet!" };
            }

            return scores.toArray(new String[0]);
        } catch (SQLException e) {
            System.err.println("Error getting high scores: " + e.getMessage());
            return new String[] { "Error loading scores" };
        }
    }

    private String formatTime(long timeMs) {
        long minutes = (timeMs / 1000) / 60;
        long seconds = (timeMs / 1000) % 60;
        long millis = (timeMs % 1000) / 10;
        return String.format("%02d:%02d.%02d", minutes, seconds, millis);
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing database: " + e.getMessage());
        }
    }

    public static class SaveData {
        private int id;
        private String playerName;
        private double playerX;
        private double playerY;
        private int cucumbersCollected;
        private boolean[] roomUnlockStates;
        private String saveDate;

        public SaveData() {
            this.roomUnlockStates = new boolean[5];
        }

        public SaveData(String playerName, double playerX, double playerY,
                int cucumbersCollected, boolean[] roomUnlockStates) {
            this.playerName = playerName;
            this.playerX = playerX;
            this.playerY = playerY;
            this.cucumbersCollected = cucumbersCollected;
            this.roomUnlockStates = roomUnlockStates;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getPlayerName() {
            return playerName;
        }

        public void setPlayerName(String playerName) {
            this.playerName = playerName;
        }

        public double getPlayerX() {
            return playerX;
        }

        public void setPlayerX(double playerX) {
            this.playerX = playerX;
        }

        public double getPlayerY() {
            return playerY;
        }

        public void setPlayerY(double playerY) {
            this.playerY = playerY;
        }

        public int getCucumbersCollected() {
            return cucumbersCollected;
        }

        public void setCucumbersCollected(int cucumbersCollected) {
            this.cucumbersCollected = cucumbersCollected;
        }

        public boolean[] getRoomUnlockStates() {
            return roomUnlockStates;
        }

        public void setRoomUnlockStates(boolean[] roomUnlockStates) {
            this.roomUnlockStates = roomUnlockStates;
        }

        public String getSaveDate() {
            return saveDate;
        }

        public void setSaveDate(String saveDate) {
            this.saveDate = saveDate;
        }

        public String roomStatesToString() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < roomUnlockStates.length; i++) {
                sb.append(roomUnlockStates[i] ? "1" : "0");
                if (i < roomUnlockStates.length - 1) {
                    sb.append(",");
                }
            }
            return sb.toString();
        }

        public static boolean[] parseRoomStates(String statesString) {
            boolean[] states = new boolean[5];
            if (statesString != null && !statesString.isEmpty()) {
                String[] parts = statesString.split(",");
                for (int i = 0; i < Math.min(parts.length, 5); i++) {
                    states[i] = "1".equals(parts[i].trim());
                }
            }
            return states;
        }
    }
}
