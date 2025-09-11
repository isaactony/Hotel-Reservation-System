package com.hotel.controller;

import com.hotel.dto.InvoiceResponse;
import com.hotel.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/invoices")
@CrossOrigin(origins = "*")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @PostMapping("/reservation/{reservationId}")
    public ResponseEntity<InvoiceResponse> createInvoiceForReservation(@PathVariable Long reservationId) {
        try {
            invoiceService.createInvoiceForReservation(reservationId);
            Optional<InvoiceResponse> invoice = invoiceService.getInvoiceByReservationId(reservationId);
            return invoice.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.badRequest().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/visitor/{visitorId}")
    public ResponseEntity<List<InvoiceResponse>> getInvoicesByVisitorId(@PathVariable Long visitorId) {
        try {
            List<InvoiceResponse> invoices = invoiceService.getInvoicesByVisitorId(visitorId);
            return ResponseEntity.ok(invoices);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/{invoiceId}")
    public ResponseEntity<InvoiceResponse> getInvoiceById(@PathVariable Long invoiceId) {
        Optional<InvoiceResponse> invoice = invoiceService.getInvoiceById(invoiceId);
        return invoice.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/reservation/{reservationId}")
    public ResponseEntity<InvoiceResponse> getInvoiceByReservationId(@PathVariable Long reservationId) {
        Optional<InvoiceResponse> invoice = invoiceService.getInvoiceByReservationId(reservationId);
        return invoice.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{invoiceId}/pay")
    public ResponseEntity<Map<String, String>> markInvoiceAsPaid(@PathVariable Long invoiceId) {
        try {
            invoiceService.markAsPaid(invoiceId);
            Map<String, String> response = Map.of("message", "Invoice marked as paid");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<InvoiceResponse>> getOverdueInvoices() {
        try {
            List<InvoiceResponse> overdueInvoices = invoiceService.getOverdueInvoices();
            return ResponseEntity.ok(overdueInvoices);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<InvoiceResponse>> getAllInvoices() {
        try {
            List<InvoiceResponse> invoices = invoiceService.getAllInvoices();
            return ResponseEntity.ok(invoices);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
