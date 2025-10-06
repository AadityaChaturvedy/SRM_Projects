package com.railway.booking.repository;

import com.railway.booking.model.Booking;
import com.railway.booking.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findByUserOrderByTrain_DateDesc(User user);
}
