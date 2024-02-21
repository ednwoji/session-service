package com.unit.session.scheduler;


import com.unit.session.Utilities.EmailSenderService;
import com.unit.session.entities.BookedSpaces;
import com.unit.session.repositories.BookedSpacesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class Tasks {

    @Autowired
    private BookedSpacesRepository bookedSpacesRepository;

    @Autowired
    private EmailSenderService emailSenderService;

    @Scheduled(cron = "0 * * * * *")
    public void sendEmailsToUsers() {

        List<BookedSpaces> bookedSpacesList = bookedSpacesRepository.findByStartDateTimeAfter(LocalDateTime.now().minusHours(2));

        bookedSpacesList.parallelStream()
                .forEach(bookedSpaces -> {
                    String htmlContent = "<html><body style='font-family: Arial, sans-serif;'>" +
                            "<div style='max-width: 600px; margin: 0 auto;'>" + // Set a max-width for the content
                            "<div style='background-color: #f2f2f2; padding: 20px; border-radius: 10px;'>" + // Bordered box
                            "<img src='cid:logo' alt='Your Logo' style='width: 100px; height: auto;'>" +
                            "<p>Dear " + bookedSpaces.getBookedBy().getFirstName() + ",</p>" +
                            "<p>Please be reminded of your session scheduled to hold in the next two hours.</p>" +
                            "<p>Space Location: " + bookedSpaces.getSpaceId().getSpaceLocation() + "</p>" +
                            "<p><b><h2> Please be informed you can engage the host for extension of time if you're unable to make it to the location today</p></b></h2>" +
                            "<p><b>Thank you for choosing Unit Session</b></p>" +
                            "</div>" +
                            "</div>" +
                            "</body></html>";

                    try {
                        emailSenderService.sendEmail(bookedSpaces.getBookedBy().getEmail(), "Session Reminder", htmlContent);
                    }
                    catch (Exception e) {
//                        System.out.println(e.getMessage());
                    }
                });
    }

}
