package application.controller;

public class UserSession {

    private static UserSession instance;
    private String username;
    private int userId;
    private boolean isGuest;   
    private int highScore;
    private int totalMissions;
    private String bestTime;
    private String lastDifficulty;


    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public int getHighScore() { return highScore; }
    public void setHighScore(int highScore) { this.highScore = highScore; }

    public int getTotalMissions() { return totalMissions; }
    public void setTotalMissions(int totalMissions) { this.totalMissions = totalMissions; }

    public String getBestTime() { return bestTime; }
    public void setBestTime(String bestTime) { this.bestTime = bestTime; }

    public String getLastDifficulty() { return lastDifficulty; }
    public void setLastDifficulty(String lastDifficulty) { this.lastDifficulty = lastDifficulty; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public boolean isGuest() { return isGuest; }
    public void setGuest(boolean guest) { isGuest = guest; }
 
    public void cleanUserSession() {
        this.username = null;
        this.userId = 0;
        this.isGuest = true;
        this.highScore = 0;
        this.totalMissions = 0;
        this.bestTime = "00:00";
        this.lastDifficulty = "NORMAL";
        System.out.println("🚀 Session Cleared: Pilot Logged Out.");
    }
}