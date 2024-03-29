package com.unit.session.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Column;
import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpaceDto {

    private String userId;
    private String spaceLocation;
    private String spaceType;
    private ArrayList<MultipartFile> spaceImages;
    private String spaceId;
    private String chargePerDay;
    private String description;
    private String bookingStatus;
    private ArrayList<String> spaceRules;

    private String size;
    private ArrayList<String> visitDays;
    private String visitStartTime;
    private String visitEndTime;
    private String practice;
    private String musicDetails;
    private String additionalDetails;
    private int duration;
    private String startDateTime;
    private String endDateTime;

    private double lowerPriceRange;
    private double upperPriceRange;
    private ArrayList<String> spaceImage;

}
