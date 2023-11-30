package com.unit.session.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpaceDto {

    private String userId;
    private String spaceLocation;
    private String spaceType;
    private String spaceImage;
    private String spaceId;
    private String chargePerDay;
    private String description;
}
