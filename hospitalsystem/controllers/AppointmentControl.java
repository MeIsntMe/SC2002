package hospitalsystem.controllers;

import hospitalsystem.MainSystem;
import hospitalsystem.model.Appointment;
import hospitalsystem.model.Appointment.AppointmentSlot;
import hospitalsystem.enums.AppointmentStatus;
import hospitalsystem.model.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import hospitalsystem.enums.PrescriptionStatus;
import java.time.LocalDateTime;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.BufferedReader;
import java.io.IOException;

public class AppointmentControl {
    private Appointment appointment;
    private static HashMap<String, Appointment> allAppointments = new HashMap<>();

    public AppointmentControl(Appointment appointment) {
        this.appointment = appointment;
    }

    public static void loadAppointmentsFromCSV(String filePath) {
        try (BufferedReader br = Files.newBufferedReader(Paths.get(filePath))) {
            String line;
            boolean isFirstLine = true;
            while ((line = br.readLine()) != null) {
                if (isFirstLine) { // Skip header row
                    isFirstLine = false;
                    continue;
                }
                String[] values = line.split(",");
                if (values.length < 5) continue;

                String appointmentID = values[0];
                String patientID = values[1];
                String doctorID = values[2];
                LocalDateTime dateTime = LocalDateTime.parse(values[3]);
                String statusStr = values[4];

                AppointmentStatus status = AppointmentStatus.valueOf(statusStr);
                Patient patient = MainSystem.findPatientById(patientID);
                Doctor doctor = MainSystem.findDoctorById(doctorID);
                AppointmentSlot slot = new AppointmentSlot(dateTime.getYear(), dateTime.getMonthValue(), dateTime.getDayOfMonth(), dateTime.getHour(), dateTime.getMinute());

                Appointment appointment = new Appointment(appointmentID, patient, doctor, slot);
                appointment.setStatus(status);
                allAppointments.put(appointmentID, appointment);
            }
        } catch (IOException e) {
            System.out.println("Error loading appointments from CSV file: " + e.getMessage());
        }
    }

    public boolean bookSlot() {
        if (appointment.isAvailable()) {
            appointment.setAvailable(false);
            appointment.setStatus(AppointmentStatus.BOOKED);
            System.out.println("Appointment booked successfully.");
            allAppointments.put(appointment.getAppointmentID(), appointment);
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
            allAppointments.put(appointment.getAppointmentID(), appointment);
            return true;
        }
        System.out.println("Slot is already available.");
        return false;
    }

    public boolean rescheduleAppointment(AppointmentSlot newSlot) {
        if (!appointment.isAvailable()) {
            appointment.setAvailable(true);
            appointment.setStatus(AppointmentStatus.CANCELLED);
            appointment.setSlot(newSlot);
            appointment.setAvailable(false);
            appointment.setStatus(AppointmentStatus.BOOKED);
            System.out.println("Appointment rescheduled successfully.");
            allAppointments.put(appointment.getAppointmentID(), appointment);
            return true;
        }
        System.out.println("Cannot reschedule an available appointment.");
        return false;
    }

    public void recordOutcome(String consultationNotes, HashMap<String, PrescriptionStatus> prescriptions) {
        appointment.getAppointmentOutcome().setConsultationNotes(consultationNotes);
        appointment.getAppointmentOutcome().setPrescriptions(prescriptions);
        appointment.setStatus(AppointmentStatus.COMPLETED);
        System.out.println("Appointment outcome recorded.");
        allAppointments.put(appointment.getAppointmentID(), appointment);
    }

    public void viewAppointmentDetails() {
        System.out.println("Appointment ID: " + appointment.getAppointmentID());
        System.out.println("Patient: " + appointment.getPatient());
        System.out.println("Doctor: " + appointment.getDoctor().getName());
        System.out.println("Slot: " + appointment.getSlot());
        System.out.println("Status: " + appointment.getStatus());
        System.out.println("Consultation Notes: " + appointment.getConsultationNotes());
        System.out.println("Prescriptions: ");
        HashMap<String, PrescriptionStatus> prescriptions = appointment.getPrescriptions();
        for (String prescription : prescriptions.keySet()) {
            System.out.println(" - " + prescription + ": " + prescriptions.get(prescription));
        }
    }

    // Static method to generate weekly slots
    public static List<AppointmentSlot> generateWeeklySlots() {
        List<AppointmentSlot> slots = new ArrayList<>();
        int year = LocalDateTime.now().getYear();
        int month = LocalDateTime.now().getMonthValue();
        int day = LocalDateTime.now().getDayOfMonth();
        int[][] times = {{9, 0}, {10, 30}, {13, 0}, {14, 30}};
        for (int i = 0; i < 5; i++) { // Loop through Monday to Friday
            for (int[] time : times) {
                slots.add(new AppointmentSlot(year, month, day + i, time[0], time[1]));
            }
        }
        return slots;
    }

    public static List<Appointment> getAllScheduledAppointments(Patient patient) {
        List<Appointment> scheduledAppointments = new ArrayList<>();
        for (Appointment appointment : allAppointments.values()) {
            if (appointment.getPatient().equals(patient) && appointment.getStatus() == AppointmentStatus.BOOKED) {
                scheduledAppointments.add(appointment);
            }
        }
        return scheduledAppointments;
    }
}
