package application.controller;

import application.model.Session;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LevelUpController {

    @FXML private VBox mainContainer; 
    @FXML private Label statusLabel;
    @FXML private Label typeLabel;
    @FXML private Label levelLabel;
    
    @FXML
    public void initialize() {
    	if (statusLabel != null) {
            statusLabel.setText("LOGGING TO ARCHIVE... SUCCESS ✅");
        }
        if (levelLabel != null) {
            levelLabel.setText("RANK: COMMANDER");
        }
    }

    @FXML
    private void startNextMission() {
        // 1. Get current states
        String currentDiff = Session.currentDifficulty;
        String currentMode = Session.selectedMode;

        if (currentDiff.equals("Super Easy (3x4)")) {
            Session.currentDifficulty = "Easy (4x4)";
            Session.selectedMode = "Animals";
        } 
        else if (currentDiff.equals("Easy (4x4)")) {
            Session.currentDifficulty = "Medium (4x5)";
            Session.selectedMode = "Space"; 
        }
        else if (currentDiff.equals("Medium (4x5)")) {
            Session.currentDifficulty = "Hard (5x6)";
            Session.selectedMode = "Food"; 
        }

        System.out.println("🛸 PREVIOUS MISSION: " + currentMode + " COMPLETED.");
        System.out.println("🚀 NEXT MISSION: " + Session.selectedMode + " at " + Session.currentDifficulty);

        switchScene("/application/view/MemoryGame.fxml");
    }
    @FXML
    private void goToProfile() {
        // This handles the "RETURN TO PROFILE" button
        switchScene("/application/view/Profile.fxml");
    }

    private void switchScene(String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) mainContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            System.err.println("Grammar Error: Path " + fxmlPath + " is incorrect!");
            e.printStackTrace();
        }
    }
}