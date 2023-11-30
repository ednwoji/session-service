package com.unit.session.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ManyToAny;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "spaces")
public class Spaces {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long spaceId;

    @Column(name = "space_location")
    private String spaceLocation;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "space_type")
    private SpaceTypes spaceType;

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

//    @Column(name = "capacity")
//    private int capacity;
}
