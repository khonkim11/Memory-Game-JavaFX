package application.controller;

public class GameRecord {
    private String date;
    private String difficulty;
    private int score;

    public GameRecord(String date, String difficulty, int score) {
        this.date = date;
        this.difficulty = difficulty;
        this.score = score;
    }

    public String getDate() { return date; }
    public String getDifficulty() { return difficulty; }
    public int getScore() { return score; }
}