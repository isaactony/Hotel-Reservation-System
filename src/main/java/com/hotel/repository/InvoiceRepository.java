package com.hotel.repository;

import com.hotel.entity.Invoice;
import com.hotel.entity.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByReservationId(Long reservationId);
    List<Invoice> findByReservationVisitorId(Long visitorId);
    List<Invoice> findByStatus(InvoiceStatus status);
    List<Invoice> findByDueDateBeforeAndStatus(LocalDateTime dueDate, InvoiceStatus status);
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
}
