package hospitalsystem.appointmentcontrol;

import hospitalsystem.data.*;
import hospitalsystem.enums.*;
import hospitalsystem.model.*;
import java.util.List;


public class PatientAppointmentControl extends AppointmentControl{
    // move majority of patient appointment related stuff here 
    public static List<Appointment> getScheduledSlots(Patient patient) {
        return Database.appointmentMap.values().stream()
                .filter(apt -> apt.getPatient().getID().equals(patient.getID()))
                .filter(apt -> !apt.getIsAvailable() && apt.getStatus() != AppointmentStatus.COMPLETED)
                .sorted((a1, a2) -> a1.getSlot().getDateTime().compareTo(a2.getSlot().getDateTime()))
                .toList();
    }
}
