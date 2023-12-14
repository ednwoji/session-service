package com.unit.session.services;

import com.unit.session.dto.SpaceDto;
import com.unit.session.dto.UsersDto;
import com.unit.session.entities.BookedSpaces;
import com.unit.session.entities.Spaces;

import java.util.List;

public interface SpaceService {
    Spaces saveSpace(SpaceDto spaceDto);

    List<Spaces> findSpaceByUser(UsersDto usersDto);

    Spaces findSpaceBySpaceId(String spaceId);

    void updateSpaceBookingStatus(Spaces spaces, SpaceDto spaceDto);

    List<BookedSpaces> findAllBookedSpacesForTenants(String userId);
}
