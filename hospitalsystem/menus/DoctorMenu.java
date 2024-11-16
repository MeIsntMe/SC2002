package hospitalsystem.menus;

import hospitalsystem.HMS;
import hospitalsystem.appointmentcontrol.DoctorAppointmentControl;
import hospitalsystem.data.Database;
import hospitalsystem.enums.*;
import hospitalsystem.model.*;
import hospitalsystem.usercontrol.DoctorUserControl;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class DoctorMenu implements MenuInterface {
    private final Doctor doctor;
    private final Scanner scanner;

    public DoctorMenu(User user) {
        this.doctor = (Doctor) user;
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void displayMenu() {
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
                int choice = Integer.parseInt(scanner.nextLine());

                switch (choice) {
                    case 1:
                        // View Patient Medical Record
                        handleViewPatientRecord();
                        break;
                    case 2:
                        // Update Patient Medical Record
                        handleUpdatePatientRecord();
                        break;
                    case 3:
                        // View Personal Schedule
                        DoctorAppointmentControl.displayPersonalSchedule(doctor);
                        break;
                    case 4:
                        // Set Availablity
                        handleSetAvailability();
                        break;
                    case 5:
                        // Accept/Decline Appointments
                        handleAppointmentRequests();
                        break;
                    case 6:
                        // View Upcoming Appointments
                        DoctorAppointmentControl.displayUpcomingAppointments(doctor);
                        break;
                    case 7:
                        //Record Appointment Outcomes
                        handleRecordOutcome();
                        break;
                    case 8:
                        // Logout
                        HMS.logout();
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

    private void handleViewPatientRecord() {
        System.out.print("Enter Patient ID: ");
        String patientId = scanner.nextLine();
        Patient patient = DoctorUserControl.findPatientById(patientId);
        if (patient != null) {
            DoctorUserControl.displayUserDetails(patient);
        }
    }

    private void handleUpdatePatientRecord() {
        System.out.print("Enter Patient ID: ");
        String patientId = scanner.nextLine();
        Patient patient = DoctorUserControl.findPatientById(patientId);
        if (patient != null) {
            DoctorUserControl.updateUserDetails(patient, this.doctor);
        }
    }

    private void handleSetAvailability() {
        System.out.println("\n=== Managing Doctor Availability ===");
        System.out.println("1. Generate slots for next week");
        System.out.println("2. Mark slot as unavailable");
        System.out.println("3. Mark slot as available");
        System.out.println("4. Return to main menu");

        try {
            int choice = Integer.parseInt(scanner.nextLine());
            switch (choice) {
                case 1:
                    DoctorAppointmentControl.generateNextWeekSlots(doctor);
                    break;
                case 2:
                    handleMarkSlotUnavailable();
                    break;
                case 3:
                    handleMarkSlotAvailable();
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

    private void handleMarkSlotUnavailable() {
        List<Appointment> availableSlots = DoctorAppointmentControl.getAvailableSlots(doctor);
        DoctorAppointmentControl.displayAvailableSlots(availableSlots);

        if (availableSlots.isEmpty()) {
            System.out.println("No available slots to mark as unavailable.");
            return;
        }

        System.out.print("Enter slot number to mark as unavailable (0 to cancel): ");
        try {
            int choice = Integer.parseInt(scanner.nextLine());
            if (choice == 0) return;
            if (choice < 1 || choice > availableSlots.size()) {
                System.out.println("Invalid slot number.");
                return;
            }

            DoctorAppointmentControl.markSlotUnavailable(doctor, availableSlots.get(choice - 1));
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }

    private void handleMarkSlotAvailable() {
        List<Appointment> unavailableSlots = DoctorAppointmentControl.getUnavailableSlots(doctor);
        DoctorAppointmentControl.displayUnavailableSlots(unavailableSlots);

        if (unavailableSlots.isEmpty()) {
            System.out.println("No unavailable slots to mark as available.");
            return;
        }

        System.out.print("Enter slot number to mark as available (0 to cancel): ");
        try {
            int choice = Integer.parseInt(scanner.nextLine());
            if (choice == 0) return;
            if (choice < 1 || choice > unavailableSlots.size()) {
                System.out.println("Invalid slot number.");
                return;
            }

            DoctorAppointmentControl.markSlotAvailable(doctor, unavailableSlots.get(choice - 1));
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }

    private void handleAppointmentRequests() {
        List<Appointment> pendingAppointments = DoctorAppointmentControl.getPendingAppointments(doctor);

        if (pendingAppointments.isEmpty()) {
            System.out.println("No pending appointment requests.");
            return;
        }

        DoctorAppointmentControl.displayPendingAppointments(pendingAppointments);

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
                DoctorAppointmentControl.acceptAppointment(doctor, selectedAppointment);
            } else {
                DoctorAppointmentControl.declineAppointment(doctor, selectedAppointment);
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }

    private void handleRecordOutcome() {
        List<Appointment> bookedAppointments = DoctorAppointmentControl.getBookedAppointments(doctor);

        if (bookedAppointments.isEmpty()) {
            System.out.println("No appointments to record outcomes for.");
            return;
        }

        DoctorAppointmentControl.displayBookedAppointments(bookedAppointments);

        System.out.print("Enter appointment number (0 to cancel): ");
        try {
            int choice = Integer.parseInt(scanner.nextLine());
            if (choice == 0) return;
            if (choice < 1 || choice > bookedAppointments.size()) {
                System.out.println("Invalid appointment number.");
                return;
            }

            Appointment selectedAppointment = bookedAppointments.get(choice - 1);

            // Get consultation notes
            System.out.println("Enter consultation notes (press Enter twice to finish):");
            StringBuilder notes = new StringBuilder();
            String line;
            while (!(line = scanner.nextLine()).isEmpty()) {
                notes.append(line).append("\n");
            }

            // Get prescription
            ArrayList<Prescription.MedicineSet> prescribedMedicineList = new ArrayList<>();
            while (true) {
                System.out.print("Add prescription? (y/n): ");
                if (!scanner.nextLine().toLowerCase().startsWith("y")) break;

                System.out.print("Enter medication name: ");
                String medicineName = scanner.nextLine();

                System.out.print("Enter quantity: ");
                int quantity = Integer.parseInt(scanner.nextLine());

                Medicine medicine = Database.inventoryMap.get(medicineName);
                if (medicine == null) {
                    System.out.println("Medicine not found in inventory.");
                    continue;
                }

                prescribedMedicineList.add(new Prescription.MedicineSet(medicine, quantity));
            }

            Prescription prescription = new Prescription(
                    prescribedMedicineList,
                    doctor.getID(),
                    selectedAppointment.getPatient().getID(),
                    PrescriptionStatus.PENDING
            );

            DoctorAppointmentControl.recordOutcome(selectedAppointment, notes.toString(), prescription);
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }
}
