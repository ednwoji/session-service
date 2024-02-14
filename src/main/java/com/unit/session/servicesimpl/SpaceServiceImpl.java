package com.unit.session.servicesimpl;

import com.google.maps.model.LatLng;
import com.unit.session.Utilities.EmailSenderService;
import com.unit.session.Utilities.Utils;
import com.unit.session.dto.SpaceDto;
import com.unit.session.dto.UsersDto;
import com.unit.session.entities.*;
import com.unit.session.repositories.*;
import com.unit.session.services.AccountService;
import com.unit.session.services.SpaceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class SpaceServiceImpl implements SpaceService {

    @Autowired
    private SpaceRepository spaceRepository;


    @Autowired
    private SpaceImagesRepository spaceImagesRepository;

    @Autowired
    private BookedSpacesRepository bookedSpacesRepository;

    @Autowired
    private AccountsRepository accountsRepository;


    @Autowired
    private EmailSenderService emailSenderService;



    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private Utils utils;

    @Override
    @Transactional
    public Spaces saveSpace(SpaceDto spaceDto) {
        if(spaceDto.getSpaceImage() == null || spaceDto.getSpaceType() == null || spaceDto.getSpaceLocation() == null) {
            throw new RuntimeException("Please fill all details");
        }

        Spaces spaces1 = null;
        Users users = utils.validateUserId(spaceDto.getUserId());
        LatLng result = utils.getLatLng(spaceDto.getSpaceLocation());
        log.info("Result is "+result.toString());
        if (users != null) {
                Spaces spaces = new Spaces();
                spaces.setSpaceOwner(users);
                spaces.setSpaceImage(spaceDto.getSpaceImage().get(0));
                spaces.setSpaceLocation(spaceDto.getSpaceLocation());
                spaces.setSpaceType(spaceDto.getSpaceType());
                spaces.setLng(result.lng);
                spaces.setLat(result.lat);
                spaces.setChargePerDay(Double.parseDouble(spaceDto.getChargePerDay()));
                spaces.setDescription(spaceDto.getDescription());
                spaces.setSize(spaceDto.getSize());
                spaces.setMusicDetails(spaceDto.getMusicDetails());
                spaces.setPractice(spaceDto.getPractice());
                spaces.setVisitEndTime(spaceDto.getVisitEndTime());
                spaces.setVisitStartTime(spaceDto.getVisitStartTime());
                spaces.setVisitDays(spaceDto.getVisitDays());
                spaces.setAdditionalDetails(spaceDto.getAdditionalDetails());
                spaces.setDateAdded(LocalDateTime.now());
                spaces.setSpaceRules(spaceDto.getSpaceRules());

                spaces1 = spaceRepository.save(spaces);
                log.info("Spaces saved successfully");

                for(String image : spaceDto.getSpaceImage()) {
                    SpaceImages spaceImages = new SpaceImages();
                    spaceImages.setSpaceImage(image);
                    spaceImages.setSpaces(spaces1);
                    spaceImages.setSpaceOwner(users);

                    spaceImagesRepository.save(spaceImages);
                }
                log.info("Spaces images saved successfully");


        }
        return spaces1;
    }

    @Override
    public List<Spaces> findSpaceByUser(UsersDto usersDto) {
        Users user = utils.validateUserId(usersDto.getUserId());
        if(user != null) {
            return spaceRepository.findBySpaceOwnerAndActive(user, true);
        }

        return null;
    }

    @Override
    public Spaces findSpaceBySpaceId(String spaceId) {
//        return spaceRepository.findBySpaceIdAndBookingStatus(Long.parseLong(spaceId), Booking.PENDING).orElse(null);
        return spaceRepository.findBySpaceIdAndActive(Long.parseLong(spaceId), true).orElse(null);

    }

    @Override
    public void updateSpaceBookingStatus(Spaces spaces, SpaceDto spaceDto) {
        spaces.setBookingStatus(Booking.valueOf(spaceDto.getBookingStatus()));
        Spaces newSpace = spaceRepository.save(spaces);
        bookSpaceForTenant(newSpace, spaceDto);
        addAccountForHost(newSpace, spaceDto);
        sendEmail(spaces, spaceDto);
    }

    @Override
    public List<BookedSpaces> findAllBookedSpacesForTenants(String userId) {
        return bookedSpacesRepository.findByBookedBy(utils.validateUserId(userId));
    }

    @Override
    public List<SpaceImages> findSpaceImagesByUser(UsersDto usersDto) {
        Users user = utils.validateUserId(usersDto.getUserId());
        if(user != null) {
            return spaceImagesRepository.findBySpaceOwner(user);
        }

        return null;
    }

    @Override
    public Spaces findBookedSpaceBySpaceId(String spaceId) {
        return spaceRepository.findBySpaceIdAndBookingStatusAndActive(Long.parseLong(spaceId), Booking.BOOKED, true).orElse(null);

    }

    @Override
    @Transactional
    public void deleteSpaceById(String spaceId) {
        Spaces space = spaceRepository.findBySpaceId(Long.valueOf(spaceId)).orElse(null);
        if(space != null) {
            space.setActive(false);
            spaceRepository.save(space);
        }
    }

    @Override
    public Spaces updateRules(SpaceDto spaceDto) {
        Spaces spaces = spaceRepository.findBySpaceId(Long.valueOf(spaceDto.getSpaceId())).orElse(null);
        if(spaces != null) {
            spaces.setSpaceRules(spaceDto.getSpaceRules());
            return spaceRepository.save(spaces);
        }
        return null;
    }

    @Override
    public Spaces updateSpaceImages(SpaceDto spaceDto) {
        Spaces spaces1 = spaceRepository.findBySpaceId(Long.valueOf(spaceDto.getSpaceId())).orElse(null);
        Users users = utils.validateUserId(spaceDto.getUserId());

        if (spaces1 != null && users != null) {
            List<SpaceImages> spaceImages = spaceImagesRepository.findBySpaces(spaces1);
            spaces1.setSpaceImage(spaceDto.getSpaceImage().get(0));
            spaceRepository.save(spaces1);
            log.info("Space image updated successfully");

            if(!spaceImages.isEmpty()) {
                for (SpaceImages images : spaceImages) {
                    spaceImagesRepository.delete(images);
                }
            }

            for(String image : spaceDto.getSpaceImage()) {
                SpaceImages spaceImages1 = new SpaceImages();
                spaceImages1.setSpaceImage(image);
                spaceImages1.setSpaces(spaces1);
                spaceImages1.setSpaceOwner(users);

                spaceImagesRepository.save(spaceImages1);
            }
            log.info("Spaces images saved successfully");
        }

        return spaces1;
    }

    @Override
    public List<Spaces> filterSpacesByPreference(SpaceDto spaceDto) {

//        if(spaceDto.getSpaceLocation() != null && spaceDto.getSpaceType() != null) {
            return spaceRepository.findBySpaceLocationAndActiveAndSpaceTypeAndChargePerDayBetween(spaceDto.getSpaceType(), spaceDto.getSpaceLocation(), true, spaceDto.getLowerPriceRange(), spaceDto.getUpperPriceRange());
//            return spaceRepository.findBySpaceLocationAndActiveAndSpaceTypeAndChargePerDayBetween(spaceDto.getLowerPriceRange(), spaceDto.getUpperPriceRange());




    }

    public void bookSpaceForTenant(Spaces spaces, SpaceDto spaceDto) {

        log.info("Booking spaces for tenant:::");
        BookedSpaces bookedSpaces = new BookedSpaces();
        bookedSpaces.setBookedBy(utils.validateUserId(spaceDto.getUserId()));
        bookedSpaces.setSpaceId(spaces);
        bookedSpaces.setSpaceOwner(spaces.getSpaceOwner());
        bookedSpaces.setBookedTime(LocalDateTime.now());
        bookedSpaces.setExpiryDate(LocalDateTime.now().plusDays(1L));
        bookedSpaces.setDuration(spaceDto.getDuration());
        bookedSpaces.setStartDateTime(spaceDto.getStartDateTime());
        bookedSpaces.setEndDateTime(spaceDto.getEndDateTime());

        bookedSpacesRepository.save(bookedSpaces);

    }


    public void addAccountForHost(Spaces spaces, SpaceDto spaceDto) {
        log.info("Funding host acccount with "+spaces.getChargePerDay());
        log.info("Total cost is "+spaceDto.getDuration());

        double totalAmount = spaceDto.getDuration() * spaces.getChargePerDay();

        Users hostUser = spaces.getSpaceOwner();
        Accounts accounts1 = accountsRepository.findByHostName(hostUser).orElse(null);

        if(accounts1 == null) {
            Accounts accounts = new Accounts();
            accounts.setAccountBalance(totalAmount);
            accounts.setAmountWithdrawn(0);
            accounts.setHostName(hostUser);
            accountsRepository.save(accounts);
        }

        else {
            accounts1.setAccountBalance(accounts1.getAccountBalance() + totalAmount);
            accountsRepository.save(accounts1);
        }

    }



    public void sendEmail(Spaces spaces, SpaceDto spaceDto) {

        log.info("Sending mail to user:::::");
        Users user = usersRepository.findByEmail(utils.validateUserId(spaceDto.getUserId()).getEmail()).orElse(null);
        String htmlContent = null;
        double totalAmount = spaceDto.getDuration() * spaces.getChargePerDay();
        double serviceCharge = totalAmount * 0.1;
        double totalFees = totalAmount + serviceCharge;

        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        double roundedServiceCharge = Double.parseDouble(decimalFormat.format(serviceCharge));
        double roundedTotalFees = Double.parseDouble(decimalFormat.format(totalFees));

        if(user != null) {

            htmlContent = "<html><body style='font-family: Arial, sans-serif;'>" +
                    "<div style='max-width: 600px; margin: 0 auto;'>" + // Set a max-width for the content
                    "<div style='background-color: #f2f2f2; padding: 20px; border-radius: 10px;'>" + // Bordered box
                    "<img src='cid:logo' alt='Your Logo' style='width: 100px; height: auto;'>" +
                    "<p>Dear " + user.getFirstName() + ",</p>" +
                    "<p>You have successfully booked a space</p>" +
                    "<p>Space Location: " + spaces.getSpaceLocation() + "</p>" +
                    "<p><b><h2>Total Fees: " + roundedTotalFees + "</p></b></h2>" +
                    "<p>Rent Fee: " + totalAmount + "</p>" +
                    "<p>Charges(10%): " + roundedServiceCharge + "</p>" +
                    "<p><b>Thank you for choosing Unit Session</b></p>" +
                    "</div>" +
                    "</div>" +
                    "</body></html>";

            try {
                emailSenderService.sendEmail(user.getEmail(), "Unit Session Payment Receipt", htmlContent);
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
