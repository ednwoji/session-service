package com.unit.session.servicesimpl;

import com.google.maps.model.LatLng;
import com.unit.session.Utilities.Utils;
import com.unit.session.dto.SpaceDto;
import com.unit.session.dto.UsersDto;
import com.unit.session.entities.SpaceTypes;
import com.unit.session.entities.Spaces;
import com.unit.session.entities.Users;
import com.unit.session.repositories.SpaceRepository;
import com.unit.session.services.SpaceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class SpaceServiceImpl implements SpaceService {

    @Autowired
    private SpaceRepository spaceRepository;

    @Autowired
    private Utils utils;

    @Override
    public Spaces saveSpace(SpaceDto spaceDto) {
        if(spaceDto.getSpaceImage() == null || spaceDto.getSpaceType() == null || spaceDto.getSpaceLocation() == null) {
            throw new RuntimeException("Please fill all details");
        }

        Spaces spaces1 = null;
        Users users = utils.validateUserId(spaceDto.getUserId());
        LatLng result = utils.getLatLng(spaceDto.getSpaceLocation());
        log.info("Result is "+result.toString());
        if (users != null) {
            Spaces spaces = new Spaces();
            spaces.setSpaceOwner(users);
            spaces.setSpaceImage(spaceDto.getSpaceImage());
            spaces.setSpaceLocation(spaceDto.getSpaceLocation());
            spaces.setSpaceType(SpaceTypes.valueOf(spaceDto.getSpaceType()));
            spaces.setLng(result.lng);
            spaces.setLat(result.lat);
            spaces.setChargePerDay(Double.parseDouble(spaceDto.getChargePerDay()));
            spaces.setDescription(spaceDto.getDescription());

            spaces1 = spaceRepository.save(spaces);
        }
        return spaces1;
    }

    @Override
    public List<Spaces> findSpaceByUser(UsersDto usersDto) {
        Users user = utils.validateUserId(usersDto.getUserId());
        if(user != null) {
            return spaceRepository.findBySpaceOwner(user);
        }

        return null;
    }

    @Override
    public Spaces findSpaceBySpaceId(String spaceId) {
        return spaceRepository.findBySpaceId(Long.parseLong(spaceId)).orElse(null);
    }
}
