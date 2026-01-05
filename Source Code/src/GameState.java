public class GameState {
    public enum State {
        MENU,
        PLAYING,
        WON,
        GAME_OVER,
        PAUSED
    }

    private State currentState;
    private int cucumbersCollected;
    private int totalCucumbers;
    private String gameOverMessage;
    private long startTime;
    private long completionTime;

    public GameState() {
        this.currentState = State.MENU;
        this.cucumbersCollected = 0;
        this.totalCucumbers = 5;
        this.gameOverMessage = "";
        this.startTime = 0;
        this.completionTime = 0;
    }

    public void collectCucumber() {
        cucumbersCollected++;
        if (cucumbersCollected >= totalCucumbers) {
            currentState = State.WON;
            completionTime = System.currentTimeMillis() - startTime;
        }
    }

    public void caughtByGuard(String guardName) {
        currentState = State.GAME_OVER;
        gameOverMessage = "Caught by " + guardName + "!";
    }

    public void reset() {
        currentState = State.PLAYING;
        cucumbersCollected = 0;
        gameOverMessage = "";
        startTime = System.currentTimeMillis();
        completionTime = 0;
    }

    public int getProgressPercentage() {
        return (cucumbersCollected * 100) / totalCucumbers;
    }

    public long getElapsedTime() {
        if (currentState == State.WON) {
            return completionTime;
        }
        return System.currentTimeMillis() - startTime;
    }

    public long getCompletionTime() {
        return completionTime;
    }

    public String formatTime(long timeMs) {
        long minutes = (timeMs / 1000) / 60;
        long seconds = (timeMs / 1000) % 60;
        long millis = (timeMs % 1000) / 10;
        return String.format("%02d:%02d.%02d", minutes, seconds, millis);
    }

    public State getCurrentState() {
        return currentState;
    }

    public int getCucumbersCollected() {
        return cucumbersCollected;
    }

    public int getTotalCucumbers() {
        return totalCucumbers;
    }

    public String getGameOverMessage() {
        return gameOverMessage;
    }

    public boolean isPlaying() {
        return currentState == State.PLAYING;
    }

    public boolean isWon() {
        return currentState == State.WON;
    }

    public boolean isGameOver() {
        return currentState == State.GAME_OVER;
    }

    public void setCucumbersCollected(int count) {
        this.cucumbersCollected = count;
        if (cucumbersCollected >= totalCucumbers) {
            currentState = State.WON;
        }
    }

    public boolean isMenu() {
        return currentState == State.MENU;
    }

    public void startGame() {
        currentState = State.PLAYING;
        cucumbersCollected = 0;
        startTime = System.currentTimeMillis();
        completionTime = 0;
        gameOverMessage = "";
    }

    public void returnToMenu() {
        currentState = State.MENU;
    }
}
