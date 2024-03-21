package com.unit.session.repositories;

import com.unit.session.entities.BankDetails;
import com.unit.session.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BankDetailsRepository extends JpaRepository<BankDetails, Long> {
    List<BankDetails> findByHostName(Users user);
}
