package com.unit.session.repositories;


import com.unit.session.entities.SpaceImages;
import com.unit.session.entities.Spaces;
import com.unit.session.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpaceImagesRepository extends JpaRepository<SpaceImages, Long> {
    List<SpaceImages> findBySpaceOwner(Users user);
    List<SpaceImages> findBySpaces(Spaces spaces);

    void deleteBySpaces(Spaces space);
}
