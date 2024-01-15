package com.unit.session.repositories;

import com.unit.session.entities.Accounts;
import com.unit.session.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface AccountsRepository extends JpaRepository<Accounts, Long> {
    Optional<Accounts> findByHostName(Users hostUser);
}
