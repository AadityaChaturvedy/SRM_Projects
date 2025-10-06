package com.railway.booking.model;

public class Train {
    private int id;
    private String name;
    private String source;
    private String destination;
    private String date;
    private String departureTime;
    private int seats;
    private double price;
    private String via;

    public int getId() { return id; }
    public String getName() { return name; }
    public String getSource() { return source; }
    public String getDestination() { return destination; }
    public String getDate() { return date; }
    public String getDepartureTime() { return departureTime; }
    public int getSeats() { return seats; }
    public double getPrice() { return price; }
    public String getVia() { return via; }

    public void setVia(String via) { this.via = via; }
}
