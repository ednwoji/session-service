package com.unit.session.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "booked_spaces")
public class BookedSpaces {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long bookedSpaceId;

    @ManyToOne
    @JoinColumn(name = "booked_by", referencedColumnName = "user_id")
    private Users bookedBy;

    @ManyToOne
    @JoinColumn(name = "space_owner", referencedColumnName = "user_id")
    private Users spaceOwner;

    @ManyToOne
    @JoinColumn(name = "space_id", referencedColumnName = "space_id")
    private Spaces spaceId;

    @Column(name = "booked_time")
    private LocalDateTime bookedTime;

    @Column(name="duration")
    private int duration;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

}
