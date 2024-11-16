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
 * @author Your Name
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
    public static void manualRequest(Scanner sc){
        Medicine medicine = getMedicineInput(sc);
        submitReplenishmentRequest(medicine, sc);
    }

    /**
     * Automatically generates replenishment requests for all medicines with low stock.
     * Checks inventory for medicines below minimum stock level and submits requests.
     *
     * @param sc Scanner object for reading quantity input
     */
    public static void autoRequestLowStockReplenishment(Scanner sc) {
        System.out.println("Checking for medications with low stock...");
        boolean foundLowStock = false;

        for (Medicine medicine : Database.inventoryMap.values()) {
            if (medicine.getIsLowStock()) {
                System.out.println(medicine.getMedicineName() + "has low stock. Requesting replenishment...");
                submitReplenishmentRequest(medicine, sc);
            } 
        }
        if (!foundLowStock) 
            System.out.println("No low stock medicine found. ");
    }

    /**
     * Submits a replenishment request for a specific medicine.
     * Creates and stores a new replenishment request in the system.
     *
     * @param medicine Medicine object for which to submit request
     * @param sc Scanner object for reading quantity input
     * @throws InputMismatchException if invalid quantity is entered
     */
    public static void submitReplenishmentRequest(Medicine medicine, Scanner sc) {
        try {
            System.out.print("Enter quantity for replenishment: ");
            int quantity = sc.nextInt();
            sc.nextLine(); 

            int requestID = Database.requestMap.size() + 1;

            // Submit request
            ReplenishmentRequest request = new ReplenishmentRequest(requestID, medicine.getMedicineName(), quantity);
            Database.requestMap.put(requestID, request);
            System.out.println("Submitted: Request ID " + requestID);
        } catch (InputMismatchException e) {
            System.out.println("Invalid quantity. Please enter a number.");
        }
    }
    
}
