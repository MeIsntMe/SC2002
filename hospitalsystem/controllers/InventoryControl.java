package hospitalsystem.controllers;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class InventoryControl {
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
