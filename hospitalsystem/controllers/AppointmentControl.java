package hospitalsystem.controllers;

import hospitalsystem.MainSystem;
import hospitalsystem.model.Appointment;
import hospitalsystem.model.Appointment.AppointmentSlot;
import hospitalsystem.enums.AppointmentStatus;
import hospitalsystem.model.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import hospitalsystem.enums.PrescriptionStatus;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

//central database for appointments
public class AppointmentControl {
    private static HashMap<String, Appointment> allAppointments = new HashMap<>();

    public static void loadAppointmentsFromFile(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine(); // Skip header row
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                String appointmentID = values[0].trim();
                String patientID = values[1].trim();
                String doctorID = values[2].trim();
                String dateTimeStr = values[3].trim();
                LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
                String statusStr = values[4].trim();

                AppointmentStatus status = AppointmentStatus.valueOf(statusStr);
                Patient patient = AppointmentControl.findPatientById(patientID);
                Doctor doctor = AppointmentControl.findDoctorById(doctorID);
                AppointmentSlot slot = new AppointmentSlot(dateTime.getYear(), dateTime.getMonthValue(), dateTime.getDayOfMonth(), dateTime.getHour(), dateTime.getMinute());

                Appointment appointment = new Appointment(appointmentID, patient, doctor, slot);
                appointment.setStatus(status);
                allAppointments.put(appointmentID, appointment);
            }
        } catch (IOException e) {
            System.out.println("Error loading appointments from file: " + e.getMessage());
        }
    }

    private static Patient findPatientById(String patientID) {
        for (Patient patient : MainSystem.patients) {
            if (patient.getID().equals(patientID)) {
                return patient;
            }
        }
        return null;
    }

    private static Doctor findDoctorById(String doctorID) {
        for (Doctor doctor : MainSystem.doctors) {
            if (doctor.getID().equals(doctorID)) {
                return doctor;
            }
        }
        return null;
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
