package hospitalsystem.controllers;

import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;

import hospitalsystem.MainSystem;
import hospitalsystem.model.Inventory;

public class InventoryControl {
    
    // MANAGE INVENTORY MENU
    public static void manageInventoryMenu(){
        while (true) {
            Scanner sc = new Scanner(System.in);
            
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
                    case 5 -> updateLowStockAlarmLine(sc);
                    case 6 -> { sc.close(); return; }
                    default -> System.out.println("Invalid input! Please enter a number between 1-5 ");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input! ");
            }
        }
    }

    public static void displayInventory() {
        if (MainSystem.inventoryMap.isEmpty()) {
            System.out.println("The inventory is currently empty.");
            return;
        }

        // Print in table format with headers 
        System.out.printf("%-20s %-15s %-20s%n", "Medicine Name", "Initial Stock", "Low Stock Alert Level");
        System.out.println("-------------------------------------------------------------");
        for (Map.Entry<String, Inventory> entry : MainSystem.inventoryMap.entrySet()) {
            Inventory med = entry.getValue();
            System.out.printf("%-20s %-15d %-20d%n", med.getMedicineName(), med.getInitialStock(), med.getLowStockAlert());
        }
    }

    public static void addMedicine(Scanner sc){
        boolean repeat;
        do {
            System.out.println("=========================================");  
            System.out.println("Inventory Management > Add Medicine");
            System.out.print("Enter name of medicine to add:");
            String medicineName = sc.nextLine();
            System.out.print("Enter initial stock level: ");
            int initialStock = sc.nextInt();
            sc.nextLine();
            System.out.print("Enter low stock level alarm: ");
            int lowStockAlert = sc.nextInt();
            sc.nextLine();

            Inventory medicine = new Inventory(medicineName, initialStock, lowStockAlert);
            MainSystem.inventoryMap.put(medicineName, medicine);
            System.out.printf("%s successfully added into inventory.", medicineName);
            
            // Give option to repeat 
            repeat = MainSystem.getRepeatChoice(sc);
        } while (repeat);
    }

    public static void removeMedicine(Scanner sc){
        boolean repeat;
        do {
            System.out.println("=========================================");  
            System.out.println("Inventory Management > Remove Medicine");
            System.out.print("Enter name of medicine to remove:");
            String medicineName = sc.nextLine();
            
            if (MainSystem.inventoryMap.containsKey(medicineName)) {
                MainSystem.inventoryMap.remove(medicineName);
                System.out.println(medicineName + " has been removed from the inventory.");
            } else 
                System.out.println("Medicine not found in inventory: " + medicineName);
            
            // Give option to repeat 
            repeat = MainSystem.getRepeatChoice(sc);
        } while (repeat);
    }

    public static void updateStockLevel(Scanner sc) {
        boolean repeat;
        do {
            System.out.println("=========================================");  
            System.out.println("Inventory Management > Update Stock");
            System.out.print("Enter the name of the medicine to update: ");
            String medicineName = sc.nextLine().trim();

            // Check if medicine exists
            if (MainSystem.inventoryMap.containsKey(medicineName)) {
                Inventory medicine = MainSystem.inventoryMap.get(medicineName);
                
                // Prompt for new stock
                System.out.println("Current stock level for " + medicineName + " is " + medicine.getInitialStock());
                System.out.println("Enter the new stock level: ");
                try {
                    int newStockLevel = Integer.parseInt(sc.nextLine().trim());
                    medicine.setInitialStock(newStockLevel); 
                    System.out.println("Stock level for " + medicineName + " has been updated to " + newStockLevel);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a valid number.");
                }
            } else 
                System.out.println("Medicine not found in inventory: " + medicineName);
            
            // Give option to repeat 
            repeat = MainSystem.getRepeatChoice(sc);
        }  while (repeat);
    } 

    public static void updateLowStockAlarmLine(Scanner sc) {
        boolean repeat;
        do {
            System.out.println("=========================================");  
            System.out.println("Inventory Management > Update Low Stock Alarm Line");
            System.out.print("Enter the name of the medicine to update: ");
            String medicineName = sc.nextLine().trim();

            // Check if medicine exists
            if (MainSystem.inventoryMap.containsKey(medicineName)) {
                Inventory medicine = MainSystem.inventoryMap.get(medicineName);
                
                // Prompt for new stock
                System.out.println("Current low stock alert line for " + medicineName + " is " + medicine.getLowStockAlert());
                System.out.println("Enter the new alert line: ");
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
    
}
