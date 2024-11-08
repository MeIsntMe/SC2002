package hospitalsystem.model;

import java.util.ArrayList;
import java.util.List;
import hospitalsystem.controllers.DoctorControl;
import hospitalsystem.enums.Specialization;
import hospitalsystem.enums.AppointmentStatus;
import hospitalsystem.model.Patient;

public class Doctor extends User {
    private String doctorID;
    private Specialization specialization;
    private List<AppointmentSlot> availableSlots;
    private List<Appointment> upcomingAppointments;

    public Doctor(String doctorID, String name, String gender, String password, Specialization specialization) {
        super(doctorID, name, gender, password);
        this.doctorID = doctorID;
        this.specialization = specialization;
        this.availableSlots = new ArrayList<>();
        this.upcomingAppointments = new ArrayList<>();
    }

    // Getter and Setter methods
    public String getDoctorID() {
        return doctorID;
    }

    public Specialization getSpecialization() {
        return specialization;
    }

    public void setSpecialization(Specialization specialization) {
        this.specialization = specialization;
    }

    public List<AppointmentSlot> getAvailableSlots() {
        return availableSlots;
    }

    public void setAvailableSlots(List<AppointmentSlot> availableSlots) {
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

    @Override
    public void displayMenu() {
        DoctorControl.displayMenu();
    }
}
