package application.controller;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import java.util.List;
import java.util.Map;
import javafx.stage.Stage;           
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import java.io.IOException;       
import javafx.scene.Scene;
import javafx.scene.control.Button;
import application.database.LeaderboardDatabase;

public class LeaderboardController {
	@FXML private Button returnBtn;
	@FXML private VBox rankingsContainer;

    private void addRandomPraise() {
        String[] praises = {
            "YOU'RE A CHAMPION! 🥇", 
            "ABSOLUTELY AMAZING! ⭐", 
            "FUTURE PILOT IN THE MAKING! 🚀", 
            "UNSTOPPABLE! 🔥"
        };
        
        int randomIndex = (int)(Math.random() * praises.length);
        Label praiseLabel = new Label(praises[randomIndex]);
        praiseLabel.setStyle("-fx-text-fill: #f1c40f; -fx-font-size: 22; -fx-font-weight: bold;");
        
    }
    
    @FXML
    public void initialize() {
    	addRandomPraise();
        displayKidsLeaderboard();
    }

    private void displayKidsLeaderboard() {
        List<Map<String, String>> topPlayers = LeaderboardDatabase.getTopTenPlayers();
        int delay = 0;

        for (Map<String, String> player : topPlayers) {
            // Create a "Player Card"
            HBox card = createPlayerCard(player);
            card.setOpacity(0);
            card.setTranslateX(-100); // Start off-screen to the left

            rankingsContainer.getChildren().add(card);

            // Create Pop-in Animation for each card
            FadeTransition ft = new FadeTransition(Duration.millis(500), card);
            ft.setToValue(1.0);

            TranslateTransition tt = new TranslateTransition(Duration.millis(500), card);
            tt.setToX(0);

            // Stagger the animations so they appear one by one
            SequentialTransition st = new SequentialTransition(new PauseTransition(Duration.millis(delay)), ft);
            ParallelTransition pt = new ParallelTransition(st, tt);
            pt.play();

            delay += 150; // Delay for the next card
        }
    }

    private HBox createPlayerCard(Map<String, String> player) {
        HBox card = new HBox(20);
        card.setStyle("-fx-background-color: #34495e; -fx-background-radius: 15; -fx-padding: 15; -fx-alignment: center-left; -fx-border-color: #f1c40f; -fx-border-width: 2;");
        
        // Rank Badge
        Label rank = new Label("#" + player.get("rank"));
        rank.setStyle("-fx-text-fill: #f1c40f; -fx-font-size: 24; -fx-font-weight: bold;");

        // Name and Score
        VBox details = new VBox(5);
        Label name = new Label(player.get("username"));
        name.setStyle("-fx-text-fill: white; -fx-font-size: 18; -fx-font-weight: bold;");
        
        Label stats = new Label("⭐ Score: " + player.get("score") + "  |  🎯 Acc: " + player.get("accuracy"));
        stats.setStyle("-fx-text-fill: #bdc3c7; -fx-font-size: 14;");

        details.getChildren().addAll(name, stats);

        // XP Medal
        Label xp = new Label("💎 " + player.get("xp") + " XP");
        xp.setStyle("-fx-text-fill: #55efc4; -fx-font-weight: bold;");

        card.getChildren().addAll(rank, details, xp);
        return card;
    }
    @FXML
    private void handleReturn() {
        try {
            Stage currentStage = (Stage) returnBtn.getScene().getWindow();
            
            Parent root = FXMLLoader.load(getClass().getResource("/application/view/login.fxml"));

            currentStage.close();

            // 4. Create and show the LOGIN window
            Stage loginStage = new Stage();
            loginStage.setScene(new Scene(root));
            loginStage.setTitle("Pilot Login");
            loginStage.show();

        } catch (IOException e) {
            System.err.println("❌ Navigation Error!");
            e.printStackTrace();
        }
    }
}