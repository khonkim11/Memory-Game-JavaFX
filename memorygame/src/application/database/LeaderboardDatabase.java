package application.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LeaderboardDatabase {

    public static List<Map<String, String>> getTopTenPlayers() {
        List<Map<String, String>> leaderboard = new ArrayList<>();

        // 1. The SQL Query (Calculating all stats in one go)
        String sql = "SELECT username, " +
                     "MAX(score) as top_score, " +
                     "SUM(xp_earned) as total_xp, " +
                     "AVG(accuracy) as avg_acc, " +
                     "MAX(play_date) as last_active " +
                     "FROM game_history " +
                     "GROUP BY username " +
                     "ORDER BY top_score DESC " +
                     "LIMIT 10";

        // 2. The try-with-resources block (This is where 'rs' is created)
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            int rank = 1;
            
            // 3. The loop (We MUST be inside this loop to use 'rs')
            while (rs.next()) {
                Map<String, String> row = new HashMap<>();
                
                // Fetching data from the ResultSet (rs)
                row.put("rank", String.valueOf(rank++));
                row.put("username", rs.getString("username").toUpperCase());
                row.put("score", String.valueOf(rs.getInt("top_score")));
                row.put("xp", String.valueOf(rs.getInt("total_xp")));
                
                // FIX: Accuracy is now calculated inside the loop where 'rs' exists
                double accValue = rs.getDouble("avg_acc");
                row.put("accuracy", String.format("%.1f%%", accValue));
                
                // Formatting the date
                String rawDate = rs.getString("last_active");
                row.put("last_seen", (rawDate != null) ? rawDate.substring(0, 16) : "N/A");
                
                leaderboard.add(row);
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Database Error: " + e.getMessage());
        }
        
        return leaderboard;
    }
}