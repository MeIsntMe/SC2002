package hospitalsystem.controllers;

import java.util.List;
import hospital.models.Prescription;
import hospital.enums.PrescriptionStatus;
import hospital.models.Pharmacist;
import hospital.models.Medicine;

public class PharmacistControl {

    private Pharmacist pharmacist;
    private InventoryControl inventoryControl;

    // Constructor
    public PharmacistControl(Pharmacist pharmacist, InventoryControl inventoryControl) {
        this.pharmacist = pharmacist;
        this.inventoryControl = inventoryControl;
    }

    // View all prescriptions for the pharmacist
    public void viewPrescriptions() {
        List<Prescription> prescriptions = pharmacist.getPrescriptions();
        System.out.println("Pharmacist's Prescriptions:");
        for (Prescription prescription : prescriptions) {
            System.out.println(prescription);
        }
    }

    // Update prescription status to mark fulfillment, rejection, or other states
    public void updatePrescriptionStatus(Prescription prescription, PrescriptionStatus status) {
        prescription.setStatus(status);
        System.out.println("Prescription for " + prescription.getMedicine().getMedicineName() + 
                           " updated to " + status);
    }

    // View the entire medication inventory
    public void viewInventory() {
        inventoryControl.displayInventory();
    }

    // Check the stock of a specific medication based on the medication name in the inventory
    public int checkMedicationStock(String medicationName) {
        int stock = inventoryControl.checkStock(medicationName);
        if (stock == -1) {
            System.out.println("Medication " + medicationName + " not found in inventory.");
        } else {
            System.out.println("Medication: " + medicationName + ", Stock: " + stock);
        }
        return stock;
    }

    // Request replenishment for a specific medication
    public void requestReplenishment(String medicationName, int quantity) {
        if (inventoryControl.submitReplenishmentRequest(medicationName, quantity)) {
            System.out.println("Replenishment requested for " + medicationName + ", quantity: " + quantity);
        } else {
            System.out.println("Failed to request replenishment for " + medicationName);
        }
    }

    // Automatically check for low stock medications and display them
    public void checkLowStockMedications() {
        List<String> lowStockMeds = inventoryControl.getLowStockMedications();
        System.out.println("Medications below low stock threshold:");
        for (String med : lowStockMeds) {
            System.out.println("- " + med);
        }
    }

    // Fulfill a prescription by checking inventory and updating status
    public void fulfillPrescription(Prescription prescription) {
        Medicine medicine = prescription.getMedicine();
        String medicineName = medicine.getMedicineName();
        int currentStock = checkMedicationStock(medicineName);

        if (currentStock < prescription.getDosage()) {
            System.out.println("Insufficient stock for " + medicineName + " to fulfill the prescription.");
            requestReplenishment(medicineName, prescription.getDosage());
            prescription.setStatus(PrescriptionStatus.PENDING);
            System.out.println("Prescription status set to PENDING due to low stock.");
        } else {
            // Update stock and mark prescription as dispensed
            inventoryControl.reduceStock(medicineName, prescription.getDosage());
            prescription.setStatus(PrescriptionStatus.DISPENSED);
            System.out.println("Prescription for " + medicineName + " has been dispensed to Patient ID: " + prescription.getPatientID());
        }
    }
}
