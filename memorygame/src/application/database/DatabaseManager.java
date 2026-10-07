package application.database;

import java.sql.*;
  
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DatabaseManager {
	public static Connection getConnection() throws SQLException {
	    return DriverManager.getConnection(URL, USER, PASS);
	}
	
    private static final String DB_NAME = "memorygame";
    private static final String SERVER_URL = "jdbc:mysql://localhost:3307/";
    private static final String URL = SERVER_URL + DB_NAME;
    private static final String USER = "root";
    private static final String PASS = ""; 

    
    public static void initialize() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            // 1. Connect to the SERVER first (to create the DB)
            try (Connection conn = DriverManager.getConnection(SERVER_URL, USER, PASS);
                 Statement stmt = conn.createStatement()) {
                
                // Now 'stmt' is valid inside these brackets
                stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
                System.out.println("✅ Database '" + DB_NAME + "' verified.");
            }

            // 2. Connect to the specific DATABASE (to create the Tables)
            try (Connection conn = getConnection(); // This uses the full URL with DB_NAME
                 Statement stmt = conn.createStatement()) {
                
                // Create Players Table
                String playerSql = "CREATE TABLE IF NOT EXISTS players (" +
                        "player_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " + 
                        "username VARCHAR(50) UNIQUE NOT NULL, " +            
                        "password_hash VARCHAR(255) NOT NULL, " +
                        "high_score INT DEFAULT 0, " +
                        "games_played INT DEFAULT 0, " +
                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
                stmt.execute(playerSql);

                // Create History Table
                String historySql = "CREATE TABLE IF NOT EXISTS game_history (" +
                        "history_id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "username VARCHAR(50), " +
                        "score INT, " +
                        "difficulty VARCHAR(50), " +
                        "time_taken VARCHAR(20), " +
                        "play_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                        "FOREIGN KEY (username) REFERENCES players(username))";
                stmt.execute(historySql);

                System.out.println("✅ Tables ready.");
            }
        } catch (Exception e) {
            System.err.println("❌ Database Error: " + e.getMessage());
        }
    }
    
    public static boolean validateLogin(String username, String password) {
        String sql = "SELECT password_hash FROM players WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // In a real app, use BCrypt here. For now, simple string match:
                    return password.equals(rs.getString("password_hash"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public static boolean registerUser(String username, String password) {
        // Check if user exists first
        if (userExists(username)) return false;
        String sql = "INSERT INTO players (username, password_hash) VALUES (?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            
            pstmt.executeUpdate();
            return true; 
            
        } catch (SQLException e) {
            System.err.println("❌ Database Error: " + e.getMessage());
            return false;
        }
    }
    public static boolean userExists(String username) {
        String sql = "SELECT 1 FROM players WHERE username = ? LIMIT 1";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); 
            }
        } catch (SQLException e) {
            return false;
        }
    } 
    public static void saveMatchResult(int playerId, int score, String diff, String time) {
        // We now insert player_id instead of username
        String sql = "INSERT INTO game_history (player_id, score, difficulty, time_taken) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, playerId); // Use the ID here
            pstmt.setInt(2, score);
            pstmt.setString(3, diff);
            pstmt.setString(4, time);
            pstmt.executeUpdate();
            
            System.out.println("✅ Match history saved for Player ID: " + playerId);
        } catch (SQLException e) {
            System.err.println("❌ Save Error: " + e.getMessage());
        }
    }

    public static int getTotalGlobalGames() {
        String sql = "SELECT SUM(games_played) as total FROM players";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("❌ Pulse Error: " + e.getMessage());
        }
        return 0;
    }
 // 1. Ensure the method signature accepts TWO extra integers: xp and accuracy
    public static void saveGameResult(String username, int score, String time, String diff, int xp, int accuracy) {
        
        // 2. The SQL must have 6 placeholders (?) to match the columns you want to fill
        String sql = "INSERT INTO game_history (username, score, time_taken, difficulty, xp_earned, accuracy) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // 3. Match the parameters to the placeholders in the exact order
            pstmt.setString(1, username);   // username
            pstmt.setInt(2, score);         // score
            pstmt.setString(3, time);       // time_taken
            pstmt.setString(4, diff);       // difficulty
            pstmt.setInt(5, xp);           // xp_earned
            pstmt.setInt(6, accuracy);     // accuracy
            
            pstmt.executeUpdate();
            System.out.println("🚀 Mission Data Saved: XP and Accuracy logged.");
            
        } catch (SQLException e) {
            System.err.println("❌ Database Save Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void syncPilotData(String username,
            int sessionScore,
            String level,
            int sessionTime) {

    	String sql = "UPDATE players SET " +
    			"high_score = GREATEST(high_score, ?), " +
    			"passed_level = ?, " +
    			"games_played = games_played + 1, " +
    			"best_time_seconds = CASE " +
    			"    WHEN best_time_seconds = 0 THEN ? " +
    			"    ELSE LEAST(best_time_seconds, ?) END, " +
    			"longest_time_seconds = GREATEST(longest_time_seconds, ?) " +
    			"WHERE username = ?";

    	try (Connection conn = getConnection();
    			PreparedStatement pstmt = conn.prepareStatement(sql)) {

    		pstmt.setInt(1, sessionScore);
    		pstmt.setString(2, level);
    		pstmt.setInt(3, sessionTime);
			pstmt.setInt(4, sessionTime);
			pstmt.setInt(5, sessionTime);
			pstmt.setString(6, username);
				
				int rows = pstmt.executeUpdate();
				
				if (rows > 0) {
				System.out.println("🚀 Data Synced for " + username);
				} else {
				System.out.println("⚠ No user found with username: " + username);
				}
				
				} catch (SQLException e) {
				System.err.println("❌ DB Sync Error: " + e.getMessage());
			}
		}
    
}