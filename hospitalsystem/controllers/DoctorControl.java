package hospitalsystem.controllers;

import hospitalsystem.MainSystem;
import hospitalsystem.enums.PrescriptionStatus;
import hospitalsystem.model.Doctor;
import hospitalsystem.model.Patient;
import hospitalsystem.model.MedicalRecord;
import hospitalsystem.model.Appointment;
import hospitalsystem.model.Appointment.AppointmentOutcome;
import hospitalsystem.enums.AppointmentStatus;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Scanner;

public class DoctorControl {
    private Doctor doctor; // Removed specialization field as per the update

    public DoctorControl(Doctor doctor) {
        this.doctor = doctor;
    }

    public static void viewPatientMedicalRecord(Patient patient) {
        MedicalRecord mr = patient.getMedicalRecord();
        ArrayList<AppointmentOutcome> appointmentOutcomes = mr.getAppointmentOutcomes();
        int lastSlot = appointmentOutcomes.size()-1;

        System.out.println("====================================");
        System.out.println("           Medical Record           ");
        System.out.println("====================================");
        System.out.println("Patient ID: " + mr.getId());
        System.out.println("Name: " + mr.getName());
        System.out.println("Date of Birth: " + mr.getDOB());
        System.out.println("Gender: " + mr.getGender());
        if (mr.getPhoneNumber().equals("")){
            System.out.println("Phone Number: " + mr.getPhoneNumber());
        }
        if (mr.getEmailAddress().equals("")){
            System.out.println("Email Address: " + mr.getEmailAddress());
        }
        System.out.println("Blood Type: " + mr.getBloodType());
        System.out.println("-----");
        System.out.println("List of Past Appointment Outcomes:");
        for (AppointmentOutcome outcome:appointmentOutcomes){
            System.out.println("Appointment Date: " + outcome.getAppointmentDate());
            System.out.println("Service Type: " + outcome.getServiceType());
            
            System.out.println("Prescriptions:");
            HashMap<String, PrescriptionStatus> prescriptions = outcome.getPrescriptions();
            for (String prescriptionName : prescriptions.keySet()) {
                System.out.println(" - " + prescriptionName + ": " + prescriptions.get(prescriptionName));
            }
            
            System.out.println("Consultation Notes: ");
            System.out.println(outcome.getConsultationNotes());
            if (outcome != appointmentOutcomes.get(lastSlot)){
                System.out.println("-----");
            }
        }
        System.out.println("=====================================");
    }

    public boolean updatePatientMedicalRecord(Patient patient, MedicalRecord record) {
        // Placeholder for updating medical record logic - actual implementation needed
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

    private static Patient findPatientById(String patientId) {
        for (Patient p : MainSystem.patients) {
            if (p.getID().equals(patientId)) {
                return p;
            }
        }
        return null;
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
                String patientId = null;
                Patient patient = null;
                switch (choice) {
                    case 1:
                        System.out.print("Enter Patient ID: ");
                        patientId = scanner.nextLine();
                        patient = findPatientById(patientId);
                        if (patient != null) {
                            viewPatientMedicalRecord(patient);
                        } else {
                            System.out.println("Patient not found.");
                        }
                        break;
                    case 2:
                        System.out.print("Enter Patient ID: ");
                        patientId = scanner.nextLine();
                        patient = findPatientById(patientId);
                        if (patient != null) {
                            // Assuming we have a method to create a new medical record object or get details for update
                            //MedicalRecord updatedRecord = new MedicalRecord updatedRecord = new MedicalRecord(patient); // Create a new medical record from the patient data
                            //updatePatientMedicalRecord(patient, updatedRecord);
                        } else {
                            System.out.println("Patient not found.");
                        }
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
