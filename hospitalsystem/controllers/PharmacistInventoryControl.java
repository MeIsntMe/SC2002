package hospitalsystem.controllers;

import java.util.Scanner;

public class PharmacistInventoryControl extends InventoryControl {

    public PharmacistInventoryControl() {
        super();
    }

    // Method specific to Pharmacist to submit a replenishment request
    public void submitReplenishmentRequest(Scanner sc) {
        System.out.print("Enter the medication name to request replenishment: ");
        String medicationName = sc.nextLine();
        if (inventoryMap.containsKey(medicationName)) {
            System.out.print("Enter the quantity for replenishment: ");
            int quantity = sc.nextInt();
            sc.nextLine();
            Inventory medicine = inventoryMap.get(medicationName);
            medicine.setInitialStock(medicine.getInitialStock() + quantity);
            System.out.println("Replenishment request submitted for " + medicationName + ", added " + quantity + " units.");
        } else {
            System.out.println("Error: Medication " + medicationName + " not found in inventory.");
        }
    }
}

