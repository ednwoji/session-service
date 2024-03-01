package com.unit.session.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.ManyToAny;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
//@NoArgsConstructor
@Table(name = "spaces")
public class Spaces {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long spaceId;

    @Column(name = "space_location")
    private String spaceLocation;

//    @Enumerated(value = EnumType.STRING)
    @Column(name = "space_type")
    private String spaceType;

    @ManyToOne
    @JoinColumn(name = "space_owner", referencedColumnName = "user_id")
    private Users spaceOwner;

    @Column(name = "latitude")
    private double lat;

    @Column(name = "longitude")
    private double lng;


    @Lob
    @Type(type="org.hibernate.type.TextType")
    @Column(name = "space_image")
    private String spaceImage;

    @Transient
    private double distance;

    @Column(name = "charge_per_day")
    private Double chargePerDay;

    @Column(name = "description")
    private String description;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "booking_status")
    private Booking bookingStatus;

    @Column(name = "size")
    private String size;

    @ElementCollection
    @Column(name = "visit_day")
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<String> visitDays;

    @ElementCollection
    @Column(name = "space_rules")
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<String> spaceRules;

    @Column(name = "visit_start_time")
    private String visitStartTime;

    @Column(name = "visit_end_time")
    private String visitEndTime;

    @Column(name = "practice_with_tenant")
    private String practice;

    @Column(name = "music_availability")
    private String musicDetails;

    @Column(name = "additional_note")
    private String additionalDetails;

    @Column(name = "date_added")
    private LocalDateTime dateAdded;

    @Column(name = "active")
    private Boolean active = true;

//    @Column(name = "capacity")
//    private int capacity;

    public Spaces() {
        this.bookingStatus = Booking.PENDING;
    }
}
