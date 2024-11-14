package hospitalsystem.model;

import java.util.ArrayList;
import java.util.List;

public class Doctor extends User {
    private List<Appointment.AppointmentSlot> availableSlots;
    private List<Appointment> upcomingAppointments;

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