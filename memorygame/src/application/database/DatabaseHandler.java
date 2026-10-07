package application.database;

import java.sql.*;

public class DatabaseHandler {

    private static final String URL = "jdbc:mysql://localhost:3307/memorygame"; 
    private static final String USER = "root";
    private static final String PASS = ""; 

    public static void insertReview(String username, int rating, String comment) throws SQLException {

        String checkUserQuery = "SELECT COUNT(*) FROM players WHERE username = ?";
        
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            
            try (PreparedStatement checkStmt = conn.prepareStatement(checkUserQuery)) {
                checkStmt.setString(1, username);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) == 0) {
                    // This throws an exception back to the Controller if the user is fake
                    throw new SQLException("UNAUTHORIZED: PLAYER ID NOT FOUND IN ARCHIVES");
                }
            }

            // 2. If user exists, proceed to insert the review
            String insertQuery = "INSERT INTO game_reviews (username, rating, review_text) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
                pstmt.setString(1, username);
                pstmt.setInt(2, rating);
                pstmt.setString(3, comment);
                pstmt.executeUpdate();
            }
        }
    }
}