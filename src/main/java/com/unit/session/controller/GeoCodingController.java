package com.unit.session.controller;

import com.google.maps.model.LatLng;
import com.unit.session.dto.LocationDto;
import com.unit.session.entities.Spaces;
import com.unit.session.servicesimpl.GeoCodingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/map")
@CrossOrigin(origins = "*")
@Slf4j
public class GeoCodingController {

    @Autowired
    private GeoCodingService geocodingService;

    @GetMapping("/getLatLng")
    public LatLng getLatLng(@RequestParam String address) {
        try {
            return geocodingService.getLatLngFromAddress(address);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @PostMapping("/getnearestlocations")
    public List<Spaces> getLocationsAround(@RequestBody LocationDto locationDto) {
        log.info("Incoming payload to get locations around is "+locationDto.toString());
        return geocodingService.findNearestLocations(locationDto.getLatitude(), locationDto.getLongitude());
    }
}
