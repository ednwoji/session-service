package com.unit.session.services;

import com.unit.session.dto.AccountsDto;
import com.unit.session.dto.UsersDto;
import com.unit.session.entities.BankDetails;
import com.unit.session.entities.Users;

import java.util.List;

public interface AccountService {
    String getAccountBalance(Users users);

    List<BankDetails> saveAccountDetails(AccountsDto accountsDto);

    List<BankDetails> findAllAccounts(String userId);
}
