package hospitalsystem.controllers;

import hospitalsystem.MainSystem;
import hospitalsystem.model.Doctor;
import hospitalsystem.model.Patient;
import hospitalsystem.model.MedicalRecord;
import hospitalsystem.model.Appointment;

import hospitalsystem.enums.AppointmentStatus;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DoctorControl {
    private Doctor doctor; // Removed specialization field as per the update

    public DoctorControl(Doctor doctor) {
        this.doctor = doctor;
    }

    public void viewPatientMedicalRecord(Patient patient) {
        MedicalRecord record = patient.getMedicalRecord();
        System.out.println("Medical Record for " + patient + ":\n" + record.getRecordDetails());
    }

    public boolean updatePatientMedicalRecord(Patient patient, MedicalRecord record) {
        patient.setMedicalRecord(record);
        System.out.println("Medical record updated successfully for patient " + patient);
        return true;
    }

    public boolean setAvailability(List<Appointment.AppointmentSlot> slots) {
        doctor.setAvailableSlots(slots);
        System.out.println("Availability updated successfully.");
        return true;
    }

    public boolean acceptAppointment(Appointment appointment) {
        if (appointment.getStatus() == AppointmentStatus.PENDING) {
            if (doctor.getUpcomingAppointments().contains(appointment)) {
                System.out.println("Appointment is already in the doctor's schedule.");
                return false;
            }
            appointment.setStatus(AppointmentStatus.BOOKED);
            doctor.addAppointment(appointment);
            System.out.println("Appointment accepted for patient " + appointment.getPatient());
            return true;
        }
        return false;
    }

    public boolean declineAppointment(Appointment appointment) {
        if (appointment.getStatus() == AppointmentStatus.PENDING) {
            appointment.setStatus(AppointmentStatus.CANCELLED);
            System.out.println("Appointment declined for patient " + appointment.getPatient());
            return true;
        }
        return false;
    }

    public List<Appointment> viewUpcomingAppointments() {
        List<Appointment> appointments = doctor.getUpcomingAppointments();
        System.out.println("Upcoming Appointments:");
        for (Appointment appointment : appointments) {
            System.out.println("- Patient: " + appointment.getPatient() + ", Date: " + appointment.getSlot().getDay() + ", Time: " + appointment.getSlot().getTime());
        }
        return appointments;
    }

    public boolean recordAppointmentOutcome(Appointment appointment, String outcome) {
        if (doctor.getUpcomingAppointments().contains(appointment)) {
            if (outcome == null || outcome.trim().isEmpty()) {
                System.out.println("Invalid outcome. Please provide a valid outcome description.");
                return false;
            }
            appointment.setOutcome(outcome);
            appointment.setStatus(AppointmentStatus.COMPLETED);
            System.out.println("Appointment outcome recorded for patient " + appointment.getPatient());
            return true;
        }
        return false;
    }

    public static void displayMenu() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("=========================================");
            System.out.println("Doctor Menu");
            System.out.println("1. View Patient Medical Record");
            System.out.println("2. Update Patient Medical Record");
            System.out.println("3. Set Availability");
            System.out.println("4. View Upcoming Appointments");
            System.out.println("5. Record Appointment Outcome");
            System.out.println("6. Logout");
            System.out.print("Enter choice: ");

            try {
                String input = scanner.nextLine();
                int choice = Integer.parseInt(input);
                switch (choice) {
                    case 1:
                        // View Patient MR
                        break;
                    case 2:
                        // Update Patient MR
                        break;
                    case 3:
                        // Set Availability
                        break;
                    case 4:
                        // View upcoming appointments
                        break;
                    case 5:
                        // Record an appointment outcome
                        break;
                    case 6:
                        System.out.println("Logging out...");
                        MainSystem.currentUser = null;
                        return;
                    default:
                        System.out.println("Invalid choice, try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            } catch (Exception e) {
                System.out.println("An error has occurred: " + e.getMessage());
                scanner.nextLine(); // Clear the invalid input
            }
        }
    }
}
