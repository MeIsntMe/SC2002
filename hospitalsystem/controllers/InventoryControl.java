package hospitalsystem.controllers;
import java.util.HashMap;
import java.util.Map;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import hospitalsystem.model.Medicine;
import hospitalsystem.model.ReplenishmentRequest;

import java.time.LocalDate;

import java.util.Scanner;

import hospitalsystem.MainSystem;
import hospitalsystem.model.Medicine;

public class InventoryControl {
    
    // HashMap to store inventory 
    public static Map<String, Medicine> inventoryMap = new HashMap<>(); //key is medicine name
    public static Map<String, ReplenishmentRequest> requestMap = new HashMap<>(); //key is medicine name

    public static void displayInventory() {
        if (inventoryMap.isEmpty()) {
            System.out.println("The inventory is currently empty.");
            return;
        }

        // Print in table format with headers 
        System.out.printf("%-20s %-15s %-20s%n", "Medicine Name", "Initial Stock", "Low Stock Alert Level");
        System.out.println("-------------------------------------------------------------");
        for (Map.Entry<String, Medicine> entry : inventoryMap.entrySet()) {
            Medicine med = entry.getValue();
            System.out.printf("%-20s %-15d %-20d%n", med.getMedicineName(), med.getInitialStock(), med.getLowStockAlert());
        }
    }

    public static void addMedicine(Scanner sc){
        boolean repeat;
        System.out.println("=========================================");  
        System.out.println("Inventory Management > Add Medicine");
        do {
            System.out.print("Enter name of medicine to add:");
            String medicineName = sc.nextLine();
            System.out.print("Enter initial stock level: ");
            int initialStock = sc.nextInt();
            sc.nextLine();
            System.out.print("Enter low stock level alarm: ");
            int lowStockAlert = sc.nextInt();
            System.out.print("Enter expiration date (YYYY-MM-DD): ");
            LocalDate expirationDate = LocalDate.parse(sc.next().trim());
            sc.nextLine();

            Medicine medicine = new Medicine(medicineName, initialStock, lowStockAlert, expirationDate);
            inventoryMap.put(medicineName, medicine);
            System.out.printf("%s successfully added into inventory%n", medicineName);
            
            // Give option to repeat 
            repeat = MainSystem.getRepeatChoice(sc);
        } while (repeat);
    }

    public static void removeMedicine(Scanner sc){
        boolean repeat;
        System.out.println("=========================================");  
        System.out.println("Inventory Management > Remove Medicine");
        do {
            System.out.print("Enter name of medicine to remove:");
            String medicineName = sc.nextLine();
            
            if (inventoryMap.containsKey(medicineName)) {
                inventoryMap.remove(medicineName);
                System.out.println(medicineName + " has been removed from the inventory.");
            } else 
                System.out.println("Medicine not found in inventory: " + medicineName);
            
            // Give option to repeat 
            repeat = MainSystem.getRepeatChoice(sc);
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
            if (inventoryMap.containsKey(medicineName)) {
                Medicine medicine = inventoryMap.get(medicineName);
                
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

    public static void loadInventoryFromCSV(String filePath) {
        try (Scanner scanner = new Scanner(new File(filePath))) {
            scanner.nextLine(); // Skip the header line if there is one
            while (scanner.hasNextLine()) {
                String[] inventoryData = scanner.nextLine().split(",");
                String medicineName = inventoryData[0].trim();
                int initialStock = Integer.parseInt(inventoryData[1].trim());
                int lowStockAlert = Integer.parseInt(inventoryData[2].trim());
                LocalDate expDate = LocalDate.parse(inventoryData[3].trim()); 

                Medicine medicine = new Medicine(medicineName, initialStock, lowStockAlert, expDate);
                inventoryMap.put(medicineName, medicine);
            }
            System.out.println("Inventory loaded successfully from CSV.");
        } catch (FileNotFoundException e) {
            System.out.println("CSV file not found: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Error parsing number from CSV: " + e.getMessage());
        }
    }

    // MERGE CONFLICT WITH BELOW - TO DISCUSS

    private Map<String, Integer> inventory; // Store medication and their quantities
    private Map<String, Integer> lowStockThresholds; // Thresholds for low stock alerts

    // Check stock level of a specific medication
    public int checkStock(String medicationName) {
        return inventory.getOrDefault(medicationName, -1); // Returns -1 if not found
    }

    // Method to check if a medication is below the low stock threshold
    public boolean isLowStock(String medicationName) {
        if (inventory.containsKey(medicationName)) {
            return inventory.get(medicationName) <= lowStockThresholds.get(medicationName);
        }
        System.out.println("Medication not found in inventory.");
        return false;
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
