package com.unit.session.repositories;

import com.unit.session.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByUserId(Long userId);
    Optional<Users> findByEmail(String email);
}
