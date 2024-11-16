package hospitalsystem.appointmentcontrol;

import hospitalsystem.data.*;
import hospitalsystem.enums.*;
import hospitalsystem.model.*;
import java.util.List;

/**
 * Manages patient-specific appointment operations in the hospital system.
 * Provides functionality for patients to view and manage their appointments.
 *
 * @author Gracelynn
 * @version 1.0
 * @since 2024-03-16
 */
public class PatientAppointmentControl extends AppointmentControl{
    // move majority of patient appointment related stuff here
    /**
     * Retrieves all scheduled (non-completed) appointments for a specific patient.
     * Includes appointments that are booked or pending but excludes completed ones.
     *
     * @param patient the patient whose scheduled appointments are to be retrieved
     * @return a list of active appointments sorted by date and time
     */
    public static List<Appointment> getScheduledSlots(Patient patient) {
        return Database.appointmentMap.values().stream()
                .filter(apt -> apt.getPatient().getID().equals(patient.getID()))
                .filter(apt -> !apt.getIsAvailable() && apt.getStatus() != AppointmentStatus.COMPLETED)
                .sorted((a1, a2) -> a1.getSlot().getDateTime().compareTo(a2.getSlot().getDateTime()))
                .toList();
    }
}
