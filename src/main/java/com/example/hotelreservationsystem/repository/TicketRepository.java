package com.example.hotelreservationsystem.repository;

import com.example.hotelreservationsystem.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket,Long> {
}
