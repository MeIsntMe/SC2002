package hospitalsystem.controllers;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

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


    // MERGE CONFLICT WITH BELOW - TO DISCUSS

    private Map<String, Integer> inventory; // Store medication and their quantities
    private Map<String, Integer> lowStockThresholds; // Thresholds for low stock alerts

    // Constructor
    public InventoryControl() {
        inventory = new HashMap<>();
        lowStockThresholds = new HashMap<>();
    }

    // Method to add a new medication to inventory
    public void addMedication(String medicationName, int initialStock, int lowStockThreshold) {
        inventory.put(medicationName, initialStock);
        lowStockThresholds.put(medicationName, lowStockThreshold);
        System.out.println(medicationName + " added to inventory with initial stock of " + initialStock);
    }

    // Method to view the current stock of all medications
    public void viewInventory() {
        System.out.println("Medication Inventory:");
        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
            System.out.println("Medication: " + entry.getKey() + ", Quantity: " + entry.getValue());
        }
    }
    // Check stock level of a specific medication
    public int checkStock(String medicationName) {
        return inventory.getOrDefault(medicationName, -1); // Returns -1 if not found
    }

    // Remove a medication from inventory
    public boolean removeMedication(String medicationName) {
        if (inventory.containsKey(medicationName)) {
            inventory.remove(medicationName);
            lowStockThresholds.remove(medicationName);
            System.out.println(medicationName + " has been removed from the inventory.");
            return true;
        } else {
            System.out.println("Error: Medication " + medicationName + " not found in inventory.");
            return false;
        }
    }

    // Update the low stock threshold for a medication
    public void updateLowStockThreshold(String medicationName, int newThreshold) {
        if (lowStockThresholds.containsKey(medicationName)) {
            lowStockThresholds.put(medicationName, newThreshold);
            System.out.println("Low stock threshold for " + medicationName + " updated to " + newThreshold);
        } else {
            System.out.println("Error: Medication " + medicationName + " not found in inventory.");
        }
    }

    // Method to check if a medication is below the low stock threshold
    public boolean isLowStock(String medicationName) {
        if (inventory.containsKey(medicationName)) {
            return inventory.get(medicationName) <= lowStockThresholds.get(medicationName);
        }
        System.out.println("Medication not found in inventory.");
        return false;
    }

    // Method to submit a replenishment request
    public boolean submitReplenishmentRequest(String medicationName, int quantity) {
        if (inventory.containsKey(medicationName)) {
            inventory.put(medicationName, inventory.get(medicationName) + quantity);
            System.out.println("Replenishment request: Added " + quantity + " units of " + medicationName);
            return true;
        } else {
            System.out.println("Error: Medication " + medicationName + " does not exist in inventory.");
            return false;
        }
    }

    // Method to update inventory manually
    public void updateMedicationStock(String medicationName, int newQuantity) {
        if (inventory.containsKey(medicationName)) {
            inventory.put(medicationName, newQuantity);
            System.out.println(medicationName + " stock updated to " + newQuantity);
        } else {
            System.out.println("Error: Medication " + medicationName + " does not exist in inventory.");
        }
    }

    // Get a list of medications below the low stock threshold
    public List<String> getLowStockMedications() {
        List<String> lowStockMedications = new ArrayList<>();
        for (String medication : inventory.keySet()) {
            if (isLowStock(medication)) {
                lowStockMedications.add(medication);
            }
        }
        return lowStockMedications;
    }

    // Get information about a specific medication
    public String getMedicationInfo(String medicationName) {
        if (inventory.containsKey(medicationName)) {
            int stock = inventory.get(medicationName);
            int threshold = lowStockThresholds.get(medicationName);
            return "Medication: " + medicationName + ", Stock: " + stock + ", Low Stock Threshold: " + threshold;
        } else {
            return "Error: Medication " + medicationName + " not found in inventory.";
        }
    }
}
