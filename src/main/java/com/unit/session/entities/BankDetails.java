package com.unit.session.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "bank_details")
public class BankDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long bankDetailsId;
    @ManyToOne
    @JoinColumn(name = "user", referencedColumnName = "user_id")
    private Users hostName;
    private String account_number;
    private String account_holder_name;
    private String account_holder_type;
    private String currency;
    private String routing_number;
    private String bank_name;

    private String payPalEmail;
}
