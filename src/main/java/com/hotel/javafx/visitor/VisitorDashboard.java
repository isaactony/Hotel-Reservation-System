package com.hotel.javafx.visitor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hotel.dto.ReservationRequest;
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

public class VisitorDashboard extends Application {
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private final String baseUrl = "http://127.0.0.1:8081/api";
    
    private TableView<Room> availableRoomsTable;
    private TableView<ReservationResponse> myReservationsTable;
    private ObservableList<Room> availableRooms;
    private ObservableList<ReservationResponse> myReservations;
    
    private Long currentVisitorId;

    public VisitorDashboard() {
        this.currentVisitorId = null; // Will be set by constructor
    }

    public VisitorDashboard(Long visitorId) {
        this.currentVisitorId = visitorId;
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Hotel Reservation System - Visitor Dashboard");
        
        // Create main layout
        BorderPane mainLayout = new BorderPane();
        
        // Create menu bar
        MenuBar menuBar = createMenuBar();
        mainLayout.setTop(menuBar);
        
        // Create tab pane
        TabPane tabPane = new TabPane();
        
        // Room Search tab
        Tab searchTab = new Tab("Search Rooms");
        searchTab.setContent(createRoomSearchTab());
        searchTab.setClosable(false);
        
        // My Reservations tab
        Tab reservationsTab = new Tab("My Reservations");
        reservationsTab.setContent(createMyReservationsTab());
        reservationsTab.setClosable(false);
        
        tabPane.getTabs().addAll(searchTab, reservationsTab);
        mainLayout.setCenter(tabPane);
        
        Scene scene = new Scene(mainLayout, 1200, 800);
        primaryStage.setScene(scene);
        primaryStage.show();
        
        // Load initial data
        if (currentVisitorId != null) {
            loadMyReservations();
        } else {
            showErrorDialog("Error: Visitor ID not set. Please login again.");
        }
    }

    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();
        
        Menu fileMenu = new Menu("File");
        MenuItem refreshItem = new MenuItem("Refresh Data");
        refreshItem.setOnAction(e -> {
            loadMyReservations();
        });
        fileMenu.getItems().add(refreshItem);
        
        Menu helpMenu = new Menu("Help");
        MenuItem aboutItem = new MenuItem("About");
        aboutItem.setOnAction(e -> showAboutDialog());
        helpMenu.getItems().add(aboutItem);
        
        menuBar.getMenus().addAll(fileMenu, helpMenu);
        return menuBar;
    }

    private VBox createRoomSearchTab() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
        
        // Title
        Label title = new Label("Room Search & Booking");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        // Search controls
        HBox searchControls = new HBox(10);
        searchControls.setAlignment(Pos.CENTER_LEFT);
        
        Label checkInLabel = new Label("Check-in Date:");
        DatePicker checkInDatePicker = new DatePicker(LocalDate.now());
        
        Label checkOutLabel = new Label("Check-out Date:");
        DatePicker checkOutDatePicker = new DatePicker(LocalDate.now().plusDays(1));
        
        Label guestsLabel = new Label("Number of Guests:");
        Spinner<Integer> guestsSpinner = new Spinner<>(1, 10, 1);
        
        Button searchBtn = new Button("Search Available Rooms");
        searchBtn.setOnAction(e -> searchAvailableRooms(
            checkInDatePicker.getValue(),
            checkOutDatePicker.getValue(),
            guestsSpinner.getValue()
        ));
        
        searchControls.getChildren().addAll(
            checkInLabel, checkInDatePicker,
            checkOutLabel, checkOutDatePicker,
            guestsLabel, guestsSpinner,
            searchBtn
        );
        
        // Available rooms table
        Label tableTitle = new Label("Available Rooms");
        tableTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        
        availableRoomsTable = new TableView<>();
        availableRooms = FXCollections.observableArrayList();
        availableRoomsTable.setItems(availableRooms);
        
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
        
        TableColumn<Room, Double> priceCol = new TableColumn<>("Price per Night");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        priceCol.setPrefWidth(120);
        
        TableColumn<Room, Double> ratingCol = new TableColumn<>("Rating");
        ratingCol.setCellValueFactory(new PropertyValueFactory<>("rating"));
        ratingCol.setPrefWidth(80);
        
        TableColumn<Room, Integer> occupancyCol = new TableColumn<>("Max Occupancy");
        occupancyCol.setCellValueFactory(new PropertyValueFactory<>("maxOccupancy"));
        occupancyCol.setPrefWidth(100);
        
        TableColumn<Room, String> amenitiesCol = new TableColumn<>("Amenities");
        amenitiesCol.setCellValueFactory(new PropertyValueFactory<>("amenities"));
        amenitiesCol.setPrefWidth(200);
        
        availableRoomsTable.getColumns().addAll(idCol, imageCol, numberCol, typeCol, priceCol, ratingCol, occupancyCol, amenitiesCol);
        
        // Booking controls
        HBox bookingControls = new HBox(10);
        Button bookBtn = new Button("Book Selected Room");
        bookBtn.setOnAction(e -> bookSelectedRoom(
            checkInDatePicker.getValue(),
            checkOutDatePicker.getValue(),
            guestsSpinner.getValue()
        ));
        
        TextArea specialRequestsArea = new TextArea();
        specialRequestsArea.setPromptText("Special requests (optional)");
        specialRequestsArea.setPrefRowCount(3);
        
        bookingControls.getChildren().addAll(bookBtn, new Label("Special Requests:"), specialRequestsArea);
        
        layout.getChildren().addAll(title, searchControls, tableTitle, availableRoomsTable, bookingControls);
        return layout;
    }

    private VBox createMyReservationsTab() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
        
        // Title
        Label title = new Label("My Reservations");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        // Controls
        HBox controls = new HBox(10);
        Button refreshBtn = new Button("Refresh");
        refreshBtn.setOnAction(e -> loadMyReservations());
        
        Button cancelBtn = new Button("Cancel Selected");
        cancelBtn.setOnAction(e -> cancelSelectedReservation());
        
        controls.getChildren().addAll(refreshBtn, cancelBtn);
        
        // Table
        myReservationsTable = new TableView<>();
        myReservations = FXCollections.observableArrayList();
        myReservationsTable.setItems(myReservations);
        
        // Table columns
        TableColumn<ReservationResponse, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        
        TableColumn<ReservationResponse, String> roomCol = new TableColumn<>("Room");
        roomCol.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        
        TableColumn<ReservationResponse, String> checkInCol = new TableColumn<>("Check-in");
        checkInCol.setCellValueFactory(new PropertyValueFactory<>("checkInDate"));
        
        TableColumn<ReservationResponse, String> checkOutCol = new TableColumn<>("Check-out");
        checkOutCol.setCellValueFactory(new PropertyValueFactory<>("checkOutDate"));
        
        TableColumn<ReservationResponse, Integer> guestsCol = new TableColumn<>("Guests");
        guestsCol.setCellValueFactory(new PropertyValueFactory<>("numberOfGuests"));
        
        TableColumn<ReservationResponse, Double> amountCol = new TableColumn<>("Total Amount");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        
        TableColumn<ReservationResponse, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        myReservationsTable.getColumns().addAll(idCol, roomCol, checkInCol, checkOutCol, guestsCol, amountCol, statusCol);
        
        layout.getChildren().addAll(title, controls, myReservationsTable);
        return layout;
    }

    private void searchAvailableRooms(LocalDate checkInDate, LocalDate checkOutDate, Integer numberOfGuests) {
        if (checkInDate == null || checkOutDate == null) {
            showErrorDialog("Please select both check-in and check-out dates.");
            return;
        }
        
        if (checkInDate.isAfter(checkOutDate) || checkInDate.isEqual(checkOutDate)) {
            showErrorDialog("Check-in date must be before check-out date.");
            return;
        }
        
        try {
            String url = baseUrl + "/rooms/available/dates?checkInDate=" + checkInDate + "&checkOutDate=" + checkOutDate;
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                Room[] roomArray = objectMapper.readValue(response.body(), Room[].class);
                availableRooms.clear();
                
                // Filter rooms by occupancy
                Arrays.stream(roomArray)
                    .filter(room -> room.getMaxOccupancy() >= numberOfGuests)
                    .forEach(room -> availableRooms.add(room));
                
                if (availableRooms.isEmpty()) {
                    showInfoDialog("No rooms available for the selected dates and number of guests.");
                }
            } else {
                showErrorDialog("Failed to search rooms: " + response.body());
            }
        } catch (IOException | InterruptedException e) {
            showErrorDialog("Error searching rooms: " + e.getMessage());
        }
    }

    private void bookSelectedRoom(LocalDate checkInDate, LocalDate checkOutDate, Integer numberOfGuests) {
        Room selectedRoom = availableRoomsTable.getSelectionModel().getSelectedItem();
        if (selectedRoom == null) {
            showErrorDialog("Please select a room to book.");
            return;
        }
        
        // Get special requests from the text area
        TextArea specialRequestsArea = (TextArea) ((HBox) availableRoomsTable.getParent().getChildrenUnmodifiable().get(4)).getChildren().get(2);
        String specialRequests = specialRequestsArea.getText();
        
        try {
            ReservationRequest reservationRequest = new ReservationRequest(
                selectedRoom.getId(),
                checkInDate,
                checkOutDate,
                numberOfGuests,
                specialRequests,
                currentVisitorId
            );
            
            String json = objectMapper.writeValueAsString(reservationRequest);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/reservations"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                ReservationResponse reservation = objectMapper.readValue(response.body(), ReservationResponse.class);
                showInfoDialog("Reservation created successfully!\nReservation ID: " + reservation.getId() + 
                             "\nTotal Amount: $" + reservation.getTotalAmount());
                loadMyReservations(); // Refresh reservations
            } else {
                showErrorDialog("Failed to create reservation: " + response.body());
            }
        } catch (IOException | InterruptedException e) {
            showErrorDialog("Error creating reservation: " + e.getMessage());
        }
    }

    private void loadMyReservations() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/reservations/visitor/" + currentVisitorId))
                    .GET()
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                ReservationResponse[] reservationArray = objectMapper.readValue(response.body(), ReservationResponse[].class);
                myReservations.clear();
                myReservations.addAll(Arrays.asList(reservationArray));
            } else {
                showErrorDialog("Failed to load reservations: " + response.body());
            }
        } catch (IOException | InterruptedException e) {
            showErrorDialog("Error loading reservations: " + e.getMessage());
        }
    }

    private void cancelSelectedReservation() {
        ReservationResponse selected = myReservationsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showErrorDialog("Please select a reservation to cancel.");
            return;
        }
        
        if ("CANCELLED".equals(selected.getStatus())) {
            showErrorDialog("This reservation is already cancelled.");
            return;
        }
        
        // Confirm cancellation
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Cancellation");
        confirmAlert.setHeaderText("Cancel Reservation");
        confirmAlert.setContentText("Are you sure you want to cancel reservation #" + selected.getId() + "?");
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create(baseUrl + "/reservations/" + selected.getId() + "/cancel"))
                            .PUT(HttpRequest.BodyPublishers.noBody())
                            .build();
                    
                    HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                    
                    if (httpResponse.statusCode() == 200) {
                        showInfoDialog("Reservation cancelled successfully.");
                        loadMyReservations(); // Refresh the table
                    } else {
                        showErrorDialog("Failed to cancel reservation: " + httpResponse.body());
                    }
                } catch (IOException | InterruptedException e) {
                    showErrorDialog("Error cancelling reservation: " + e.getMessage());
                }
            }
        });
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
        alert.setContentText("Visitor Dashboard v1.0\n\nA comprehensive hotel reservation management system built with JavaFX and Spring Boot.");
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
