package com.unit.session.servicesimpl;

import com.unit.session.entities.Accounts;
import com.unit.session.entities.Users;
import com.unit.session.repositories.AccountsRepository;
import com.unit.session.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class AccountServiceImpl implements AccountService {


    @Autowired
    private AccountsRepository accountsRepository;

    @Override
    public String getAccountBalance(Users users) {
        Accounts accounts = accountsRepository.findByHostName(users).orElse(null);
        return accounts != null ? accounts.getAccountBalance()+ "~~"+ accounts.getAmountWithdrawn() : null;
    }
}
