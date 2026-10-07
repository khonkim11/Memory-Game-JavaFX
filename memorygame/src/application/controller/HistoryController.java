package application.controller;

import application.database.DatabaseManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.sql.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader; 
import javafx.scene.Parent;        
import javafx.scene.Scene;       
import javafx.stage.Stage;      
import javafx.scene.Node;         
import java.io.IOException;

public class HistoryController {
    
    @FXML private VBox historyContainer; 
    @FXML private Label statusLabel;

    @FXML
    public void initialize() {
        loadPlayerHistory();
    }

    private void loadPlayerHistory() {
        String currentUser = UserSession.getInstance().getUsername();
        historyContainer.getChildren().clear(); // Clear old data

        String query = "SELECT difficulty, score, xp_earned, time_taken, accuracy, play_date " +
                       "FROM game_history WHERE username = ? ORDER BY play_date DESC";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, currentUser);
            ResultSet rs = pstmt.executeQuery();

            int count = 0;
            while (rs.next()) {
                // Create a "Mission Card" for each row
                historyContainer.getChildren().add(createMissionCard(
                    rs.getString("difficulty"),
                    rs.getInt("score"),
                    rs.getString("time_taken"),
                    rs.getInt("accuracy"),
                    rs.getString("play_date")
                ));
                count++;
            }

            statusLabel.setText(count + " MISSIONS RECOVERED FOR " + currentUser.toUpperCase());

        } catch (SQLException e) {
            e.printStackTrace();
            statusLabel.setText("DATABASE ERROR");
        }
    }

    // This helper method designs the card UI purely in Java
    private HBox createMissionCard(String diff, int score, String time, int acc, String date) {
        HBox card = new HBox(20);
        card.setStyle("-fx-background-color: #0f0c29; -fx-padding: 15; -fx-border-color: #00d2ff; -fx-border-width: 0.5;");
        card.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        VBox mainInfo = new VBox(5);
        Label lblDiff = new Label(diff.toUpperCase());
        lblDiff.setStyle("-fx-text-fill: #3aedb9; -fx-font-weight: bold; -fx-font-size: 14;");
        Label lblDate = new Label(date);
        lblDate.setStyle("-fx-text-fill: #666; -fx-font-size: 10;");
        mainInfo.getChildren().addAll(lblDiff, lblDate);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        VBox stats = new VBox(2);
        stats.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
        Label lblScore = new Label("SCORE: " + score);
        lblScore.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        Label lblTime = new Label("TIME: " + time + " | ACC: " + acc + "%");
        lblTime.setStyle("-fx-text-fill: #00d2ff; -fx-font-size: 11;");
        stats.getChildren().addAll(lblScore, lblTime);

        card.getChildren().addAll(mainInfo, spacer, stats);
        return card;
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/application/view/GameSelection.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}