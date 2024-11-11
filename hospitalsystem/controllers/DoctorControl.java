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

public class DoctorControl {
    public static void displayMenu() {
        Scanner scanner = new Scanner(System.in);
        Doctor currentDoctor = (Doctor) MainSystem.currentUser;

        while (true) {
            System.out.println("=========================================");
            System.out.println("Doctor Menu");
            System.out.println("1. View Patient Medical Record");           // Test Case 9
            System.out.println("2. Update Patient Medical Record");         // Test Case 10
            System.out.println("3. View Personal Schedule");               // Test Case 11
            System.out.println("4. Set Availability");                     // Test Case 12
            System.out.println("5. Accept/Decline Appointments");          // Test Case 13
            System.out.println("6. View Upcoming Appointments");           // Test Case 14
            System.out.println("7. Record Appointment Outcome");           // Test Case 15
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
                        viewPersonalSchedule(currentDoctor);
                        break;
                    case 4:
                        handleSetAvailability(currentDoctor, scanner);
                        break;
                    case 5:
                        handleAppointmentRequests(currentDoctor, scanner);
                        break;
                    case 6:
                        viewUpcomingAppointments(currentDoctor);
                        break;
                    case 7:
                        handleRecordOutcome(currentDoctor, scanner);
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
                scanner.nextLine(); // Clear the invalid input
            }
        }
    }

    private static void handleViewPatientRecord(Scanner scanner) {
        System.out.print("Enter Patient ID: ");
        String patientId = scanner.nextLine();
        Patient patient = findPatientById(patientId);
        if (patient != null) {
            viewPatientMedicalRecord(patient);
        } else {
            System.out.println("Patient not found.");
        }
    }

    private static void viewPersonalSchedule(Doctor doctor) {
        System.out.println("\n=== Personal Schedule ===");

        // Show Available Slots
        List<AppointmentSlot> availableSlots = doctor.getAvailableSlots().stream()
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
        List<Appointment> bookedAppointments = doctor.getUpcomingAppointments().stream()
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

    private static void handleAppointmentRequests(Doctor currentDoctor, Scanner scanner) {
        System.out.println("\n=== Pending Appointment Requests ===");

        List<Appointment> pendingAppointments = currentDoctor.getUpcomingAppointments().stream()
                .filter(apt -> apt.getStatus() == AppointmentStatus.PENDING && !apt.isAvailable())
                .sorted((a1, a2) -> a1.getSlot().getDateTime().compareTo(a2.getSlot().getDateTime()))
                .toList();

        if (pendingAppointments.isEmpty()) {
            System.out.println("No pending appointment requests.");
            return;
        }

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
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }

    private static void handleUpdatePatientRecord(Scanner scanner) {
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
                    addNewMedicalUpdate(patient, (Doctor) MainSystem.currentUser, scanner);
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

    private static void addNewMedicalUpdate(Patient patient, Doctor doctor, Scanner scanner) {
        System.out.println("\n=== Add New Medical Update ===");

        // Get consultation notes
        System.out.println("Enter medical notes (press Enter twice to finish):");
        StringBuilder notes = new StringBuilder();
        String line;
        while (!(line = scanner.nextLine()).isEmpty()) {
            notes.append(line).append("\n");
        }

        // Get prescriptions
        HashMap<String, PrescriptionStatus> prescriptions = new HashMap<>();
        while (true) {
            System.out.print("Add prescription? (y/n): ");
            if (!scanner.nextLine().toLowerCase().startsWith("y")) break;

            System.out.print("Enter medication name: ");
            String medication = scanner.nextLine();
            prescriptions.put(medication, PrescriptionStatus.PENDING);
        }

        // Create a new appointment record for this update
        String appointmentID = String.format("MR_%s_%d", patient.getID(), System.currentTimeMillis());

        // Create slot with current time
        LocalDateTime now = LocalDateTime.now();
        Appointment.AppointmentSlot slot = new Appointment(appointmentID, patient, doctor, null)
                .new AppointmentSlot(
                now.getYear(),
                now.getMonthValue(),
                now.getDayOfMonth(),
                now.getHour(),
                now.getMinute()
        );

        // Create and configure appointment
        Appointment appointment = new Appointment(appointmentID, patient, doctor, slot);
        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointment.setAvailable(false);

        // Record the outcome
        AppointmentControl.recordOutcome(
                appointmentID,
                notes.toString(),
                prescriptions
        );

        // Update medical record's appointment outcomes
        ArrayList<AppointmentOutcome> outcomes = patient.getMedicalRecord().getAppointmentOutcomes();
        if (outcomes == null) {
            outcomes = new ArrayList<>();
        }
        outcomes.add(appointment.getAppointmentOutcome());
        patient.getMedicalRecord().setAppointmentOutcomes(outcomes);

        System.out.println("Medical record updated successfully.");
    }

    private static Patient findPatientById(String patientId) {
        User user = MainSystem.patientsMap.get(patientId);
        if (user instanceof Patient) {
            return (Patient) user;
        }
        return null;
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

    private static void handleSetAvailability(Doctor currentDoctor, Scanner scanner) {
        System.out.println("\n=== Managing Doctor Availability ===");

        // Get current and next week slots
        List<Appointment.AppointmentSlot> thisWeekSlots = getThisWeekSlots(currentDoctor);
        List<Appointment.AppointmentSlot> nextWeekSlots = getNextWeekSlots(currentDoctor);

        boolean hasThisWeekSlots = !thisWeekSlots.isEmpty();
        boolean hasNextWeekSlots = !nextWeekSlots.isEmpty();

        if (!hasThisWeekSlots && !hasNextWeekSlots) {
            // No slots created yet
            System.out.println("No slots found. Creating slots for this week and next week.");
            AppointmentControl.generateWeeklySlots(currentDoctor); // This week
            AppointmentControl.generateWeeklySlots(currentDoctor); // Next week
            displayAvailableSlots(currentDoctor);

        } else if (hasThisWeekSlots && !hasNextWeekSlots) {
            // Only this week exists, create next week
            System.out.println("Creating slots for next week.");
            AppointmentControl.generateWeeklySlots(currentDoctor);
            displayAvailableSlots(currentDoctor);

        } else {
            // Both weeks exist, allow modifications
            manageExistingSlots(currentDoctor, scanner);
        }
    }

    private static void manageExistingSlots(Doctor doctor, Scanner scanner) {
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
                        displayAvailableSlots(doctor);
                        break;
                    case 2:
                        markSlotUnavailable(doctor, scanner);
                        break;
                    case 3:
                        markSlotAvailable(doctor, scanner);
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

    private static void markSlotUnavailable(Doctor doctor, Scanner scanner) {
        System.out.println("\n=== Mark Slot as Unavailable ===");
        List<Appointment> availableAppointments = doctor.getUpcomingAppointments().stream()
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
                    doctor.getID(),
                    selectedAppointment.getSlot(),
                    false  // makeAvailable = false
            );
            System.out.println("Slot marked as unavailable successfully.");

        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }

    private static void markSlotAvailable(Doctor doctor, Scanner scanner) {
        System.out.println("\n=== Mark Slot as Available ===");
        List<Appointment> unavailableAppointments = doctor.getUpcomingAppointments().stream()
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
                    doctor.getID(),
                    selectedAppointment.getSlot(),
                    true  // makeAvailable = true
            );
            System.out.println("Slot marked as available successfully.");

        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }

    private static void handleRecordOutcome(Doctor currentDoctor, Scanner scanner) {
        // Show upcoming appointments first
        List<Appointment> appointments = currentDoctor.getUpcomingAppointments().stream()
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

            // Get prescriptions
            HashMap<String, PrescriptionStatus> prescriptions = new HashMap<>();
            while (true) {
                System.out.print("Add prescription? (y/n): ");
                if (!scanner.nextLine().toLowerCase().startsWith("y")) break;

                System.out.print("Enter medication name: ");
                String medication = scanner.nextLine();
                prescriptions.put(medication, PrescriptionStatus.PENDING);
            }

            // Record the outcome
            AppointmentControl.recordOutcome(
                    selectedAppointment.getAppointmentID(),
                    notes.toString(),
                    prescriptions
            );

            System.out.println("Appointment outcome recorded successfully.");

        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }

    private static List<Appointment.AppointmentSlot> getThisWeekSlots(Doctor doctor) {
        LocalDateTime now = LocalDateTime.now();
        return doctor.getAvailableSlots().stream()
                .filter(slot -> {
                    LocalDateTime slotTime = slot.getDateTime();
                    long daysBetween = ChronoUnit.DAYS.between(now.toLocalDate(), slotTime.toLocalDate());
                    return daysBetween >= 0 && daysBetween <= 7;
                })
                .toList();
    }

    private static List<Appointment.AppointmentSlot> getNextWeekSlots(Doctor doctor) {
        LocalDateTime now = LocalDateTime.now();
        return doctor.getAvailableSlots().stream()
                .filter(slot -> {
                    LocalDateTime slotTime = slot.getDateTime();
                    long daysBetween = ChronoUnit.DAYS.between(now.toLocalDate(), slotTime.toLocalDate());
                    return daysBetween > 7 && daysBetween <= 14;
                })
                .toList();
    }

    private static void displayAvailableSlots(Doctor doctor) {
        List<Appointment.AppointmentSlot> slots = doctor.getAvailableSlots();
        System.out.println("\n=== Available Slots ===");
        System.out.println("This Week:");
        for (int i = 0; i < slots.size(); i++) {
            Appointment.AppointmentSlot slot = slots.get(i);
            System.out.printf("%d. %s %s\n", i + 1, slot.getDate(), slot.getTime());
        }
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