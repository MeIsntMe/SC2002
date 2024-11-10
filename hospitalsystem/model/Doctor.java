package hospitalsystem.model;

import java.util.ArrayList;
import java.util.List;
import hospitalsystem.controllers.DoctorControl;
import hospitalsystem.enums.AppointmentStatus;
import hospitalsystem.model.Appointment.AppointmentSlot;
import hospitalsystem.model.Patient;

public class Doctor extends User {
    private String doctorID;
    private int age; 
    private List<Appointment.AppointmentSlot> availableSlots;
    private List<Appointment> upcomingAppointments;

    public Doctor(String doctorID, String name, String gender, int age, String password) {
        super(doctorID, name, gender, password);
        this.age = age;
        this.doctorID = doctorID;
        this.availableSlots = new ArrayList<>();
        this.upcomingAppointments = new ArrayList<>();
    }
    //An Xian: why is there another doctor ID when ID is alr in User 

    // Getter and Setter methods
    public String getDoctorID() {
        return doctorID;
    }

    public int getAge() {
        return age;
    }

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

    public void displayMenu() {
        DoctorControl.displayMenu();
    }
}
