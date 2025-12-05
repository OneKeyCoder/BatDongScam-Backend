package com.se100.bds.services.domains.notification.impl;

import com.se100.bds.models.entities.appointment.Appointment;
import com.se100.bds.models.entities.notification.Notification;
import com.se100.bds.models.entities.property.Media;
import com.se100.bds.models.entities.user.User;
import com.se100.bds.repositories.domains.notification.NotificationRepository;
import com.se100.bds.services.domains.notification.NotificationService;
import com.se100.bds.utils.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private static final String DEFAULT_NOTIFICATION_IMAGE = "https://static.batdongsam.com/notifications/default.png";

    private final NotificationRepository notificationRepository;

    @Override
    @Async
    public void notifyAppointmentBooked(Appointment appointment) {
        if (appointment == null) {
            return;
        }

        String propertyTitle = extractPropertyTitle(appointment);
        String message = String.format("Your viewing for %s is pending confirmation.", propertyTitle);
        sendNotification(appointment.getCustomer() != null ? appointment.getCustomer().getUser() : null,
                "Viewing booked",
                message,
                appointment);

        if (appointment.getAgent() != null && appointment.getAgent().getUser() != null) {
            String agentMessage = String.format("New viewing assigned for %s on %s.",
                    propertyTitle,
                    appointment.getRequestedDate());
            sendNotification(appointment.getAgent().getUser(),
                    "New viewing assignment",
                    agentMessage,
                    appointment);
        }
    }

    @Override
    @Async
    public void notifyAppointmentAssigned(Appointment appointment) {
        if (appointment == null || appointment.getAgent() == null || appointment.getAgent().getUser() == null) {
            return;
        }

        String propertyTitle = extractPropertyTitle(appointment);
        String title = "Viewing confirmed";
        String message = String.format("Agent %s is now handling your viewing for %s.",
                appointment.getAgent().getUser().getFullName(),
                propertyTitle);
        sendNotification(appointment.getCustomer() != null ? appointment.getCustomer().getUser() : null,
                title,
                message,
                appointment);

        String agentTitle = "You have been assigned";
        String agentMessage = String.format("You are assigned to the viewing for %s on %s.",
                propertyTitle,
                appointment.getRequestedDate());
        sendNotification(appointment.getAgent().getUser(), agentTitle, agentMessage, appointment);
    }

    @Override
    @Async
    public void notifyAppointmentCancelled(Appointment appointment, String reason) {
        if (appointment == null) {
            return;
        }

        String propertyTitle = extractPropertyTitle(appointment);
        String cancellationMessage = String.format("Viewing for %s was cancelled. %s",
                propertyTitle,
                reason != null ? reason : "");

        sendNotification(appointment.getCustomer() != null ? appointment.getCustomer().getUser() : null,
                "Viewing cancelled",
                cancellationMessage,
                appointment);

        if (appointment.getAgent() != null && appointment.getAgent().getUser() != null) {
            String agentMessage = String.format("Viewing for %s has been cancelled.", propertyTitle);
            sendNotification(appointment.getAgent().getUser(),
                    "Assigned viewing cancelled",
                    agentMessage,
                    appointment);
        }
    }

    @Override
    @Async
    public void notifyAppointmentCompleted(Appointment appointment) {
        if (appointment == null || appointment.getCustomer() == null) {
            return;
        }

        String propertyTitle = extractPropertyTitle(appointment);
        String message = String.format("Thanks for attending the viewing for %s. Please consider leaving feedback.", propertyTitle);
        sendNotification(appointment.getCustomer().getUser(),
                "Viewing completed",
                message,
                appointment);
    }

    private void sendNotification(User recipient, String title, String message, Appointment appointment) {
        if (recipient == null) {
            return;
        }

        Notification notification = Notification.builder()
                .recipient(recipient)
                .type(Constants.NotificationTypeEnum.APPOINTMENT_REMINDER)
                .title(title)
                .message(message)
                .relatedEntityType(Constants.RelatedEntityTypeEnum.APPOINTMENT)
                .relatedEntityId(resolveRelatedEntityId(appointment))
                .deliveryStatus(Constants.NotificationStatusEnum.PENDING)
                .isRead(Boolean.FALSE)
                .imgUrl(resolveImage(appointment))
                .build();

        notificationRepository.save(notification);
        log.debug("Created appointment notification '{}' for recipient {}", title, recipient.getId());
    }

    private String resolveRelatedEntityId(Appointment appointment) {
        return appointment != null && appointment.getId() != null
                ? appointment.getId().toString()
                : null;
    }

    private String resolveImage(Appointment appointment) {
        if (appointment != null && appointment.getProperty() != null) {
            List<Media> mediaList = appointment.getProperty().getMediaList();
            if (mediaList != null && !mediaList.isEmpty() && mediaList.get(0).getFilePath() != null) {
                return mediaList.get(0).getFilePath();
            }
        }
        return DEFAULT_NOTIFICATION_IMAGE;
    }

    private String extractPropertyTitle(Appointment appointment) {
        if (appointment != null && appointment.getProperty() != null && appointment.getProperty().getTitle() != null) {
            return appointment.getProperty().getTitle();
        }
        return "property";
    }
}
