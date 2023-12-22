package com.unit.session.entities;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Table(name = "space_images")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpaceImages {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "space_image_id")
    private Long spaceImageId;

    @ManyToOne
    @JoinColumn(name = "space_id", referencedColumnName = "spaceId")
    private Spaces spaces;

    @Lob
    @Type(type="org.hibernate.type.TextType")
    @Column(name = "space_image")
    private String spaceImage;

    @ManyToOne
    @JoinColumn(name = "space_owner", referencedColumnName = "user_id")
    private Users spaceOwner;
}
