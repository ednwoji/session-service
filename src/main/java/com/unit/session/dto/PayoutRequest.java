package com.unit.session.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PayoutRequest {

    private String recipientEmail;
    private double amount;
    private String currency;
    private String userId;
}
