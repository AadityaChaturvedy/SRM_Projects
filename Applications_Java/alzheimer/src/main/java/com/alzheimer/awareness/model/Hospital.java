package com.alzheimer.awareness.model;

public class Hospital {
    private String name;
    private String address;
    private String phone;
    private double distanceKm;
    private String specialization;

    public Hospital() {}

    public Hospital(String name, String address, String phone, double distanceKm) {
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.distanceKm = distanceKm;
        this.specialization = "Neurology/Memory Care";
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(double distanceKm) { this.distanceKm = distanceKm; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    @Override
    public String toString() {
        return String.format("%s\n%s\nPhone: %s\nDistance: %.1f km", 
                           name, address, phone, distanceKm);
    }
}
