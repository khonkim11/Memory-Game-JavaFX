package application.model;

public class PlayerStats {
    private String difficulty;
    private int score;
    private int xpEarned;
    private String timeTaken;
    private int accuracy;
    private String playDate;

    public PlayerStats(String difficulty, int score, int xpEarned, String timeTaken, int accuracy, String playDate) {
        this.difficulty = difficulty;
        this.score = score;
        this.xpEarned = xpEarned;
        this.timeTaken = timeTaken;
        this.accuracy = accuracy;
        this.playDate = playDate;
    }

    // Getters are REQUIRED for TableView to show data
    public String getDifficulty() { return difficulty; }
    public int getScore() { return score; }
    public int getXpEarned() { return xpEarned; }
    public String getTimeTaken() { return timeTaken; }
    public int getAccuracy() { return accuracy; }
    public String getPlayDate() { return playDate; }
}