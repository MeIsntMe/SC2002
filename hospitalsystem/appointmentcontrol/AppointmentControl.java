package hospitalsystem.appointmentcontrol;

import hospitalsystem.data.*;
import hospitalsystem.enums.*;
import hospitalsystem.model.*;
import hospitalsystem.model.Appointment.AppointmentOutcome;
import hospitalsystem.model.Appointment.AppointmentSlot;
import java.time.LocalDateTime;
import java.util.*;

public class AppointmentControl {

    // Utility methods for managing appointments
    public static List<Appointment> getAppointmentsByDoctorID(String doctorID) {
        return Database.appointmentMap.values().stream()
                .filter(appointment -> appointment.getDoctor().getID().equals(doctorID))
                .sorted()
                .toList();
    }

    public static List<Appointment> getAppointmentsByPatientID(String patientID) {
        return Database.appointmentMap.values().stream()
                .filter(appointment -> appointment.getPatient().getID().equals(patientID))
                .sorted()
                .toList();
    }

    public static Appointment getAppointmentByAppointmentID(String appointmentID) {
        return Database.appointmentMap.get(appointmentID);
    }
    public static boolean cancelSlot(String appointmentID) {
        Appointment appointment = Database.appointmentMap.get(appointmentID);
        if (appointment != null && !appointment.getIsAvailable()) {
            appointment.setIsAvailable(true);
            appointment.setStatus(AppointmentStatus.CANCELLED);
            Database.appointmentMap.put(appointmentID, appointment);
            appointment.getDoctor().removeAppointment(appointment);
            return true;
        }
        return false;
    }

    public static List<Appointment> getAvailableSlots(Doctor doctor) {
        return Database.appointmentMap.values().stream()
                .filter(apt -> apt.getDoctor().getID().equals(doctor.getID()))
                .filter(Appointment::getIsAvailable)
                .sorted((a1, a2) -> a1.getSlot().getDateTime().compareTo(a2.getSlot().getDateTime()))
                .toList();
    }

    public static List<Appointment> getUnavailableSlots(Doctor doctor) {
        return Database.appointmentMap.values().stream()
                .filter(apt -> apt.getDoctor().getID().equals(doctor.getID()))
                .filter(apt -> !apt.getIsAvailable() && apt.getStatus() == AppointmentStatus.UNAVAILABLE)
                .sorted((a1, a2) -> a1.getSlot().getDateTime().compareTo(a2.getSlot().getDateTime()))
                .toList();
    }

    public static List<Appointment> getPendingAppointments(Doctor doctor) {
        return Database.appointmentMap.values().stream()
                .filter(apt -> apt.getDoctor().getID().equals(doctor.getID()))
                .filter(apt -> apt.getStatus() == AppointmentStatus.PENDING && !apt.getIsAvailable())
                .sorted((a1, a2) -> a1.getSlot().getDateTime().compareTo(a2.getSlot().getDateTime()))
                .toList();
    }

    public static List<Appointment> getBookedAppointments(Doctor doctor) {
        return Database.appointmentMap.values().stream()
                .filter(apt -> apt.getDoctor().getID().equals(doctor.getID()))
                .filter(apt -> apt.getStatus() == AppointmentStatus.BOOKED)
                .sorted((a1, a2) -> a1.getSlot().getDateTime().compareTo(a2.getSlot().getDateTime()))
                .toList();
    }

    public static String generateAppointmentID(String doctorID, AppointmentSlot slot) {
        LocalDateTime dt = slot.getDateTime();
        return String.format("APT_%s_%d%02d%02d%02d%02d",
                doctorID,
                dt.getYear(),
                dt.getMonthValue(),
                dt.getDayOfMonth(),
                dt.getHour(),
                dt.getMinute()
        );
    }

    public static boolean updatePrescriptionStatus(Prescription prescription, PrescriptionStatus newStatus) {
        prescription.setStatus(newStatus);
        return true;
    }
    

    public static boolean addAppointment(int slotIndex, Doctor doctor, Patient patient){
        try {
            List<AppointmentSlot> newSlots = doctor.getAvailableSlots();
            AppointmentSlot selectedslot = newSlots.remove(slotIndex);
            doctor.setAvailableSlots(newSlots);
            Appointment newAppointment = new Appointment(AppointmentControl.generateAppointmentID(doctor.getID(), selectedslot), patient, doctor, selectedslot);
            doctor.addAppointment(newAppointment);

            List<Appointment> newAppointments = patient.getAppointments();
            newAppointments.add(newAppointment);
            patient.setAppointments(newAppointments);

            Database.appointmentMap.put(patient.getID(), newAppointment);
            Database.appointmentMap.put(doctor.getID(), newAppointment);

            return true;
        } catch (Exception e){
            System.out.println("Error Occured: " + e);
            return false;
        }
    }

    public static String getAppointmentOutcomesString(Patient patient, String gap){
        StringBuilder sb = new StringBuilder();
        sb.append(gap).append("Appointment Outcomes: ");

        // Assuming patient.getCompletedAppointments() returns a List<AppointmentOutcome>
        List<AppointmentOutcome> outcomes = patient.getMedicalRecord().getAppointmentOutcomes();
        
        if (outcomes != null && !outcomes.isEmpty()) {
            for (AppointmentOutcome outcome : outcomes) {
                sb.append(gap).append("\n  ").append(outcome); // Uses outcome's toString method
            }
        } else {
            sb.append(gap).append("\n  No completed appointments.");
        }

        return sb.toString();
    }
    
}