package hospitalsystem.controllers;

import java.util.Scanner;

public class AdminInventoryControl extends InventoryControl {

    public AdminInventoryControl() {
        super();
    }

    // Method specific to Administrator to update the low stock alert line
    public void updateLowStockAlert(Scanner sc) {
        System.out.print("Enter the name of the medicine to update low stock alert: ");
        String medicineName = sc.nextLine().trim();
        if (inventoryMap.containsKey(medicineName)) {
            Inventory medicine = inventoryMap.get(medicineName);
            System.out.print("Enter the new low stock alert level: ");
            int newAlertLine = sc.nextInt();
            sc.nextLine();
            medicine.setLowStockAlert(newAlertLine);
            System.out.println("Low stock alert level for " + medicineName + " updated to " + newAlertLine);
        } else {
            System.out.println("Medicine not found in inventory: " + medicineName);
        }
    }
}

