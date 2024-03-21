package com.unit.session.dto;

import com.unit.session.entities.Users;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountsDto {

    private String userId;
    private String account_number;
    private String account_holder_name;
    private String account_holder_type;
    private String currency;
    private String routing_number;
    private Long amount;
    private String bank_name;

    private String payPayEmail;

}
