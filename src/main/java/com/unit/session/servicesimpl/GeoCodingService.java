package com.unit.session.servicesimpl;


import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.AddressComponentType;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.GeolocationResult;
import com.google.maps.model.LatLng;
import com.unit.session.dto.Location;
import com.unit.session.entities.Booking;
import com.unit.session.entities.SpaceImages;
import com.unit.session.entities.Spaces;
import com.unit.session.repositories.SpaceImagesRepository;
import com.unit.session.repositories.SpaceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class GeoCodingService {

    @Autowired
    private SpaceRepository spaceRepository;

    @Autowired
    private SpaceImagesRepository spaceImagesRepository;

    @Value("${google.api.key}")
    private String apiKey;

    public LatLng getLatLngFromAddress(String address) throws Exception {
        GeoApiContext context = new GeoApiContext.Builder().apiKey(apiKey).build();
        GeocodingResult[] results = GeocodingApi.geocode(context, address).await();
        if (results != null && results.length > 0) {
            return results[0].geometry.location;
        } else {
            throw new RuntimeException("No results found for the given address");
        }
    }

    public List<Spaces> findSpacesByAddress(String address) throws Exception {
        LatLng geolocationResult = getLatLngFromAddress(address);
        double targetLatitude = geolocationResult.lat;
        double targetLongitude = geolocationResult.lng;
        log.info("Latitude and Longitude is ::"+targetLatitude+" and "+targetLongitude);
        List<Spaces> allSpaces = spaceRepository.findByActive(true);
        //List<Spaces> allSpaces = spaceRepository.findAll();
        return allSpaces.stream()
                .sorted(Comparator.comparingDouble(space ->
                        calculateHaversineDistance(targetLatitude, targetLongitude, space.getLat(), space.getLng())))
                .collect(Collectors.toList());
    }


    public List<Spaces> findSpacesByAddressWithRadius(String address) throws Exception {
        LatLng geolocationResult = getLatLngFromAddress(address);
        Location stateCountry = getStateAndCountryFromLatLng(geolocationResult.lat, geolocationResult.lng);
        double targetLatitude = geolocationResult.lat;
        double targetLongitude = geolocationResult.lng;
        double radius = 100;
        log.info("Latitude and Longitude is ::"+targetLatitude+" and "+targetLongitude);
//        List<Spaces> spacesAroundAddress = new ArrayList<>();
        List<Spaces> allSpaces = spaceRepository.findByActive(true);

        List<Spaces> spacesAroundAddress = allSpaces.parallelStream()
                .filter(space -> {
                    Location spaceStateCountry = null;
                    try {
                        spaceStateCountry = getStateAndCountryFromLatLng(space.getLat(), space.getLng());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    return spaceStateCountry.getState() != null &&
                            stateCountry.getState() != null &&
                            spaceStateCountry.getState().equalsIgnoreCase(stateCountry.getState());
                })
                .collect(Collectors.toList());

        return spacesAroundAddress;
    }


//    public List<Spaces> findSpacesByAddressWithRadius(String address) throws Exception {
//        LatLng geolocationResult = getLatLngFromAddress(address);
//        String stateCountry = getStateAndCountryFromLatLng(geolocationResult.lat, geolocationResult.lng);
//        double targetLatitude = geolocationResult.lat;
//        double targetLongitude = geolocationResult.lng;
//        double radius = 100;
//        log.info("Latitude and Longitude is ::"+targetLatitude+" and "+targetLongitude);
//
//        List<Spaces> allSpaces = spaceRepository.findByActive(true);
//        List<Spaces> spacesAroundAddress = allSpaces.stream()
//                .filter(space ->
//                        isWithinRadius(targetLatitude, targetLongitude, space.getLat(), space.getLng(), radius))
//                .collect(Collectors.toList());
//
//        return spacesAroundAddress;
//    }

    private boolean isWithinRadius(double targetLat, double targetLng, double spaceLat, double spaceLng, double radius) {
        // Haversine distance calculation
        double distance = calculateHaversineDistance(targetLat, targetLng, spaceLat, spaceLng);
        return distance <= radius;
    }



    public List<Spaces> findNearestLocations(double currentLatitude, double currentLongitude) {
//        List<Spaces> allLocations = spaceRepository.findByBookingStatus(Booking.PENDING);
        List<Spaces> allLocations = spaceRepository.findByActive(true);
        return calculateDistancesAndSort(allLocations, currentLatitude, currentLongitude);
    }

    private List<Spaces> calculateDistancesAndSort(List<Spaces> locations, double currentLatitude, double currentLongitude) {
        // Implement Haversine formula and sorting logic
        return locations.stream()
                .peek(location -> location.setDistance(calculateHaversineDistance(
                        currentLatitude, currentLongitude,
                        location.getLat(), location.getLng())))
                .sorted(Comparator.comparingDouble(Spaces::getDistance))
                .collect(Collectors.toList());
    }


    private double calculateHaversineDistance(double startLat, double startLon, double endLat, double endLon) {
        final int R = 6371; // Radius of the Earth in kilometers
        double dLat = Math.toRadians(endLat - startLat);
        double dLon = Math.toRadians(endLon - startLon);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(startLat)) * Math.cos(Math.toRadians(endLat)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }



    private Location getStateAndCountryFromLatLng(double lat, double lng) throws Exception {
        GeoApiContext context = new GeoApiContext.Builder().apiKey(apiKey).build();
        GeocodingResult[] results = GeocodingApi.reverseGeocode(context, new com.google.maps.model.LatLng(lat, lng)).await();

        String state = null;
        Location location = new Location();
        for (GeocodingResult result : results) {
            for (com.google.maps.model.AddressComponent addressComponent : result.addressComponents) {
                for (AddressComponentType type : addressComponent.types) {
                    if (type == AddressComponentType.ADMINISTRATIVE_AREA_LEVEL_1) {
                        state = addressComponent.longName;
                        location.setState(state);
                        // You can also use addressComponent.shortName for abbreviated state name
                    }
                    if (type == AddressComponentType.COUNTRY) {
                        String country = addressComponent.longName;
                        location.setCountry(country);
                        log.info(state + ", " + country);
                        return location;
                    }
                }
            }
        }
        return null;
    }
}
