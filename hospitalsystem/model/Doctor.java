package hospitalsystem.model;

import java.util.ArrayList;
import java.util.List;

import hospitalsystem.MainSystem;
import hospitalsystem.controllers.DoctorControl;
import hospitalsystem.enums.AppointmentStatus;
import hospitalsystem.model.Appointment.AppointmentSlot;

public class Doctor extends User {
    private int age;
    private List<Appointment.AppointmentSlot> availableSlots;
    private List<Appointment> upcomingAppointments;

    public Doctor(String doctorID, String name, String gender, int age, String password) {
        super(doctorID, name, gender, password);
        this.age = age;
        this.availableSlots = new ArrayList<>();
        this.upcomingAppointments = new ArrayList<>();
    }

    // Getter and Setter methods
    public int getAge() {return age;}
    
    public void setAge(int age ) {this.age = age; }

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

    public static void displayMenu() {
        DoctorControl control = new DoctorControl(MainSystem.currentUser);
        control.displayMenu();
    }
}