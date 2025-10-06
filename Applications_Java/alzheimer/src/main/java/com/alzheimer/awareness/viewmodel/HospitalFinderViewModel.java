package com.alzheimer.awareness.viewmodel;

import com.alzheimer.awareness.model.Hospital;
import com.alzheimer.awareness.service.HospitalFinderService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class HospitalFinderViewModel {
    private HospitalFinderService hospitalService;
    private List<Hospital> searchResults;
    private String lastSearchLocation;

    public HospitalFinderViewModel() {
        this.hospitalService = new HospitalFinderService();
        this.searchResults = new ArrayList<>();
    }

    public List<Hospital> searchHospitals(String location) {
        lastSearchLocation = location;

        if (location.matches("\\d{6}")) {
            searchResults = hospitalService.searchHospitalsByPincode(location);
        } else {
            searchResults = hospitalService.searchHospitalsByPincode("000000");
        }

        // Sort by distance
        searchResults.sort(Comparator.comparing(Hospital::getDistanceKm));

        return new ArrayList<>(searchResults);
    }

    public List<Hospital> getSearchResults() {
        return new ArrayList<>(searchResults);
    }

    public List<Hospital> filterByDistance(double maxDistanceKm) {
        return searchResults.stream()
                .filter(hospital -> hospital.getDistanceKm() <= maxDistanceKm)
                .collect(Collectors.toList());
    }

    public List<Hospital> sortByName() {
        return searchResults.stream()
                .sorted(Comparator.comparing(Hospital::getName))
                .collect(Collectors.toList());
    }

    public String getLastSearchLocation() {
        return lastSearchLocation;
    }

    public boolean hasResults() {
        return !searchResults.isEmpty();
    }

    public int getResultCount() {
        return searchResults.size();
    }
}
