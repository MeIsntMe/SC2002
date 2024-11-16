package hospitalsystem.usercontrol;
import hospitalsystem.model.*;
import hospitalsystem.data.Database;
import hospitalsystem.enums.PrescriptionStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Handles the core logic and operations for pharmacists.
 */
public class PharmacistUserControl {

/**
 * Displays all appointment outcome records, including associated prescriptions,
 * for the pharmacist. Fetches data from the shared database to ensure accuracy.
 *
 * @param sc Scanner instance for user input.
 */
    public static void viewAppointmentOutcomeRecord(Scanner sc) {
        System.out.print("Enter Appointment ID to view prescription details: ");
        String appointmentID = sc.nextLine();

        // Retrieve the appointment from the database
        Appointment appointment = Database.appointmentMap.get(appointmentID);

        if (appointment == null) {
            System.out.println("No appointment found with ID: " + appointmentID);
            return;
        }

        // Display appointment details
        System.out.printf("Appointment ID: %s | Patient ID: %s | Doctor ID: %s%n",
                appointment.getAppointmentID(),
                appointment.getPatient().getID(),
                appointment.getDoctor().getID());

        // Retrieve the prescription
        Prescription prescription = appointment.getPrescription();

        if (prescription != null && !prescription.getMedicineList().isEmpty()) {
            System.out.println("Prescriptions:");
            for (Map.Entry<Medicine, Integer> entry : prescription.getMedicineList().entrySet()) {
                Medicine medicine = entry.getKey();
                int dosage = entry.getValue();
                System.out.printf("    Medicine: %s | Dosage: %d | Status: %s%n",
                        medicine.getMedicineName(),
                        dosage,
                        prescription.getStatus());
            }
            System.out.println("--------------------------------------------------");
        } else {
            System.out.println("No prescriptions associated with this appointment.");
        }
    }


/**
 * Updates the status of a specific prescription for a given appointment.
 *
 * @param sc Scanner instance for user input.
 */
    public static void updatePrescriptionStatus(Scanner sc) {
        // Prompt for appointment ID
        System.out.print("Enter Appointment ID to update prescription status: ");
        String appointmentID = sc.nextLine();

        // Validate and fetch appointment
        Appointment appointment = Database.appointmentMap.get(appointmentID);
        if (appointment == null) {
            System.out.println("No appointment found with ID: " + appointmentID);
            return;
        }

        // Fetch prescription associated with the appointment
        Prescription prescription = appointment.getPrescription();
        if (prescription == null || prescription.getMedicineList().isEmpty()) {
            System.out.println("No prescriptions found for this appointment.");
            return;
        }

        // Display prescriptions in the appointment
        System.out.println("Prescriptions in Appointment " + appointmentID + ":");
        List<Medicine> medicines = new ArrayList<>(prescription.getMedicineList().keySet());
        for (int i = 0; i < medicines.size(); i++) {
            Medicine medicine = medicines.get(i);
            System.out.printf("%d. %s (Status: %s)%n", i + 1, medicine.getMedicineName(), prescription.getStatus());
        }

        // Prompt for prescription to update
        System.out.print("Select prescription to update (by number): ");
        int prescriptionIndex;
        try {
            prescriptionIndex = Integer.parseInt(sc.nextLine()) - 1;
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid number.");
            return;
        }

        // Validate selected prescription
        if (prescriptionIndex < 0 || prescriptionIndex >= medicines.size()) {
            System.out.println("Invalid selection. Please select a valid prescription.");
            return;
        }

        Medicine selectedMedicine = medicines.get(prescriptionIndex);

        // Prompt for new status
        System.out.print("Enter new status (e.g., PENDING, DISPENSED, REJECTED): ");
        String statusInput = sc.nextLine().toUpperCase();

        try {
            PrescriptionStatus newStatus = PrescriptionStatus.valueOf(statusInput);
            prescription.setStatus(newStatus); // Update prescription status
            System.out.printf("Prescription status for %s updated to %s successfully.%n",
                    selectedMedicine.getMedicineName(), newStatus);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid status entered. Please use PENDING, DISPENSED, or REJECTED.");
        }
    }



   /**
     * Handles the process of submitting a replenishment request for a specific medication.
     * Prompts the user to enter the medication name and quantity for replenishment.
     * Validates if the medication exists in the inventory and ensures the entered quantity is valid.
     * Creates a new replenishment request and adds it to the system's request map.
     * After submission, displays all replenishment requests in a tabular format,
     * including their status (e.g., PENDING, APPROVED, REJECTED).
     *
     * @param sc Scanner instance for capturing user input.
     */
    
    public static void submitReplenishmentRequest(Scanner sc) {
        // Step 1: Submit a replenishment request
        System.out.println("Enter the medication name to request replenishment: ");
        String medicineName = sc.nextLine();
    
        if (!Database.inventoryMap.containsKey(medicineName)) {
            System.out.println("Medicine " + medicineName + " does not exist in the inventory.");
            return;
        }
    
        Medicine medicine = Database.inventoryMap.get(medicineName);
    
        System.out.println("Enter the quantity for replenishment: ");
        int quantity;
        try {
            quantity = Integer.parseInt(sc.nextLine());
            if (quantity <= 0) {
                System.out.println("Invalid quantity. Please enter a positive number.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid numerical quantity.");
            return;
        }
    
        // Generate a unique request ID
        int requestID = Database.requestMap.size() + 1; // Simple auto-increment logic
    
        // Create a new replenishment request
        ReplenishmentRequest request = new ReplenishmentRequest(requestID, medicine, quantity);
        Database.requestMap.put(request.getRequestID(), request);
    
        System.out.printf("Replenishment request submitted for medicine: %s, quantity: %d%n", medicineName, quantity);
    
        // Step 2: View all replenishment requests
        System.out.println("\nReplenishment Request Status:");
        if (Database.requestMap.isEmpty()) {
            System.out.println("No replenishment requests have been submitted.");
        } else {
            System.out.printf("%-10s %-20s %-15s %-15s%n", "Request ID", "Medicine Name", "Quantity", "Status");
            System.out.println("-----------------------------------------------------------------");
            for (ReplenishmentRequest req : Database.requestMap.values()) {
                System.out.printf("%-10d %-20s %-15d %-15s%n",
                        req.getRequestID(),
                        req.getMedicine().getMedicineName(),
                        req.getRequestedQuantity(),
                        req.getStatus());
            }
        }
    }
    

    // can remove since function already there in inventory control as displayinventory
    /**
     * Displays the inventory details for all medicines.
     */
    public static void viewInventory() {
        if (Database.inventoryMap.isEmpty()) {
            System.out.println("The inventory is currently empty.");
            return;
        }

        System.out.printf("%-20s %-15s %-15s%n", "Medicine Name", "Total Quantity", "Low Stock Alert");
        System.out.println("-------------------------------------------------------------");
        for (Map.Entry<String, Medicine> entry : Database.inventoryMap.entrySet()) {
            Medicine medicine = entry.getValue();
            System.out.printf("%-20s %-15d %-15s%n",
                    medicine.getMedicineName(),
                    medicine.getTotalQuantity(),
                    (medicine.getIsLowStock() ? "**LOW STOCK**" : ""));
        }
    }

}

