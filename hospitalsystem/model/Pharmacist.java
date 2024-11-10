package hospitalsystem.model;
import java.util.List;

public class Pharmacist extends User {
    // Fields
    private List<Prescription> prescriptions; // List of prescriptions to manage
    private InventoryControl inventoryControl; // Access to inventory control for managing stock

    // Constructor
    public Pharmacist(String userId, String password, InventoryControl inventoryControl) {
        super(userId, password);
        this.inventoryControl = inventoryControl;
    }
    // Methods

    // View all prescriptions assigned to this pharmacist
    public void viewPrescriptions() {
        System.out.println("Viewing all prescriptions:");
        for (Prescription prescription : prescriptions) {
            System.out.println(prescription);
        }
    }

    // Update the status of a prescription
    public void updatePrescriptionStatus(Prescription prescription, PrescriptionStatus status) {
        prescription.setStatus(status);
        System.out.println("Prescription status updated to: " + status);
    }

    // Request replenishment for a medication if the stock is low
    public void requestReplenishment(String medicationName, int quantity) {
        boolean requestSuccess = inventoryControl.submitReplenishmentRequest(medicationName, quantity);
        if (requestSuccess) {
            System.out.println("Replenishment request submitted successfully for " + medicationName);
        } else {
            System.out.println("Failed to submit replenishment request for " + medicationName);
        }
    }

    // Getter for prescriptions list
    public List<Prescription> getPrescriptions() {
        return prescriptions;
    }

    // Getter for inventory control instance
    public InventoryControl getInventoryControl() {
        return inventoryControl;
    }
}
