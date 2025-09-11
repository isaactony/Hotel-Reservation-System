package com.hotel.service;

import com.hotel.dto.InvoiceResponse;
import com.hotel.entity.Invoice;
import com.hotel.entity.InvoiceStatus;
import com.hotel.entity.Reservation;
import com.hotel.repository.InvoiceRepository;
import com.hotel.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    public Invoice createInvoiceForReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        // Check if invoice already exists for this reservation
        Optional<Invoice> existingInvoice = invoiceRepository.findByReservationId(reservationId);
        if (existingInvoice.isPresent()) {
            return existingInvoice.get();
        }

        Invoice invoice = new Invoice(reservation, reservation.getTotalAmount());
        return invoiceRepository.save(invoice);
    }

    public List<InvoiceResponse> getInvoicesByVisitorId(Long visitorId) {
        List<Invoice> invoices = invoiceRepository.findByReservationVisitorId(visitorId);
        return invoices.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public Optional<InvoiceResponse> getInvoiceById(Long invoiceId) {
        return invoiceRepository.findById(invoiceId)
                .map(this::convertToResponse);
    }

    public Optional<InvoiceResponse> getInvoiceByReservationId(Long reservationId) {
        return invoiceRepository.findByReservationId(reservationId)
                .map(this::convertToResponse);
    }

    public Invoice markAsPaid(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        invoice.setStatus(InvoiceStatus.PAID);
        invoice.setPaidAt(LocalDateTime.now());
        return invoiceRepository.save(invoice);
    }

    public List<InvoiceResponse> getOverdueInvoices() {
        List<Invoice> overdueInvoices = invoiceRepository.findByDueDateBeforeAndStatus(
                LocalDateTime.now(), InvoiceStatus.PENDING);
        
        // Mark overdue invoices
        overdueInvoices.forEach(invoice -> {
            invoice.setStatus(InvoiceStatus.OVERDUE);
            invoiceRepository.save(invoice);
        });

        return overdueInvoices.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<InvoiceResponse> getAllInvoices() {
        List<Invoice> invoices = invoiceRepository.findAll();
        return invoices.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private InvoiceResponse convertToResponse(Invoice invoice) {
        Reservation reservation = invoice.getReservation();
        return new InvoiceResponse(
                invoice.getId(),
                invoice.getInvoiceNumber(),
                reservation.getId(),
                reservation.getRoom().getRoomNumber(),
                reservation.getRoom().getRoomType(),
                reservation.getCheckInDate().atStartOfDay(),
                reservation.getCheckOutDate().atStartOfDay(),
                reservation.getNumberOfGuests(),
                invoice.getSubtotal(),
                invoice.getTaxRate(),
                invoice.getTaxAmount(),
                invoice.getTotalAmount(),
                invoice.getStatus().toString(),
                invoice.getCreatedAt(),
                invoice.getDueDate(),
                invoice.getPaidAt(),
                invoice.getNotes(),
                reservation.getVisitor().getFirstName() + " " + reservation.getVisitor().getLastName(),
                reservation.getVisitor().getEmail()
        );
    }
}