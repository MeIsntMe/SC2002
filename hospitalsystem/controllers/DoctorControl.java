package hospitalsystem.controllers;

import hospitalsystem.MainSystem;
import hospitalsystem.enums.*;
import hospitalsystem.model.*;
import hospitalsystem.model.Appointment.AppointmentOutcome;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class DoctorControl implements MenuInterface{

    @Override
    public void displayMenu() {
        Scanner scanner = new Scanner(System.in);
        Doctor currentDoctor = (Doctor) MainSystem.currentUser;

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
                        handleViewPatientRecord(scanner);
                        break;
                    case 2:
                        handleUpdatePatientRecord(scanner);
                        break;
                    case 3:
                        handleSetAvailability(currentDoctor, scanner);
                        break;
                    case 4:
                        viewUpcomingAppointments(currentDoctor);
                        break;
                    case 5:
                        handleRecordOutcome(currentDoctor, scanner);
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

    private static void handleViewPatientRecord(Scanner scanner) {
        System.out.print("Enter Patient ID: ");
        String patientId = scanner.nextLine();
        Patient patient = findPatientById(patientId, scanner);
        if (patient != null) {
            viewPatientMedicalRecord(patient);
        } else {
            System.out.println("Patient not found.");
        }
    }

    private static Patient findPatientById(String patientId, Scanner scanner) {
        // Create a new Patient instance with the scanner
        return new Patient(patientId, scanner);
    }

    private static void viewPatientMedicalRecord(Patient patient) {
        MedicalRecord mr = patient.getMedicalRecord();
        if (mr == null) {
            System.out.println("Medical record not found for patient ID: " + patient.getID());
            return;
        }

        System.out.println("====================================");
        System.out.println("           Medical Record           ");
        System.out.println("====================================");
        System.out.println("Patient ID: " + mr.getId());
        System.out.println("Name: " + mr.getName());
        System.out.println("Date of Birth: " + mr.getDOB());
        System.out.println("Gender: " + mr.getGender());
        if (!mr.getPhoneNumber().isEmpty()) {
            System.out.println("Phone Number: " + mr.getPhoneNumber());
        }
        if (!mr.getEmailAddress().isEmpty()) {
            System.out.println("Email Address: " + mr.getEmailAddress());
        }
        System.out.println("Blood Type: " + mr.getBloodType());
        System.out.println("-----");

        ArrayList<AppointmentOutcome> appointmentOutcomes = mr.getAppointmentOutcomes();
        if (appointmentOutcomes.isEmpty()) {
            System.out.println("No past appointments found.");
        } else {
            System.out.println("List of Past Appointment Outcomes:");
            for (AppointmentOutcome outcome : appointmentOutcomes) {
                System.out.println("\nAppointment Date: " + outcome.getAppointment().getSlot().getDate());

                HashMap<String, PrescriptionStatus> prescriptions = outcome.getPrescriptions();
                if (!prescriptions.isEmpty()) {
                    System.out.println("Prescriptions:");
                    for (String prescriptionName : prescriptions.keySet()) {
                        System.out.println(" - " + prescriptionName + ": " + prescriptions.get(prescriptionName));
                    }
                }

                String notes = outcome.getConsultationNotes();
                if (notes != null && !notes.trim().isEmpty()) {
                    System.out.println("Consultation Notes: ");
                    System.out.println(notes);
                }
            }
        }
        System.out.println("=====================================");
    }

    private static void viewUpcomingAppointments(Doctor doctor) {
        List<Appointment> appointments = doctor.getUpcomingAppointments();
        if (appointments.isEmpty()) {
            System.out.println("No upcoming appointments.");
            return;
        }

        System.out.println("\nUpcoming Appointments:");
        for (Appointment appointment : appointments) {
            System.out.println("----------------------------------------");
            System.out.println("Appointment ID: " + appointment.getAppointmentID());
            System.out.println("Patient ID: " + appointment.getPatient().getID());
            System.out.println("Date/Time: " + appointment.getSlot().toString());
            System.out.println("Status: " + appointment.getStatus());
        }
    }

    // Placeholder methods for other menu options
    private static void handleUpdatePatientRecord(Scanner scanner) {
        // Implementation needed
        System.out.println("Update patient record functionality coming soon...");
    }

    private static void handleSetAvailability(Doctor currentDoctor, Scanner scanner) {
        // Implementation needed
        System.out.println("Set availability functionality coming soon...");
    }

    private static void handleRecordOutcome(Doctor currentDoctor, Scanner scanner) {
        // Implementation needed
        System.out.println("Record outcome functionality coming soon...");
    }
}