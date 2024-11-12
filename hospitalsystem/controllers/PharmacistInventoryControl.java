package hospitalsystem.controllers;

import java.util.Scanner;
import hospitalsystem.model.Medicine;
import hospitalsystem.model.ReplenishmentRequest;
import hospitalsystem.model.Batch;
import java.time.LocalDate;

public class PharmacistInventoryControl extends InventoryControl {

    public PharmacistInventoryControl() {
        super();
    }

    // Method for Pharmacist to submit a manual replenishment request
    public void submitReplenishmentRequest(Scanner sc) {
        System.out.print("Enter the medication name to request replenishment: ");
        String medicineName = sc.nextLine();

        // Check if the medicine exists 
        if (inventoryMap.containsKey(medicineName)) {
            System.out.print("Enter the quantity for replenishment: ");
            int quantity = sc.nextInt();
            sc.nextLine();  // Consume newline
            System.out.print("Enter the desired expiration date for the replenishment batch (YYYY-MM-DD): ");
            LocalDate expirationDate = LocalDate.parse(sc.nextLine().trim());

            // Create a replenishment request (without adding to inventory)
            ReplenishmentRequest request = new ReplenishmentRequest(medicineName, quantity, expirationDate);
            requestMap.put(medicineName, request);
            System.out.println("Replenishment request submitted for " + medicineName + " with " + quantity + " units (expiration: " + expirationDate + ").");

        } else {
            System.out.println("Error: Medication " + medicineName + " not found in inventory.");
        }
    }

    // Method to automatically submit replenishment requests for expiring medicines
    public void checkAndRequestReplenishmentForExpiringMedicines() {
        System.out.println("Checking for medications nearing expiration...");
        boolean foundExpiring = false;

        for (Medicine medicine : inventoryMap.values()) {
            for (Batch batch : medicine.getNearingExpirationBatches(2)) { // Check for batches expiring within 2 weeks
                if (batch.getQuantity() > 0) {
                    foundExpiring = true;
                    System.out.println("Warning: Batch of " + medicine.getMedicineName() + " is nearing expiration on " + batch.getExpirationDate() + " with " + batch.getQuantity() + " units.");

                    // Create a replenishment request for the expiring batch’s quantity (without adding to inventory)
                    submitAutomaticReplenishmentRequest(medicine.getMedicineName(), batch.getQuantity(), LocalDate.now().plusMonths(6)); // Example future expiration
                }
            }
        }

        if (!foundExpiring) {
            System.out.println("No medications are nearing expiration.");
        }
    }

    // Helper method to submit automatic replenishment requests for expiring medicines
    private void submitAutomaticReplenishmentRequest(String medicationName, int quantity, LocalDate expirationDate) {
        System.out.println("Automatically submitting replenishment request for " + quantity + " units of " + medicationName + ".");
        ReplenishmentRequest request = new ReplenishmentRequest(medicationName, quantity, expirationDate);
        requestMap.put(medicationName, request);
    }
}
