package hospitalsystem.appointmentcontrol;

import hospitalsystem.data.*;
import hospitalsystem.enums.*;
import hospitalsystem.model.*;
import hospitalsystem.model.Appointment.AppointmentOutcome;
import hospitalsystem.model.Appointment.AppointmentSlot;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Base class providing core appointment management functionality for the hospital system.
 * This class implements common utility methods used across different user roles for
 * appointment retrieval, filtering, and management.
 *
 * @author Gracelynn, Leo
 * @version 1.0
 * @since 2024-03-16
 */
public class AppointmentControl {

    /**
     * Retrieves all appointments associated with a specific doctor.
     *
     * @param doctorID the unique identifier of the doctor
     * @return a sorted list of appointments assigned to the specified doctor
     */
    public static List<Appointment> getAppointmentsByDoctorID(String doctorID) {
        return Database.appointmentMap.values().stream()
                .filter(appointment -> appointment.getDoctor().getID().equals(doctorID))
                .sorted()
                .toList();
    }

    /**
     * Retrieves all appointments for a specific patient.
     *
     * @param patientID the unique identifier of the patient
     * @return a sorted list of appointments booked by the specified patient
     */
    public static List<Appointment> getAppointmentsByPatientID(String patientID) {
        return Database.appointmentMap.values().stream()
                .filter(appointment -> appointment.getPatient().getID().equals(patientID))
                .sorted()
                .toList();
    }

    /**
     * Retrieves a specific appointment by its ID.
     *
     * @param appointmentID the unique identifier of the appointment
     * @return the appointment matching the specified ID, or null if not found
     */
    public static Appointment getAppointmentByAppointmentID(String appointmentID) {
        return Database.appointmentMap.get(appointmentID);
    }

    /**
     * Retrieves available appointment slots for a specific doctor.
     *
     * @param doctor the doctor whose available slots are to be retrieved
     * @return a sorted list of available appointments
     */
    public static List<Appointment> getAvailableSlots(Doctor doctor) {
        return Database.appointmentMap.values().stream()
                .filter(apt -> apt.getDoctor().getID().equals(doctor.getID()))
                .filter(Appointment::getIsAvailable)
                .sorted((a1, a2) -> a1.getSlot().getDateTime().compareTo(a2.getSlot().getDateTime()))
                .toList();
    }

    /**
     * Retrieves unavailable appointment slots for a specific doctor.
     *
     * @param doctor the doctor whose unavailable slots are to be retrieved
     * @return a sorted list of unavailable appointments
     */
    public static List<Appointment> getUnavailableSlots(Doctor doctor) {
        return Database.appointmentMap.values().stream()
                .filter(apt -> apt.getDoctor().getID().equals(doctor.getID()))
                .filter(apt -> !apt.getIsAvailable() && apt.getStatus() == AppointmentStatus.UNAVAILABLE)
                .sorted((a1, a2) -> a1.getSlot().getDateTime().compareTo(a2.getSlot().getDateTime()))
                .toList();
    }

    /**
     * Retrieves pending appointments for a specific doctor.
     *
     * @param doctor the doctor whose pending appointments are to be retrieved
     * @return a sorted list of pending appointments
     */
    public static List<Appointment> getPendingAppointments(Doctor doctor) {
        return Database.appointmentMap.values().stream()
                .filter(apt -> apt.getDoctor().getID().equals(doctor.getID()))
                .filter(apt -> apt.getStatus() == AppointmentStatus.PENDING && !apt.getIsAvailable())
                .sorted((a1, a2) -> a1.getSlot().getDateTime().compareTo(a2.getSlot().getDateTime()))
                .toList();
    }

    /**
     * Retrieves booked appointments for a specific doctor.
     *
     * @param doctor the doctor whose booked appointments are to be retrieved
     * @return a sorted list of booked appointments
     */
    public static List<Appointment> getBookedAppointments(Doctor doctor) {
        return Database.appointmentMap.values().stream()
                .filter(apt -> apt.getDoctor().getID().equals(doctor.getID()))
                .filter(apt -> apt.getStatus() == AppointmentStatus.BOOKED)
                .sorted((a1, a2) -> a1.getSlot().getDateTime().compareTo(a2.getSlot().getDateTime()))
                .toList();
    }

    /**
     * Generates a unique appointment ID based on doctor ID and appointment slot.
     * Format: APT_[doctorID]_[YYYYMMDDHHMM]
     *
     * @param doctorID the doctor's unique identifier
     * @param slot the appointment slot containing date and time information
     * @return a formatted appointment ID string
     */
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

    /**
     * Generates a formatted string containing all appointment outcomes for a patient.
     *
     * @param patient the patient whose appointment outcomes are to be retrieved
     * @param gap the string to use for indentation/spacing in the output
     * @return a formatted string containing all appointment outcomes
     */
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