package com.unit.session.repositories;

import com.unit.session.entities.Booking;
import com.unit.session.entities.Spaces;
import com.unit.session.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpaceRepository extends JpaRepository<Spaces, Long> {
    List<Spaces> findBySpaceOwner(Users user);
    List<Spaces> findByBookingStatus(Booking booking);

    Optional<Spaces> findBySpaceIdAndBookingStatus(long spaceId, Booking booking);

    Optional<Spaces> findBySpaceId(long spaceId);

    List<Spaces> findByActive(boolean b);

    List<Spaces> findBySpaceOwnerAndActive(Users user, boolean b);

    Optional<Spaces> findBySpaceIdAndActive(long l, boolean b);

    Optional<Spaces> findBySpaceIdAndBookingStatusAndActive(long l, Booking booking, boolean b);

//    List<Spaces> findBySpaceLocationAndActiveAndSpaceTypeAndChargePerDayBetween(String spaceLocation, boolean b, String spaceType, int lowerPriceRange, int upperPriceRange);

    @Query("SELECT a FROM Spaces a WHERE 1=1" +
            " AND (:spaceType IS NULL OR a.spaceType = :spaceType)" +
            " AND (:spaceLocation IS '' OR a.spaceLocation = :spaceLocation)" +
            " AND (a.active = :active)" +
            " AND (a.chargePerDay BETWEEN :lowerPriceRange AND :upperPriceRange)")
        List<Spaces> findBySpaceLocationAndActiveAndSpaceTypeAndChargePerDayBetween(@Param("spaceType") String spaceType, @Param("spaceLocation") String spaceLocation, @Param("active") boolean active, @Param("lowerPriceRange") double lowerPriceRange, @Param("upperPriceRange") double upperPriceRange);


//    @Query("SELECT a FROM Spaces a WHERE 1=1" +
//            " AND (COALESCE(:spaceType, null) is null or a.spaceType = :spaceType)"+
//            " AND(COALESCE(:spaceLocation, '') is null or a.spaceLocation = :spaceLocation)"+
//            " AND (a.active = :active)" +
//            " AND (a.chargePerDay BETWEEN :lowerPriceRange AND :upperPriceRange)")
//    List<Spaces> findBySpaceLocationAndActiveAndSpaceTypeAndChargePerDayBetween(@Param("spaceType") String spaceType, @Param("spaceLocation") String spaceLocation, @Param("active") boolean active, @Param("lowerPriceRange") double lowerPriceRange, @Param("upperPriceRange") double upperPriceRange);



//    @Query("SELECT a FROM Spaces a WHERE a.chargePerDay BETWEEN :lowerPriceRange AND :upperPriceRange")
//    List<Spaces> findBySpaceLocationAndActiveAndSpaceTypeAndChargePerDayBetween(@Param("lowerPriceRange") double lowerPriceRange, @Param("upperPriceRange") double upperPriceRange);

}

