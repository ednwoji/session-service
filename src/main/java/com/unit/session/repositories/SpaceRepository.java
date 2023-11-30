package com.unit.session.repositories;

import com.unit.session.entities.Spaces;
import com.unit.session.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpaceRepository extends JpaRepository<Spaces, Long> {
    List<Spaces> findBySpaceOwner(Users user);

    Optional<Spaces> findBySpaceId(long spaceId);
}
