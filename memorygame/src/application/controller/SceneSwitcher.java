package application.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.stage.Stage;
import java.io.IOException;

public class SceneSwitcher {

    public static void goTo(Node node, String fxmlPath) {
        try {
            // 1. Get the current stage from the node passed in
            Stage stage = (Stage) node.getScene().getWindow();

            // 2. Load the new FXML file
            FXMLLoader loader = new FXMLLoader(SceneSwitcher.class.getResource(fxmlPath));
            Parent root = loader.load();

            // 3. Create a new scene with the loaded root
            Scene scene = new Scene(root);

            // 4. Update the stage
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            System.err.println("❌ Critical Error: Could not load " + fxmlPath);
            e.printStackTrace();
        } catch (NullPointerException e) {
            System.err.println("❌ Resource Error: FXML path not found: " + fxmlPath);
        }
    }
}