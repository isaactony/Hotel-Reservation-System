package com.hotel.javafx.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class LoginScreen extends Application {
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private final String baseUrl = "http://127.0.0.1:8081/api";
    
    private Stage primaryStage;
    private String currentUserType = "visitor"; // Default to visitor

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Hotel Reservation System - Login");
        
        showLoginScreen();
    }

    private void showLoginScreen() {
        VBox mainLayout = new VBox(20);
        mainLayout.setPadding(new Insets(30));
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setStyle("-fx-background-color: #f5f5f5;");

        // Title
        Label title = new Label("Hotel Reservation System");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // User type selection
        HBox userTypeBox = new HBox(20);
        userTypeBox.setAlignment(Pos.CENTER);
        
        ToggleGroup userTypeGroup = new ToggleGroup();
        RadioButton visitorRadio = new RadioButton("Visitor");
        RadioButton adminRadio = new RadioButton("Admin");
        
        visitorRadio.setToggleGroup(userTypeGroup);
        adminRadio.setToggleGroup(userTypeGroup);
        visitorRadio.setSelected(true);
        
        visitorRadio.setOnAction(e -> currentUserType = "visitor");
        adminRadio.setOnAction(e -> currentUserType = "admin");
        
        userTypeBox.getChildren().addAll(visitorRadio, adminRadio);

        // Login form
        VBox loginForm = new VBox(15);
        loginForm.setAlignment(Pos.CENTER);
        loginForm.setStyle("-fx-background-color: white; -fx-padding: 30; -fx-background-radius: 10;");

        Label loginTitle = new Label("Login");
        loginTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #34495e;");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setPrefWidth(250);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setPrefWidth(250);

        Button loginButton = new Button("Login");
        loginButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px; -fx-pref-width: 250; -fx-pref-height: 35;");
        loginButton.setOnAction(e -> handleLogin(usernameField.getText(), passwordField.getText()));

        Button registerButton = new Button("Don't have an account? Register");
        registerButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #3498db; -fx-underline: true;");
        registerButton.setOnAction(e -> showRegistrationScreen());

        // Test connection button
        Button testButton = new Button("Test Connection");
        testButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 12px; -fx-pref-width: 250; -fx-pref-height: 30;");
        testButton.setOnAction(e -> testConnection());

        loginForm.getChildren().addAll(loginTitle, usernameField, passwordField, loginButton, registerButton, testButton);

        // Sample accounts info
        VBox sampleInfo = new VBox(5);
        sampleInfo.setAlignment(Pos.CENTER);
        sampleInfo.setStyle("-fx-background-color: #ecf0f1; -fx-padding: 15; -fx-background-radius: 5;");
        
        Label sampleTitle = new Label("Sample Accounts:");
        sampleTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        Label visitorInfo = new Label("Visitor: visitor1 / password123");
        Label adminInfo = new Label("Admin: admin1 / password123");
        
        visitorInfo.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");
        adminInfo.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");
        
        sampleInfo.getChildren().addAll(sampleTitle, visitorInfo, adminInfo);

        mainLayout.getChildren().addAll(title, userTypeBox, loginForm, sampleInfo);

        Scene scene = new Scene(mainLayout, 500, 600);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private void testConnection() {
        String[] urls = {"http://127.0.0.1:8081/api/rooms", "http://localhost:8081/api/rooms"};
        
        for (String url : urls) {
            try {
                System.out.println("Testing connection to: " + url);
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .timeout(Duration.ofSeconds(5))
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                
                System.out.println("Response status: " + response.statusCode());
                System.out.println("Response body: " + response.body().substring(0, Math.min(200, response.body().length())));
                
                if (response.statusCode() == 200) {
                    showAlert("Connection Test", "✅ Connection successful!\nURL: " + url + "\nStatus: " + response.statusCode());
                    return;
                } else {
                    showAlert("Connection Test", "⚠️ Connection failed!\nURL: " + url + "\nStatus: " + response.statusCode() + "\nResponse: " + response.body());
                }
            } catch (Exception e) {
                System.out.println("Connection test failed for " + url + ": " + e.getMessage());
                showAlert("Connection Test", "❌ Connection failed!\nURL: " + url + "\nError: " + e.getMessage());
            }
        }
    }

    private void showRegistrationScreen() {
        VBox mainLayout = new VBox(20);
        mainLayout.setPadding(new Insets(30));
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setStyle("-fx-background-color: #f5f5f5;");

        // Title
        Label title = new Label("Hotel Reservation System - Registration");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // User type selection
        HBox userTypeBox = new HBox(20);
        userTypeBox.setAlignment(Pos.CENTER);
        
        ToggleGroup userTypeGroup = new ToggleGroup();
        RadioButton visitorRadio = new RadioButton("Visitor");
        RadioButton adminRadio = new RadioButton("Admin");
        
        visitorRadio.setToggleGroup(userTypeGroup);
        adminRadio.setToggleGroup(userTypeGroup);
        visitorRadio.setSelected(true);
        
        visitorRadio.setOnAction(e -> currentUserType = "visitor");
        adminRadio.setOnAction(e -> currentUserType = "admin");
        
        userTypeBox.getChildren().addAll(visitorRadio, adminRadio);

        // Registration form
        VBox regForm = new VBox(15);
        regForm.setAlignment(Pos.CENTER);
        regForm.setStyle("-fx-background-color: white; -fx-padding: 30; -fx-background-radius: 10;");

        Label regTitle = new Label("Create Account");
        regTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #34495e;");

        // Common fields
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setPrefWidth(250);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setPrefWidth(250);

        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.setPrefWidth(250);

        TextField firstNameField = new TextField();
        firstNameField.setPromptText("First Name");
        firstNameField.setPrefWidth(250);

        TextField lastNameField = new TextField();
        lastNameField.setPromptText("Last Name");
        lastNameField.setPrefWidth(250);

        TextField phoneField = new TextField();
        phoneField.setPromptText("Phone Number");
        phoneField.setPrefWidth(250);

        // Visitor-specific fields
        VBox visitorFields = new VBox(10);
        TextField addressField = new TextField();
        addressField.setPromptText("Address");
        addressField.setPrefWidth(250);

        TextField cityField = new TextField();
        cityField.setPromptText("City");
        cityField.setPrefWidth(250);

        TextField countryField = new TextField();
        countryField.setPromptText("Country");
        countryField.setPrefWidth(250);

        TextField postalField = new TextField();
        postalField.setPromptText("Postal Code");
        postalField.setPrefWidth(250);

        visitorFields.getChildren().addAll(addressField, cityField, countryField, postalField);

        // Admin-specific fields
        VBox adminFields = new VBox(10);
        TextField departmentField = new TextField();
        departmentField.setPromptText("Department");
        departmentField.setPrefWidth(250);

        adminFields.getChildren().addAll(departmentField);

        // Show/hide fields based on user type
        visitorFields.setVisible(currentUserType.equals("visitor"));
        adminFields.setVisible(currentUserType.equals("admin"));

        visitorRadio.setOnAction(e -> {
            currentUserType = "visitor";
            visitorFields.setVisible(true);
            adminFields.setVisible(false);
        });

        adminRadio.setOnAction(e -> {
            currentUserType = "admin";
            visitorFields.setVisible(false);
            adminFields.setVisible(true);
        });

        Button registerButton = new Button("Register");
        registerButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 14px; -fx-pref-width: 250; -fx-pref-height: 35;");
        registerButton.setOnAction(e -> handleRegistration(
            usernameField.getText(), passwordField.getText(), emailField.getText(),
            firstNameField.getText(), lastNameField.getText(), phoneField.getText(),
            currentUserType.equals("visitor") ? addressField.getText() : null,
            currentUserType.equals("visitor") ? cityField.getText() : null,
            currentUserType.equals("visitor") ? countryField.getText() : null,
            currentUserType.equals("visitor") ? postalField.getText() : null,
            currentUserType.equals("admin") ? departmentField.getText() : null
        ));

        Button backButton = new Button("Back to Login");
        backButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #3498db; -fx-underline: true;");
        backButton.setOnAction(e -> showLoginScreen());

        regForm.getChildren().addAll(regTitle, usernameField, passwordField, emailField, 
                                    firstNameField, lastNameField, phoneField, 
                                    visitorFields, adminFields, registerButton, backButton);

        mainLayout.getChildren().addAll(title, userTypeBox, regForm);

        Scene scene = new Scene(mainLayout, 500, 800);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
    }

    private void handleLogin(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Please enter both username and password.");
            return;
        }

        try {
            Map<String, String> loginData = new HashMap<>();
            loginData.put("username", username);
            loginData.put("password", password);

            String json = objectMapper.writeValueAsString(loginData);
            String loginUrl = baseUrl + "/auth/login";
            
            System.out.println("Attempting login to: " + loginUrl);
            System.out.println("Request data: " + json);
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(loginUrl))
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(10))
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            System.out.println("Response status: " + response.statusCode());
            System.out.println("Response body: " + response.body());

            if (response.statusCode() == 200) {
                Map<String, Object> responseData = objectMapper.readValue(response.body(), Map.class);
                String userType = (String) responseData.get("userType");
                Long userId = ((Number) responseData.get("userId")).longValue();
                
                // Verify user type matches selection
                if ((currentUserType.equals("admin") && !"ADMIN".equals(userType)) ||
                    (currentUserType.equals("visitor") && !"VISITOR".equals(userType))) {
                    showAlert("Error", "Please select the correct user type for this account.");
                    return;
                }

                showAlert("Success", "Login successful!");
                
                // Open appropriate dashboard
                if ("ADMIN".equals(userType)) {
                    openAdminDashboard();
                } else {
                    openVisitorDashboard(userId);
                }
            } else {
                showAlert("Error", "Login failed!\nStatus: " + response.statusCode() + "\nResponse: " + response.body());
            }
        } catch (IOException | InterruptedException e) {
            showAlert("Error", "Login failed: " + e.getMessage());
        }
    }

    private void handleRegistration(String username, String password, String email, String firstName, 
                                  String lastName, String phone, String address, String city, 
                                  String country, String postalCode, String department) {
        if (username.isEmpty() || password.isEmpty() || email.isEmpty() || firstName.isEmpty() || lastName.isEmpty()) {
            showAlert("Error", "Please fill in all required fields.");
            return;
        }

        try {
            String endpoint = currentUserType.equals("admin") ? "/auth/register/admin" : "/auth/register/visitor";
            
            Map<String, String> regData = new HashMap<>();
            regData.put("username", username);
            regData.put("password", password);
            regData.put("email", email);
            regData.put("firstName", firstName);
            regData.put("lastName", lastName);
            regData.put("phoneNumber", phone);

            if (currentUserType.equals("visitor")) {
                regData.put("address", address);
                regData.put("city", city);
                regData.put("country", country);
                regData.put("postalCode", postalCode);
            } else {
                regData.put("department", department);
            }

            String json = objectMapper.writeValueAsString(regData);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + endpoint))
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(10))
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                showAlert("Success", "Registration successful! You can now login.");
                showLoginScreen();
            } else {
                showAlert("Error", "Registration failed!\nStatus: " + response.statusCode() + "\nResponse: " + response.body());
            }
        } catch (IOException | InterruptedException e) {
            showAlert("Error", "Registration failed: " + e.getMessage());
        }
    }

    private void openAdminDashboard() {
        try {
            com.hotel.javafx.admin.AdminDashboard adminApp = new com.hotel.javafx.admin.AdminDashboard();
            Stage adminStage = new Stage();
            adminApp.start(adminStage);
            primaryStage.hide();
        } catch (Exception e) {
            showAlert("Error", "Failed to open admin dashboard: " + e.getMessage());
        }
    }

    private void openVisitorDashboard(Long userId) {
        try {
            com.hotel.javafx.visitor.VisitorDashboard visitorApp = new com.hotel.javafx.visitor.VisitorDashboard(userId);
            Stage visitorStage = new Stage();
            visitorApp.start(visitorStage);
            primaryStage.hide();
        } catch (Exception e) {
            showAlert("Error", "Failed to open visitor dashboard: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}