package com.railway.booking.model;

import jakarta.persistence.*;

@Entity
@Table(name = "trains")
public class Train {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String source;
    private String destination;
    private String date;
    private String departureTime;
    private Integer seats;
    private Double price;
    @Column(name = "via")
    private String via;

    public Train() {}
    public Train(String name, String source, String destination, String date, String departureTime, Integer seats, Double price, String via) {
        this.name = name;
        this.source = source;
        this.destination = destination;
        this.date = date;
        this.departureTime = departureTime;
        this.seats = seats;
        this.price = price;
        this.via = via;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getDepartureTime() { return departureTime; }
    public void setDepartureTime(String departureTime) { this.departureTime = departureTime; }
    public Integer getSeats() { return seats; }
    public void setSeats(Integer seats) { this.seats = seats; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public String getVia() { return via; }
    public void setVia(String via) { this.via = via; }
}
