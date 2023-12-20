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
    private String bookingStatus;

    private String size;
    private String visitDays;
    private String visitStartTime;
    private String visitEndTime;
    private String practice;
    private String musicDetails;
    private String additionalDetails;
    private int duration;
}
