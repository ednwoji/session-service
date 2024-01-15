package com.unit.session.entities;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "account_balance_users")
public class Accounts {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long accountId;

    @ManyToOne
    @JoinColumn(name = "host_name", referencedColumnName = "user_id")
    private Users hostName;

    @Column(name = "account_balance")
    private double accountBalance;

    @Column(name = "amount_withdrawn")
    private double amountWithdrawn;
}
