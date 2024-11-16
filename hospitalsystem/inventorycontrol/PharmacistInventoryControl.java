package hospitalsystem.inventorycontrol;

import java.util.InputMismatchException;
import java.util.Scanner;

import hospitalsystem.data.Database;
import hospitalsystem.model.Medicine;
import hospitalsystem.model.ReplenishmentRequest;

/**
 * Manages pharmacist-specific inventory operations.
 * Provides functionality for submitting and managing replenishment requests
 * and monitoring stock levels.
 *
 * @author Shaivi
 * @version 1.0
 * @since 2024-03-16
 */
public class PharmacistInventoryControl extends InventoryControl {

    /**
     * Submits a manual replenishment request for a specific medicine.
     * Allows pharmacists to request stock replenishment with custom quantities.
     *
     * @param sc Scanner object for reading medicine and quantity input
     */
    public static void manualRequest(Scanner sc) {
        Medicine medicine = getMedicineInput(sc);
    
        if (medicine == null) {
            System.out.println("Medicine not found in inventory.");
            return;
        }
    
        System.out.print("Enter the quantity for replenishment: ");
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
    
        // Delegate to the programmatic method
        submitReplenishmentRequestForMedicine(medicine, quantity);
    }

    /**
     * Automatically submit replenishment requests for medicines with low stock.
     */
    public static void autoRequestLowStockReplenishment() {
        System.out.println("Checking for medications with low stock...");
        boolean foundLowStock = false;

        for (Medicine medicine : Database.inventoryMap.values()) {
            if (medicine.getIsLowStock()) {
                foundLowStock = true;
                System.out.println("Low stock detected for: " + medicine.getMedicineName());
                submitReplenishmentRequestForMedicine(medicine, medicine.getMinStockLevel() * 2);
            }
        }

        if (!foundLowStock) {
            System.out.println("No low-stock medicines found.");
        }
    }

    /**
     * Check for expired medicines, remove them, and submit replenishment requests if needed.
     */
    public static void checkAndHandleExpiredMedicines() {
        System.out.println("Checking for expired medicines...");
        boolean foundExpired = false;

        for (Medicine medicine : Database.inventoryMap.values()) {
            Iterator<Batch> iterator = medicine.getBatches().iterator();

            while (iterator.hasNext()) {
                Batch batch = iterator.next();
                if (batch.isExpired()) {
                    foundExpired = true;
                    System.out.println("Removing expired batch of " + medicine.getMedicineName() +
                            " (Expiration Date: " + batch.getExpirationDate() + ").");
                    iterator.remove();
                }
            }

            // Check stock and request replenishment if low after removing expired batches
            if (medicine.getIsLowStock()) {
                System.out.println("Low stock detected for: " + medicine.getMedicineName());
                submitReplenishmentRequestForMedicine(medicine, medicine.getMinStockLevel() * 2);
            }
        }

        if (!foundExpired) {
            System.out.println("No expired medicines found.");
        }
    }

    /**
     * Interactive method to submit a replenishment request and view all requests.
     * Prompts the user for input and validates the request.
     *
     * @param sc Scanner instance for user input.
     */
    public static void submitReplenishmentRequest(Scanner sc) {
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
    
        // Delegate to the programmatic method
        submitReplenishmentRequestForMedicine(medicine, quantity);
    
        // View all replenishment requests
        displayReplenishmentRequests();
    }
    

    /**
     * Programmatic method to submit a replenishment request for a specific medicine.
     * Typically called by other functions, such as automated inventory checks.
     *
     * @param medicine Medicine object for which to request replenishment.
     * @param quantity Quantity requested.
     */
    public static void submitReplenishmentRequestForMedicine(Medicine medicine, int quantity) {
        int requestID = Database.requestMap.size() + 1; // Auto-increment ID
        ReplenishmentRequest request = new ReplenishmentRequest(requestID, medicine, quantity);
        Database.requestMap.put(requestID, request);
        System.out.printf("Replenishment request submitted for medicine: %s, quantity: %d%n",
                medicine.getMedicineName(), quantity);
    }
    

    /**
     * Displays all replenishment requests in a tabular format.
     */
    private static void displayReplenishmentRequests() {
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
    
}
