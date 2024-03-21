package com.unit.session.servicesimpl;

import com.unit.session.Utilities.Utils;
import com.unit.session.dto.AccountsDto;
import com.unit.session.dto.UsersDto;
import com.unit.session.entities.Accounts;
import com.unit.session.entities.BankDetails;
import com.unit.session.entities.Users;
import com.unit.session.repositories.AccountsRepository;
import com.unit.session.repositories.BankDetailsRepository;
import com.unit.session.services.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j
public class AccountServiceImpl implements AccountService {


    @Autowired
    private AccountsRepository accountsRepository;

    @Autowired
    private BankDetailsRepository bankDetailsRepository;
    @Autowired
    private Utils utils;

    @Override
    public String getAccountBalance(Users users) {
        Accounts accounts = accountsRepository.findByHostName(users).orElse(null);
        return accounts != null ? accounts.getAccountBalance()+ "~~"+ accounts.getAmountWithdrawn() : null;
    }

    @Override
    public List<BankDetails> saveAccountDetails(AccountsDto accountsDto) {
        BankDetails details = new BankDetails();
        details.setPayPalEmail(accountsDto.getPayPayEmail());
        details.setCurrency("USD");
        details.setHostName(utils.validateUserId(accountsDto.getUserId()));
        try {
            bankDetailsRepository.save(details);
            return findAllAccounts(accountsDto.getUserId());
        }
        catch(Exception e) {
            log.info(e.getMessage());
            return null;
        }

    }

    @Override
    public List<BankDetails> findAllAccounts(String userId) {
        Users user = utils.validateUserId(userId);
        return bankDetailsRepository.findByHostName(user);
    }
}
