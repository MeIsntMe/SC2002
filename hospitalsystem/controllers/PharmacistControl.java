import java.util.List;
import hospital.models.Prescription;
import hospital.models.PrescriptionStatus;
import hospital.controls.InventoryControl;
import hospital.models.Pharmacist;

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

    // Update prescription status
    public void updatePrescriptionStatus(Prescription prescription, PrescriptionStatus status) {
        pharmacist.updatePrescriptionStatus(prescription, status);
        System.out.println("Prescription " + prescription.getId() + " updated to " + status);
    }

    // View the entire medication inventory
    public void viewInventory() {
        inventoryControl.viewInventory();
    }

    // Check the stock of a specific medication
    public int checkMedicationStock(String medicationName) {
        int stock = inventoryControl.checkStock(medicationName);
        if (stock == -1) {
            System.out.println("Medication " + medicationName + " not found.");
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

    // Get list of medications below low stock threshold
    public void checkLowStockMedications() {
        List<String> lowStockMeds = inventoryControl.getLowStockMedications();
        System.out.println("Medications below low stock threshold:");
        for (String med : lowStockMeds) {
            System.out.println("- " + med);
        }
    }
}

