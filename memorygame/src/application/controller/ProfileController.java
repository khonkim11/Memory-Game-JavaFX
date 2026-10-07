package application.controller;

import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import java.util.Map;
import application.database.ProfileDatabase;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
import javafx.scene.Parent;

public class ProfileController {

    @FXML private Label pilotNameDisplay;
    @FXML private Label highScoreLabel;
    @FXML private Label missionsLabel;
    @FXML private Label bestTimeLabel;
    @FXML private Label diffLabel;
    @FXML private Label avgScoreLabel;
    @FXML private Label xpLabel;
    @FXML private Label rankLabel;
    @FXML private Circle scannerRing;
    @FXML private ImageView profileAvatar;
    @FXML private VBox mainContainer;
    
    @FXML
    public void initialize() {
        // 1. Start animations
        startScannerAnimation();

        String avatarUrl = "https://img.freepik.com/free-vector/man-wearing-eyeglasses-avatar-character_24877-83454.jpg?semt=ais_user_personalization&w=740&q=80";
        
        try {
            // The 'true' parameter enables background loading so the UI doesn't freeze
            Image image = new Image(avatarUrl, true); 
            profileAvatar.setImage(image);
        } catch (Exception e) {
            System.err.println("Failed to load avatar: " + e.getMessage());
            // Optional: Set a local placeholder image if the URL fails
        }
        
        // 2. Identify the current user from session
        String user = UserSession.getInstance().getUsername();
        if (user == null || user.isEmpty()) {
            pilotNameDisplay.setText("GUEST PILOT");
            return;
        }

        pilotNameDisplay.setText(user.toUpperCase());

        // 3. Fetch all details from the Database
        Map<String, String> stats = ProfileDatabase.getPilotStats(user);

        // 4. Update UI with all details

        highScoreLabel.setText(stats.getOrDefault("highScore", "0"));
        missionsLabel.setText(stats.getOrDefault("totalMissions", "0"));
        bestTimeLabel.setText(stats.getOrDefault("bestTime", "--:--"));
        

        String lastDiff = stats.getOrDefault("lastDiff", "N/A");
        diffLabel.setText(lastDiff.toUpperCase());


        if (avgScoreLabel != null) 
            avgScoreLabel.setText(stats.getOrDefault("avgScore", "0"));
        
        if (xpLabel != null) 
            xpLabel.setText(stats.getOrDefault("totalXP", "0") + " XP");

        // Logic for Rank (Example: based on mission count)
        if (rankLabel != null) {
            int missions = Integer.parseInt(stats.getOrDefault("totalMissions", "0"));
            rankLabel.setText(determineRank(missions));
        }
    }

    private String determineRank(int missions) {
        if (missions >= 50) return "ACE PILOT";
        if (missions >= 20) return "VETERAN";
        if (missions >= 5)  return "SOLDIER";
        return "ROOKIE";
    }

    private void startScannerAnimation() {
        if (scannerRing != null) {
            RotateTransition rt = new RotateTransition(Duration.seconds(4), scannerRing);
            rt.setByAngle(360);
            rt.setCycleCount(RotateTransition.INDEFINITE);
            rt.setInterpolator(Interpolator.LINEAR);
            rt.play();
        }
    }

    @FXML
    private void handleReturn(ActionEvent event) {
        try {
           
        	FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/view/GameSelection.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            System.out.println("🛸 Returning to Game Selection...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/view/login.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            System.out.println("🔒 Logged out.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
   
    @FXML
    private void handleLevelUpClick(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/application/view/LevelUp.fxml"));
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace(); 
            System.out.println("Path Error: Check if it is LevelUp.fxml or levelUp.fxml");
        }
    }

    // Ensure this method is in your ProfileController to handle the switching
    public void switchScene(String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) mainContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.out.println("Error: Make sure " + fxmlPath + " exists in your folder!");
            e.printStackTrace();
        }
    }
    
}