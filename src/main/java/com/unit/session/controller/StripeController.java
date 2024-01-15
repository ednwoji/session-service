package com.unit.session.controller;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.param.ChargeCreateParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/payment")
@CrossOrigin(origins = "*")
@Slf4j
public class StripeController {

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

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
}
