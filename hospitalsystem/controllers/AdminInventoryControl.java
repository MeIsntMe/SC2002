package hospitalsystem.controllers;

import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;

import hospitalsystem.MainSystem;
import hospitalsystem.enums.RequestStatus;
import hospitalsystem.model.Medicine;
import hospitalsystem.model.ReplenishmentRequest;

//Inventory Control Class specific to Admin 
public class AdminInventoryControl extends InventoryControl {

    public AdminInventoryControl() {
        super();
    }

    // MANAGE INVENTORY MENU (Admin specific)
    public static void manageInventoryMenu(){
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
            System.out.println("6. Exit Medical Inventory Management");
            System.out.print("Enter choice: "); 

            try{
                int choice = sc.nextInt();
                switch (choice) {
                    case 1 -> displayInventory();
                    case 2 -> addMedicine(sc);  
                    case 3 -> removeMedicine(sc);
                    case 4 -> updateStockLevel(sc);
                    case 5 -> updateLowStockAlert(sc);
                    case 6 -> { sc.close(); return; }
                    default -> System.out.println("Invalid input! Please enter a number between 1-5 ");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input! Please enter a number between 1-5");
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
                    System.out.println("Stock level for " + medicineName + " has been updated to " + newAlertLine);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a valid number.");
                }
            } else 
                System.out.println("Medicine not found in inventory: " + medicineName);
            
            // Give option to repeat 
            repeat = MainSystem.getRepeatChoice(sc);
        }  while (repeat);
    }

    public static void approveRequests(Scanner sc) {
        
        // Display all requests 
        viewAllRequests();

        // Reject requests if any  
        System.out.println("All requests will be approved by default");
        while (true) {
            System.out.println("Enter medicine name to reject request (or leave blank to approve all): ");
            String medName = sc.nextLine();
            if (medName == "") break; 
            else {
                ReplenishmentRequest request = requestMap.get(medName);
                if (request.getStatus() == RequestStatus.PENDING) {
                    request.reject();
                    System.out.printf("%s request has been rejected%n", medName);
                }
            }
        }

        // Approve all others by default 
        for (Map.Entry<String, ReplenishmentRequest> entry : requestMap.entrySet()) {
            ReplenishmentRequest request = entry.getValue();

            if (request.getStatus() == RequestStatus.PENDING) {
                request.accept();
                System.out.println("All remaining requests have been accepted");

                //Increase Stock 
                String medName = request.getMedicineName();
                Medicine med = inventoryMap.get(medName);
                int newStock = med.getInitialStock() + request.getRequestedQuantity();
                med.setInitialStock(newStock);
            }
        }
    }

    public static void viewAllRequests() {
        if (requestMap.isEmpty()) {
            System.out.println("There are no requests.");
            return;
        }
        // Print in table format  
        System.out.println("Request List: ");
        System.out.printf("%-20s %-15s %-20s%n", "Medicine Name", "Requested Qty", "Status");
        System.out.println("-------------------------------------------------------");
        for (Map.Entry<String, ReplenishmentRequest> entry : requestMap.entrySet()) {
            ReplenishmentRequest req = entry.getValue();
            System.out.printf("%-20s %-15d %-20s%n", req.getMedicineName(), req.getRequestedQuantity(), req.getStatus());
        }
    }
}

