package hospitalsystem.appointmentcontrol;

import hospitalsystem.data.Database;
import hospitalsystem.enums.AppointmentStatus;
import hospitalsystem.model.Appointment;

public class AdminAppointmentControl extends AppointmentControl{
    // view all scheduled appointments (include appointment deets)
    public static void viewAllAppointments(){
        for (Appointment appointment : Database.appointmentMap.values()) {
            System.out.println(appointment);
            if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
                System.err.println(appointment.getAppointmentOutcome());
            }
        }
    }
}
