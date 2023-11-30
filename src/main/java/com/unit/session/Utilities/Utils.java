package com.unit.session.Utilities;


import com.google.maps.model.LatLng;
import com.unit.session.entities.Users;
import com.unit.session.repositories.*;
import com.unit.session.servicesimpl.GeoCodingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Service
public class Utils {

    @Autowired
    private SpaceRepository spaceRepository;
    @Autowired
    private UsersRepository userRepository;

    @Autowired
    private GeoCodingService geoCodingService;


    public Users validateUserId(String userId) {
      Optional<Users> users =  userRepository.findByUserId(Long.valueOf(userId));
      return users.orElse(null);
    }

    public LatLng getLatLng(String address) {
        try {
            return geoCodingService.getLatLngFromAddress(address);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
