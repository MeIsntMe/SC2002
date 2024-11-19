package hospitalsystem.inventorycontrol;

import hospitalsystem.HMS;
import hospitalsystem.data.Database;
import hospitalsystem.enums.RequestStatus;
import hospitalsystem.model.Medicine;
import hospitalsystem.model.Medicine.Batch;
import hospitalsystem.model.ReplenishmentRequest;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

/**
 * Manages administrator-specific inventory operations.
 * Provides functionality for stock management, including adding/removing stock,
 * managing expired medicine, and handling replenishment requests.
 *
 * @author An Xian
 * @version 1.0
 * @since 2024-11-19
 */
public class AdminInventoryControl extends InventoryControl {

    /**
     * Interactive method for managing medicine stock levels.
     * Allows administrators to add or remove stock quantities.
     *
     * @param sc Scanner object for reading user input
     */
    public static void manageStock(Scanner sc) {
        while (true){
            Medicine medicine = getMedicineInput(sc);

            // Get add or remove options 
            System.out.println("Stock management options:");
            System.out.println("1. Add stock for a medicine");
            System.out.println("2. Remove stock for a medicine");
            System.out.println("3. Add new medicine");
            System.out.println("4. Remove all expired medicines");
            System.out.println("Enter choice (1-4): ");
            
            try {
                int choice = Integer.parseInt(sc.nextLine());
                
                System.out.print("Enter the quantity: ");
                int quantity = Integer.parseInt(sc.nextLine());

                switch (choice){
                    case 1 -> addStock(medicine, quantity, sc);
                    case 2 -> removeStock(medicine, quantity);
                    case 3 -> addNewMedicine(sc);
                    case 4 -> removeExpiredStock();
                    default -> System.out.println("Invalid input. Please enter '1' or '2'.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number 1-4.");
            }

            // Option to repeat
            if (!HMS.repeat(sc)) break;
        }
    }

    /**
     * Adds new stock to a specific medicine batch.
     * Creates a new batch with specified quantity and expiration date.
     *
     * @param medicine Medicine object to add stock to
     * @param quantity Amount of stock to add
     * @param sc Scanner object for reading expiration date input
     */
    public static void addStock(Medicine medicine, int quantity, Scanner sc) {
        
            LocalDate expirationDate = null;
    
            // Handle expiration date input with try-catch for invalid date format
            while (expirationDate == null) {
                System.out.print("Enter the expiration date of new stock (YYYY-MM-DD): ");
                String dateInput = sc.nextLine();
                try {
                    expirationDate = LocalDate.parse(dateInput);
                } catch (DateTimeParseException e) {
                    System.out.println("Invalid date format. Please enter the date in YYYY-MM-DD format.");
                }
            }
    
            Batch newBatch = medicine.new Batch(quantity, expirationDate);
            medicine.getBatches().add(newBatch);
            System.out.println(medicine.getMedicineName() + " stock updated.");
    }

    /**
     * Removes specified quantity of stock from medicine batches.
     * Removes from oldest batches first and handles batch depletion.
     *
     * @param medicine Medicine object to remove stock from
     * @param quantity Amount of stock to remove
     */
    public static void removeStock(Medicine medicine, int quantity) {
        
        // Check if there is enough stock to remove
        if (quantity > medicine.getTotalQuantity()) {
            System.out.println("Cannot remove more than available stock (" + medicine.getTotalQuantity() + ").");
            return; //Exit
        }

        int remainingQuantity = quantity;
        Iterator<Batch> iterator = medicine.getBatches().iterator();

        while (iterator.hasNext() && remainingQuantity > 0) {
            Batch batch = iterator.next();
            // If fully depleted, remove the batch 
            if (batch.getQuantity() <= remainingQuantity) {
                iterator.remove();
                remainingQuantity -= batch.getQuantity();
            } else {
                batch.setQuantity(batch.getQuantity() - remainingQuantity);
                remainingQuantity = 0; // All quantity removed
            }
        }
        System.out.println("Stock updated.");
    }

    /**
     * Removes all expired medicine batches from inventory.
     * Checks expiration dates against current date and removes expired stock.
     */
    public static void removeExpiredStock(){
        System.out.println("Checking for expired medicine...");
        boolean hasExpiredStock = false; 
        LocalDate today = LocalDate.now(); 

        for (Map.Entry<String, Medicine> entry : Database.inventoryMap.entrySet()) {
            Medicine med = entry.getValue();

            for (Medicine.Batch batch : med.getBatches()) {
                if (batch.getExpirationDate().isBefore(today)) {
                    hasExpiredStock = true;
                    System.out.println("Removing expired batch of " + med.getMedicineName() + 
                                       ". Batch Expired on: " + batch.getExpirationDate());
                    removeStock(med, batch.getQuantity());
                }
            }
        }
        if (!hasExpiredStock){
            System.out.println("No expired medicine found.");
        }
    }

    /**
     * Adds a new medicine to the inventory system.
     * Collects medicine details and creates initial stock batch.
     *
     * @param sc Scanner object for reading medicine details
     */
    public static void addNewMedicine(Scanner sc) {
        while (true) {
            
            System.out.print("Enter name of medicine to add: ");
            String medicineName = sc.nextLine();

            // Check if medicine already exists  
            if (Database.inventoryMap.containsKey(medicineName)) {
                System.out.println("Medicine " + medicineName + " already exists in the inventory.");
                continue;
            }

            // Create new medicine
            System.out.print("Enter minumum stock level: ");
            int minStockLevel = sc.nextInt();
            System.out.print("Enter usage instructions: ");
            String instructions = sc.nextLine();
            System.out.print("Enter inputted stock amount: "); 
            int newStock = sc.nextInt();
            Medicine newMedicine = new Medicine(medicineName, minStockLevel, instructions);
            
            // Add batch
            addStock(newMedicine, newStock, sc);
            
            // Option to repeat 
            if (!HMS.repeat(sc)) break;
        }
    }

    /**
     * Updates the low stock alert level for a specific medicine.
     *
     * @param sc Scanner object for reading new alert level
     */
    public static void updateLowStockAlert(Scanner sc) {
        while (true){
            Medicine medicine = getMedicineInput(sc);

            // Set new alert level 
            System.out.print("Enter new low stock alert level: ");
            try {
                int newAlertLine = Integer.parseInt(sc.nextLine());
                medicine.setMinimumSttockLevel(newAlertLine);
                System.out.println("Low stock alert level has been updated to " + newAlertLine);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
            
            // Option to continue
            if (!HMS.repeat(sc)) break;
        }
    }

    /**
     * Manages replenishment requests from pharmacists.
     * Allows administrators to approve or reject requests and update stock accordingly.
     *
     * @param sc Scanner object for reading input
     */
    public static void manageRequests(Scanner sc) {
        
        // View all requests
        displayAllRequests();

        while (true) {
            ReplenishmentRequest request = getRequestInput(sc); 

            // Check that request is pending
            if (request.getStatus() != RequestStatus.PENDING) {
                System.out.println("Request not found or already processed.");
                continue;
            }

            // Approve or reject 
            System.out.println("1. Approve");
            System.out.println("2. Reject");
            System.out.println("Enter choice (1/2): ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice){
                case 1: // Approve 
                    request.setStatus(RequestStatus.APPROVED);
                    System.out.println("Request approved.");
                    
                    // Add stock 
                    Medicine med = Database.inventoryMap.get(request.getMedicine().getMedicineName());
                    int requestedQuantity = request.getRequestedQuantity();
                    addStock(med, requestedQuantity, sc);

                case 2: // Reject 
                    request.setStatus(RequestStatus.REJECTED);
                    System.out.println("Request rejected.");
                default:
                System.out.println("Invalid choice. Select 1 or 2.");
            }  

        // Option to repeat
        if (!HMS.repeat(sc)) break;
        }
    }

}
