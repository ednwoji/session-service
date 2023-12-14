package com.unit.session.servicesimpl;

import com.unit.session.dto.UsersDto;
import com.unit.session.entities.Roles;
import com.unit.session.entities.Users;
import com.unit.session.repositories.UsersRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class UserService implements UserDetailsService {

    @Autowired
    private UsersRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Users> users = userRepository.findByEmail(email);

        if(users.isPresent()) {
            return users.get();
        }
        else {
            throw new UsernameNotFoundException("User not found with Email: "+email);
        }


    }

    public Users createNewUser(UsersDto userDto) {

        Optional<Users> user = userRepository.findByEmail(userDto.getEmail());
        if(user.isPresent()) {
            log.info("Email already exists on the DB");
            throw new RuntimeException("User already exists");
        }

        Users users = new Users();
        users.setFirstName(userDto.getFirstName());
        users.setLastName(users.getLastName());
        users.setRole(Roles.valueOf(userDto.getRole()));
        users.setEmail(userDto.getEmail());
        users.setPassword(passwordEncoder.encode(userDto.getPassword()));

        try{
            userRepository.save(users);
        }
        catch(Exception e) {
            throw new RuntimeException("Email already exists");
        }

        return users;
    }

    public void updateUserRole(Users users) {
        String desiredRole = users.getRole().equals(Roles.HOST) ? Roles.TENANT.name() : Roles.HOST.name();
        users.setRole(Roles.valueOf(desiredRole));
        userRepository.save(users);
    }
}
