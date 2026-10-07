package application.controller;

import javafx.animation.*; 
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.input.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import application.model.Session;
import application.database.DatabaseManager;
import javafx.application.Platform;
import javafx.scene.effect.DropShadow;
public class MemoryGameController {

    @FXML private GridPane gridPane;
    @FXML private Button hintButton;
    @FXML private Label matchesLabel, modeLabel, scoreLabel, timerLabel;
    @FXML private ProgressBar progressBar;
    @FXML private ImageView userImageView;
    @FXML private HBox profileBox;
    @FXML private StackPane rootPane;

    private int rows, cols, totalPairs;
    private int matchesFound = 0;
    private int score = 0;
    private int hintsRemaining = 3;
    private int secondsElapsed = 0;
    private boolean isHintActive = false;
    private boolean busy = false;
    private StackPane firstSelected = null;
    private StackPane secondSelected = null;
    private Timeline gameTimer;

    private final String[] emojiPool = { "🍎", "🚀", "💎", "👻", "⚽", "🎸", "🌈", "🍄", "🔥", "⚡", "🍀", "🎮", "🦖", "🍦", "🛸", "🎨", "🧩", "🎬" };
    private final String[] animalPool = { "🐶", "🐱", "🐭", "🐹", "🐰", "🦊", "🐻", "🐼", "🐨", "🐯", "🦁", "🐮", "🐷", "🐸", "🐵", "🐧", "🐦", "🐥" };
    private final String[] foodPool = { "🍕", "🍔", "🍟", "🍣", "🌮", "🍩", "🧁", "🍦", "🍭", "🍉", "🍓", "🍍", "🥨", "🍳", "🍿", "🍫", "🥐", "🥞" };
    private final String[] weatherPool = { 
    	    "☀️", "☁️", "⛈️", "❄️", "🌪️", "🌊", "🌋", "🌙", 
    	    "⭐", "🌵", "🌻", "🌲", "🍂", "🐚", "🌈", "🌦️", "🌑", "💨" 
    	};

    	private final String[] spacePool = { 
    	    "🪐", "🌟", "☄️", "🛰️", "🚀", "🛸", "👩‍🚀", "👽", 
    	    "🔭", "🌌", "🛰️", "🌠", "☀️", "🌜", "🌑", "🌏", "📡", "👾" 
    	};

    @FXML
    public void initialize() {
        gridPane.setAlignment(Pos.CENTER);

        String difficulty = (Session.currentDifficulty != null) ? Session.currentDifficulty : "Super Easy (3x4)";
        String mode = (Session.selectedMode != null) ? Session.selectedMode : "Emoji";
        
        // 2. Clear old grid to prevent ghost cards
        gridPane.getChildren().clear();
        
        // 3. Apply the system's choice
        setupDifficulty(difficulty); 
        modeLabel.setText("MISSION: " + mode.toUpperCase());

        matchesFound = 0;
        score = 0;
        hintsRemaining = 3;
        updateProgress();
        
        // 5. Start the engine
        startTimer();
        generateGrid(); 
        
        // 6. UI Polish
        if(userImageView != null) {
            Circle clip = new Circle(21, 21, 21);
            userImageView.setClip(clip);
        }
    }

    // --- GAME LOGIC ---

    private void handleCardClick(StackPane pane) {
        // Multi-click protection
        if (busy || isHintActive || pane == firstSelected || pane.getOpacity() < 1.0) return;

        flipCard(pane, true);

        if (firstSelected == null) {
            firstSelected = pane;
        } else {
            secondSelected = pane;
            checkMatch();
        }
    }

    private void checkMatch() {
        busy = true; 
        String s1 = ((Label) firstSelected.getChildren().get(0)).getText();
        String s2 = ((Label) secondSelected.getChildren().get(0)).getText();

        if (s1.equals(s2)) {
            matchesFound++;
            score += 150;
            animateMatchSuccess(firstSelected, secondSelected);
            
            updateProgress();

            // Check for Win Condition
            if (matchesFound == totalPairs) {
                System.out.println("Win detected! Matches: " + matchesFound + "/" + totalPairs);
                handleWin();
            }

            firstSelected = null;
            secondSelected = null;
            busy = false;
        } else {
            score = Math.max(0, score - 40);
            updateProgress();
            animateMatchFailure(firstSelected, secondSelected);

            PauseTransition pause = new PauseTransition(Duration.seconds(1.2));
            pause.setOnFinished(e -> {
                flipCard(firstSelected, false);
                flipCard(secondSelected, false);
                firstSelected.setEffect(null);
                secondSelected.setEffect(null);
                firstSelected = null;
                secondSelected = null;
                busy = false;
            });
            pause.play();
        }
    }

    // --- ANIMATIONS ---

    private void flipCard(StackPane card, boolean showSecret) {
        RotateTransition rot1 = new RotateTransition(Duration.millis(150), card);
        rot1.setAxis(Rotate.Y_AXIS);
        rot1.setFromAngle(0);
        rot1.setToAngle(90);
        rot1.setInterpolator(Interpolator.EASE_BOTH); 

        rot1.setOnFinished(e -> {
            card.getChildren().get(0).setVisible(showSecret); 
            card.getChildren().get(1).setVisible(!showSecret); 
            
            // Dynamic styling for the "Secret" side
            if (showSecret) {
                card.setStyle("-fx-background-color: #2c3e50; -fx-background-radius: 12; -fx-border-color: #3aedb9; -fx-border-width: 2;");
            } else {
                card.setStyle("-fx-background-color: #34495e; -fx-background-radius: 12;");
            }

            RotateTransition rot2 = new RotateTransition(Duration.millis(150), card);
            rot2.setAxis(Rotate.Y_AXIS);
            rot2.setFromAngle(90);
            rot2.setToAngle(0);
            rot2.setInterpolator(Interpolator.EASE_BOTH);
            rot2.play();
        });
        rot1.play();
    }

    private void animateMatchSuccess(StackPane p1, StackPane p2) {
        for (StackPane p : new StackPane[]{p1, p2}) {
            ScaleTransition pop = new ScaleTransition(Duration.millis(250), p);
            pop.setToX(1.2); pop.setToY(1.2);
            pop.setCycleCount(2); pop.setAutoReverse(true);

            FadeTransition fade = new FadeTransition(Duration.millis(500), p);
            fade.setToValue(0.7);

            Glow glow = new Glow(0.8);
            p.setEffect(glow);

            ParallelTransition success = new ParallelTransition(pop, fade);
            success.play();
            p.setDisable(true);
        }
    }

    private void animateMatchFailure(StackPane p1, StackPane p2) {
        for (StackPane p : new StackPane[]{p1, p2}) {
            // 1. The No-No Shake
            RotateTransition shake = new RotateTransition(Duration.millis(80), p);
            shake.setFromAngle(-8); shake.setToAngle(8);
            shake.setCycleCount(4); shake.setAutoReverse(true);

            // 2. Shrink and Blur (The "Oops" look)
            ScaleTransition shrink = new ScaleTransition(Duration.millis(200), p);
            shrink.setToX(0.85);
            shrink.setToY(0.85);
            
            // 3. Red Glow + Blur
            DropShadow errorGlow = new DropShadow(20, Color.RED);
            p.setEffect(errorGlow);

            // Play together
            ParallelTransition errorAnim = new ParallelTransition(shake, shrink);
            errorAnim.setOnFinished(e -> {
                // Return to normal size after flip
                p.setScaleX(1.0);
                p.setScaleY(1.0);
            });
            errorAnim.play();
        }
    }

    @FXML
    private void handleHint(ActionEvent event) {
        if (hintsRemaining <= 0 || busy || isHintActive) {
            showWarningEffect(hintButton);
            return; 
        }

        hintsRemaining--;
        isHintActive = true;
        hintButton.setText("💡 HINTS: " + hintsRemaining);

        for (Node node : gridPane.getChildren()) {
            if (node instanceof StackPane card && card.getOpacity() == 1.0) {
                if (card != firstSelected && card != secondSelected) {
                    flipCard(card, true);
                    DropShadow hintGlow = new DropShadow(20, Color.GOLD);
                    card.setEffect(hintGlow);
                }
            }
        }

        PauseTransition pause = new PauseTransition(Duration.seconds(2.5));
        pause.setOnFinished(e -> {
            for (Node node : gridPane.getChildren()) {
                if (node instanceof StackPane card && card.getOpacity() == 1.0) {
                    if (card != firstSelected && card != secondSelected) {
                        flipCard(card, false);
                        card.setEffect(null);
                    }
                }
            }
            isHintActive = false;
        });
        pause.play();
    }

    // --- UTILITY ---

    private void generateGrid() {
        // 1. CLEAR the old game board (Crucial for Level 2 onwards)
        gridPane.getChildren().clear();

        // 2. GET the symbols based on the System Choice (Mode + Difficulty)
        // This ensures 1st and 2nd games have different icons
        List<String> symbols = getShuffledSymbols();

        // 3. SAFETY CHECK: Ensure we have enough symbols for the grid
        if (symbols.size() < (rows * cols)) {
            System.err.println("❌ Error: Not enough symbols for a " + rows + "x" + cols + " grid!");
            return;
        }

        // 4. DRAW the grid with animations
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                // Remove the first symbol and create the card
                StackPane card = createCard(symbols.remove(0));
                gridPane.add(card, c, r);
                
                // INITIAL STATE for animation (Invisible and Small)
                card.setOpacity(0);
                card.setScaleX(0.5); 
                card.setScaleY(0.5);
                
                // FADE AND SCALE ANIMATIONS
                FadeTransition ft = new FadeTransition(Duration.millis(300), card);
                ft.setToValue(1.0);
                
                ScaleTransition st = new ScaleTransition(Duration.millis(300), card);
                st.setToX(1.0); 
                st.setToY(1.0);
                
                // PARALLEL EXECUTION with a "Wave" delay
                ParallelTransition pt = new ParallelTransition(ft, st);
                pt.setDelay(Duration.millis((r + c) * 50)); // Creates a cool diagonal pop-in effect
                pt.play();
            }
        }
    }
    private List<String> getShuffledSymbols() {
        List<String> list = new ArrayList<>();
        
        // Pick the pool based on the System's Session choice
        String mode = Session.selectedMode; 
        String[] pool = switch (mode) {
            case "Animals" -> animalPool;
            case "Space"   -> spacePool;
            case "Food"    -> foodPool;
            case "Weather" -> weatherPool;
            default        -> emojiPool; 
        };

        // totalPairs was calculated in your setupDifficulty() method
        for (int i = 0; i < totalPairs; i++) {
            String symbol = pool[i % pool.length];
            list.add(symbol);
            list.add(symbol); 
        }
        
        Collections.shuffle(list);
        return list;
    }

    private StackPane createCard(String symbol) {
        StackPane pane = new StackPane();
        pane.setPrefSize(80, 80);
        pane.setStyle("-fx-background-color: #34495e; -fx-background-radius: 12; -fx-cursor: hand;");

        Label secret = new Label(symbol);
        
        secret.setStyle("-fx-font-family: 'Segoe UI Emoji'; -fx-font-size: 36; -fx-text-fill: white;");

        secret.setEffect(new DropShadow(5, Color.BLACK));
        secret.setVisible(false);

        Label cover = new Label("?");
        cover.setStyle("-fx-font-size: 24; -fx-text-fill: #3aedb9; -fx-font-weight: bold;");

        pane.getChildren().addAll(secret, cover);
        pane.setOnMouseClicked(e -> handleCardClick(pane));
        
        // Hover effect for "Smooth" feel
        pane.setOnMouseEntered(e -> pane.setStyle("-fx-background-color: #3d5a7a; -fx-background-radius: 12; -fx-scale-x: 1.05; -fx-scale-y: 1.05;"));
        pane.setOnMouseExited(e -> pane.setStyle("-fx-background-color: #34495e; -fx-background-radius: 12; -fx-scale-x: 1.0; -fx-scale-y: 1.0;"));

        return pane;
    }

    private void updateProgress() {
        if (progressBar == null) {
            System.out.println("Warning: progressBar is null. Check fx:id in FXML!");
            return; 
        }
        
        if (totalPairs > 0) {
            double progress = (double) matchesFound / totalPairs;
            progressBar.setProgress(progress);
        }
        matchesLabel.setText("MATCHES: " + matchesFound + " / " + totalPairs);
        scoreLabel.setText("SCORE: " + score);
    }

    private void startTimer() {
        if (gameTimer != null) gameTimer.stop();
        secondsElapsed = 0;
        gameTimer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            secondsElapsed++;
            timerLabel.setText(String.format("TIME: %02d:%02d", secondsElapsed / 60, secondsElapsed % 60));
        }));
        gameTimer.setCycleCount(Animation.INDEFINITE);
        gameTimer.play();
    }

    private void setupDifficulty(String diff) {
        if (diff.contains("Super Easy")) { rows = 3; cols = 4; }
        else if (diff.contains("Easy"))   { rows = 4; cols = 4; }
        else if (diff.contains("Medium")) { rows = 4; cols = 5; }
        else if (diff.contains("Hard"))   { rows = 5; cols = 6; }
        else if (diff.contains("Nightmare")) { rows = 6; cols = 6; }
        else { rows = 4; cols = 4; }
        
        totalPairs = (rows * cols) / 2;
        
        double gap = (rows > 4) ? 5.0 : 10.0;
        gridPane.setHgap(gap);
        gridPane.setVgap(gap);

        if (modeLabel != null) {
            modeLabel.setText("SECTOR: " + Session.selectedMode.toUpperCase() + " [" + rows + "x" + cols + "]");
        }
    }   

    private void handleWin() {
        if (gameTimer != null) gameTimer.stop();
        
        // 1. Calculate raw seconds from timerLabel (e.g., "00:27" -> 27)
        int rawSeconds = 0;
        try {
            String timerText = timerLabel.getText().replace("TIME: ", "").trim();
            String[] parts = timerText.split(":");
            int mins = Integer.parseInt(parts[0]);
            int secs = Integer.parseInt(parts[1]);
            rawSeconds = (mins * 60) + secs;
        } catch (Exception e) {
            System.err.println("Timer parsing failed: " + e.getMessage());
        }

        final int finalTime = rawSeconds; // Make it final for use in the lambda

        Platform.runLater(() -> {
            // --- Visual Victory Flash ---
            if (rootPane != null && rootPane.getScene() != null) {
                double w = rootPane.getScene().getWidth();
                double h = rootPane.getScene().getHeight();
                
                javafx.scene.shape.Rectangle flash = new javafx.scene.shape.Rectangle(w, h, Color.WHITE);
                flash.setOpacity(0);
                flash.setMouseTransparent(true);
                
                if (rootPane instanceof Pane) {
                    ((Pane) rootPane).getChildren().add(flash);
                }

                FadeTransition ft = new FadeTransition(Duration.millis(300), flash);
                ft.setFromValue(0); ft.setToValue(0.6); ft.setCycleCount(2); ft.setAutoReverse(true);
                ft.setOnFinished(ev -> {
                     if (rootPane instanceof Pane) ((Pane) rootPane).getChildren().remove(flash);
                });
                ft.play();
            }

            // --- Route to Dialog (Fixed Parameters) ---
            String username = UserSession.getInstance().getUsername();
            
            if (UserSession.getInstance().isGuest()) {
                showGuestVictoryDialog(); 
            } else {
                // FIX: Now passes both the name AND the time
                showMemberVictoryDialog(username, finalTime);
            }
        });
    }
    
    private void showGuestVictoryDialog() {
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setTitle("Mission Debrief");
        
        // Updated Header with clearer status
        alert.setHeaderText("UNITS SECURED: RANK BRONZE");
        
        alert.setContentText(
            "--- MISSION SUMMARY ---\n" +
            "FINAL SCORE : " + score + "\n" +
            "ELAPSED TIME: " + timerLabel.getText().replace("TIME: ", "") + "\n\n" +
            "LOGS: Training Sector Cleared.\n" +
            "RESTRICTION: Level 2+ Data Encrypted.\n\n" +
            "Sign in to synchronize progress and unlock Elite Challenges."
        );

        ButtonType loginBtn = new ButtonType("UPGRADE TO ELITE");
        ButtonType menuBtn = new ButtonType("RETURN TO BASE");
        alert.getButtonTypes().setAll(loginBtn, menuBtn);

        DialogPane dp = alert.getDialogPane();
        
        dp.setStyle(
            "-fx-background-color: #0b0d17; " +
            "-fx-border-color: #d4af37; " +
            "-fx-border-width: 2px; " +
            "-fx-border-radius: 10px; " +
            "-fx-background-radius: 10px;"
        );

        Label headerLabel = (Label) dp.lookup(".header-panel .label");
        if (headerLabel != null) {
            headerLabel.setStyle(
                "-fx-text-fill: #d4af37; " +
                "-fx-font-size: 20px; " +
                "-fx-font-weight: bold; " +
                "-fx-font-family: 'Verdana';"
            );
        }

        Label contentLabel = (Label) dp.lookup(".content.label");
        if (contentLabel != null) {
            contentLabel.setStyle(
                "-fx-text-fill: #3aedb9; " + 
                "-fx-font-family: 'Monospaced'; " +
                "-fx-font-size: 13px;"
            );
        }

        // 4. Glow and Buttons
        dp.setEffect(new DropShadow(BlurType.THREE_PASS_BOX, Color.rgb(212, 175, 55, 0.4), 25, 0, 0, 0));

        Button b1 = (Button) dp.lookupButton(loginBtn);
        Button b2 = (Button) dp.lookupButton(menuBtn);

        // Gold Button for Upgrade
        b1.setStyle("-fx-background-color: #d4af37; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-cursor: hand;");
        
        // Ghost Button for Menu
        b2.setStyle("-fx-background-color: transparent; -fx-border-color: #7f8c8d; -fx-text-fill: #7f8c8d; -fx-cursor: hand;");

        // Pulse Animation for the main action
        ScaleTransition pulse = new ScaleTransition(Duration.seconds(0.8), b1);
        pulse.setToX(1.08); pulse.setToY(1.08); pulse.setCycleCount(Animation.INDEFINITE); pulse.setAutoReverse(true);
        pulse.play();

        alert.showAndWait().ifPresent(response -> {
            if (response == loginBtn) {
                System.out.println("User chose: Login");
                returnToLogin();
            } else {
                System.out.println("User chose: Selection");
                returnToSelection();
            }
        });
    }
    private void showMemberVictoryDialog(String pilotName, int finalTime) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Mission Debrief");
        
        // 1. Define these ONCE outside the lambda so they are "resolved"
        String currentTimeText = timerLabel.getText().replace("TIME: ", "");
        String selectedDiff = (SelectionController.selectedDifficulty != null) ? 
                              SelectionController.selectedDifficulty : "Normal";

        alert.setHeaderText("SIGNATURE DETECTED: " + pilotName.toUpperCase());
        alert.setContentText(
            "MISSION SUCCESS!\n\n" +
            "Final Score: " + score + "\n" +
            "Total Time : " + currentTimeText + "\n\n" +
            "Syncing data to Pilot Log..."
        );

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    DatabaseManager.syncPilotData(pilotName, this.score, selectedDiff, finalTime);

                    // UPDATED CALL WITH 6 ARGUMENTS
                    int xp = this.score / 10; 
                    int acc = 100; 

                    DatabaseManager.saveGameResult(
                        pilotName, 
                        this.score, 
                        currentTimeText, 
                        selectedDiff, 
                        xp, 
                        acc
                    );

                    System.out.println("✅ Data stored successfully.");
                    navigateToProfile(pilotName);
                } catch (Exception e) {
                    System.err.println("❌ Sync/Navigation Failed: " + e.getMessage());
                    returnToSelection();
                }
            }
        });
    }
    private void navigateToProfile(String pilotName) {
        try {
            // 1. Update Session
            UserSession.getInstance().setUsername(pilotName);

            // 2. LOAD FIX: Added "/view/" to the path
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/view/profile.fxml"));
            Parent root = loader.load();

            // 3. Get Controller
            ProfileController controller = loader.getController();
            if (controller != null) {
                controller.initialize(); 
            }

            // 4. Get Stage
            Stage stage = (Stage) timerLabel.getScene().getWindow();
            
            // 5. Apply Scene
            Scene scene = new Scene(root);
            stage.setScene(scene);
            
            // 6. Fade In
            root.setOpacity(0);
            FadeTransition ft = new FadeTransition(Duration.millis(800), root);
            ft.setFromValue(0);
            ft.setToValue(1);
            ft.play();
            
            System.out.println("🚀 Navigated to Profile for: " + pilotName);

        } catch (IOException e) {
            System.err.println("❌ Navigation Failed: Check if /application/view/profile.fxml exists.");
            e.printStackTrace();
        }
    }

    private void returnToLogin() {
        switchScene("/application/view/login.fxml"); 
    }

    private void returnToSelection() {
        switchScene("/application/view/GameSelection.fxml"); 
    }
        
    private void switchScene(String fxmlPath) {
        try {
            // 1. Load the new FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            
            // 2. Get the Stage
            // We use Platform.runLater to ensure the Alert has closed fully first
            Platform.runLater(() -> {
                Stage stage = (Stage) gridPane.getScene().getWindow();
                
                // 3. Optional: Add a "Hyper-space" Transition
                root.setOpacity(0);
                Scene scene = new Scene(root);
                stage.setScene(scene);
                
                FadeTransition fadeIn = new FadeTransition(Duration.millis(500), root);
                fadeIn.setToValue(1.0);
                fadeIn.play();
                
                System.out.println("Successfully switched to: " + fxmlPath);
            });
            
        } catch (IOException e) {
            System.err.println("❌ Navigation Error: Cannot find " + fxmlPath);
            e.printStackTrace();
        } catch (NullPointerException e) {
            System.err.println("❌ Path Error: The FXML path is likely wrong.");
        }
    }
    
    private void showWarningEffect(Node node) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(50), node);
        tt.setByX(8); tt.setCycleCount(6); tt.setAutoReverse(true);
        String old = node.getStyle();
        node.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 20;");
        tt.setOnFinished(e -> { node.setTranslateX(0); node.setStyle(old); });
        tt.play();
    }

    @FXML 
    private void handleBackHome(ActionEvent event) { 
        changeScene(event, "/application/view/GameSelection.fxml"); 
    }
    @FXML private void handleRestart(ActionEvent event) { initialize(); }

    private void changeScene(ActionEvent event, String fxml) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxml));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML private void handleProfileHover(MouseEvent e) { profileBox.setScaleX(1.05); profileBox.setScaleY(1.05); profileBox.setTranslateY(-5); }
    @FXML private void handleProfileNormal(MouseEvent e) { profileBox.setScaleX(1.0); profileBox.setScaleY(1.0); profileBox.setTranslateY(0); }

    @FXML
    private void handleViewProfile(MouseEvent event) { // Make sure it's MouseEvent!
        // Gentle fade out before switching scenes
        FadeTransition ft = new FadeTransition(Duration.millis(300), profileBox.getScene().getRoot());
        ft.setFromValue(1.0);
        ft.setToValue(0.0);
        ft.setOnFinished(e -> {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/application/view/profile.fxml"));
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
            } catch (IOException ex) {
                System.err.println("Could not load profile.fxml");
            }
        });
        ft.play();
    }
    
}