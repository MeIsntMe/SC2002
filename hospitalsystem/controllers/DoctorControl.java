package hospitalsystem.controllers;

import hospitalsystem.MainSystem;
import hospitalsystem.enums.*;
import hospitalsystem.model.*;
import hospitalsystem.model.Appointment.AppointmentSlot;
import hospitalsystem.model.Appointment.AppointmentOutcome;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class DoctorControl implements MenuInterface{
    private final Doctor doctor;  // Add instance variable

    public DoctorControl(User user) {  // Add constructor
        if (!(user instanceof Doctor)) {
            throw new IllegalArgumentException("User must be a Doctor");
        }
        this.doctor = (Doctor) user;
    }

    @Override
    public void displayMenu() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("=========================================");
            System.out.println("Doctor Menu");
            System.out.println("1. View Patient Medical Record");
            System.out.println("2. Update Patient Medical Record");
            System.out.println("3. View Personal Schedule");
            System.out.println("4. Set Availability");
            System.out.println("5. Accept/Decline Appointments");
            System.out.println("6. View Upcoming Appointments");
            System.out.println("7. Record Appointment Outcome");
            System.out.println("8. Logout");
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
                        viewPersonalSchedule();  // Changed to use instance method
                        break;
                    case 4:
                        handleSetAvailability(scanner);
                        break;
                    case 5:
                        handleAppointmentRequests(scanner);  // Updated to use instance method
                        break;
                    case 6:
                        viewUpcomingAppointments();  // Changed to use instance method
                        break;
                    case 7:
                        handleRecordOutcome(scanner);
                        break;
                    case 8:
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
                scanner.nextLine();
            }
        }
    }

    private void handleViewPatientRecord(Scanner scanner) {
        System.out.print("Enter Patient ID: ");
        String patientId = scanner.nextLine();
        Patient patient = findPatientById(patientId);
        if (patient != null) {
            viewPatientMedicalRecord(patient);
        } else {
            System.out.println("Patient not found.");
        }
    }

    private void viewPersonalSchedule() {
        System.out.println("\n=== Personal Schedule ===");

        // Show Available Slots
        List<AppointmentSlot> availableSlots = this.doctor.getAvailableSlots().stream()
                .sorted((s1, s2) -> s1.getDateTime().compareTo(s2.getDateTime()))
                .toList();

        System.out.println("\nAvailable Slots:");
        if (availableSlots.isEmpty()) {
            System.out.println("No available slots set.");
        } else {
            for (AppointmentSlot slot : availableSlots) {
                System.out.printf("- %s %s\n", slot.getDate(), slot.getTime());
            }
        }

        // Show Booked Appointments
        List<Appointment> bookedAppointments = this.doctor.getUpcomingAppointments().stream()
                .filter(apt -> apt.getStatus() == AppointmentStatus.BOOKED)
                .sorted((a1, a2) -> a1.getSlot().getDateTime().compareTo(a2.getSlot().getDateTime()))
                .toList();

        System.out.println("\nBooked Appointments:");
        if (bookedAppointments.isEmpty()) {
            System.out.println("No booked appointments.");
        } else {
            for (Appointment apt : bookedAppointments) {
                System.out.printf("- %s %s - Patient: %s\n",
                        apt.getSlot().getDate(),
                        apt.getSlot().getTime(),
                        apt.getPatient().getName());
            }
        }
    }

    private void handleUpdatePatientRecord(Scanner scanner) {
        System.out.print("Enter Patient ID: ");
        String patientId = scanner.nextLine();
        Patient patient = findPatientById(patientId);
        if (patient == null) {
            System.out.println("Patient not found.");
            return;
        }

        MedicalRecord mr = patient.getMedicalRecord();
        if (mr == null) {
            System.out.println("Medical record not found.");
            return;
        }

        System.out.println("\n=== Update Patient Medical Record ===");
        System.out.println("Current Record:");
        System.out.println("Patient ID: " + mr.getId());
        System.out.println("Name: " + mr.getName());
        System.out.println("Date of Birth: " + mr.getDOB());
        System.out.println("Gender: " + mr.getGender());
        System.out.println("Blood Type: " + mr.getBloodType());

        System.out.println("\nWhat would you like to update?");
        System.out.println("1. Add New Medical Update");
        System.out.println("2. Return to Main Menu");

        try {
            int choice = Integer.parseInt(scanner.nextLine());
            switch (choice) {
                case 1:
                    addNewMedicalUpdate(patient, scanner);
                    break;
                case 2:
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }

    private void addNewMedicalUpdate(Patient patient, Scanner scanner) {
        System.out.println("\n=== Add New Medical Update ===");

        // Get consultation notes
        System.out.println("Enter medical notes (press Enter twice to finish):");
        StringBuilder notes = new StringBuilder();
        String line;
        while (!(line = scanner.nextLine()).isEmpty()) {
            notes.append(line).append("\n");
        }

        // Get prescriptions (always PENDING)
        List<AppointmentControl.Prescription> prescriptions = new ArrayList<>();
        while (true) {
            System.out.print("Add prescription? (y/n): ");
            if (!scanner.nextLine().toLowerCase().startsWith("y")) break;

            System.out.print("Enter medication name: ");
            String medication = scanner.nextLine();
            prescriptions.add(new AppointmentControl.Prescription(medication, PrescriptionStatus.PENDING));
        }

        // Create a new appointment record for this update
        String appointmentID = String.format("MR_%s_%d", patient.getID(), System.currentTimeMillis());

        // Create slot with current time
        LocalDateTime now = LocalDateTime.now();
        Appointment.AppointmentSlot slot = new Appointment.AppointmentSlot(
                now.getYear(),
                now.getMonthValue(),
                now.getDayOfMonth(),
                now.getHour(),
                now.getMinute()
        );

        // Create and configure appointment
        Appointment appointment = new Appointment(appointmentID, patient, this.doctor, slot);
        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointment.setAvailable(false);

        // Record the outcome
        AppointmentControl.recordOutcome(
                appointmentID,
                notes.toString(),
                prescriptions
        );

        System.out.println("Medical record updated successfully.");
    }

    private Patient findPatientById(String patientId) {
        User user = MainSystem.patientsMap.get(patientId);
        if (user instanceof Patient) {
            return (Patient) user;
        }
        return null;
    }

    private void viewPatientMedicalRecord(Patient patient) {
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

                // Display prescriptions using new format
                List<AppointmentControl.Prescription> prescriptions =
                        AppointmentControl.getAppointmentPrescriptions(outcome.getAppointment().getAppointmentID());

                if (!prescriptions.isEmpty()) {
                    System.out.println("Prescriptions:");
                    for (AppointmentControl.Prescription prescription : prescriptions) {
                        System.out.printf(" - %s: %s\n",
                                prescription.getMedicineName(),
                                prescription.getStatus());
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

    private void handleSetAvailability(Scanner scanner) {
        AppointmentControl.loadAppointmentsFromCSV();

        System.out.println("\n=== Managing Doctor Availability ===");

        List<Appointment.AppointmentSlot> thisWeekSlots = getThisWeekSlots();
        List<Appointment.AppointmentSlot> nextWeekSlots = getNextWeekSlots();

        boolean hasThisWeekSlots = !thisWeekSlots.isEmpty();
        boolean hasNextWeekSlots = !nextWeekSlots.isEmpty();

        if (!hasThisWeekSlots && !hasNextWeekSlots) {
            System.out.println("No slots found. Creating slots for this week and next week.");
            AppointmentControl.generateWeeklySlots(this.doctor);
            AppointmentControl.generateWeeklySlots(this.doctor);
            displayAvailableSlots();
        } else if (hasThisWeekSlots && !hasNextWeekSlots) {
            System.out.println("Creating slots for next week.");
            AppointmentControl.generateWeeklySlots(this.doctor);
            displayAvailableSlots();
        } else {
            manageExistingSlots(scanner);
        }

        AppointmentControl.saveAppointmentsToCSV();
    }

    private void handleAppointmentRequests(Scanner scanner) {
        // 1. Load CSV to HashMap
        AppointmentControl.loadAppointmentsFromCSV();

        System.out.println("\n=== Pending Appointment Requests ===");
        List<Appointment> pendingAppointments = this.doctor.getUpcomingAppointments().stream()
                .filter(apt -> apt.getStatus() == AppointmentStatus.PENDING && !apt.isAvailable())
                .sorted((a1, a2) -> a1.getSlot().getDateTime().compareTo(a2.getSlot().getDateTime()))
                .toList();

        if (pendingAppointments.isEmpty()) {
            System.out.println("No pending appointment requests.");
            return;
        }

        // Display pending appointments
        for (int i = 0; i < pendingAppointments.size(); i++) {
            Appointment apt = pendingAppointments.get(i);
            System.out.printf("%d. Patient: %s - Date: %s %s\n",
                    i + 1,
                    apt.getPatient().getName(),
                    apt.getSlot().getDate(),
                    apt.getSlot().getTime());
        }

        System.out.print("Enter appointment number to process (0 to cancel): ");
        try {
            int choice = Integer.parseInt(scanner.nextLine());
            if (choice == 0) return;
            if (choice < 1 || choice > pendingAppointments.size()) {
                System.out.println("Invalid appointment number.");
                return;
            }

            Appointment selectedAppointment = pendingAppointments.get(choice - 1);
            System.out.print("Accept appointment? (y/n): ");
            String response = scanner.nextLine().trim().toLowerCase();

            if (response.startsWith("y")) {
                selectedAppointment.setStatus(AppointmentStatus.BOOKED);
                System.out.println("Appointment accepted successfully.");
            } else {
                selectedAppointment.setStatus(AppointmentStatus.CANCELLED);
                selectedAppointment.setAvailable(true);
                System.out.println("Appointment declined.");
            }

            // Save changes to CSV
            AppointmentControl.saveAppointmentsToCSV();

        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }

    private void manageExistingSlots(Scanner scanner) {
        while (true) {
            System.out.println("\n=== Manage Availability ===");
            System.out.println("1. View all slots");
            System.out.println("2. Mark slot as unavailable");
            System.out.println("3. Mark slot as available");
            System.out.println("4. Return to main menu");
            System.out.print("Enter choice: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());
                switch (choice) {
                    case 1:
                        displayAvailableSlots();
                        break;
                    case 2:
                        markSlotUnavailable(scanner);
                        break;
                    case 3:
                        markSlotAvailable(scanner);
                        break;
                    case 4:
                        return;
                    default:
                        System.out.println("Invalid choice.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private void markSlotUnavailable(Scanner scanner) {
        System.out.println("\n=== Mark Slot as Unavailable ===");
        List<Appointment> availableAppointments = this.doctor.getUpcomingAppointments().stream()
                .filter(Appointment::isAvailable)
                .sorted((a1, a2) -> a1.getSlot().getDateTime().compareTo(a2.getSlot().getDateTime()))
                .toList();

        if (availableAppointments.isEmpty()) {
            System.out.println("No available slots to mark as unavailable.");
            return;
        }

        // Display available slots
        System.out.println("Available Slots:");
        for (int i = 0; i < availableAppointments.size(); i++) {
            Appointment apt = availableAppointments.get(i);
            AppointmentSlot slot = apt.getSlot();
            System.out.printf("%d. %s %s\n",
                    i + 1,
                    slot.getDate(),
                    slot.getTime()
            );
        }

        // Get slot selection
        System.out.print("Enter slot number to mark as unavailable (0 to cancel): ");
        try {
            int slotNum = Integer.parseInt(scanner.nextLine());
            if (slotNum == 0) {
                System.out.println("Operation cancelled.");
                return;
            }
            if (slotNum < 1 || slotNum > availableAppointments.size()) {
                System.out.println("Invalid slot number.");
                return;
            }

            // Update the slot
            Appointment selectedAppointment = availableAppointments.get(slotNum - 1);
            AppointmentControl.updateSlotAvailability(
                    this.doctor.getID(),
                    selectedAppointment.getSlot(),
                    false  // makeAvailable = false
            );
            System.out.println("Slot marked as unavailable successfully.");

            // Save changes to CSV
            AppointmentControl.saveAppointmentsToCSV();

        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }

    private void markSlotAvailable(Scanner scanner) {
        System.out.println("\n=== Mark Slot as Available ===");
        List<Appointment> unavailableAppointments = this.doctor.getUpcomingAppointments().stream()
                .filter(apt -> !apt.isAvailable() && apt.getStatus() == AppointmentStatus.UNAVAILABLE)
                .sorted((a1, a2) -> a1.getSlot().getDateTime().compareTo(a2.getSlot().getDateTime()))
                .toList();

        if (unavailableAppointments.isEmpty()) {
            System.out.println("No unavailable slots to mark as available.");
            return;
        }

        // Display unavailable slots
        System.out.println("Unavailable Slots:");
        for (int i = 0; i < unavailableAppointments.size(); i++) {
            Appointment apt = unavailableAppointments.get(i);
            AppointmentSlot slot = apt.getSlot();
            System.out.printf("%d. %s %s\n",
                    i + 1,
                    slot.getDate(),
                    slot.getTime()
            );
        }

        // Get slot selection
        System.out.print("Enter slot number to mark as available (0 to cancel): ");
        try {
            int slotNum = Integer.parseInt(scanner.nextLine());
            if (slotNum == 0) {
                System.out.println("Operation cancelled.");
                return;
            }
            if (slotNum < 1 || slotNum > unavailableAppointments.size()) {
                System.out.println("Invalid slot number.");
                return;
            }

            // Update the slot
            Appointment selectedAppointment = unavailableAppointments.get(slotNum - 1);
            AppointmentControl.updateSlotAvailability(
                    this.doctor.getID(),
                    selectedAppointment.getSlot(),
                    true  // makeAvailable = true
            );
            System.out.println("Slot marked as available successfully.");

            // Save changes to CSV
            AppointmentControl.saveAppointmentsToCSV();

        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }

    private void handleRecordOutcome(Scanner scanner) {
        // 1. Load CSV to HashMap
        AppointmentControl.loadAppointmentsFromCSV();

        List<Appointment> appointments = this.doctor.getUpcomingAppointments().stream()
                .filter(apt -> apt.getStatus() == AppointmentStatus.BOOKED)
                .toList();

        if (appointments.isEmpty()) {
            System.out.println("No appointments to record outcomes for.");
            return;
        }

        System.out.println("\n=== Record Appointment Outcome ===");
        for (int i = 0; i < appointments.size(); i++) {
            Appointment apt = appointments.get(i);
            System.out.printf("%d. %s - Patient: %s - Date: %s %s\n",
                    i + 1,
                    apt.getAppointmentID(),
                    apt.getPatient().getName(),
                    apt.getSlot().getDate(),
                    apt.getSlot().getTime());
        }

        System.out.print("Enter appointment number (or 0 to cancel): ");
        try {
            int choice = Integer.parseInt(scanner.nextLine());
            if (choice == 0) return;
            if (choice < 1 || choice > appointments.size()) {
                System.out.println("Invalid appointment number.");
                return;
            }

            Appointment selectedAppointment = appointments.get(choice - 1);

            // Get consultation notes
            System.out.println("Enter consultation notes (press Enter twice to finish):");
            StringBuilder notes = new StringBuilder();
            String line;
            while (!(line = scanner.nextLine()).isEmpty()) {
                notes.append(line).append("\n");
            }

            // Get prescriptions (always PENDING)
            List<AppointmentControl.Prescription> prescriptions = new ArrayList<>();
            while (true) {
                System.out.print("Add prescription? (y/n): ");
                if (!scanner.nextLine().toLowerCase().startsWith("y")) break;

                System.out.print("Enter medication name: ");
                String medication = scanner.nextLine();
                prescriptions.add(new AppointmentControl.Prescription(medication, PrescriptionStatus.PENDING));
            }

            // Record the outcome
            AppointmentControl.recordOutcome(
                    selectedAppointment.getAppointmentID(),
                    notes.toString(),
                    prescriptions
            );

            System.out.println("Appointment outcome recorded successfully.");

            // Save changes to CSV
            AppointmentControl.saveAppointmentsToCSV();

        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }


    private List<Appointment.AppointmentSlot> getThisWeekSlots() {
        LocalDateTime now = LocalDateTime.now();
        return this.doctor.getAvailableSlots().stream()
                .filter(slot -> {
                    LocalDateTime slotTime = slot.getDateTime();
                    long daysBetween = ChronoUnit.DAYS.between(now.toLocalDate(), slotTime.toLocalDate());
                    return daysBetween >= 0 && daysBetween <= 7;
                })
                .toList();
    }

    private List<Appointment.AppointmentSlot> getNextWeekSlots() {  // Changed from static
        LocalDateTime now = LocalDateTime.now();
        return this.doctor.getAvailableSlots().stream()
                .filter(slot -> {
                    LocalDateTime slotTime = slot.getDateTime();
                    long daysBetween = ChronoUnit.DAYS.between(now.toLocalDate(), slotTime.toLocalDate());
                    return daysBetween > 7 && daysBetween <= 14;
                })
                .toList();
    }

    private void displayAvailableSlots() {
        List<Appointment.AppointmentSlot> slots = this.doctor.getAvailableSlots();
        System.out.println("\n=== Available Slots ===");
        System.out.println("This Week:");
        for (int i = 0; i < slots.size(); i++) {
            Appointment.AppointmentSlot slot = slots.get(i);
            System.out.printf("%d. %s %s\n", i + 1, slot.getDate(), slot.getTime());
        }
    }

    private boolean isSlotInThisWeek(AppointmentSlot slot) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime slotTime = slot.getDateTime();
        long daysBetween = ChronoUnit.DAYS.between(now.toLocalDate(), slotTime.toLocalDate());
        return daysBetween >= 0 && daysBetween <= 7;
    }

    private boolean isSlotInNextWeek(AppointmentSlot slot) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime slotTime = slot.getDateTime();
        long daysBetween = ChronoUnit.DAYS.between(now.toLocalDate(), slotTime.toLocalDate());
        return daysBetween > 7 && daysBetween <= 14;
    }

    private void viewUpcomingAppointments() {
        List<Appointment> appointments = this.doctor.getUpcomingAppointments();
        if (appointments.isEmpty()) {
            System.out.println("No upcoming appointments.");
            return;
        }

        System.out.println("\nUpcoming Appointments:");
        for (Appointment appointment : appointments) {
            System.out.println("----------------------------------------");
            System.out.println("Appointment ID: " + appointment.getAppointmentID());
            if (appointment.getPatient() != null) {
                System.out.println("Patient ID: " + appointment.getPatient().getID());
                System.out.println("Patient Name: " + appointment.getPatient().getName());
            } else {
                System.out.println("Status: Available");
            }
            System.out.println("Date/Time: " + appointment.getSlot().toString());
            System.out.println("Status: " + appointment.getStatus());
        }
    }
}