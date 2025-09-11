package com.hotel.javafx.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hotel.dto.ReservationResponse;
import com.hotel.entity.Room;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public class AdminDashboard extends Application {
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private final String baseUrl = "http://127.0.0.1:8081/api";
    
    private TableView<ReservationResponse> reservationTable;
    private TableView<Room> roomTable;
    private ObservableList<ReservationResponse> reservations;
    private ObservableList<Room> rooms;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Hotel Reservation System - Admin Dashboard");
        
        // Create main layout
        BorderPane mainLayout = new BorderPane();
        
        // Create menu bar
        MenuBar menuBar = createMenuBar();
        mainLayout.setTop(menuBar);
        
        // Create tab pane
        TabPane tabPane = new TabPane();
        
        // Reservations tab
        Tab reservationsTab = new Tab("Reservations");
        reservationsTab.setContent(createReservationsTab());
        reservationsTab.setClosable(false);
        
        // Rooms tab
        Tab roomsTab = new Tab("Rooms");
        roomsTab.setContent(createRoomsTab());
        roomsTab.setClosable(false);
        
        // Visitors tab
        Tab visitorsTab = new Tab("Visitors");
        visitorsTab.setContent(createVisitorsTab());
        visitorsTab.setClosable(false);
        
        tabPane.getTabs().addAll(reservationsTab, roomsTab, visitorsTab);
        mainLayout.setCenter(tabPane);
        
        Scene scene = new Scene(mainLayout, 1200, 800);
        primaryStage.setScene(scene);
        primaryStage.show();
        
        // Load initial data
        loadReservations();
        loadRooms();
    }

    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();
        
        Menu fileMenu = new Menu("File");
        MenuItem refreshItem = new MenuItem("Refresh Data");
        refreshItem.setOnAction(e -> {
            loadReservations();
            loadRooms();
        });
        fileMenu.getItems().add(refreshItem);
        
        Menu helpMenu = new Menu("Help");
        MenuItem aboutItem = new MenuItem("About");
        aboutItem.setOnAction(e -> showAboutDialog());
        helpMenu.getItems().add(aboutItem);
        
        menuBar.getMenus().addAll(fileMenu, helpMenu);
        return menuBar;
    }

    private VBox createReservationsTab() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
        
        // Title
        Label title = new Label("Reservation Management");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        // Controls
        HBox controls = new HBox(10);
        Button refreshBtn = new Button("Refresh");
        refreshBtn.setOnAction(e -> loadReservations());
        
        Button updateStatusBtn = new Button("Update Status");
        updateStatusBtn.setOnAction(e -> updateReservationStatus());
        
        controls.getChildren().addAll(refreshBtn, updateStatusBtn);
        
        // Table
        reservationTable = new TableView<>();
        reservations = FXCollections.observableArrayList();
        reservationTable.setItems(reservations);
        
        // Table columns
        TableColumn<ReservationResponse, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        
        TableColumn<ReservationResponse, String> visitorCol = new TableColumn<>("Visitor");
        visitorCol.setCellValueFactory(new PropertyValueFactory<>("visitorName"));
        
        TableColumn<ReservationResponse, String> roomCol = new TableColumn<>("Room");
        roomCol.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        
        TableColumn<ReservationResponse, String> checkInCol = new TableColumn<>("Check-in");
        checkInCol.setCellValueFactory(new PropertyValueFactory<>("checkInDate"));
        
        TableColumn<ReservationResponse, String> checkOutCol = new TableColumn<>("Check-out");
        checkOutCol.setCellValueFactory(new PropertyValueFactory<>("checkOutDate"));
        
        TableColumn<ReservationResponse, Integer> guestsCol = new TableColumn<>("Guests");
        guestsCol.setCellValueFactory(new PropertyValueFactory<>("numberOfGuests"));
        
        TableColumn<ReservationResponse, Double> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        
        TableColumn<ReservationResponse, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        reservationTable.getColumns().addAll(idCol, visitorCol, roomCol, checkInCol, checkOutCol, guestsCol, amountCol, statusCol);
        
        layout.getChildren().addAll(title, controls, reservationTable);
        return layout;
    }

    private VBox createRoomsTab() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
        
        // Title
        Label title = new Label("Room Management");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        // Controls
        HBox controls = new HBox(10);
        Button refreshBtn = new Button("Refresh");
        refreshBtn.setOnAction(e -> loadRooms());
        
        Button addRoomBtn = new Button("Add Room");
        addRoomBtn.setOnAction(e -> showAddRoomDialog());
        
        controls.getChildren().addAll(refreshBtn, addRoomBtn);
        
        // Table
        roomTable = new TableView<>();
        rooms = FXCollections.observableArrayList();
        roomTable.setItems(rooms);
        
        // Table columns
        TableColumn<Room, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(50);
        
        TableColumn<Room, String> imageCol = new TableColumn<>("Photo");
        imageCol.setCellFactory(column -> new TableCell<Room, String>() {
            private final ImageView imageView = new ImageView();
            private final Label noImageLabel = new Label("No Image");
            
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                
                if (empty || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    Room room = getTableRow().getItem();
                    if (room.getPhotoUrl() != null && !room.getPhotoUrl().isEmpty()) {
                        try {
                            Image image = new Image(room.getPhotoUrl(), 80, 60, true, true);
                            imageView.setImage(image);
                            imageView.setFitWidth(80);
                            imageView.setFitHeight(60);
                            imageView.setPreserveRatio(true);
                            setGraphic(imageView);
                        } catch (Exception e) {
                            setGraphic(noImageLabel);
                        }
                    } else {
                        setGraphic(noImageLabel);
                    }
                }
            }
        });
        imageCol.setPrefWidth(100);
        
        TableColumn<Room, String> numberCol = new TableColumn<>("Room Number");
        numberCol.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        numberCol.setPrefWidth(100);
        
        TableColumn<Room, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("roomType"));
        typeCol.setPrefWidth(100);
        
        TableColumn<Room, Double> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        priceCol.setPrefWidth(100);
        
        TableColumn<Room, Double> ratingCol = new TableColumn<>("Rating");
        ratingCol.setCellValueFactory(new PropertyValueFactory<>("rating"));
        ratingCol.setPrefWidth(80);
        
        TableColumn<Room, Integer> occupancyCol = new TableColumn<>("Max Occupancy");
        occupancyCol.setCellValueFactory(new PropertyValueFactory<>("maxOccupancy"));
        occupancyCol.setPrefWidth(100);
        
        TableColumn<Room, Boolean> availableCol = new TableColumn<>("Available");
        availableCol.setCellValueFactory(new PropertyValueFactory<>("isAvailable"));
        availableCol.setPrefWidth(80);
        
        roomTable.getColumns().addAll(idCol, imageCol, numberCol, typeCol, priceCol, ratingCol, occupancyCol, availableCol);
        
        layout.getChildren().addAll(title, controls, roomTable);
        return layout;
    }

    private VBox createVisitorsTab() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
        
        Label title = new Label("Visitor Management");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        Label info = new Label("Visitor management features will be implemented here.");
        info.setStyle("-fx-font-style: italic;");
        
        layout.getChildren().addAll(title, info);
        return layout;
    }

    private void loadReservations() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/reservations"))
                    .GET()
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                ReservationResponse[] reservationArray = objectMapper.readValue(response.body(), ReservationResponse[].class);
                reservations.clear();
                reservations.addAll(Arrays.asList(reservationArray));
            } else {
                showErrorDialog("Failed to load reservations: " + response.body());
            }
        } catch (IOException | InterruptedException e) {
            showErrorDialog("Error loading reservations: " + e.getMessage());
        }
    }

    private void loadRooms() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/rooms"))
                    .GET()
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                Room[] roomArray = objectMapper.readValue(response.body(), Room[].class);
                rooms.clear();
                rooms.addAll(Arrays.asList(roomArray));
            } else {
                showErrorDialog("Failed to load rooms: " + response.body());
            }
        } catch (IOException | InterruptedException e) {
            showErrorDialog("Error loading rooms: " + e.getMessage());
        }
    }

    private void updateReservationStatus() {
        ReservationResponse selected = reservationTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showErrorDialog("Please select a reservation to update.");
            return;
        }
        
        // Create status selection dialog
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Update Reservation Status");
        dialog.setHeaderText("Select new status for reservation #" + selected.getId());
        
        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll("PENDING", "CONFIRMED", "CHECKED_IN", "CHECKED_OUT", "CANCELLED", "NO_SHOW");
        statusCombo.setValue(selected.getStatus());
        
        VBox content = new VBox(10);
        content.getChildren().addAll(new Label("Status:"), statusCombo);
        dialog.getDialogPane().setContent(content);
        
        ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == updateButtonType) {
                return statusCombo.getValue();
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(newStatus -> {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(baseUrl + "/reservations/" + selected.getId() + "/status?status=" + newStatus))
                        .PUT(HttpRequest.BodyPublishers.noBody())
                        .build();
                
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                
                if (response.statusCode() == 200) {
                    loadReservations(); // Refresh the table
                    showInfoDialog("Reservation status updated successfully.");
                } else {
                    showErrorDialog("Failed to update reservation status: " + response.body());
                }
            } catch (IOException | InterruptedException e) {
                showErrorDialog("Error updating reservation status: " + e.getMessage());
            }
        });
    }

    private void showAddRoomDialog() {
        Dialog<Room> dialog = new Dialog<>();
        dialog.setTitle("Add New Room");
        dialog.setHeaderText("Enter room details");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        TextField roomNumberField = new TextField();
        TextField roomTypeField = new TextField();
        TextField priceField = new TextField();
        TextField occupancyField = new TextField();
        TextArea descriptionArea = new TextArea();
        TextField amenitiesField = new TextField();
        TextField photoUrlField = new TextField();
        TextField ratingField = new TextField();
        
        // Set default photo URLs for different room types
        photoUrlField.setPromptText("e.g., https://images.unsplash.com/photo-...");
        ratingField.setPromptText("e.g., 4.5");
        
        // Add listener to suggest photo URLs based on room type
        roomTypeField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.trim().isEmpty()) {
                String suggestedUrl = getSuggestedPhotoUrl(newVal.toLowerCase());
                if (suggestedUrl != null) {
                    photoUrlField.setText(suggestedUrl);
                }
            }
        });
        
        grid.add(new Label("Room Number:"), 0, 0);
        grid.add(roomNumberField, 1, 0);
        grid.add(new Label("Room Type:"), 0, 1);
        grid.add(roomTypeField, 1, 1);
        grid.add(new Label("Price:"), 0, 2);
        grid.add(priceField, 1, 2);
        grid.add(new Label("Max Occupancy:"), 0, 3);
        grid.add(occupancyField, 1, 3);
        grid.add(new Label("Description:"), 0, 4);
        grid.add(descriptionArea, 1, 4);
        grid.add(new Label("Amenities:"), 0, 5);
        grid.add(amenitiesField, 1, 5);
        grid.add(new Label("Photo URL:"), 0, 6);
        grid.add(photoUrlField, 1, 6);
        grid.add(new Label("Rating:"), 0, 7);
        grid.add(ratingField, 1, 7);
        
        dialog.getDialogPane().setContent(grid);
        
        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                try {
                    Room room = new Room();
                    room.setRoomNumber(roomNumberField.getText());
                    room.setRoomType(roomTypeField.getText());
                    room.setPrice(java.math.BigDecimal.valueOf(Double.parseDouble(priceField.getText())));
                    room.setMaxOccupancy(Integer.parseInt(occupancyField.getText()));
                    room.setDescription(descriptionArea.getText());
                    room.setAmenities(amenitiesField.getText());
                    room.setPhotoUrl(photoUrlField.getText());
                    
                    // Set rating if provided, otherwise default to 0.0
                    String ratingText = ratingField.getText().trim();
                    if (!ratingText.isEmpty()) {
                        room.setRating(Double.parseDouble(ratingText));
                    } else {
                        room.setRating(0.0);
                    }
                    
                    room.setIsAvailable(true);
                    return room;
                } catch (NumberFormatException e) {
                    showErrorDialog("Please enter valid numbers for price, occupancy, and rating.");
                    return null;
                }
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(room -> {
            try {
                String json = objectMapper.writeValueAsString(room);
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(baseUrl + "/rooms"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(json))
                        .build();
                
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                
                if (response.statusCode() == 200) {
                    loadRooms(); // Refresh the table
                    showInfoDialog("Room added successfully.");
                } else {
                    showErrorDialog("Failed to add room: " + response.body());
                }
            } catch (IOException | InterruptedException e) {
                showErrorDialog("Error adding room: " + e.getMessage());
            }
        });
    }

    private String getSuggestedPhotoUrl(String roomType) {
        switch (roomType.toLowerCase()) {
            case "standard":
                return "https://images.unsplash.com/photo-1631049307264-da0ec9d70304?w=800";
            case "deluxe":
                return "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=800";
            case "suite":
                return "https://images.unsplash.com/photo-1611892440504-42a792e24d32?w=800";
            case "family":
                return "https://images.unsplash.com/photo-1595576508898-0ad5c879a061?w=800";
            case "business":
                return "https://images.unsplash.com/photo-1590490360182-c33d57733427?w=800";
            default:
                return "https://images.unsplash.com/photo-1631049307264-da0ec9d70304?w=800";
        }
    }

    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfoDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showAboutDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("Hotel Reservation System");
        alert.setContentText("Admin Dashboard v1.0\n\nA comprehensive hotel reservation management system built with JavaFX and Spring Boot.");
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
