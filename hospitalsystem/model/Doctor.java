package hospitalsystem.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a doctor in the Hospital Management System.
 * A doctor has a list of available appointment slots and upcoming appointments.
 *
 * @author Leo
 * @version 1.0
 * @since 2024-11-19
 */
public class Doctor extends User {
    /**
     * The list of available appointment slots for the doctor.
     */
    private List<Appointment.AppointmentSlot> availableSlots;
    /**
     * The list of upcoming appointments for the doctor.
     */
    private List<Appointment> upcomingAppointments;

    /**
     * Constructs a Doctor object with the given parameters.
     *
     * @param doctorID The unique identifier of the doctor.
     * @param name The name of the doctor.
     * @param age The age of the doctor.
     * @param gender The gender of the doctor.
     * @param password The password of the doctor.
     */
    public Doctor(String doctorID, String name, int age, String gender, String password) {
        super(doctorID, name, age, gender, password);
        this.availableSlots = new ArrayList<>();
        this.upcomingAppointments = new ArrayList<>();
    }

    // Getter and Setter methods

    public List<Appointment.AppointmentSlot> getAvailableSlots() {
        return availableSlots;
    }

    public void setAvailableSlots(List<Appointment.AppointmentSlot> availableSlots) {
        this.availableSlots = availableSlots;
    }

    public List<Appointment> getUpcomingAppointments() {
        return upcomingAppointments;
    }

    public void addAppointment(Appointment appointment) {
        this.upcomingAppointments.add(appointment);
    }

    public void removeAppointment(Appointment appointment) {
        this.upcomingAppointments.remove(appointment);
    }

}