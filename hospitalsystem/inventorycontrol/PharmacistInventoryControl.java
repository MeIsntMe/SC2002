package hospitalsystem.inventorycontrol;

import java.util.InputMismatchException;
import java.util.Scanner;

import hospitalsystem.data.Database;
import hospitalsystem.model.Medicine;
import hospitalsystem.model.ReplenishmentRequest; 

public class PharmacistInventoryControl extends InventoryControl {

    // Method to manually submit replenishment request
    public static void manualRequest(Scanner sc){
        Medicine medicine = getMedicineInput(sc);
        submitReplenishmentRequest(medicine, sc);
    }

    // Method to automatically submit replenishment requests for low stock medicines
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
