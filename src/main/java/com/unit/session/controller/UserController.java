package com.unit.session.controller;

import com.unit.session.Utilities.EmailSenderService;
import com.unit.session.Utilities.Utils;
import com.unit.session.dto.CustomResponse;
import com.unit.session.dto.Responses;
import com.unit.session.dto.SpaceDto;
import com.unit.session.dto.UsersDto;
import com.unit.session.entities.Roles;
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

import javax.mail.MessagingException;
import java.util.List;

@RequestMapping("/users")
@RestController
@Slf4j
@CrossOrigin
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
    public ResponseEntity<?> validateUser(@RequestBody UsersDto usersDto, CustomResponse customResponse) throws MessagingException {

        log.info("payload is "+usersDto.toString());
        try{
             authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                     usersDto.getEmail(), usersDto.getPassword()
             ));
             log.info("Successfully logged in::::");
            UserDetails userDetails = userService.loadUserByUsername(usersDto.getEmail());
            return new ResponseEntity<>(userDetails, HttpStatus.OK);
        }
        catch(BadCredentialsException e) {
            log.info("Failed to log in::::");
            customResponse = new CustomResponse(Responses.WRONG_CREDENTIALS.getCode(), Responses.WRONG_CREDENTIALS.getMessage());
            return new ResponseEntity<>(customResponse, HttpStatus.OK);
        }
    }

    @PostMapping("/findById")
    public ResponseEntity<?> findAllUsersByUserId(@RequestBody UsersDto usersDto) {
        log.info("Incoming payload for user enquiry is "+usersDto.toString());
        Users users = utils.validateUserId(usersDto.getUserId());
        log.info("Retrieved user for the ID is "+users.getFirstName());
        return new ResponseEntity<>(users, HttpStatus.OK);
    }


    @PostMapping("/updateUserRole")
    public ResponseEntity<?> updateUserRole(@RequestBody UsersDto usersDto) {
        log.info("Incoming request to update role::: "+usersDto.toString());
        Users users = utils.validateUserId(usersDto.getUserId());
        if(users != null) {
            userService.updateUserRole(users);
            return new ResponseEntity<>(users, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.OK);
    }


    @PostMapping("/getUsers")
    public ResponseEntity<?> getUserList(@RequestBody UsersDto usersDto) {
        Users users = utils.validateUserId(usersDto.getUserId());
        if(users != null) {
            if(!users.getRole().equals(Roles.ADMIN)) {
               return new ResponseEntity<>(new CustomResponse(Responses.UNAUTHORIZED_USER.getCode(), Responses.UNAUTHORIZED_USER.getMessage()), HttpStatus.UNAUTHORIZED);
            }
            return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
        }
        return new ResponseEntity<>(new CustomResponse(Responses.WRONG_USERNAME.getCode(), Responses.WRONG_USERNAME.getMessage()), HttpStatus.UNAUTHORIZED);
    }


    @PostMapping("/modify-user")
    public ResponseEntity<?> disableUser(@RequestBody UsersDto usersDto) {

        Users users = utils.validateUserId(usersDto.getInitiatorId());
        Users users1 = utils.validateUserId(usersDto.getUserId());
        log.info(users1.toString());
        if(users != null) {
            if(!users.getRole().equals(Roles.ADMIN)) {
                return new ResponseEntity<>(new CustomResponse(Responses.UNAUTHORIZED_USER.getCode(), Responses.UNAUTHORIZED_USER.getMessage()), HttpStatus.UNAUTHORIZED);
            }
            return new ResponseEntity<>(userService.disableUsers(users1), HttpStatus.OK);
        }
        return new ResponseEntity<>(new CustomResponse(Responses.WRONG_USERNAME.getCode(), Responses.WRONG_USERNAME.getMessage()), HttpStatus.UNAUTHORIZED);

    }
}
