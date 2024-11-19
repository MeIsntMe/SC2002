package hospitalsystem.appointmentcontrol;

import hospitalsystem.data.Database;
import hospitalsystem.enums.AppointmentStatus;
import hospitalsystem.model.Appointment;

/**
 * Manages administrator-specific appointment operations in the hospital system.
 * Provides system-wide appointment oversight and management capabilities.
 *
 * @author Your Name
 * @version 1.0
 * @since 2024-03-16
 */
public class AdminAppointmentControl extends AppointmentControl{

    /**
     * Displays all appointments in the system with their complete details.
     * For completed appointments, also displays the appointment outcome.
     */
    public static void viewAllAppointments(){
        for (Appointment appointment : Database.appointmentMap.values()) {
            System.out.println(appointment);
            if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
                System.out.println(appointment.getAppointmentOutcome());
            }
        }
    }
}
