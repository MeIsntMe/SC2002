public class Pharmacist extends User {
    // Fields
    private List<Prescription> prescriptions; // List of prescriptions to manage
    private Inventory inventory; // Access to medication inventory

    // Constructor
    public Pharmacist(String id, String name, Inventory inventory) {
        super(id, name); // Call to superclass constructor
        this.inventory = inventory;
        this.prescriptions = new ArrayList<>();
    }

    // Methods

    // View the list of prescriptions for processing
    public void viewPrescriptions() {
        for (Prescription prescription : prescriptions) {
            System.out.println(prescription);
        }
    }

    // Update the status of a specific prescription
    public void updatePrescriptionStatus(Prescription prescription, String status) {
        prescription.setStatus(status);
        System.out.println("Prescription status updated to " + status);
    }

    // View current medication inventory
    public void viewInventory() {
        System.out.println(inventory);
    }

    // Submit replenishment request
    public void submitReplenishmentRequest(String medicationName, int quantity) {
        inventory.requestReplenishment(medicationName, quantity);
        System.out.println("Replenishment request submitted for " + medicationName);
    }

    // Additional methods as needed for functionality
}
