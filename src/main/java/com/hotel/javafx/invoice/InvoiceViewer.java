package com.hotel.javafx.invoice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hotel.dto.InvoiceResponse;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.print.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.format.DateTimeFormatter;

public class InvoiceViewer extends Application {
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String baseUrl = "http://127.0.0.1:8081/api";
    
    private InvoiceResponse invoice;
    private VBox invoiceContent;
    private Stage primaryStage;

    public InvoiceViewer(InvoiceResponse invoice) {
        this.invoice = invoice;
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Invoice - " + invoice.getInvoiceNumber());
        primaryStage.setWidth(800);
        primaryStage.setHeight(1000);

        // Create main layout
        BorderPane mainLayout = new BorderPane();
        
        // Create invoice content
        invoiceContent = createInvoiceContent();
        
        // Create scroll pane for invoice content
        ScrollPane scrollPane = new ScrollPane(invoiceContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        
        // Create control buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setPadding(new Insets(10));
        buttonBox.setAlignment(Pos.CENTER);
        
        Button printButton = new Button("Print Invoice");
        printButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        printButton.setOnAction(e -> printInvoice());
        
        Button closeButton = new Button("Close");
        closeButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold;");
        closeButton.setOnAction(e -> primaryStage.close());
        
        buttonBox.getChildren().addAll(printButton, closeButton);
        
        mainLayout.setCenter(scrollPane);
        mainLayout.setBottom(buttonBox);
        
        Scene scene = new Scene(mainLayout);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createInvoiceContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setStyle("-fx-background-color: white;");
        
        // Hotel Header
        VBox header = createHeader();
        
        // Invoice Details
        VBox invoiceDetails = createInvoiceDetails();
        
        // Customer Information
        VBox customerInfo = createCustomerInfo();
        
        // Reservation Details
        VBox reservationDetails = createReservationDetails();
        
        // Invoice Items
        VBox invoiceItems = createInvoiceItems();
        
        // Totals
        VBox totals = createTotals();
        
        // Footer
        VBox footer = createFooter();
        
        content.getChildren().addAll(header, invoiceDetails, customerInfo, 
                                   reservationDetails, invoiceItems, totals, footer);
        
        return content;
    }

    private VBox createHeader() {
        VBox header = new VBox(10);
        header.setAlignment(Pos.CENTER);
        
        Text hotelName = new Text("GRAND HOTEL RESERVATION SYSTEM");
        hotelName.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        hotelName.setTextAlignment(TextAlignment.CENTER);
        
        Text tagline = new Text("Your Comfort, Our Priority");
        tagline.setFont(Font.font("Arial", 14));
        tagline.setTextAlignment(TextAlignment.CENTER);
        
        Text address = new Text("123 Hotel Street, City, State 12345\nPhone: (555) 123-4567 | Email: info@grandhotel.com");
        address.setFont(Font.font("Arial", 12));
        address.setTextAlignment(TextAlignment.CENTER);
        
        // Add separator line
        Separator separator = new Separator();
        
        header.getChildren().addAll(hotelName, tagline, address, separator);
        return header;
    }

    private VBox createInvoiceDetails() {
        VBox details = new VBox(10);
        
        Text invoiceTitle = new Text("INVOICE");
        invoiceTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        
        HBox invoiceNumberBox = new HBox();
        Text invoiceNumberLabel = new Text("Invoice Number: ");
        invoiceNumberLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        Text invoiceNumber = new Text(invoice.getInvoiceNumber());
        invoiceNumber.setFont(Font.font("Arial", 12));
        invoiceNumberBox.getChildren().addAll(invoiceNumberLabel, invoiceNumber);
        
        HBox dateBox = new HBox();
        Text dateLabel = new Text("Invoice Date: ");
        dateLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        Text date = new Text(invoice.getCreatedAt().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
        date.setFont(Font.font("Arial", 12));
        dateBox.getChildren().addAll(dateLabel, date);
        
        HBox dueDateBox = new HBox();
        Text dueDateLabel = new Text("Due Date: ");
        dueDateLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        Text dueDate = new Text(invoice.getDueDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
        dueDate.setFont(Font.font("Arial", 12));
        dueDateBox.getChildren().addAll(dueDateLabel, dueDate);
        
        HBox statusBox = new HBox();
        Text statusLabel = new Text("Status: ");
        statusLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        Text status = new Text(invoice.getStatus());
        status.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        status.setStyle("-fx-fill: " + getStatusColor(invoice.getStatus()) + ";");
        statusBox.getChildren().addAll(statusLabel, status);
        
        details.getChildren().addAll(invoiceTitle, invoiceNumberBox, dateBox, dueDateBox, statusBox);
        return details;
    }

    private VBox createCustomerInfo() {
        VBox customerInfo = new VBox(10);
        
        Text customerTitle = new Text("BILL TO:");
        customerTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        
        Text customerName = new Text(invoice.getVisitorName());
        customerName.setFont(Font.font("Arial", 12));
        
        Text customerEmail = new Text(invoice.getVisitorEmail());
        customerEmail.setFont(Font.font("Arial", 12));
        
        customerInfo.getChildren().addAll(customerTitle, customerName, customerEmail);
        return customerInfo;
    }

    private VBox createReservationDetails() {
        VBox reservationDetails = new VBox(10);
        
        Text reservationTitle = new Text("RESERVATION DETAILS:");
        reservationTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        
        HBox roomBox = new HBox();
        Text roomLabel = new Text("Room: ");
        roomLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        Text room = new Text(invoice.getRoomNumber() + " (" + invoice.getRoomType() + ")");
        room.setFont(Font.font("Arial", 12));
        roomBox.getChildren().addAll(roomLabel, room);
        
        HBox checkInBox = new HBox();
        Text checkInLabel = new Text("Check-in: ");
        checkInLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        Text checkIn = new Text(invoice.getCheckInDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
        checkIn.setFont(Font.font("Arial", 12));
        checkInBox.getChildren().addAll(checkInLabel, checkIn);
        
        HBox checkOutBox = new HBox();
        Text checkOutLabel = new Text("Check-out: ");
        checkOutLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        Text checkOut = new Text(invoice.getCheckOutDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
        checkOut.setFont(Font.font("Arial", 12));
        checkOutBox.getChildren().addAll(checkOutLabel, checkOut);
        
        HBox guestsBox = new HBox();
        Text guestsLabel = new Text("Number of Guests: ");
        guestsLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        Text guests = new Text(invoice.getNumberOfGuests().toString());
        guests.setFont(Font.font("Arial", 12));
        guestsBox.getChildren().addAll(guestsLabel, guests);
        
        reservationDetails.getChildren().addAll(reservationTitle, roomBox, checkInBox, checkOutBox, guestsBox);
        return reservationDetails;
    }

    private VBox createInvoiceItems() {
        VBox items = new VBox(10);
        
        Text itemsTitle = new Text("INVOICE ITEMS:");
        itemsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        
        // Create table-like layout
        GridPane table = new GridPane();
        table.setHgap(20);
        table.setVgap(5);
        
        // Headers
        Text descriptionHeader = new Text("Description");
        descriptionHeader.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        Text amountHeader = new Text("Amount");
        amountHeader.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        
        table.add(descriptionHeader, 0, 0);
        table.add(amountHeader, 1, 0);
        
        // Room charges
        Text roomDescription = new Text("Room Charges");
        roomDescription.setFont(Font.font("Arial", 12));
        Text roomAmount = new Text("$" + invoice.getSubtotal().toString());
        roomAmount.setFont(Font.font("Arial", 12));
        
        table.add(roomDescription, 0, 1);
        table.add(roomAmount, 1, 1);
        
        items.getChildren().addAll(itemsTitle, table);
        return items;
    }

    private VBox createTotals() {
        VBox totals = new VBox(10);
        
        GridPane totalsTable = new GridPane();
        totalsTable.setHgap(20);
        totalsTable.setVgap(5);
        
        Text subtotalLabel = new Text("Subtotal:");
        subtotalLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        Text subtotal = new Text("$" + invoice.getSubtotal().toString());
        subtotal.setFont(Font.font("Arial", 12));
        
        Text taxLabel = new Text("Tax (" + invoice.getTaxRate().multiply(java.math.BigDecimal.valueOf(100)) + "%):");
        taxLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        Text tax = new Text("$" + invoice.getTaxAmount().toString());
        tax.setFont(Font.font("Arial", 12));
        
        Text totalLabel = new Text("TOTAL:");
        totalLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        Text total = new Text("$" + invoice.getTotalAmount().toString());
        total.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        
        totalsTable.add(subtotalLabel, 0, 0);
        totalsTable.add(subtotal, 1, 0);
        totalsTable.add(taxLabel, 0, 1);
        totalsTable.add(tax, 1, 1);
        totalsTable.add(totalLabel, 0, 2);
        totalsTable.add(total, 1, 2);
        
        totals.getChildren().add(totalsTable);
        return totals;
    }

    private VBox createFooter() {
        VBox footer = new VBox(20);
        footer.setAlignment(Pos.CENTER);
        
        Separator separator = new Separator();
        
        Text thankYou = new Text("Thank you for choosing Grand Hotel!");
        thankYou.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        thankYou.setTextAlignment(TextAlignment.CENTER);
        
        Text contact = new Text("For any questions regarding this invoice, please contact us at:\nPhone: (555) 123-4567 | Email: billing@grandhotel.com");
        contact.setFont(Font.font("Arial", 10));
        contact.setTextAlignment(TextAlignment.CENTER);
        
        footer.getChildren().addAll(separator, thankYou, contact);
        return footer;
    }

    private String getStatusColor(String status) {
        switch (status.toUpperCase()) {
            case "PAID":
                return "#4CAF50"; // Green
            case "PENDING":
                return "#FF9800"; // Orange
            case "OVERDUE":
                return "#f44336"; // Red
            case "CANCELLED":
                return "#9E9E9E"; // Gray
            default:
                return "#000000"; // Black
        }
    }

    private void printInvoice() {
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null) {
            boolean showDialog = job.showPrintDialog(primaryStage.getOwner());
            if (showDialog) {
                boolean success = job.printPage(invoiceContent);
                if (success) {
                    job.endJob();
                    showAlert("Success", "Invoice printed successfully!");
                } else {
                    showAlert("Error", "Failed to print invoice.");
                }
            }
        } else {
            showAlert("Error", "No printer available.");
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
