package hospitalsystem.inventorycontrol;

import java.util.Scanner;

import hospitalsystem.model.Medicine;
import hospitalsystem.model.ReplenishmentRequest;
//import hospitalsystem.model.Batch;
//import java.time.LocalDate;

public class PharmacistInventoryControl extends InventoryControl {

    public PharmacistInventoryControl() {
        super();
    }

    // Method for Pharmacist to submit a manual replenishment request (without expiration date)
    public void submitReplenishmentRequest(Scanner sc) {
        System.out.print("Enter the medication name to request replenishment: ");
        String medicineName = sc.nextLine();

        // Check if the medicine exists
        if (inventoryMap.containsKey(medicineName)) {
            System.out.print("Enter the quantity for replenishment: ");
            int quantity = sc.nextInt();
            sc.nextLine();  // Consume newline

            // Create a replenishment request (without expiration date)
            ReplenishmentRequest request = new ReplenishmentRequest(medicineName, quantity);
            addReplenishmentRequest(request);
            System.out.println("Replenishment request submitted for " + medicineName + " with " + quantity + " units.");
        } else {
            System.out.println("Error: Medication " + medicineName + " not found in inventory.");
        }
    }

    // Method to automatically submit replenishment requests for expiring medicines
    public void checkAndRequestReplenishmentForExpiringMedicines() {
        System.out.println("Checking for medications nearing expiration...");
        boolean foundExpiring = false;

        for (Medicine medicine : inventoryMap.values()) {
            for (Medicine.Batch batch : medicine.getNearingExpirationBatches(2)) { // Check for batches expiring within 2 weeks
                if (batch.getQuantity() > 0) {
                    foundExpiring = true;
                    System.out.println("Warning: Batch of " + medicine.getMedicineName() + " is nearing expiration on " + batch.getExpirationDate() + " with " + batch.getQuantity() + " units.");

                    // Submit a replenishment request for the expiring batch’s quantity (without expiration date)
                    submitAutomaticReplenishmentRequest(medicine.getMedicineName(), batch.getQuantity());
                }
            }
        }

        if (!foundExpiring) {
            System.out.println("No medications are nearing expiration.");
        }
    }

    // Helper method to submit automatic replenishment requests for expiring medicines (without expiration date)
    private void submitAutomaticReplenishmentRequest(String medicationName, int quantity) {
        System.out.println("Automatically submitting replenishment request for " + quantity + " units of " + medicationName + ".");
        ReplenishmentRequest request = new ReplenishmentRequest(medicationName, quantity);
        addReplenishmentRequest(request);
    }


    // Add the replenishment request to the requestMap
    private void addReplenishmentRequest(ReplenishmentRequest request) {
        requestMap.put(request.getMedicineName(), request);
        System.out.println("Replenishment request added for " + request.getMedicineName() + 
                           " with quantity " + request.getRequestedQuantity());
    }

}
