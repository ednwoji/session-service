package com.unit.session.controller;

import com.unit.session.Utilities.Utils;
import com.unit.session.dto.CustomResponse;
import com.unit.session.dto.Responses;
import com.unit.session.dto.SpaceDto;
import com.unit.session.dto.UsersDto;
import com.unit.session.entities.Spaces;
import com.unit.session.entities.Users;
import com.unit.session.servicesimpl.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/users")
@RestController
@Slf4j
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;


    @Autowired
    private Utils utils;

    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody UsersDto userDto, CustomResponse customResponse) {

        log.info("Payload is "+userDto.toString());
        try {

            Users users = userService.createNewUser(userDto);
            customResponse = new CustomResponse(Responses.USER_CREATED.getCode(), Responses.USER_CREATED.getMessage());
            return new ResponseEntity<>(customResponse, HttpStatus.OK);
        }
        catch(Exception e) {
            customResponse = new CustomResponse(Responses.UNEXPECTED_ERROR.getCode(), e.getMessage());
            return new ResponseEntity<>(customResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> validateUser(@RequestBody UsersDto usersDto, CustomResponse customResponse) {

        log.info("payload is "+usersDto.toString());
        try{
             authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                     usersDto.getEmail(), usersDto.getPassword()
             ));
             log.info("Successfully logged in::::");
            UserDetails userDetails = userService.loadUserByUsername(usersDto.getEmail());
            log.info("Username is "+userDetails.getUsername());
            return new ResponseEntity<>(userDetails, HttpStatus.OK);
        }
        catch(BadCredentialsException e) {
            log.info("Failed to log in::::");
            customResponse = new CustomResponse(Responses.WRONG_CREDENTIALS.getCode(), Responses.WRONG_CREDENTIALS.getMessage());
            return new ResponseEntity<>(customResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/findById")
    public ResponseEntity<?> findAllUsersByUserId(@RequestBody UsersDto usersDto) {
        log.info("Incoming payload for user enquiry is "+usersDto.toString());
        Users users = utils.validateUserId(usersDto.getUserId());
        log.info("Retrieved user for the ID is "+users.getFirstName());
        return new ResponseEntity<>(users, HttpStatus.OK);
    }
}
