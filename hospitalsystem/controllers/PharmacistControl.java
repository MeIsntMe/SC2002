package hospitalsystem.controllers;

import java.util.List;
import java.util.Scanner;
import hospitalsystem.model.Prescription;
import hospitalsystem.enums.PrescriptionStatus;
import hospitalsystem.model.Pharmacist;
import hospitalsystem.model.Medicine;
import java.time.LocalDate;

public class PharmacistControl implements MenuInterface {
    private Pharmacist pharmacist;
    private InventoryControl inventoryControl;

    // Constructor
    public PharmacistControl(Pharmacist pharmacist, InventoryControl inventoryControl) {
        this.pharmacist = pharmacist;
        this.inventoryControl = inventoryControl;
    }

    @Override
    public void displayMenu() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("=========================================");
            System.out.println("Pharmacist Control Menu:");
            System.out.println("1. Manage Prescriptions");
            System.out.println("2. View Inventory and Stock Information");
            System.out.println("3. Submit and View Replenishment Requests");
            System.out.println("4. Exit");
            System.out.print("Enter choice: ");

            int choice = sc.nextInt();
            sc.nextLine();  // Consume newline

            switch (choice) {
                case 1:
                    managePrescriptions(sc);
                    break;
                case 2:
                    viewInventoryAndStock(sc);
                    break;
                case 3:
                    manageReplenishmentRequests(sc);
                    break;
                case 4:
                    System.out.println("Exiting Pharmacist Control.");
                    return;
                default:
                    System.out.println("Invalid input. Please select an option between 1 and 4.");
            }
        }
    }

    // Manage Prescriptions sub-menu
    private void managePrescriptions(Scanner sc) {
        System.out.println("=== Manage Prescriptions ===");
        System.out.println("1. View Prescriptions");
        System.out.println("2. Update Prescription Status");
        System.out.println("3. Fulfill Prescription");
        System.out.print("Select an option: ");
        
        int option = sc.nextInt();
        sc.nextLine(); // Consume newline
        switch (option) {
            case 1:
                viewPrescriptions();
                break;
            case 2:
                System.out.print("Enter Prescription ID to update: ");
                int prescriptionId = sc.nextInt();
                sc.nextLine(); // Consume newline
                Prescription prescription = findPrescriptionById(prescriptionId);
                if (prescription != null) {
                    System.out.print("Enter new status (e.g., PENDING, DISPENSED, REJECTED): ");
                    String status = sc.nextLine().toUpperCase();
                    updatePrescriptionStatus(prescription, PrescriptionStatus.valueOf(status));
                } else {
                    System.out.println("Prescription not found.");
                }
                break;
            case 3:
                System.out.print("Enter Prescription ID to fulfill: ");
                prescriptionId = sc.nextInt();
                sc.nextLine(); // Consume newline
                prescription = findPrescriptionById(prescriptionId);
                if (prescription != null) {
                    fulfillPrescription(prescription);
                } else {
                    System.out.println("Prescription not found.");
                }
                break;
            default:
                System.out.println("Invalid option. Returning to main menu.");
        }
    }

    // View Inventory and Stock Information sub-menu
    private void viewInventoryAndStock(Scanner sc) {
        System.out.println("=== View Inventory and Stock ===");
        System.out.println("1. View Entire Inventory");
        System.out.println("2. Check Medication Stock");
        System.out.println("3. Check Low Stock Medications");
        System.out.print("Select an option: ");

        int option = sc.nextInt();
        sc.nextLine(); // Consume newline
        switch (option) {
            case 1:
                viewInventory();
                break;
            case 2:
                System.out.print("Enter Medication Name: ");
                String medicationName = sc.nextLine();
                checkMedicationStock(medicationName);
                break;
            case 3:
                checkLowStockMedications();
                break;
            default:
                System.out.println("Invalid option. Returning to main menu.");
        }
    }

    // Submit and View Replenishment Requests sub-menu
    private void manageReplenishmentRequests(Scanner sc) {
        System.out.println("=== Replenishment Requests ===");
        System.out.println("1. Submit Replenishment Request");
        System.out.println("2. View Replenishment Request Status");
        System.out.print("Select an option: ");

        int option = sc.nextInt();
        sc.nextLine(); // Consume newline
        switch (option) {
            case 1:
                System.out.print("Enter medication name: ");
                String medicineName = sc.nextLine();
                System.out.print("Enter quantity: ");
                int quantity = sc.nextInt();
                sc.nextLine(); // Consume newline
                System.out.print("Enter expiration date (YYYY-MM-DD): ");
                String date = sc.nextLine();
                submitReplenishmentRequest(medicineName, quantity, LocalDate.parse(date));
                break;
            case 2:
                viewReplenishmentRequestStatus();
                break;
            default:
                System.out.println("Invalid option. Returning to main menu.");
        }
    }

    // Remove later - menu shows all pharmacist functions but not required according Assignment
    // // Display menu for pharmacist-specific operations
    // @Override
    // public void displayMenu(){
    //     System.out.println("=========================================");
    //     System.out.println("Pharmacist Control Menu:");
    //     System.out.println("1. View Prescriptions");
    //     System.out.println("2. Update Prescription Status");
    //     System.out.println("3. View Medication Inventory");
    //     System.out.println("4. Check Medication Stock");
    //     System.out.println("5. Check Low Stock Medications");
    //     System.out.println("6. Fulfill Prescription");
    //     System.out.println("7. Submit Replenishment Request");
    //     System.out.println("8. View Replenishment Request Status");
    //     System.out.println("9. Logout");
    //     System.out.print("Enter choice: ");
    // }

    // This is the pharmacist menu according to Assignment - The one above is a modified version of this but has a better flow i think 
    //  @Override
    // public void displayMenu() {
    //     Scanner sc = new Scanner(System.in);
    //     while (true) {
    //         System.out.println("=========================================");
    //         System.out.println("Pharmacist Control Menu:");
    //         System.out.println("1. View Appointment Outcome Record");
    //         System.out.println("2. Update Prescription Status");
    //         System.out.println("3. View Medication Inventory");
    //         System.out.println("4. Submit Replenishment Request");
    //         System.out.println("5. Logout");
    //         System.out.print("Enter choice: ");

    //         int choice = sc.nextInt();
    //         sc.nextLine();  // Consume newline

    //         switch (choice) {
    //             case 1:
    //                 viewAppointmentOutcomeRecord();
    //                 break;
    //             case 2:
    //                 updatePrescriptionStatus(sc);
    //                 break;
    //             case 3:
    //                 viewInventory();
    //                 break;
    //             case 4:
    //                 submitReplenishmentRequest(sc);
    //                 break;
    //             case 5:
    //                 System.out.println("Logging out of Pharmacist Control.");
    //                 return;
    //             default:
    //                 System.out.println("Invalid input. Please select an option between 1 and 5.");
    //         }
    //     }
    // }

    
     // Method to find a prescription by ID
    private Prescription findPrescriptionById(int id) {
        for (Prescription prescription : pharmacist.getPrescriptions()) {
            if (prescription.getId() == id) {
                return prescription;
            }
        }
        return null;
    }


    // View all prescriptions assigned to the pharmacist
    public void viewPrescriptions() {
        List<Prescription> prescriptions = pharmacist.getPrescriptions();
        System.out.println("Pharmacist's Prescriptions:");
        for (Prescription prescription : prescriptions) {
            System.out.println(prescription);
        }
    }

    // Update the status of a specific prescription
    public void updatePrescriptionStatus(Prescription prescription, PrescriptionStatus status) {
        prescription.setStatus(status);
        System.out.println("Prescription for " + prescription.getMedicine().getMedicineName() + 
                           " updated to " + status);
    }

    // View entire medication inventory
    public void viewInventory() {
        inventoryControl.displayInventory();
    }

    // Check the stock of a specific medication based on its name
    public int checkMedicationStock(String medicationName) {
        int stock = inventoryControl.checkStock(medicationName);
        if (stock == -1) {
            System.out.println("Medication " + medicationName + " not found in inventory.");
        } else {
            System.out.println("Medication: " + medicationName + ", Stock: " + stock);
        }
        return stock;
    }

    // Automatically list medications that are below the low stock threshold
    public void checkLowStockMedications() {
        List<String> lowStockMeds = inventoryControl.getLowStockMedications();
        System.out.println("Medications below low stock threshold:");
        for (String med : lowStockMeds) {
            System.out.println("- " + med);
        }
    }

    // Fulfill a prescription by verifying stock levels and updating status
    public void fulfillPrescription(Prescription prescription) {
        Medicine medicine = prescription.getMedicine();
        String medicineName = medicine.getMedicineName();
        int currentStock = checkMedicationStock(medicineName);

        if (currentStock < prescription.getDosage()) {
            System.out.println("Insufficient stock for " + medicineName + " to fulfill the prescription.");
            submitReplenishmentRequest(medicineName, prescription.getDosage());
            prescription.setStatus(PrescriptionStatus.PENDING);
            System.out.println("Prescription status set to PENDING due to low stock.");
        } else {
            // Reduce stock and mark prescription as dispensed
            inventoryControl.reduceStock(medicineName, prescription.getDosage());
            prescription.setStatus(PrescriptionStatus.DISPENSED);
            System.out.println("Prescription for " + medicineName + " has been dispensed to Patient ID: " + prescription.getPatientID());
        }
    }

    // Method for submitting a replenishment request
    public void submitReplenishmentRequest(String medicineName, int quantity) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter expiration date for replenishment (YYYY-MM-DD): ");
        LocalDate expirationDate = LocalDate.parse(sc.nextLine().trim());

        // Submit a request without directly updating inventory
        if (inventoryControl.inventoryMap.containsKey(medicineName)) {
            ReplenishmentRequest request = new ReplenishmentRequest(medicineName, quantity, expirationDate);
            inventoryControl.requestMap.put(medicineName, request);
            System.out.println("Replenishment request submitted for " + medicineName + " (" + quantity + " units, expires on " + expirationDate + ").");
        } else {
            System.out.println("Error: Medication " + medicineName + " not found in inventory.");
        }
    }

    // View the status of all submitted replenishment requests
    public void viewReplenishmentRequestStatus() {
        System.out.println("\nReplenishment Request Status:");
        if (inventoryControl.requestMap.isEmpty()) {
            System.out.println("No replenishment requests have been submitted.");
        } else {
            for (ReplenishmentRequest request : inventoryControl.requestMap.values()) {
                System.out.printf("Medicine: %s, Quantity: %d, Expiration: %s, Status: %s%n",
                    request.getMedicineName(), request.getRequestedQuantity(), request.getExpirationDate(), request.getStatus());
            }
        }
    }
}
