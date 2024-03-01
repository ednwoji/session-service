package com.unit.session.controller;


import com.unit.session.dto.CustomResponse;
import com.unit.session.dto.Responses;
import com.unit.session.dto.SpaceDto;
import com.unit.session.dto.UsersDto;
import com.unit.session.entities.BookedSpaces;
import com.unit.session.entities.SpaceImages;
import com.unit.session.entities.Spaces;
import com.unit.session.services.SpaceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/spaces")
@CrossOrigin
@Slf4j
public class SpaceController {


    @Autowired
    private SpaceService spaceService;

    @PostMapping("/add-space")
    public ResponseEntity<?> addSpace(@RequestBody SpaceDto spaceDto, CustomResponse customResponse) {

        log.info("Incoming payload for spaces creation::::::::: ");

        try {
            Spaces spaces = spaceService.saveSpace(spaceDto);
            customResponse = new CustomResponse(Responses.SPACE_CREATED.getCode(), Responses.SPACE_CREATED.getMessage());
            return new ResponseEntity<>(customResponse, HttpStatus.OK);
        }catch (Exception e){
            customResponse = new CustomResponse(Responses.UNEXPECTED_ERROR.getCode(), e.getMessage());
            return new ResponseEntity<>(customResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/getSpaces")
    public ResponseEntity<?> findAllSpacesByUserId(@RequestBody UsersDto usersDto) {
        log.info("Incoming payload for space retrieval is "+usersDto.toString());
        List<Spaces> allSpaces = spaceService.findSpaceByUser(usersDto);
        if(!allSpaces.isEmpty()) {
            log.info("Retrieved Space is "+allSpaces.get(0).getSpaceLocation());
            return new ResponseEntity<>(allSpaces, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @PostMapping("/getSpaceImages")
    public ResponseEntity<?> findAllSpaceImagesByUserId(@RequestBody UsersDto usersDto) {
        log.info("Incoming payload for space retrieval is "+usersDto.toString());
        List<SpaceImages> allSpaces = spaceService.findSpaceImagesByUser(usersDto);
        if(!allSpaces.isEmpty()) {
            log.info("Retrieved Space is "+allSpaces.get(0).getSpaceImageId());
            return new ResponseEntity<>(allSpaces, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }




    @PostMapping("/findById")
    public ResponseEntity<?> findAllPendingSpacesBySpaceId(@RequestBody SpaceDto spaceDto) {
        log.info("Incoming payload for spaces is "+spaceDto.toString());
        Spaces space = spaceService.findSpaceBySpaceId(spaceDto.getSpaceId());
        log.info("Retrieved space for the ID is "+space.getSpaceLocation());
        return new ResponseEntity<>(space, HttpStatus.OK);
    }


    @PostMapping("/findBookedById")
    public ResponseEntity<?> findAllBookedSpacesBySpaceId(@RequestBody SpaceDto spaceDto) {
        log.info("Incoming payload for spaces is "+spaceDto.toString());
        Spaces space = spaceService.findBookedSpaceBySpaceId(spaceDto.getSpaceId());
        log.info("Retrieved space for the ID is "+space.getSpaceLocation());
        return new ResponseEntity<>(space, HttpStatus.OK);
    }


    @PostMapping("/book-space")
    public ResponseEntity<?> updateBookingStatusOfSpace(@RequestBody SpaceDto spaceDto, CustomResponse customResponse) {
        log.info("Incoming payload for updating spaces is "+spaceDto.toString());
        Spaces spaces = spaceService.findSpaceBySpaceId(spaceDto.getSpaceId());
        if(spaces != null) {
            spaceService.updateSpaceBookingStatus(spaces, spaceDto);
            customResponse = new CustomResponse(Responses.SPACE_CREATED.getCode(), Responses.SPACE_CREATED.getMessage());
            return new ResponseEntity<>(customResponse, HttpStatus.OK);
        }

        customResponse = new CustomResponse(Responses.UNEXPECTED_ERROR.getCode(), Responses.UNEXPECTED_ERROR.getMessage());
        return new ResponseEntity<>(customResponse, HttpStatus.INTERNAL_SERVER_ERROR);


    }


    @PostMapping("/getbookedspaces")
    public ResponseEntity<?> getBookedSpacesForTenants(@RequestBody UsersDto usersDto) {
        log.info("Incoming payload to get list of booked spaces for tenants:: "+usersDto.toString());
        List<BookedSpaces> spaces = spaceService.findAllBookedSpacesForTenants(usersDto.getUserId());
        return new ResponseEntity<>(spaces, HttpStatus.OK);
    }


    @PostMapping("/deleteById")
    public ResponseEntity<?> deleteSpaceById(@RequestBody SpaceDto spaceDto) {
        log.info("Deleting space with ID "+spaceDto.getSpaceId());
        spaceService.deleteSpaceById(spaceDto.getSpaceId());
        return new ResponseEntity<>("Deleted", HttpStatus.OK);
    }

    @PostMapping("/updateRulesById")
    public ResponseEntity<?> updateRulesById(@RequestBody SpaceDto spaceDto) {
        log.info("Updating space with ID "+spaceDto.getSpaceId());
        Spaces spaces = spaceService.updateRules(spaceDto);
        return new ResponseEntity<>(spaces, HttpStatus.OK);
    }

    @PostMapping("/updateLocationById")
    public ResponseEntity<?> updateSpaceLocationById(@RequestBody SpaceDto spaceDto) {
        log.info("Updating space with ID "+spaceDto.getSpaceId());
        Spaces spaces = spaceService.updateLocation(spaceDto);
        return new ResponseEntity<>(spaces, HttpStatus.OK);
    }

    @PostMapping("/updateUrlById")
    public ResponseEntity<?> updateYoutubeUrl(@RequestBody SpaceDto spaceDto) {
        log.info("Updating space with ID "+spaceDto.getSpaceId());
        Spaces spaces = spaceService.updateYoutube(spaceDto);
        return new ResponseEntity<>(spaces, HttpStatus.OK);
    }


    @PostMapping("/updateSpaceImages")
    public ResponseEntity<?> updateSpaceImages(@RequestBody SpaceDto spaceDto, CustomResponse customResponse) {
        log.info("Incoming request to change image is "+spaceDto.toString());
        try {
            Spaces spaces = spaceService.updateSpaceImages(spaceDto);
            customResponse = new CustomResponse(Responses.SPACE_CREATED.getCode(), Responses.SPACE_CREATED.getMessage());
            return new ResponseEntity<>(customResponse, HttpStatus.OK);
        }catch (Exception e){
            log.info("Exception is "+e.getMessage());
            customResponse = new CustomResponse(Responses.UNEXPECTED_ERROR.getCode(), e.getMessage());
            return new ResponseEntity<>(customResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/filterspacesbyPreference")
    public ResponseEntity<List<Spaces>> findSpacesByPreference(@RequestBody SpaceDto spaceDto) {

        log.info("Ïncoming request to filter spaces "+spaceDto);
        List<Spaces> allSpaces = spaceService.filterSpacesByPreference(spaceDto);
        log.info("Back here::::::");
        if(!allSpaces.isEmpty()) {
            log.info("First space location is "+allSpaces.get(0));
        }
        return new ResponseEntity<>(allSpaces, HttpStatus.OK);
    }

    @PostMapping("/removeSpace")
    public String deleteSpace(@RequestBody SpaceDto spaceDto) {
        spaceService.removeSpaceById(spaceDto.getSpaceId());
        return "Deleted";
    }
}

