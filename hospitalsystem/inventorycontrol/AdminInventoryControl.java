package hospitalsystem.inventorycontrol;

import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;
import java.time.LocalDate;

import hospitalsystem.HMS;
import hospitalsystem.data.Database;
import hospitalsystem.enums.RequestStatus;
import hospitalsystem.model.Medicine;
import hospitalsystem.model.Medicine.Batch;
import hospitalsystem.model.ReplenishmentRequest;

public class AdminInventoryControl extends InventoryControl {

    public static void manageStock(Scanner sc) {
        System.out.println("Enter medicine to manage stock for: ");
        String medicineName = sc.nextLine();

        System.out.println("Stock management options:");
        System.out.println("1. Add");
        System.out.println("2. Remove");
        System.out.println("Enter choice (1/2): ");
        int choice = sc.nextInt(); 
        
        System.out.println()
        
        
    }

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
            Medicine newMedicine = new Medicine(medicineName, minStockLevel, instructions);
            
            // Add batch
            addBatchToMedicine(medicineName, sc);
            
            // Option to repeat 
            if (!HMS.repeat(sc)) break;
        }
    }

    public static void addBatchToMedicine(String medicineName, Scanner sc) {
        
        Medicine medicine = Database.inventoryMap.get(medicineName);
        int quantity;

        // Prompt for batch quantity and ensure it meets minimum stock level
        do {
            System.out.print("Enter batch quantity for " + medicineName + ": ");
            quantity = sc.nextInt();
            if (quantity < medicine.getMinStockLevel()) {
                System.out.println("Quantity is below the minimum stock level of " + medicine.getMinStockLevel() + ". Please enter a valid quantity.");
            }
        } while (quantity < medicine.getMinStockLevel());

        // Prompt for expiration date
        System.out.print("Enter batch expiration date (YYYY-MM-DD): ");
        sc.nextLine(); // Consume newline
        LocalDate expDate = LocalDate.parse(sc.nextLine());

        // Create and add the new batch
        Batch newBatch = medicine.new Batch(quantity, expDate);
        medicine.getBatches().add(newBatch);
        System.out.println("New batch of " + medicineName + " added successfully.");
    }

    public static void removeMedicine(Scanner sc) {
        while (true) {
            System.out.print("Enter name of medicine to remove: ");
            String medicineName = sc.nextLine();
            
            // Check if medicine exists  
            if (!Database.inventoryMap.containsKey(medicineName)) {
                System.out.println("Medicine not found in inventory.");
                continue;
            }
            
            Database.inventoryMap.remove(medicineName);
            System.out.println(medicineName + " has been removed from the inventory.");
            
            // Option to repeat
            if (!HMS.repeat(sc)) break;

        }
    }

    // MANAGE INVENTORY MENU (Admin specific)
    public static void manageInventoryMenu() {
        while (true) {
            Scanner sc = new Scanner(System.in);
            InventoryControl.loadInventoryFromCSV("inventoryFilePath.csv");
            
            System.out.println("=========================================");
            System.out.println("Inventory Management: ");
            System.out.println("1. Display inventory");
            System.out.println("2. Add medicine");
            System.out.println("3. Remove medicine");
            System.out.println("4. Update stock level"); 
            System.out.println("5. Update low stock level alert line");
            System.out.println("6. View and Approve Replenishment Requests");
            System.out.println("7. Exit Medical Inventory Management");
            System.out.print("Enter choice: "); 

            try {
                int choice = sc.nextInt();
                sc.nextLine();  // Consume newline
                switch (choice) {
                    case 1 -> displayInventory();
                    case 2 -> addMedicine(sc);  
                    case 3 -> removeMedicine(sc);
                    case 4 -> updateStockLevel(sc);
                    case 5 -> updateLowStockAlert(sc);
                    case 6 -> approveRequests(sc);
                    case 7 -> { sc.close(); return; }
                    default -> System.out.println("Invalid input! Please enter a number between 1-7.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input! Please enter a number between 1-7.");
            }
        }
    }

    // Method specific to Administrator to update the low stock alert line
    public static void updateLowStockAlert(Scanner sc) {
        boolean repeat; 
        System.out.println("=========================================");  
        System.out.println("Inventory Management > Update Low Stock Alert Level");
        do {
            System.out.print("Enter name of medicine: ");
            String medicineName = sc.nextLine().trim();

            // Check if medicine exists 
            if (inventoryMap.containsKey(medicineName)) {
                Medicine medicine = inventoryMap.get(medicineName);

                // Set new alert level
                System.out.print("Enter new low stock alert level: ");
                try {
                    int newAlertLine = Integer.parseInt(sc.nextLine().trim());
                    medicine.setLowStockAlert(newAlertLine); 
                    System.out.println("Low stock alert level for " + medicineName + " has been updated to " + newAlertLine);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a valid number.");
                }
            } else {
                System.out.println("Medicine not found in inventory: " + medicineName);
            }
            
            // Give option to repeat 
            repeat = MainSystem.getRepeatChoice(sc);
        }  while (repeat);
    }

    // Method to approve or reject replenishment requests
    public static void approveRequests(Scanner sc) {
        // Display all requests 
        viewAllRequests();

        // Reject specific requests if needed
        System.out.println("By default, all pending requests will be approved.");
        while (true) {
            System.out.println("Enter the name of the medicine to reject request (or leave blank to approve all): ");
            String medName = sc.nextLine().trim();
            if (medName.isEmpty()) break; 
            else {
                ReplenishmentRequest request = requestMap.get(medName);
                if (request != null && request.getStatus() == RequestStatus.PENDING) {
                    request.reject();
                    System.out.printf("%s request has been rejected.%n", medName);
                } else {
                    System.out.println("Request not found or already processed.");
                }
            }
        }

        // Approve all other pending requests by default
        for (Map.Entry<String, ReplenishmentRequest> entry : requestMap.entrySet()) {
            ReplenishmentRequest request = entry.getValue();

            if (request.getStatus() == RequestStatus.PENDING) {
                request.accept();
                System.out.println("Approving request for " + request.getMedicineName() + " with " + request.getRequestedQuantity() + " units (expiration: " + request.getExpirationDate() + ").");

                // Add approved batch to inventory
                String medName = request.getMedicineName();
                Medicine medicine = inventoryMap.get(medName);
                if (medicine == null) {
                    medicine = new Medicine(medName, 10); // Default low stock alert, for example
                    inventoryMap.put(medName, medicine);
                }
                medicine.addBatch(request.getRequestedQuantity(), request.getExpirationDate());

                System.out.println("Replenishment request for " + medName + " has been approved and added to the inventory.");
            }
        }
        // Clear approved requests
        requestMap.entrySet().removeIf(entry -> entry.getValue().getStatus() == RequestStatus.APPROVED);
    }

    // Method to view all replenishment requests in a table format
    public static void viewAllRequests() {
        if (requestMap.isEmpty()) {
            System.out.println("There are no requests.");
            return;
        }
        // Print in table format  
        System.out.println("Request List: ");
        System.out.printf("%-20s %-15s %-20s %-15s%n", "Medicine Name", "Requested Qty", "Expiration Date", "Status");
        System.out.println("----------------------------------------------------------------------------");
        for (Map.Entry<String, ReplenishmentRequest> entry : requestMap.entrySet()) {
            ReplenishmentRequest req = entry.getValue();
            System.out.printf("%-20s %-15d %-20s %-15s%n", req.getMedicineName(), req.getRequestedQuantity(), req.getExpirationDate(), req.getStatus());
        }
    }
}
