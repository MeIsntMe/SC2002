package hospitalsystem.controllers;

import hospitalsystem.model.Appointment;
import hospitalsystem.model.Appointment.AppointmentSlot;
import hospitalsystem.enums.AppointmentStatus;
import hospitalsystem.model.Patient;
import hospitalsystem.model.Doctor;
import hospitalsystem.model.Prescription;
import java.util.List;
import java.util.Scanner;

public class AppointmentControl {
    private Appointment appointment;

    public AppointmentControl(Appointment appointment) {
        this.appointment = appointment;
    }

    public boolean bookSlot() {
        if (appointment.isAvailable()) {
            appointment.setAvailable(false);
            appointment.setStatus(AppointmentStatus.BOOKED);
            System.out.println("Appointment booked successfully.");
            return true;
        }
        System.out.println("Slot is not available.");
        return false;
    }

    public boolean cancelSlot() {
        if (!appointment.isAvailable()) {
            appointment.setAvailable(true);
            appointment.setStatus(AppointmentStatus.CANCELLED);
            System.out.println("Appointment cancelled successfully.");
            return true;
        }
        System.out.println("Slot is already available.");
        return false;
    }

    public void recordOutcome(String outcome) {
        appointment.setOutcome(outcome);
        appointment.setStatus(AppointmentStatus.COMPLETED);
        System.out.println("Appointment outcome recorded.");
    }

    public void viewAppointmentDetails() {
        System.out.println("Appointment ID: " + appointment.getAppointmentID());
        System.out.println("Patient: " + appointment.getPatient().getName());
        System.out.println("Doctor: " + appointment.getDoctor().getName());
        System.out.println("Slot: " + appointment.getSlot());
        System.out.println("Status: " + appointment.getStatus());
        System.out.println("Service Type: " + appointment.getServiceType());
        System.out.println("Consultation Notes: " + appointment.getConsultationNotes());
        System.out.println("Outcome: " + appointment.getOutcome());
    }

    // Static method to generate weekly slots
    public static List<AppointmentSlot> generateWeeklySlots() {
        List<AppointmentSlot> slots = new ArrayList<>();
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
        String[] times = {"9:00 AM - 10:00 AM", "10:30 AM - 11:30 AM", "1:00 PM - 2:00 PM", "2:30 PM - 3:30 PM"};

        for (String day : days) {
            for (String time : times) {
                slots.add(new AppointmentSlot(day, time));
            }
        }

        return slots;
    }
}
