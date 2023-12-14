package com.unit.session.repositories;

import com.unit.session.entities.BookedSpaces;
import com.unit.session.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface BookedSpacesRepository extends JpaRepository<BookedSpaces, Long> {
    List<BookedSpaces> findByBookedBy(Users users);
}
