package com.chunchun.staybooking.repository;

import com.chunchun.staybooking.model.Reservation;
import com.chunchun.staybooking.model.Stay;
import com.chunchun.staybooking.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByGuest(User guest);

    List<Reservation> findByStay(Stay stay);

    Reservation findByIdAndGuest(Long id, User guest); // for deletion

    List<Reservation> findByStayAndCheckoutDateAfter(Stay stay, LocalDate date);

}

