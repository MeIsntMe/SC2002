package hospitalsystem.usercontrol;
import hospitalsystem.model.*;
import hospitalsystem.data.Database;
import hospitalsystem.enums.PrescriptionStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

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
 * @author Shaivi
 * @version 1.0
 * @since 2024-03-16
 */


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



//    /**
//      * Interactive method to submit a replenishment request and view all requests.
//      * Prompts the user for input and validates the request.
//      *
//      * @param sc Scanner instance for user input.
//      */
//     public static void submitReplenishmentRequest(Scanner sc) {
//         System.out.println("Enter the medication name to request replenishment: ");
//         String medicineName = sc.nextLine();

//         if (!Database.inventoryMap.containsKey(medicineName)) {
//             System.out.println("Medicine " + medicineName + " does not exist in the inventory.");
//             return;
//         }

//         Medicine medicine = Database.inventoryMap.get(medicineName);

//         System.out.println("Enter the quantity for replenishment: ");
//         int quantity;
//         try {
//             quantity = Integer.parseInt(sc.nextLine());
//             if (quantity <= 0) {
//                 System.out.println("Invalid quantity. Please enter a positive number.");
//                 return;
//             }
//         } catch (NumberFormatException e) {
//             System.out.println("Invalid input. Please enter a valid numerical quantity.");
//             return;
//         }

//         // Delegate to the programmatic method
//         submitReplenishmentRequestForMedicine(medicine, quantity);

//         // View all replenishment requests
//         displayReplenishmentRequests();
//     }

//     /**
//      * Displays all replenishment requests in a tabular format.
//      */
//     private static void displayReplenishmentRequests() {
//         System.out.println("\nReplenishment Request Status:");
//         if (Database.requestMap.isEmpty()) {
//             System.out.println("No replenishment requests have been submitted.");
//         } else {
//             System.out.printf("%-10s %-20s %-15s %-15s%n", "Request ID", "Medicine Name", "Quantity", "Status");
//             System.out.println("-----------------------------------------------------------------");
//             for (ReplenishmentRequest req : Database.requestMap.values()) {
//                 System.out.printf("%-10d %-20s %-15d %-15s%n",
//                         req.getRequestID(),
//                         req.getMedicine().getMedicineName(),
//                         req.getRequestedQuantity(),
//                         req.getStatus());
//             }
//         }
//     }
    

//     /**
//      * Checks for expired medicines, removes them from inventory, and submits replenishment requests if necessary.
//      */
//     public static void checkAndHandleExpiredMedicines() {
//         System.out.println("Checking for expired medicines...");
//         boolean foundExpired = false;

//         for (Map.Entry<String, Medicine> entry : Database.inventoryMap.entrySet()) {
//             Medicine medicine = entry.getValue();
//             Iterator<Batch> iterator = medicine.getBatches().iterator();
//             while (iterator.hasNext()) {
//                 Batch batch = iterator.next();
//                 if (batch.isExpired()) {
//                     foundExpired = true;
//                     System.out.println("Removing expired batch of " + medicine.getMedicineName() +
//                                     " (Expiration Date: " + batch.getExpirationDate() + ").");
//                     iterator.remove();
//                 }
//             }

//             // Check if stock is low after removing expired batches
//             if (medicine.getIsLowStock()) {
//                 System.out.println("Low stock detected for " + medicine.getMedicineName() + ". Submitting replenishment request.");
//                 submitReplenishmentRequestForMedicine(medicine, medicine.getMinStockLevel() * 2); // Request double the minimum level
//             }
//         }

//         if (!foundExpired) {
//             System.out.println("No expired medicines found.");
//         }
//     }

//     /**
//      * Programmatic method to submit a replenishment request for a specific medicine.
//      * Typically called by other functions, such as automated inventory checks.
//      *
//      * @param medicine Medicine object for which to request replenishment.
//      * @param quantity Quantity requested.
//      */
//     public static void submitReplenishmentRequestForMedicine(Medicine medicine, int quantity) {
//         int requestID = Database.requestMap.size() + 1; // Simple auto-increment logic
//         ReplenishmentRequest request = new ReplenishmentRequest(requestID, medicine, quantity);
//         Database.requestMap.put(requestID, request);
//         System.out.printf("Replenishment request submitted for medicine: %s, quantity: %d%n", medicine.getMedicineName(), quantity);
//     }
}

