package application.controller;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.IOException;
import java.util.Optional;
import application.model.Session;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;


public class SelectionController {

    @FXML private ComboBox<String> difficultyCombo;
    @FXML private ToggleGroup modeGroup;
    @FXML private Label guestStatusLabel;
    @FXML private Label userNameLabel;
    @FXML private Circle statusDot;
    @FXML private Label serverStatus; 
    @FXML private Label onlineCount;  
    @FXML private Label pingLabel;
    
	public static String selectedMode = Session.selectedMode; 
	public static String selectedDifficulty = Session.currentDifficulty;
    

    @FXML
    public void initialize() {    	
        // 1. Setup ComboBox Items (Do this first)
        difficultyCombo.getItems().setAll(
            "Super Easy (3x4)", "Easy (4x4)", "Medium (4x5)", "Hard (5x6)", "Nightmare (6x6)"
        );

        difficultyCombo.setValue(Session.currentDifficulty);
        difficultyCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                Session.currentDifficulty = newVal;
                selectedDifficulty = newVal;
                System.out.println("🚀 Session Updated to: " + newVal);
            }
        });
        
        modeGroup.selectedToggleProperty().addListener((obs, oldT, newT) -> {
            if (newT != null) {
                ToggleButton selectedBtn = (ToggleButton) newT;
                // Clean the text and SAVE TO SESSION
                String modeName = selectedBtn.getText().replace(" 🔒", "").replace("▶ ", "").trim();
                
                Session.selectedMode = modeName; 
                selectedMode = modeName;      
                
                if (!UserSession.getInstance().isGuest()) {
                    applySelectionEffect(selectedBtn);
                }
            }
        });
        
        // 3. Branching Logic: Guest vs Member
        if (UserSession.getInstance().isGuest()) {
            setupGuestView(); // Handles locks, gold glow, and shows guestStatusLabel
            
            // Difficulty Lock Listener for Guests
            difficultyCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null && !newVal.equals("Super Easy (3x4)")) {
                    Platform.runLater(() -> {
                        showGuestRestrictionAlert(new ActionEvent(difficultyCombo, null));
                        difficultyCombo.getSelectionModel().select("Super Easy (3x4)");
                    });
                }
            });
        } else {
            // Logged in Pilot logic
            setupMemberView(); // Hides guestStatusLabel and resets buttons
            
            // Highlight the default button for members on start
            Platform.runLater(() -> {
                ToggleButton initial = (ToggleButton) modeGroup.getSelectedToggle();
                if (initial != null) applySelectionEffect(initial);
            });
        }
    }

    private void setupMemberView() {
        // Hide the guest-only status label
        if (guestStatusLabel != null) {
            guestStatusLabel.setVisible(false);
            guestStatusLabel.setManaged(false); 
        }

        // Default member difficulty
        difficultyCombo.getSelectionModel().select("Super Easy (3x4)");

        // Unlock all buttons and remove locks/icons
        for (Toggle toggle : modeGroup.getToggles()) {
            ToggleButton btn = (ToggleButton) toggle;
            btn.setDisable(false);
            btn.setOpacity(1.0);
            btn.setText(btn.getText().replace(" 🔒", "").replace("▶ ", ""));
            btn.setEffect(null);
            btn.setStyle(""); 
        }
    }

    private void setupGuestView() {
        // 1. Safety Check for ComboBox
        if (difficultyCombo != null) {
            difficultyCombo.getSelectionModel().select("Super Easy (3x4)");
            selectedDifficulty = "Super Easy (3x4)";
            selectedMode = "Animals"; // Set default mode
        }

        // 2. Status Label Logic (Prevents NullPointer if label is missing)
        if (guestStatusLabel != null) {
            guestStatusLabel.setText("TRAINING MODE: ANIMALS ONLY");
            guestStatusLabel.setVisible(true);
            guestStatusLabel.setManaged(true); 
            
            startPulseAnimation(guestStatusLabel);
            
            // Simple Pulse Animation
            FadeTransition ft = new FadeTransition(Duration.seconds(1.2), guestStatusLabel);
            ft.setFromValue(1.0);
            ft.setToValue(0.3);
            ft.setCycleCount(FadeTransition.INDEFINITE);
            ft.setAutoReverse(true);
            ft.play();
        }

        // 3. Toggle Group Logic (The most common crash site)
        if (modeGroup != null && modeGroup.getToggles() != null) {
            for (Toggle toggle : modeGroup.getToggles()) {
                if (toggle instanceof ToggleButton) {
                    ToggleButton btn = (ToggleButton) toggle;
                    
                    // Clean text to avoid "Animals 🔒 🔒" if method is called twice
                    String baseText = btn.getText().replace(" 🔒", "").replace("▶ ", "");

                    if (!baseText.equals("Animals")) {
                        btn.setDisable(true);
                        btn.setOpacity(0.4);
                        btn.setText(baseText + " 🔒");
                        btn.setEffect(null);
                    } else {
                        btn.setDisable(false);
                        btn.setOpacity(1.0);
                        btn.setSelected(true);
                        btn.setText("▶ " + baseText);
                        
                        // Apply Gold Glow
                        DropShadow goldGlow = new DropShadow(20, Color.web("#d4af37"));
                        btn.setEffect(goldGlow);
                        btn.setStyle("-fx-border-color: #d4af37; -fx-text-fill: #d4af37;");
                    }
                }
            }
        }
    }
    private void startPulseAnimation(Node node) {
        FadeTransition ft = new FadeTransition(Duration.seconds(1.2), node);
        ft.setFromValue(1.0);
        ft.setToValue(0.3);
        ft.setCycleCount(FadeTransition.INDEFINITE);
        ft.setAutoReverse(true);
        ft.play();
    }
    
    @FXML
    private void handleBackHome(ActionEvent event) {
    	handleSceneTransition(event, "/application/view/login.fxml", false);
    }
    private void showGuestRestrictionAlert(ActionEvent event) {
        // 1. Capture the stage immediately from the button click
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setTitle("Training Mode Only");
        alert.setHeaderText("🚀 STARTER MISSION ACTIVE");
        alert.setContentText("Guests are restricted to the Animal Training sector.\nLog in to unlock all levels!");

        ButtonType loginBtnType = new ButtonType("SIGN IN");
        ButtonType stayBtnType = new ButtonType("START TRAINING");
        ButtonType cancelBtnType = new ButtonType("BACK", ButtonBar.ButtonData.CANCEL_CLOSE); 
        
        alert.getButtonTypes().setAll(loginBtnType, stayBtnType, cancelBtnType);
        styleDialog(alert.getDialogPane()); 

        Optional<ButtonType> result = alert.showAndWait();
        
        if (result.isPresent()) {
            if (result.get() == loginBtnType) {
                goToLogin(event);
            } 
            else if (result.get() == stayBtnType) {
                // 2. SET THE TRAINING PARAMETERS
                // Make sure these variables are 'public static' in SelectionController
                selectedDifficulty = "Super Easy (3x4)";
                selectedMode = "Animals"; 
                
                try {
                    // 3. LOAD AND SWITCH TO MEMORY GAME
                    // Path must match your folder structure exactly
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/view/MemoryGame.fxml"));
                    Parent root = loader.load();
                    
                    stage.setScene(new Scene(root));
                    stage.centerOnScreen();
                    stage.show();
                    
                    handleSceneTransition(event, "/application/view/login.fxml", true);
                    System.out.println("🔒 Returning to Login and clearing session.");
                    
                } catch (IOException e) {
                    System.err.println("❌ Error: Could not load MemoryGame.fxml");
                    e.printStackTrace();
                }
            }
        }
    }
    @FXML
    private void goToLogin(ActionEvent event) {
        UserSession.getInstance().cleanUserSession(); 
        handleSceneTransition(event, "/application/view/login.fxml", false); 
    }

    private void styleDialog(DialogPane dialogPane) {
        // Main Container
        dialogPane.setStyle("-fx-background-color: #1e1e1e; -fx-border-color: #d4af37; -fx-border-width: 2px; -fx-border-radius: 5px;");
        
        // Header Text Styling
        Label headerLabel = (Label) dialogPane.lookup(".header-panel .label");
        if (headerLabel != null) {
            headerLabel.setStyle("-fx-text-fill: #d4af37; -fx-font-weight: bold; -fx-font-size: 18px;");
        }

        // Content Text Styling
        Label contentLabel = (Label) dialogPane.lookup(".content");
        if (contentLabel != null) {
            contentLabel.setStyle("-fx-text-fill: #e0e0e0; -fx-font-size: 14px;");
        }

        // Button Styling (Iterate through the button types)
        for (ButtonType bt : dialogPane.getButtonTypes()) {
            Button btn = (Button) dialogPane.lookupButton(bt);
            if (btn != null) {
                String baseStyle = "-fx-background-color: transparent; -fx-border-color: #d4af37; -fx-text-fill: #d4af37; -fx-cursor: hand; -fx-font-weight: bold;";
                btn.setStyle(baseStyle);
                
                // Hover effects
                btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #d4af37; -fx-text-fill: #1e1e1e; -fx-font-weight: bold;"));
                btn.setOnMouseExited(e -> btn.setStyle(baseStyle));
            }
        }
    }
    
    @FXML
    private void handlePlayAction(ActionEvent event) {
        
        try {
            var resource = getClass().getResource("/application/view/MemoryGame.fxml");
            
            if (resource == null) {
                System.err.println("❌ MemoryGame.fxml not found in /application/view/");
                return;
            }

            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();

            // Get the current stage from the button that was clicked
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            
            // Switch the scene
            stage.getScene().setRoot(root);
            stage.sizeToScene(); 
            stage.centerOnScreen();

        } catch (IOException e) {
            System.err.println("❌ Failed to switch to Memory Game!");
            e.printStackTrace();
        }
    }

    private void applySelectionEffect(ToggleButton selectedBtn) {

        for (Toggle t : modeGroup.getToggles()) {
            ((ToggleButton) t).setEffect(null);
            ((ToggleButton) t).setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-text-fill: white;");
        }

        DropShadow activeGlow = new DropShadow();
        activeGlow.setBlurType(BlurType.THREE_PASS_BOX);
        activeGlow.setColor(Color.web("#00d2ff"));
        activeGlow.setRadius(20);
        activeGlow.setSpread(0.2);


        selectedBtn.setEffect(activeGlow);
        selectedBtn.setStyle("-fx-background-color: rgba(0, 210, 255, 0.2); -fx-text-fill: #00d2ff; -fx-border-color: #00d2ff; -fx-border-radius: 10;");

        ScaleTransition pop = new ScaleTransition(Duration.millis(150), selectedBtn);
        pop.setFromX(1.0); pop.setFromY(1.0);
        pop.setToX(1.05);  pop.setToY(1.05);
        pop.setCycleCount(2);
        pop.setAutoReverse(true);
        pop.play();
    }

    private void handleSceneTransition(ActionEvent event, String fxmlPath, boolean isLogin) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Parent currentRoot = stage.getScene().getRoot();

        FadeTransition fadeOut = new FadeTransition(Duration.millis(400), currentRoot);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        fadeOut.setOnFinished(e -> {
            try {
                if (isLogin) UserSession.getInstance().cleanUserSession();
                
                Parent nextRoot = FXMLLoader.load(getClass().getResource(fxmlPath));
                nextRoot.setOpacity(0);
                
                stage.getScene().setRoot(nextRoot);

                // FIX: Set original dimensions for the Login screen
                if (isLogin) {
                    stage.setWidth(800);  
                    stage.setHeight(600); 
                } else {
                    stage.sizeToScene();  
                }

                stage.centerOnScreen();

                FadeTransition fadeIn = new FadeTransition(Duration.millis(400), nextRoot);
                fadeIn.setToValue(1.0);
                fadeIn.play();
                
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        fadeOut.play();
    }
    
    @FXML
    private void handleShowHistory(ActionEvent event) {
        try {
            // 1. Load the History FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/view/History.fxml"));
            Parent root = loader.load();
            
            // 2. Get current stage
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            
            // 3. Smooth Fade Transition
            root.setOpacity(0);
            Scene scene = new Scene(root);
            stage.setScene(scene);
            
            FadeTransition fadeIn = new FadeTransition(Duration.millis(500), root);
            fadeIn.setToValue(1.0);
            fadeIn.play();
            
        } catch (IOException e) {
            System.err.println("❌ Error: Could not load History.fxml. Check the path!");
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleBack(javafx.event.ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/application/view/GameSelection.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}