package hospitalsystem.usercontrol;
import hospitalsystem.appointmentcontrol.PharmacistAppointmentControl;
import hospitalsystem.data.Database;
import hospitalsystem.enums.PrescriptionStatus;
import hospitalsystem.model.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Handles pharmacist-specific operations in the hospital system.
 * Provides functionality for managing prescriptions, viewing appointment outcomes,
 * managing inventory, and handling replenishment requests.
 *
 * Key responsibilities include:
 * - Prescription status management
 * - Appointment outcome viewing
 * - Inventory monitoring
 * - Replenishment request handling
 *
 * This class interacts directly with the system database to ensure
 * real-time accuracy of pharmaceutical records and inventory status.
 *
 * @author An Xian, Shaivi
 * @version 1.0
 * @since 2024-11-19
 */


/**
 * Handles the core logic and operations for pharmacists.
 */
public class PharmacistUserControl extends UserControl{

    /**
     * Displays all appointment outcome records, including associated prescriptions,
     * for the pharmacist. Fetches data from the shared database to ensure accuracy.
     *
     */
    public static void viewAppointmentOutcomeRecord() {
        PharmacistAppointmentControl.viewCompletedAppointments();
        
        System.out.print("Enter Appointment ID to view prescription details: ");
        String appointmentID = sc.nextLine();

        // Retrieve the appointment from the database
        Appointment appointment = Database.appointmentMap.get(appointmentID);

        if (appointment == null) {
            System.out.println("No appointment found with ID: " + appointmentID);
            return;
        }

        // Display appointment details
        System.out.println("\nAppointment Details:");
        System.out.printf("Appointment ID: %s%n", appointment.getAppointmentID());
        System.out.printf("Patient ID: %s%n", appointment.getPatient().getID());
        System.out.printf("Doctor ID: %s%n", appointment.getDoctor().getID());
        System.out.printf("Status: %s%n", appointment.getStatus());

        // Display consultation notes if available
        String notes = appointment.getConsultationNotes();
        if (notes != null && !notes.trim().isEmpty()) {
            System.out.println("\nConsultation Notes: " + notes);
        }

        // Get and display prescription information
        Prescription prescription = appointment.getPrescription();
        if (prescription != null && prescription.getMedicineList() != null && !prescription.getMedicineList().isEmpty()) {
            System.out.println("\nPrescription Details:");
            System.out.printf("Status: %s%n", prescription.getStatus());
            System.out.println("Prescribed Medications:");

            for (Map.Entry<Medicine, Integer> entry : prescription.getMedicineList().entrySet()) {
                Medicine medicine = entry.getKey();
                int quantity = entry.getValue();
                System.out.printf("  - %s (Quantity: %d)%n",
                        medicine.getMedicineName(),
                        quantity);
            }
        } else {
            System.out.println("\nNo prescriptions associated with this appointment.");
        }

        System.out.println("\n--------------------------------------------------");
    }


    /**
     * Updates the status of a specific prescription for a given appointment.
    */
    public static void updatePrescriptionStatus() {
        // Prompt for appointment ID
        System.out.print("Enter Appointment ID to update prescription status: ");
        String appointmentID = sc.nextLine();

        // Validate and fetch appointment
        Appointment appointment = Database.appointmentMap.get(appointmentID);
        if (appointment == null) {
            System.out.println("No appointment found with ID: " + appointmentID);
            return;
        }

        // Get appointment outcome and prescription
        Appointment.AppointmentOutcome outcome = appointment.getAppointmentOutcome();
        if (outcome == null) {
            System.out.println("No outcome record found for this appointment.");
            return;
        }

        Prescription prescription = outcome.getPrescription();
        if (prescription == null || prescription.getMedicineList().isEmpty()) {
            System.out.println("No prescriptions found for this appointment.");
            return;
        }

        // Display current prescriptions
        System.out.println("\nCurrent Prescriptions in Appointment " + appointmentID + ":");
        List<Medicine> medicines = new ArrayList<>(prescription.getMedicineList().keySet());
        for (int i = 0; i < medicines.size(); i++) {
            Medicine medicine = medicines.get(i);
            int quantity = prescription.getMedicineList().get(medicine);
            System.out.printf("%d. %s (Quantity: %d, Status: %s)%n",
                    i + 1,
                    medicine.getMedicineName(),
                    quantity,
                    prescription.getStatus());
        }

        // Get and validate prescription selection
        System.out.print("\nSelect prescription to update (by number): ");
        int prescriptionIndex;
        try {
            prescriptionIndex = Integer.parseInt(sc.nextLine()) - 1;
            if (prescriptionIndex < 0 || prescriptionIndex >= medicines.size()) {
                System.out.println("Invalid selection. Please select a valid prescription.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid number.");
            return;
        }

        // Update prescription status
        System.out.print("Enter new status (PENDING/DISPENSED/REJECTED): ");
        String statusInput = sc.nextLine().toUpperCase();
        try {
            PrescriptionStatus newStatus = PrescriptionStatus.valueOf(statusInput);
            prescription.setStatus(newStatus);
            System.out.printf("Prescription status for %s updated to %s successfully.%n",
                    medicines.get(prescriptionIndex).getMedicineName(),
                    newStatus);

            // Update the database
            Database.appointmentMap.put(appointmentID, appointment);
            Database.saveAppointmentsToCSV();
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid status. Please use PENDING, DISPENSED, or REJECTED.");
        }
    }
}
