package com.unit.session.controller;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.param.AccountCreateParams;
import com.stripe.param.ChargeCreateParams;
import com.stripe.param.PayoutCreateParams;
import com.stripe.param.TokenCreateParams;
import com.unit.session.Utilities.Utils;
import com.unit.session.dto.*;
import com.unit.session.entities.Accounts;
import com.unit.session.entities.Users;
import com.unit.session.repositories.AccountsRepository;
import com.unit.session.repositories.UsersRepository;
import com.unit.session.services.AccountService;
import com.unit.session.services.PayPalService;
import com.unit.session.servicesimpl.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/payment")
@CrossOrigin
@Slf4j
public class StripeController {

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @Autowired
    private Utils utils;


    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountsRepository accountsRepository;

    @Autowired
    private PayPalService payPalPayoutService;


    @PostMapping("/process-payment")
    public ResponseEntity<?> processPayment(@RequestParam("tokenId") String tokenId,
                                            @RequestParam("amount") BigDecimal amount) throws StripeException {
        log.info("Request is:::::: "+tokenId);
        Stripe.apiKey = stripeSecretKey;
        long amountInCents = amount.multiply(new BigDecimal(100)).longValue();
        log.info("Amount to be paid in cents is "+amountInCents);


        try {
            // Create a charge using the token ID
            Charge charge = Charge.create(
                    new ChargeCreateParams.Builder()
                            .setAmount(amountInCents)
                            .setCurrency("usd")
                            .setSource(tokenId)
                            .setDescription("Charge for space")
                            .build()
            );
//            return new ResponseEntity<>("Payment successful: " + charge.getId(), HttpStatus.OK);
            return new ResponseEntity<>("Payment successful", HttpStatus.OK);
        } catch (StripeException e) {
            String[] parts = e.getMessage().split(";");
            log.info("Failed with error "+parts[0].trim());
            return new ResponseEntity<>(parts[0].trim(), HttpStatus.OK);
        }
    }


    @PostMapping("/account-balance")
    public ResponseEntity<?> getHostAccountBalance(@RequestBody UsersDto usersDto, CustomResponse customResponse) {
        String balance = null;
        Users users = utils.validateUserId(usersDto.getUserId());

        if(users == null) {
            customResponse = new CustomResponse(Responses.WRONG_USERNAME.getCode(), Responses.WRONG_USERNAME.getMessage());
            return new ResponseEntity<>(customResponse, HttpStatus.FORBIDDEN);
        }
        balance = accountService.getAccountBalance(users);
        log.info("Balance is "+balance);
        return new ResponseEntity<>(balance, HttpStatus.OK);
    }



    @PostMapping("/add-accounts")
    public ResponseEntity<?> addAccountDetails(@RequestBody AccountsDto accountsDto) {
        log.info("Incoming request for account details is "+accountsDto);
        return new ResponseEntity<>(accountService.saveAccountDetails(accountsDto),HttpStatus.OK);
    }

    @PostMapping("/getAccounts")
    public ResponseEntity<?> getAccountDetails(@RequestBody UsersDto usersDto) {
        log.info("Incoming request to get account list");
        return new ResponseEntity<>(accountService.findAllAccounts(usersDto.getUserId()), HttpStatus.OK);
    }


    private void updateAccountBalance(String userId, Long withdrawalAmount) {
        Accounts accounts1 = accountsRepository.findByHostName(utils.validateUserId(userId)).orElse(null);
        accounts1.setAccountBalance(accounts1.getAccountBalance() - withdrawalAmount);
        accounts1.setAmountWithdrawn(accounts1.getAmountWithdrawn() + withdrawalAmount);
        accountsRepository.save(accounts1);
    }



    @PostMapping("/generate-token")
    public ResponseEntity<String> createBankAccountToken(@RequestBody AccountsDto accountsDto) {
        try {
            Stripe.apiKey = stripeSecretKey;

            // Create a bank account token using Stripe API
            TokenCreateParams.BankAccount bankAccountParams = TokenCreateParams.BankAccount.builder()
                    .setAccountNumber(accountsDto.getAccount_number())
                    .setRoutingNumber(accountsDto.getRouting_number())
                    .setCountry("US")
                    .setCurrency("usd")
                    .setAccountHolderName(accountsDto.getAccount_holder_name())
                    .build();

            TokenCreateParams tokenParams = TokenCreateParams.builder()
                    .setBankAccount(bankAccountParams)
                    .build();

            Token token = Token.create(tokenParams);
            log.info("Token generated is "+token.getId());
            String accountId = createConnectedAccount(accountsDto, token.getId());


            return ResponseEntity.ok(initiatePayoutToBank(accountId, accountsDto.getAmount(), accountsDto.getUserId()));

        } catch (StripeException e) {
           log.info("Error generating token "+e.getMessage());
            return ResponseEntity.status(HttpStatus.OK).body(e.getMessage().split(";")[0]);

        }
    }


    public String initiatePayoutToBank(String bankAccountToken, Long amount, String userId) {
        try {
            // Set your Stripe API key
            Stripe.apiKey = stripeSecretKey;




            // Create a bank account object in Stripe using the token
            Map<String, Object> payoutParams = new HashMap<>();
            payoutParams.put("amount", (int) (amount * 100));
            payoutParams.put("currency", "usd");
            payoutParams.put("destination", bankAccountToken);
            Payout payout = Payout.create(payoutParams);
            log.info("Payout response is "+payout.toString());
            updateAccountBalance(userId, amount);

            // Return success response
            return "Payout initiated successfully";
        } catch (StripeException e) {
            log.info("Exception during payout is "+e.getMessage());
            return e.getMessage().split(";")[0];
        }
    }





    public String createConnectedAccount(AccountsDto accountsDto, String accountToken) {
        try {
            Users users = utils.validateUserId(accountsDto.getUserId());
            // Create connected account
//            AccountCreateParams params = AccountCreateParams.builder()
//                    .setType(AccountCreateParams.Type.CUSTOM)
//                    .setCountry("US")
//                    .setEmail(users.getEmail())
//                    .build();
//            Account account = Account.create(params);
//            log.info("Account creation "+account.getId());
//
//            Map<String, Object> bankAccountParams = new HashMap<>();
//            bankAccountParams.put("external_account", accountToken);
//            ExternalAccount bankAccount = account.getExternalAccounts().create(bankAccountParams);


            AccountCreateParams params =
                    AccountCreateParams.builder().setType(AccountCreateParams.Type.EXPRESS).build();
            Account account = Account.create(params);


            return account.getId();
        } catch (StripeException e) {
            log.info("Exception during creation of account "+e.getMessage());
            return "Error creating connected account";
        }
    }



    @PostMapping("/payout")
    public ResponseEntity<?> triggerPayout(@RequestBody PayoutRequest payoutRequest) {
        log.info("Incoming payload is "+payoutRequest.toString());
        String paymentStatus = payPalPayoutService.createPayout(payoutRequest.getUserId(), payoutRequest.getRecipientEmail(), payoutRequest.getAmount(), payoutRequest.getCurrency());
        log.info("Payment status is "+paymentStatus);
        if(paymentStatus.equalsIgnoreCase("PENDING")) {
            updateAccountBalance(payoutRequest.getUserId(), (long) payoutRequest.getAmount());
        }
        return new ResponseEntity<>(paymentStatus, HttpStatus.OK);
    }

}
