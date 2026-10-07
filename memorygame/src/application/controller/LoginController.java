package application.controller;

import javafx.animation.*; 
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.effect.DropShadow;
import javafx.concurrent.Task;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import application.database.DatabaseManager;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import javafx.stage.StageStyle;
import java.sql.SQLException;
import application.database.DatabaseHandler;

import javafx.scene.image.ImageView;
public class LoginController {

    @FXML private Label displayHeroName;           
    @FXML private TextField usernameField;         
    @FXML private PasswordField passwordField;      
    @FXML private TextField passwordTextField;     
    @FXML private Button loginButton;              
    @FXML private Button toggleBtn;
    @FXML private ComboBox<String> languageComboBox;
    @FXML private StackPane logoContainer;
    @FXML private Circle onlineDot;
    @FXML private Button easyBtn;
    @FXML private Button mediumBtn;
    @FXML private Button hardBtn;
    @FXML private Label usernameNoti; 
    @FXML private Label passwordNoti;
    @FXML private Label errorLabel;
    @FXML private Label topPlayerName;
    @FXML private Label topPlayerScore;
    @FXML private Label totalGamesLabel;
    @FXML private Circle pulseCircle;
    @FXML private TextArea reviewArea;
    @FXML private VBox settingsRoot; 
    @FXML private Slider difficultySlider;
    @FXML private Label difficultyValue;
    @FXML private Label notiLabel;
    @FXML private Button star1, star2, star3, star4, star5;
    @FXML private TextField usernameInput; 
    @FXML private Label ratingDescription;
    @FXML private ImageView logoImage;
    @FXML private HBox starContainer;
    
    private int selectedRating = 0;
    private boolean isPasswordVisible = false;
    private int currentGlobalCount = 12450;
    private double xOffset = 0;
    private double yOffset = 0;

    @FXML
    public void initialize() {
    	resetStars();
    	startCommunityPulse();
    	
    	usernameField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateUsernameValidation(newValue);
        });
    	
    	updateStarVisuals(0);
        
    	if (settingsRoot != null) {
            setupDraggableWindow();
            setupSettingsLogic(); 
        }
    	
        if (UserSession.getInstance().isGuest()) {
            if (mediumBtn != null) {
                mediumBtn.setDisable(true);
                mediumBtn.setText("Locked 🔒");
                mediumBtn.setOpacity(0.5);
            }
            if (hardBtn != null) {
                hardBtn.setDisable(true);
                hardBtn.setText("Locked 🔒");
                hardBtn.setOpacity(0.5);
            }
        }

        // --- 2. PASSWORD TOGGLE SETUP ---
        if (passwordField != null && passwordTextField != null) {
            passwordField.textProperty().bindBidirectional(passwordTextField.textProperty());
            passwordTextField.setVisible(false);
            passwordTextField.setManaged(false);
        }

        // --- 3. ANIMATIONS ---
        if (logoContainer != null) startKidsLogoAnimation(); 
        if (onlineDot != null) startFooterAnimations();

        // --- 4. LANGUAGE SETUP ---
        if (languageComboBox != null) {
            languageComboBox.setItems(FXCollections.observableArrayList("English", "Español", "Français", "日本語"));
            languageComboBox.setValue("English");
        }
        
        usernameField.textProperty().addListener((obs, oldVal, newVal) -> {
            updateUsernameValidation(newVal);
        });

        passwordField.textProperty().addListener((obs, oldVal, newVal) -> {
            updatePasswordValidation(newVal);
        });
        
    }
    private void setupDraggableWindow() {
        settingsRoot.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        settingsRoot.setOnMouseDragged(event -> {
            Stage stage = (Stage) settingsRoot.getScene().getWindow();
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });
    }

    // 3. Define the missing method to fix the "setupSettingsLogic" error
    private void setupSettingsLogic() {
        if (difficultySlider != null && difficultyValue != null) {
            difficultySlider.valueProperty().addListener((obs, oldVal, newVal) -> {
                int val = newVal.intValue();
                if (val == 1) difficultyValue.setText("EASY");
                else if (val == 2) difficultyValue.setText("NORMAL");
                else if (val == 3) difficultyValue.setText("HARD");
            });
        }
    }
    
    private void startCommunityPulse() {
        ScaleTransition pulse = new ScaleTransition(Duration.seconds(1.2), pulseCircle);
        pulse.setFromX(1.0); pulse.setFromY(1.0);
        pulse.setToX(2.2);   pulse.setToY(2.2);
        pulse.setCycleCount(Animation.INDEFINITE);
        pulse.setAutoReverse(true);
        pulse.play();

        // 2. Random Number "Live Update" Effect
        Timeline numberUpdater = new Timeline(new KeyFrame(Duration.seconds(3), e -> {
            // Randomly add 1 to 5 new games to simulate live activity
            int addedGames = (int) (Math.random() * 5) + 1;
            currentGlobalCount += addedGames;
            
            updateLabelWithFade(currentGlobalCount);
        }));
        numberUpdater.setCycleCount(Animation.INDEFINITE);
        numberUpdater.play();
    }

    private void updateLabelWithFade(int newCount) {
        // Create a quick "flash" effect when the number changes
        FadeTransition fade = new FadeTransition(Duration.millis(300), totalGamesLabel);
        fade.setFromValue(1.0);
        fade.setToValue(0.5);
        fade.setCycleCount(2);
        fade.setAutoReverse(true);
        
        fade.setOnFinished(event -> {
            totalGamesLabel.setText(String.format("%,d", newCount) + " GAMES PLAYED");
        });
        
        fade.play();
    }
    
    private void updateUsernameValidation(String text) {
        if (text.isEmpty()) {
            usernameNoti.setText("REQUIRED FIELD");
            usernameNoti.setStyle("-fx-background-color: #bdc3c7; -fx-text-fill: black; -fx-padding: 2 8; -fx-background-radius: 5; -fx-font-weight: bold;");
        } else if (text.length() < 3) {
            usernameNoti.setText("TOO SHORT (MIN 3)");
            usernameNoti.setStyle("-fx-background-color: #f1c40f; -fx-text-fill: black; -fx-padding: 2 8; -fx-background-radius: 5; -fx-font-weight: bold;");
        } else {
            usernameNoti.setText("VERIFIED: " + text.toUpperCase() + " ✅");
            usernameNoti.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: black; -fx-padding: 2 8; -fx-background-radius: 5; -fx-font-weight: bold;");
        }
    }

    private void updatePasswordValidation(String text) {
        if (text.length() < 6) {
            passwordNoti.setText("STRENGTH: WEAK (MIN 6)");
            passwordNoti.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: black; -fx-padding: 2 8; -fx-background-radius: 5; -fx-font-weight: bold;");
        } else {
            passwordNoti.setText("STRENGTH: SECURE 💪");
            passwordNoti.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: black; -fx-padding: 2 8; -fx-background-radius: 5; -fx-font-weight: bold;");
        }
    }
    // --- AUTHENTICATION LOGIC ---

    @FXML
    private void handleLogin(ActionEvent event) {
        resetStyles(usernameField, passwordField);

        String username = usernameField.getText().trim();
        String password = isPasswordVisible ? passwordTextField.getText() : passwordField.getText();

        if (username.length() < 3) {
            int rem = 3 - username.length();
            showInputError(usernameField, "Hero name too short! Need " + rem + " more.");
            return;
        }
        if (!username.matches("^[a-zA-Z0-9_]*$")) {
            showInputError(usernameField, "Format Error: Use only letters and numbers!");
            return;
        }

        if (password.length() < 6) {
            int rem = 6 - password.length();
            showInputError(passwordField, "Secret code too short! Need " + rem + " more.");
            return;
        }

        // 3. Database Task (Success/Fail)
        loginButton.setDisable(true);
        loginButton.setText("Accessing Galaxy...");

        Task<Boolean> loginTask = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                if (DatabaseManager.userExists(username)) {
                    return DatabaseManager.validateLogin(username, password);
                } else {
                    return DatabaseManager.registerUser(username, password);
                }
            }
        };

        loginTask.setOnSucceeded(e -> {
            if (loginTask.getValue()) {
                UserSession.getInstance().setUsername(username);
                UserSession.getInstance().setGuest(false);
                executeSceneTransition(event, "/application/view/GameSelection.fxml");
            } else {
                loginButton.setDisable(false);
                loginButton.setText("L E T ' S  P L A Y !");
                showInputError(passwordField, "Wrong secret password!");
            }
        });
        new Thread(loginTask).start();
    }
    private void resetStyles(Node... nodes) {
        for (Node n : nodes) {
            // Reset to original transparent blue border from FXML
            n.setStyle("-fx-background-color: rgba(255,255,255,0.08); " +
                       "-fx-text-fill: white; " +
                       "-fx-background-radius: 20; " +
                       "-fx-border-color: rgba(0,210,255,0.5); " +
                       "-fx-border-radius: 20; " +
                       "-fx-padding: 12;");
        }
        if (errorLabel != null) errorLabel.setText("");
    }
    private void showInputError(Node node, String message) {
        playShakeAnimation(node);
        
        // Apply a bright red border and a slight glow
        node.setStyle("-fx-border-color: #ff4757; -fx-border-width: 2.5px; " +
                      "-fx-border-radius: 20; -fx-background-radius: 20; " +
                      "-fx-effect: dropshadow(three-pass-box, rgba(255,71,87,0.4), 10, 0, 0, 0);");
        
        if (errorLabel != null) {
            errorLabel.setText("⚠️ " + message);
            errorLabel.setTextFill(Color.web("#ff4757"));
            
            // Quick pulse animation for the error message
            ScaleTransition pulse = new ScaleTransition(Duration.millis(200), errorLabel);
            pulse.setFromX(1.0); pulse.setFromY(1.0);
            pulse.setToX(1.05);  pulse.setToY(1.05);
            pulse.setCycleCount(2);
            pulse.setAutoReverse(true);
            pulse.play();
        }
    }
    
    private void setupGuestSession() {
        UserSession.getInstance().setUsername("Guest");
        UserSession.getInstance().setGuest(true);
    }
    @FXML
    private void handleGuestLogin(ActionEvent event) {

        setupGuestSession();

        try {
            Parent root = FXMLLoader.load(getClass().getResource("/application/view/GameSelection.fxml"));

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handleGuestPlay(ActionEvent event) {
        setupGuestSession();
        executeSceneTransition(event, "/application/view/GameSelection.fxml");
    }
    
    private void executeSceneTransition(ActionEvent event, String fxmlPath) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Parent currentRoot = stage.getScene().getRoot();

            FadeTransition fadeOut = new FadeTransition(Duration.millis(300), currentRoot);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);

            fadeOut.setOnFinished(e -> {
                try {
                    Parent nextRoot = FXMLLoader.load(getClass().getResource(fxmlPath));
                    nextRoot.setOpacity(0);
                    Scene scene = new Scene(nextRoot);
                    stage.setScene(scene);
                    stage.centerOnScreen();

                    FadeTransition fadeIn = new FadeTransition(Duration.millis(300), nextRoot);
                    fadeIn.setToValue(1.0);
                    fadeIn.play();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
            fadeOut.play();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void styleDialog(DialogPane pane) {
        // 1. Base Container Styling (Deep Space Blue with Neon Mint Border)
        pane.setStyle(
            "-xml-background-color: #0f0c29; " +
            "-fx-background-color: #0f0c29; " +
            "-fx-border-color: #3aedb9; " +
            "-fx-border-width: 2px; " +
            "-fx-border-radius: 10px; " +
            "-fx-background-radius: 10px;"
        );

        // 2. Style the Header/Title specifically
        pane.lookupAll(".header-panel").forEach(node -> node.setStyle("-fx-background-color: #1a1a4d; -fx-background-radius: 10 10 0 0;"));
        
        Node headerLabel = pane.lookup(".header-panel .label");
        if (headerLabel instanceof Label) {
            Label hl = (Label) headerLabel;
            hl.setStyle("-fx-text-fill: #3aedb9; -fx-font-weight: bold; -fx-font-size: 18px;");
            hl.setEffect(new DropShadow(10, Color.web("#3aedb9")));
        }

        // 3. Style all Labels (Content Text) to be white/readable
        pane.lookupAll(".label").forEach(node -> {
            if (node instanceof Label) {
                ((Label) node).setTextFill(Color.WHITE);
            }
        });

        // 4. Style the "OK" / "UNDERSTOOD" Button
        Button okButton = (Button) pane.lookupButton(ButtonType.OK);
        if (okButton != null) {
            okButton.setText("UNDERSTOOD");
            okButton.setStyle(
                "-fx-background-color: linear-gradient(to right, #00d2ff, #3aedb9); " +
                "-fx-text-fill: #0f0c29; " + // Dark text on bright button looks pro
                "-fx-font-weight: 900; " +
                "-fx-background-radius: 20px; " +
                "-fx-cursor: hand; " +
                "-fx-padding: 8 20;"
            );
            
            // Add a hover effect for extra smoothness
            okButton.setOnMouseEntered(e -> okButton.setOpacity(0.8));
            okButton.setOnMouseExited(e -> okButton.setOpacity(1.0));
        }
        
        // 5. Hide the standard white background of the button bar
        Node buttonBar = pane.lookup(".button-bar");
        if (buttonBar != null) {
            buttonBar.setStyle("-fx-background-color: transparent;");
        }
    }
    
    @FXML
    private void togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible;

        TextInputControl showField = isPasswordVisible ? passwordTextField : passwordField;
        TextInputControl hideField = isPasswordVisible ? passwordField : passwordTextField;
        
        showField.setText(hideField.getText());
        
        // 1. Swap the fields with the fade we built
        animateSwap(hideField, showField);

        toggleBtn.setText(isPasswordVisible ? "🔓" : "🔒");
        applyAdvancedToggleAnimation(toggleBtn);
    }
    private void animateSwap(TextInputControl out, TextInputControl in) {
        // 1. Prepare the 'Incoming' field
        in.setVisible(true);
        in.setManaged(true);
        in.setOpacity(0.0); // Start invisible for the fade-in

        // 2. Create the Fade-In (New Field)
        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), in);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        // 3. Create the Fade-Out (Old Field)
        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), out);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        // 4. Cleanup when the fade-out finishes
        fadeOut.setOnFinished(e -> {
            out.setVisible(false);
            out.setManaged(false);
            
            // Return focus to the new field and move cursor to the end
            in.requestFocus();
            if (in.getText() != null) {
                in.positionCaret(in.getText().length());
            }
        });

        // 5. Play both simultaneously for a smooth "dissolve" effect
        fadeIn.play();
        fadeOut.play();
    }
    private void applyAdvancedToggleAnimation(Button btn) {
        // Pop Scale
        ScaleTransition st = new ScaleTransition(Duration.millis(150), btn);
        st.setFromX(1.0); st.setFromY(1.0);
        st.setToX(1.2);   st.setToY(1.2);
        st.setAutoReverse(true);
        st.setCycleCount(2);

        // Smooth 360-degree flip
        RotateTransition rt = new RotateTransition(Duration.millis(250), btn);
        rt.setByAngle(360);
        
        st.play();
        rt.play();
    }

    private void playShakeAnimation(Node node) {
        if (node == null) return;
        TranslateTransition shake = new TranslateTransition(Duration.millis(50), node);
        shake.setFromX(0); shake.setByX(8);
        shake.setCycleCount(6); shake.setAutoReverse(true);
        shake.play();
        node.setStyle("-fx-border-color: #ff4757; -fx-border-radius: 20; -fx-background-radius: 20;");
    }

    private void startKidsLogoAnimation() {
        TranslateTransition floating = new TranslateTransition(Duration.seconds(2), logoContainer);
        floating.setByY(-10);
        floating.setAutoReverse(true);
        floating.setCycleCount(Animation.INDEFINITE);
        floating.setInterpolator(Interpolator.EASE_BOTH);

        ScaleTransition pulse = new ScaleTransition(Duration.seconds(1.5), logoContainer);
        pulse.setToX(1.08); pulse.setToY(1.08);
        pulse.setAutoReverse(true);
        pulse.setCycleCount(Animation.INDEFINITE);

        new ParallelTransition(floating, pulse).play();
    }

    private void startFooterAnimations() {
        ScaleTransition pulse = new ScaleTransition(Duration.seconds(1.5), onlineDot);
        pulse.setFromX(1.0); pulse.setFromY(1.0);
        pulse.setToX(2.0);   pulse.setToY(2.0);
        
        FadeTransition fade = new FadeTransition(Duration.seconds(1.5), onlineDot);
        fade.setFromValue(0.6);
        fade.setToValue(0.0);
        
        ParallelTransition pt = new ParallelTransition(pulse, fade);
        pt.setCycleCount(Animation.INDEFINITE);
        pt.play();
    }   
    @FXML
    private void handleSettings(ActionEvent event) {
        try {
            // 1. Correct Path (Removed extra backslash and quotes)
            var resource = getClass().getResource("/application/view/Settings.fxml");
            
            if (resource == null) {
                System.err.println("❌ Settings.fxml not found in /application/view/");
                return;
            }

            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();
            
            Stage settingsStage = new Stage();
            
            // 2. Window Appearance & Modality
            settingsStage.initStyle(StageStyle.TRANSPARENT); 
            settingsStage.initModality(Modality.APPLICATION_MODAL); // Blocks main window until closed

            // 3. Attach to main window (Owner)
            Stage owner = (Stage) ((Node) event.getSource()).getScene().getWindow();
            settingsStage.initOwner(owner);

            // 4. Scene Setup (Must be transparent for rounded FXML corners)
            Scene scene = new Scene(root);
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT); 
            settingsStage.setScene(scene);

            settingsStage.show();

        } catch (IOException e) {
            System.err.println("❌ Error loading Settings window");
            e.printStackTrace();
        }
    }
    
    
    @FXML
    private void handleLeaderboard(ActionEvent event) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/view/Leaderboard.fxml"));
            Parent root = loader.load();
            
            // --- ADD THESE TWO LINES TO REMOVE THE LOGIN PAGE ---
            Stage currentStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            currentStage.close(); 

            Stage stage = new Stage();
            stage.setTitle("Hall of Fame");
            stage.setScene(new Scene(root));
            stage.show();
            
        } catch (IOException e) {
            System.err.println("❌ Could not load Leaderboard.fxml");
            e.printStackTrace();
        }
    }
    @FXML 
    private void handleHelp(ActionEvent event) {
        Alert helpAlert = new Alert(Alert.AlertType.INFORMATION);
        helpAlert.setTitle("MISSION BRIEFING");
        helpAlert.setHeaderText(null);
        helpAlert.setGraphic(null); 

        DialogPane dialogPane = helpAlert.getDialogPane();
        
        // Create the container for the rows
        VBox helpContent = new VBox(15); // 15px spacing between rows
        helpContent.setAlignment(Pos.CENTER_LEFT);
        helpContent.setPadding(new Insets(20));

        // Add the styled rows
        helpContent.getChildren().addAll(
            createHelpRow("👤", "IDENTITY", "Enter your Hero Name to track progress."),
            createHelpRow("🎯", "OBJECTIVE", "Flip cards and match pairs to clear the board."),
            createHelpRow("⚡", "SCORING", "Faster matches earn higher Brain Power (BP).")
        );

        dialogPane.setContent(helpContent);
        styleDialog(dialogPane); 
        helpAlert.showAndWait();
    }
    private HBox createHelpRow(String icon, String title, String desc) {
        // Icon Label
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 24px; -fx-min-width: 40px;");
        
        // Text container
        VBox textContainer = new VBox(2);
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-text-fill: #3aedb9; -fx-font-weight: bold; -fx-font-size: 13px;");
        
        Label descLabel = new Label(desc);
        descLabel.setStyle("-fx-text-fill: white; -fx-font-size: 11px;");
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(300);

        textContainer.getChildren().addAll(titleLabel, descLabel);

        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.getChildren().addAll(iconLabel, textContainer);
        
        return row;
    }
    
    @FXML private void handleExit(ActionEvent event) { System.exit(0); }
    
    @FXML private void handleClose(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleLanguageChange() {
        String lang = languageComboBox.getValue();
        languageComboBox.setDisable(true);
        PauseTransition pause = new PauseTransition(Duration.seconds(0.8));
        pause.setOnFinished(e -> {
            updateFocusElements(lang);
            languageComboBox.setDisable(false);
        });
        pause.play();
    }

    private void updateFocusElements(String lang) {
        // 1. Define Translation Data
        Map<String, String[]> translations = new HashMap<>();
        
        translations.put("Español", new String[]{
            "Héroe Novato", "Rango: Cadete", "¡HERO 'MAX' ALCANZÓ EL RANGO MAESTRO!", 
            "Nombre de Héroe...", "Contraseña Secreta...", "¡A JUGAR!", "Jugar como Invitado 👤"
        });
        translations.put("Français", new String[]{
            "Héros Recrue", "Rang: Cadet", "HERO 'MAX' A ATTEINT EL RANG MAÎTRE !", 
            "Nom de Héros...", "Mot de Passe...", "JOUONS !", "Jouer en tant qu'invité 👤"
        });
        translations.put("日本語", new String[]{
            "ルーキーヒーロー", "ランク：見習い", "英雄「MAX」がマスターランクに到達！", 
            "ユーザー名...", "パスワード...", "遊びましょう！", "ゲストとして遊ぶ 👤"
        });
        translations.put("English", new String[]{
            "Rookie Hero", "Rank: Brain Cadet", "HERO 'MAX' REACHED MASTER RANK!", 
            "Hero Name...", "Secret Password...", "L E T ' S  P L A Y !", "Play as Guest 👤"
        });

        // 2. Apply Translations safely
        String[] d = translations.getOrDefault(lang, translations.get("English"));

        if (displayHeroName != null) displayHeroName.setText(d[0]);
 
        // Update Input Prompts
        if (usernameField != null) usernameField.setPromptText(d[3]);
        if (passwordField != null) passwordField.setPromptText(d[4]);
        if (passwordTextField != null) passwordTextField.setPromptText(d[4]);

        // Update Buttons & Links
        if (loginButton != null) loginButton.setText(d[5]);
        
        playLanguageSwitchAnimation();
    }
    private void playLanguageSwitchAnimation() {
        // Assuming your central VBox has an fx:id="mainForm"
        if (logoContainer != null && logoContainer.getScene() != null) {
            FadeTransition ft = new FadeTransition(Duration.millis(400), logoContainer.getParent());
            ft.setFromValue(0.5);
            ft.setToValue(1.0);
            ft.play();
        }
    }
    
    @FXML
    private void handleStarClick(javafx.event.ActionEvent event) {
        Button clickedStar = (Button) event.getSource();

        String id = clickedStar.getId();
        selectedRating = Character.getNumericValue(id.charAt(id.length() - 1));

        updateStarSelection(selectedRating);
        notiLabel.setOpacity(0.0);
    }

    private void updateStarSelection(int rating) {
        Button[] stars = {star1, star2, star3, star4, star5};
        String[] descriptions = {"CRITICAL", "LOW", "STABLE", "EXCELLENT", "OPTIMAL"};
        
        for (int i = 0; i < stars.length; i++) {
            if (i < rating) {

                stars[i].setStyle("-fx-background-color: linear-gradient(to right, #00d2ff, #3aedb9); " +
                                  "-fx-border-color: #3aedb9; " +
                                  "-fx-background-radius: 2;");
                
                // Add a heavy glow to make it look like a light
                javafx.scene.effect.Glow glow = new javafx.scene.effect.Glow(0.8);
                stars[i].setEffect(glow);
            } else {
                // Empty State
                stars[i].setStyle("-fx-background-color: #1a1d29; -fx-border-color: #333; -fx-background-radius: 2;");
                stars[i].setEffect(null);
            }
        }
        
        // Update description text based on rating
        if (rating > 0) {
            ratingDescription.setText(descriptions[rating - 1]);
            ratingDescription.setStyle("-fx-text-fill: #3aedb9; -fx-font-weight: bold;");
        }
    }
    
    private void clearForm() {
        reviewArea.clear();
        usernameInput.clear();
        selectedRating = 0;
        updateStarSelection(0); 
    }
    
    private void updateStarVisuals(int rating) {
        Button[] stars = {star1, star2, star3, star4, star5};
        String starPath = "M 10 0 L 13 7 L 20 7 L 15 12 L 17 20 L 10 15 L 3 20 L 5 12 L 0 7 L 7 7 Z";

        for (int i = 0; i < stars.length; i++) {
            // 1. Get the existing graphic
            javafx.scene.shape.SVGPath shape = (javafx.scene.shape.SVGPath) stars[i].getGraphic();
            
            if (shape == null) {
                shape = new javafx.scene.shape.SVGPath();
                shape.setContent(starPath);
                stars[i].setGraphic(shape);
            }
            
            // 3. Now it is safe to set the color
            if (i < rating) {
                shape.setFill(javafx.scene.paint.Color.web("#f1c40f")); // Yellow
                shape.setEffect(new javafx.scene.effect.Glow(0.8));
            } else {
                shape.setFill(javafx.scene.paint.Color.web("#555555")); // Grey
                shape.setEffect(null);
            }
        }
    }
    private void resetStars() {
        updateStarVisuals(0);
    }
    
    @FXML
    private void handleReviewSubmit() {
        String inputUser = usernameInput.getText().trim();
        
        if (inputUser.isEmpty()) {
            showNotification("ERROR: PLAYER ID REQUIRED", "#e74c3c");
            return;
        }

        try {
            DatabaseHandler.insertReview(inputUser, selectedRating, reviewArea.getText());
            showStatus("YOUR REVIEWS AND RATING ARE SAVED.", "#3aedb9");
            clearForm();
            
        } catch (SQLException e) {
            showWarning("YOU CANNOT RATE THIS GAME.", "Unable to review. You need to login first.");
        }
    }

    private void showWarning(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void showStatus(String message, String color) {
        notiLabel.setText(message);
        notiLabel.setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold; -fx-font-family: 'Monospaced';");
        
        // Smooth Animation: Fade In and then Fade Out
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), notiLabel);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        
        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), notiLabel);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setDelay(Duration.seconds(3)); 
        
        fadeIn.setOnFinished(e -> fadeOut.play());
        fadeIn.play();
    }


    private void showNotification(String message, String color) {
        notiLabel.setText(message);
        notiLabel.setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold;");
        notiLabel.setOpacity(1.0);

        FadeTransition fade = new FadeTransition(Duration.seconds(3), notiLabel);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        fade.play();
    }
}