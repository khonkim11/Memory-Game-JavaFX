package application.controller;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.Transition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class SettingsController {

    @FXML private StackPane settingsRoot;
    @FXML private Label pilotNameDisplay;
    @FXML private Label difficultyValue;

    @FXML
    public void initialize() {
        settingsRoot.setOpacity(0);
        
        playIntroSequence();
    }

    private void playIntroSequence() {
        // 1. Fade the whole screen in
        FadeTransition fadeIn = new FadeTransition(Duration.millis(1000), settingsRoot);
        fadeIn.setToValue(1.0);
        fadeIn.play();

        // 2. Typewriter effect for the Name
        animateText(pilotNameDisplay, "WELCOME PLAYER");
    }

    private void animateText(Label label, String fullText) {
        final Animation animation = new Transition() {
            { setCycleDuration(Duration.millis(1500)); }
            protected void interpolate(double frac) {
                final int length = fullText.length();
                final int n = Math.round(length * (float) frac);
                label.setText(fullText.substring(0, n));
            }
        };
        animation.play();
    }

    @FXML
    private void handleReturn() {
    	SceneSwitcher.goTo(settingsRoot, "/application/view/login.fxml");
    }
}