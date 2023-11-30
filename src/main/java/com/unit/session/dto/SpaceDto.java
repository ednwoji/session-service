package com.unit.session.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpaceDto {

    private String userId;
    private String spaceLocation;
    private String spaceType;
    private String spaceImage;
    private String spaceId;
}
