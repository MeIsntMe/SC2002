package hospitalsystem.inventorycontrol;

import hospitalsystem.HMS;
import hospitalsystem.data.Database;
import hospitalsystem.enums.RequestStatus;
import hospitalsystem.model.Medicine;
import hospitalsystem.model.ReplenishmentRequest;
import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


public class InventoryControl {
    
    public static void displayInventory() {

        if (!Database.inventoryMap.isEmpty()) {
            System.out.printf("%-20s %-15s %-20s %-20s%n", "Medicine Name", "Batch Quantity", "Expiration Date", "Low Stock Alert Level");
            System.out.println("-------------------------------------------------------------------------------");
            for (Map.Entry<String, Medicine> entry : Database.inventoryMap.entrySet()) {
                Medicine med = entry.getValue();
                for (Medicine.Batch batch : med.getBatches()) { 
                    System.out.printf("%-20s %-15d %-20s %-20d%n", 
                        med.getMedicineName(), batch.getQuantity(), batch.getExpirationDate(), med.getLowStockAlert());
                }
                System.out.println("  Total Quantity: " + med.getTotalQuantity() + (med.isLowStock() ? " **LOW STOCK ALERT**" : ""));
                System.out.println();
            }
        } else {
            System.out.println("The inventory is currently empty.");
            return;
        }
    }

    // Getter for inventoryMap
    public Map<String, Medicine> getInventoryMap() {
        return inventoryMap;
    }
    // Getter for requestMap
    public Map<String, ReplenishmentRequest> getRequestMap() {
        return requestMap;
    }

    // Store a new replenishment request
    public void addReplenishmentRequest(ReplenishmentRequest request) {
        requestMap.put(request.getMedicineName(), request);
        System.out.println("Replenishment request added for " + request.getMedicineName() +
                           " with quantity " + request.getRequestedQuantity());
    }

    // Retrieve pending requests
    public Iterable<ReplenishmentRequest> getPendingRequests() {
        return requestMap.values().stream()
                .filter(request -> request.getStatus() == RequestStatus.PENDING)
                .toList();
    }

    // Add a new batch with an expiration date for an approved replenishment request
    public boolean addBatchToInventory(String medicineName, int quantity, LocalDate expirationDate) {
        ReplenishmentRequest request = requestMap.get(medicineName);
        if (request != null && request.getStatus() == RequestStatus.APPROVED) {
            // Check if the medicine exists in the inventory
            Medicine medicine = Database.inventoryMap.get(medicineName);
            if (medicine == null) {
                medicine = new Medicine(medicineName, 10); // Default low stock alert threshold
                Database.inventoryMap.put(medicineName, medicine);
            }

            // Add a new batch with specified quantity and expiration date
            medicine.addBatch(quantity, expirationDate);
            requestMap.remove(medicineName);  // Remove the request once fulfilled
            System.out.println("Added batch of " + quantity + " units for " + medicineName +
                               " with expiration date " + expirationDate + ".");
            return true;
        } else {
            System.out.println("Request for " + medicineName + " is not approved or does not exist.");
            return false;
        }
    }


    public static void addMedicine(Scanner sc) {
        boolean repeat;
        do {
            System.out.print("Enter name of medicine to add: ");
            String medicineName = sc.nextLine();
            System.out.print("Enter initial stock level for the batch: ");
            int initialStock = sc.nextInt();
            System.out.print("Enter low stock alert level: ");
            int lowStockAlert = sc.nextInt();
            System.out.print("Enter expiration date (YYYY-MM-DD): ");
            LocalDate expirationDate = LocalDate.parse(sc.next().trim());
            sc.nextLine();

            // Check if the medicine already exists in the inventory; if not, create a new one
            Medicine medicine = Database.inventoryMap.get(medicineName);
            if (medicine == null) {
                medicine = new Medicine(medicineName, lowStockAlert);
                Database.inventoryMap.put(medicineName, medicine);
            }

            // Add the new batch with its quantity and expiration date
            medicine.addBatch(initialStock, expirationDate);
            System.out.printf("%s successfully added into inventory%n", medicineName);
            
            // Option to repeat the addition
            repeat = HMS.repeat(sc);
        } while (repeat);
    }

    public static void addMedicine(Scanner sc) {
        while (true) {
            System.out.print("Enter name of medicine to add: ");
            String medicineName = sc.nextLine();
            System.out.print("Enter initial stock level for the batch: ");
            int initialStock = sc.nextInt();
            System.out.print("Enter low stock alert level: ");
            int lowStockAlert = sc.nextInt();
            System.out.print("Enter expiration date (YYYY-MM-DD): ");
            LocalDate expirationDate = LocalDate.parse(sc.next().trim());
            sc.nextLine();

            // Create new medicine if it does not already exist 
            Medicine medicine = Database.inventoryMap.get(medicineName);
            if (medicine == null) {
                medicine = new Medicine(medicineName, lowStockAlert);
                Database.inventoryMap.put(medicineName, medicine);
            }

            // Add the new batch with its quantity and expiration date
            medicine.addBatch(initialStock, expirationDate);
            System.out.printf("%s successfully added into inventory%n", medicineName);
            
            // Give option to repeat 
            if (!HMS.repeat()) break;
        }
    }

    public static void removeMedicine(Scanner sc) {
        boolean repeat;
        System.out.println("=========================================");  
        System.out.println("Inventory Management > Remove Medicine");
        do {
            System.out.print("Enter name of medicine to remove: ");
            String medicineName = sc.nextLine();
            
            if (Database.inventoryMap.containsKey(medicineName)) {
                Database.inventoryMap.remove(medicineName);
                System.out.println(medicineName + " has been removed from the inventory.");
            } else {
                System.out.println("Medicine not found in inventory: " + medicineName);
            }
            
            // Option to repeat
            repeat = HMS.getRepeatChoice(sc);
        } while (repeat);
    }

    public static void updateStockLevel(Scanner sc) {
        boolean repeat; 
        System.out.println("=========================================");  
        System.out.println("Inventory Management > Update Stock");
        do {
            System.out.print("Enter the name of the medicine to update: ");
            String medicineName = sc.nextLine().trim();

            // Check if medicine exists
            if (Database.inventoryMap.containsKey(medicineName)) {
                Medicine medicine = Database.inventoryMap.get(medicineName);
                
                // Prompt for new batch stock
                System.out.println("Current total stock level for " + medicineName + " is " + medicine.getTotalQuantity());
                System.out.println("Enter the new stock level for the new batch: ");
                int newBatchStock = Integer.parseInt(sc.nextLine().trim());
                System.out.print("Enter expiration date for the new batch (YYYY-MM-DD): ");
                LocalDate expirationDate = LocalDate.parse(sc.nextLine().trim());

                // Add the new batch
                medicine.addBatch(newBatchStock, expirationDate);
                System.out.println("New batch added to " + medicineName + " with quantity " + newBatchStock);
            } else {
                System.out.println("Medicine not found in inventory: " + medicineName);
            }
            
            // Option to repeat
            repeat = HMS.getRepeatChoice(sc);
        }  while (repeat);
    } 

    // Additional unchanged methods for checking stock, low stock, etc., from the original code
    public int checkStock(String medicineName) {
        Medicine medicine = Database.inventoryMap.get(medicineName);
        return medicine != null ? medicine.getTotalQuantity() : -1;
    }

    public boolean isLowStock(String medicineName) {
        Medicine medicine = Database.inventoryMap.get(medicineName);
        return medicine != null && medicine.isLowStock();
    }

    public List<String> getLowStockMedications() {
        List<String> lowStockMedications = new ArrayList<>();
        for (Map.Entry<String, Medicine> entry : Database.inventoryMap.entrySet()) {
            if (entry.getValue().isLowStock()) {
                lowStockMedications.add(entry.getKey());
            }
        }
        return lowStockMedications;
    }
}
