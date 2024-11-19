package hospitalsystem.inventorycontrol;

import hospitalsystem.data.Database;
import hospitalsystem.model.Medicine;
import hospitalsystem.model.ReplenishmentRequest;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;

/**
 * Base class for managing hospital inventory operations.
 * Provides core functionality for viewing and managing medication inventory,
 * including stock levels, batch tracking, and replenishment requests.
 *
 * @author An Xian, Shaivi
 * @version 1.0
 * @since 2024-03-16
 */
public class InventoryControl {

    /*
     * pharmacist view inventory which displays list of medication and stock levels
     * pharmacist submit replenishment request for low stock levels 
     * admin update stock level of medicine 
     * verify that medicine stock level is updated in inventory 
     */
    
    public static void displayInventory() { //used by both 
        if (Database.inventoryMap.isEmpty()) {
            System.out.println("The inventory is currently empty.");
            return;
        }

        // Printing only total quantities
        System.out.printf("%-20s %-15s %-20s%n", "Medicine Name", "Total Quantity", "Low Stock Alert Level");
        System.out.println("-------------------------------------------------------------------------------");
        for (Map.Entry<String, Medicine> entry : Database.inventoryMap.entrySet()) {
            Medicine med = entry.getValue();
            System.out.printf("%-20s %-15s %-20s%n", med.getMedicineName(), med.getTotalQuantity(), (med.getIsLowStock() ? " **LOW STOCK ALERT**" : ""));
            System.out.println();
        }
    }

    public static void displayMedicineBatches(Scanner sc){ 

        Medicine med = getMedicineInput(sc);

        System.out.printf("%-20s %-15s %-20s %-20s%n", "Medicine Name", "Batch Quantity", "Expiration Date", "Low Stock Alert Level");
        System.out.println("-------------------------------------------------------------------------------");
        for (Medicine.Batch batch : med.getBatches()) { 
            System.out.printf("%-20s %-15d %-20s %-20d%n", 
                med.getMedicineName(), batch.getQuantity(), batch.getExpirationDate(), med.getMinStockLevel());
        }
        System.out.println("  Total Quantity: " + med.getTotalQuantity() + (med.getIsLowStock() ? " **LOW STOCK ALERT**" : ""));
    }

    public static void displayAllRequests() { // used by both 
        if (Database.requestMap.isEmpty()) {
            System.out.println("There are no replenishment requests.");
            return;
        }
        // Print in table format  
        System.out.println("Replenishment Request List: ");
        System.out.printf("%-10s %-20s %-10s %-15s%n", "ID", "Medicine Name", "Requested Qty", "Status");
        System.out.println("----------------------------------------------------------------------------");
        for (Map.Entry<Integer, ReplenishmentRequest> entry : Database.requestMap.entrySet()) {
            ReplenishmentRequest req = entry.getValue();
            System.out.printf("%-10s %-20s %-15d %-15s%n", req.getRequestID(), req.getMedicine().getMedicineName(), req.getRequestedQuantity(), req.getStatus());
        }
        System.out.println(" ");
    }

    public static Medicine getMedicineInput(Scanner sc) { //used by both 
        while (true) {
            System.out.println("Enter medicine: ");
            String medicineName = sc.nextLine();
            
            if (!Database.inventoryMap.containsKey(medicineName)) {
                System.out.println( medicineName + " does not exist in the inventory."); 
                continue;
            } 
            Medicine medicine = Database.inventoryMap.get(medicineName);
            return medicine; 
        }
    }

    public static ReplenishmentRequest getRequestInput(Scanner sc) {
        while (true) {
            System.out.println("Enter request ID: ");
            try {
                int requestID = sc.nextInt();
                sc.nextLine();

                if (!Database.requestMap.containsKey(requestID)) {
                    System.out.println("Request ID " + requestID + " is invalid.");
                    continue;
                }
                ReplenishmentRequest request = Database.requestMap.get(requestID);
                return request; 
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid numerical ID.");
            }
        }
    }

}
