package com.unit.session.controller;


import com.unit.session.dto.CustomResponse;
import com.unit.session.dto.Responses;
import com.unit.session.dto.SpaceDto;
import com.unit.session.dto.UsersDto;
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
@CrossOrigin(origins = "*")
@Slf4j
public class SpaceController {


    @Autowired
    private SpaceService spaceService;

    @PostMapping("/add-space")
    public ResponseEntity<?> addSpace(@RequestBody SpaceDto spaceDto, CustomResponse customResponse) {

        log.info("Incoming payload for spaces "+spaceDto.toString());
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
        log.info("Retrieved Space is "+allSpaces.get(0).getSpaceLocation());
        return new ResponseEntity<>(allSpaces, HttpStatus.OK);
    }


    @PostMapping("/findById")
    public ResponseEntity<?> findAllSpacesBySpaceId(@RequestBody SpaceDto spaceDto) {
        log.info("Incoming payload for spaces is "+spaceDto.toString());
        Spaces space = spaceService.findSpaceBySpaceId(spaceDto.getSpaceId());
        log.info("Retrieved space for the ID is "+space.getSpaceLocation());
        return new ResponseEntity<>(space, HttpStatus.OK);
    }
}
