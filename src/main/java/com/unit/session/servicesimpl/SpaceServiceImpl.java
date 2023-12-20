package com.unit.session.servicesimpl;

import com.google.maps.model.LatLng;
import com.unit.session.Utilities.Utils;
import com.unit.session.dto.SpaceDto;
import com.unit.session.dto.UsersDto;
import com.unit.session.entities.*;
import com.unit.session.repositories.BookedSpacesRepository;
import com.unit.session.repositories.SpaceRepository;
import com.unit.session.services.SpaceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class SpaceServiceImpl implements SpaceService {

    @Autowired
    private SpaceRepository spaceRepository;

    @Autowired
    private BookedSpacesRepository bookedSpacesRepository;

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
                spaces.setSpaceType(spaceDto.getSpaceType());
                spaces.setLng(result.lng);
                spaces.setLat(result.lat);
                spaces.setChargePerDay(Double.parseDouble(spaceDto.getChargePerDay()));
                spaces.setDescription(spaceDto.getDescription());
                spaces.setSize(spaceDto.getSize());
                spaces.setMusicDetails(spaceDto.getMusicDetails());
                spaces.setPractice(spaceDto.getPractice());
                spaces.setVisitEndTime(spaceDto.getVisitEndTime());
                spaces.setVisitStartTime(spaceDto.getVisitStartTime());
                spaces.setVisitDays(spaceDto.getVisitDays());
                spaces.setAdditionalDetails(spaceDto.getAdditionalDetails());
                spaces.setDateAdded(LocalDateTime.now());

                log.info("Spaces to be saved is " + spaces.toString());
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
        return spaceRepository.findBySpaceIdAndBookingStatus(Long.parseLong(spaceId), Booking.PENDING).orElse(null);
    }

    @Override
    public void updateSpaceBookingStatus(Spaces spaces, SpaceDto spaceDto) {
        spaces.setBookingStatus(Booking.valueOf(spaceDto.getBookingStatus()));
        Spaces newSpace = spaceRepository.save(spaces);
        bookSpaceForTenant(newSpace, spaceDto);
    }

    @Override
    public List<BookedSpaces> findAllBookedSpacesForTenants(String userId) {
        return bookedSpacesRepository.findByBookedBy(utils.validateUserId(userId));
    }

    public void bookSpaceForTenant(Spaces spaces, SpaceDto spaceDto) {

        log.info("Booking spaces for tenant:::");
        BookedSpaces bookedSpaces = new BookedSpaces();
        bookedSpaces.setBookedBy(utils.validateUserId(spaceDto.getUserId()));
        bookedSpaces.setSpaceId(spaces);
        bookedSpaces.setSpaceOwner(spaces.getSpaceOwner());
        bookedSpaces.setBookedTime(LocalDateTime.now());
        bookedSpaces.setExpiryDate(LocalDateTime.now().plusDays(1L));
        bookedSpaces.setDuration(1);

        bookedSpacesRepository.save(bookedSpaces);

    }
}
