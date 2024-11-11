package hospitalsystem.model;

import java.util.ArrayList;
import java.util.List;
import hospitalsystem.controllers.PharmacistInventoryControl;
import hospital.models.User;

public class Pharmacist extends User {
    private List<Prescription> prescriptions; // List of prescriptions managed by the pharmacist
    private final PharmacistInventoryControl inventoryControl; // Pharmacist-specific inventory control

    // Constructor
    public Pharmacist(String userId, String password, PharmacistInventoryControl inventoryControl) {
        super(userId, password);
        this.prescriptions = new ArrayList<>(); // Initialize the prescription list
        this.inventoryControl = inventoryControl;
    }

    // Method to add a prescription to the pharmacist's list
    public void addPrescription(Prescription prescription) {
        prescriptions.add(prescription);
        System.out.println("Prescription added to pharmacist's list.");
    }
    
    // Getters only, as we don’t want setters for inventory control
    public List<Prescription> getPrescriptions() {
        return prescriptions;
    }

    public PharmacistInventoryControl getInventoryControl() {
        return inventoryControl;
    }
}
