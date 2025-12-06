package com.se100.bds.services.domains.notification;

import com.se100.bds.models.entities.appointment.Appointment;

public interface NotificationService {
    void notifyAppointmentBooked(Appointment appointment);

    void notifyAppointmentAssigned(Appointment appointment);

    void notifyAppointmentCancelled(Appointment appointment, String reason);

    void notifyAppointmentCompleted(Appointment appointment);
}
