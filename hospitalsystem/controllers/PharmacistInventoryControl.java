package hospitalsystem.controllers;

import java.util.Scanner;

public class PharmacistInventoryControl extends InventoryControl {

    public PharmacistInventoryControl() {
        super();
    }

    // Method specific to Pharmacist to submit a manual replenishment request
    public void submitReplenishmentRequest(Scanner sc) {
        System.out.print("Enter the medication name to request replenishment: ");
        String medicationName = sc.nextLine();
        if (inventoryMap.containsKey(medicationName)) {
            System.out.print("Enter the quantity for replenishment: ");
            int quantity = sc.nextInt();
            sc.nextLine();
            Medicine medicine = inventoryMap.get(medicationName);
            medicine.setInitialStock(medicine.getInitialStock() + quantity);
            System.out.println("Replenishment request submitted for " + medicationName + ", added " + quantity + " units.");
        } else {
            System.out.println("Error: Medication " + medicationName + " not found in inventory.");
        }
    }

    // Method to automatically submit replenishment requests for expiring medicines
    public void checkAndRequestReplenishmentForExpiringMedicines() {
        System.out.println("Checking for medications nearing expiration...");
        boolean foundExpiring = false;

        for (Medicine medicine : inventoryMap.values()) {
            if (medicine.isNearingExpiration()) {
                System.out.println("Warning: " + medicine.getMedicineName() + " is nearing expiration on " + medicine.getExpirationDate());
                int currentStock = medicine.getInitialStock();

                // Submit an automatic replenishment request for the current stock quantity
                submitAutomaticReplenishmentRequest(medicine.getMedicineName(), currentStock);
                foundExpiring = true;
            }
        }

        if (!foundExpiring) {
            System.out.println("No medications are nearing expiration.");
        }
    }

    // Helper method to handle automatic replenishment for expiring medications
    private void submitAutomaticReplenishmentRequest(String medicationName, int quantity) {
        System.out.println("Automatically submitting replenishment request for " + quantity + " units of " + medicationName + " due to expiration.");
        // This would notify the administrator for approval in a real system
    }
    
}

