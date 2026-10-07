package application.database;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class ProfileDatabase {

    public static Map<String, String> getPilotStats(String username) {
        Map<String, String> stats = new HashMap<>();

        String sql = "SELECT " +
                     "  MAX(score) as high_score, " +
                     "  COUNT(*) as total_missions, " +
                     "  AVG(score) as avg_score, " + // New Detail
                     "  SUM(xp_earned) as total_xp " + // New Detail
                     "FROM game_history WHERE username = ?";

        // Second query for the very last game played
        String lastGameSql = "SELECT time_taken, difficulty FROM game_history " +
                             "WHERE username = ? ORDER BY play_date DESC LIMIT 1";

        try (Connection conn = DatabaseManager.getConnection()) {
            
            // Part 1: Aggregates
            try (PreparedStatement p1 = conn.prepareStatement(sql)) {
                p1.setString(1, username);
                ResultSet rs1 = p1.executeQuery();
                if (rs1.next()) {
                    stats.put("highScore", String.valueOf(rs1.getInt("high_score")));
                    stats.put("totalMissions", String.valueOf(rs1.getInt("total_missions")));
                    stats.put("avgScore", String.valueOf(rs1.getInt("avg_score")));
                    stats.put("totalXP", String.valueOf(rs1.getInt("total_xp")));
                }
            }

            // Part 2: Last Game Details
            try (PreparedStatement p2 = conn.prepareStatement(lastGameSql)) {
                p2.setString(1, username);
                ResultSet rs2 = p2.executeQuery();
                if (rs2.next()) {
                    stats.put("bestTime", rs2.getString("time_taken"));
                    stats.put("lastDiff", rs2.getString("difficulty"));
                } else {
                    stats.put("bestTime", "--:--");
                    stats.put("lastDiff", "NONE");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }
}