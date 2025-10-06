package com.railway.booking.controller;

import com.railway.booking.model.*;
import com.railway.booking.repository.*;
import com.railway.booking.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.transaction.Transactional;

import java.util.*;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class RailwayController {

    @Autowired UserRepository userRepo;
    @Autowired TrainRepository trainRepo;
    @Autowired BookingRepository bookingRepo;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserDTO req) {
        if (userRepo.findByUsername(req.getUsername()).isPresent())
            return ResponseEntity.ok(Map.of("success", false, "msg", "Username exists"));

        User user = new User();
        user.setUsername(req.getUsername());
        user.setPassword(req.getPassword());
        userRepo.save(user);

        return ResponseEntity.ok(Map.of("success", true));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDTO req) {
        Optional<User> user = userRepo.findByUsernameAndPassword(req.getUsername(), req.getPassword());
        return ResponseEntity.ok(
            user.map(u -> Map.of("success", true, "user_id", u.getId()))
                .orElse(Map.of("success", false))
        );
    }

    @GetMapping("/trains")
    public List<Train> getTrains(
        @RequestParam(defaultValue="") String from,
        @RequestParam(defaultValue="") String to
    ) {
        return trainRepo.findBySourceContainingIgnoreCaseAndDestinationContainingIgnoreCase(from, to);
    }

    @PostMapping("/train/add")
    public ResponseEntity<?> addTrain(@RequestBody Train train) {
        trainRepo.save(train);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @PostMapping("/book")
    @Transactional
    public ResponseEntity<?> book(@RequestBody BookingRequest req) {
        Optional<Train> otrain = trainRepo.findById(req.getTrainId());
        Optional<User> ouser = userRepo.findById(req.getUserId());
        if (otrain.isEmpty() || ouser.isEmpty())
            return ResponseEntity.ok(Map.of("success", false, "msg", "Train or user missing."));
        Train train = otrain.get();
        if (train.getSeats() < req.getSeats())
            return ResponseEntity.ok(Map.of("success", false, "msg", "Not enough seats available."));
        
        double priceMultiplier = switch (req.getTrainClass()) {
            case "AC" -> 1.8;
            case "First Class" -> 3.2;
            default -> 1.0;
        };
        double finalPrice = train.getPrice() * req.getSeats() * priceMultiplier;

        train.setSeats(train.getSeats() - req.getSeats());
        trainRepo.save(train);
        
        Booking booking = new Booking();
        booking.setUser(ouser.get());
        booking.setTrain(train);
        booking.setStatus("CONFIRMED");
        booking.setSeats(req.getSeats());
        booking.setTrainClass(req.getTrainClass());
        booking.setPaymentMethod(req.getPaymentMethod());
        booking.setPaymentDetails(req.getPaymentDetails());
        booking.setPrice(finalPrice);
        bookingRepo.save(booking);
        String pnr = "PNR" + booking.getId() + System.currentTimeMillis();
        booking.setPnr(pnr);
        bookingRepo.save(booking);
        return ResponseEntity.ok(Map.of("success", true, "pnr", pnr));
    }

    @GetMapping("/history/{userId}")
    public List<Map<String, Object>> userHistory(@PathVariable Integer userId) {
        Optional<User> ouser = userRepo.findById(userId);
        if (ouser.isEmpty()) return List.of();
        List<Booking> bookings = bookingRepo.findByUserOrderByTrain_DateDesc(ouser.get());
        List<Map<String, Object>> out = new ArrayList<>();
        for (Booking b : bookings) {
            Map<String, Object> entry = new HashMap<>();
            Train train = b.getTrain();
            entry.put("id", b.getId());
            entry.put("status", b.getStatus());
            entry.put("source", train.getSource());
            entry.put("destination", train.getDestination());
            entry.put("date", train.getDate());
            entry.put("train_id", train.getId());
            entry.put("train_name", train.getName());
            entry.put("departure_time", train.getDepartureTime());
            entry.put("price", b.getPrice());
            entry.put("seats", b.getSeats());
            entry.put("trainClass", b.getTrainClass());
            entry.put("paymentMethod", b.getPaymentMethod());
            out.add(entry);
        }
        return out;
    }

        @PostMapping("/cancel")
    @Transactional
    public ResponseEntity<?> cancel(@RequestBody Map<String, Object> req) {
        Optional<Booking> ob = bookingRepo.findById((Integer) req.get("booking_id"));
        if (ob.isEmpty())
            return ResponseEntity.ok(Map.of("success", false, "msg", "Booking not found"));
        Booking booking = ob.get();
        if (!"CONFIRMED".equals(booking.getStatus()))
            return ResponseEntity.ok(Map.of("success", false, "msg", "Already cancelled"));
        booking.setStatus("CANCELLED");
        bookingRepo.save(booking);
        Optional<Train> otrain = trainRepo.findById(booking.getTrain().getId());
        otrain.ifPresent(t -> {
            t.setSeats(t.getSeats() + booking.getSeats());
            trainRepo.save(t);
        });
        return ResponseEntity.ok(Map.of("success", true));
    }

    @GetMapping("/export/{userid}")
    public ResponseEntity<String> exportBookings(@PathVariable("userid") int userId) {
        Optional<User> ouser = userRepo.findById(userId);
        if (ouser.isEmpty()) {
            return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"booking_history.csv\"")
                .body("No bookings found.");
        }
        List<Booking> bookings = bookingRepo.findByUserOrderByTrain_DateDesc(ouser.get());
        StringBuilder csv = new StringBuilder("From,To,Date,Status\n");
        for (Booking b : bookings) {
            Train tr = b.getTrain();
            if(tr == null) continue;
        csv.append(tr.getSource()).append(",");
        csv.append(tr.getDestination()).append(",");
        csv.append(tr.getDate()).append(",");
        csv.append(b.getStatus()).append("\n");
    }
    return ResponseEntity.ok()
        .header("Content-Disposition", "attachment; filename=\"booking_history.csv\"")
        .body(csv.toString());
}
}
