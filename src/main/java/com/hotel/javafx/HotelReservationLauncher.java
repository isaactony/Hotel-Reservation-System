package com.hotel.javafx;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class HotelReservationLauncher extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Hotel Reservation System - Launcher");
        
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        
        Label title = new Label("Hotel Reservation Management System");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        
        Label subtitle = new Label("Welcome to the Hotel Reservation System");
        subtitle.setStyle("-fx-font-size: 16px;");
        
        Button startButton = new Button("Start Application");
        startButton.setStyle("-fx-font-size: 14px; -fx-pref-width: 200px; -fx-pref-height: 40px;");
        startButton.setOnAction(e -> {
            try {
                com.hotel.javafx.auth.LoginScreen loginApp = new com.hotel.javafx.auth.LoginScreen();
                Stage loginStage = new Stage();
                loginApp.start(loginStage);
                primaryStage.hide();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        
        Label instructions = new Label("Make sure the Spring Boot server is running on http://localhost:8080");
        instructions.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
        
        layout.getChildren().addAll(title, subtitle, startButton, instructions);
        
        Scene scene = new Scene(layout, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
