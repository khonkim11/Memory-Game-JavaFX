package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;
import application.database.DatabaseManager; 

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {

        DatabaseManager.initialize();
        
        try {
        	Parent root = FXMLLoader.load(getClass().getResource("/application/view/login.fxml"));
        	
            if (root == null) {
                System.out.println("Error: FXML file not found at the specified path!");
                return;
            }

            Scene scene = new Scene(root);
            primaryStage.setTitle("PLAYER PROFILE");
            primaryStage.setScene(scene);
            primaryStage.show();
            
        } catch (Exception e) {
            System.err.println("Critical Failure during Launch:");
            e.printStackTrace(); 
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}