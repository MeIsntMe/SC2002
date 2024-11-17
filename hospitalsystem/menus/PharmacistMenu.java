package hospitalsystem.menus;

import java.util.List;
import hospitalsystem.model.Prescription;
import hospitalsystem.appointmentcontrol.AppointmentControl;
import hospitalsystem.data.Database;
import java.util.Scanner;
import hospitalsystem.enums.PrescriptionStatus;
import hospitalsystem.model.Appointment;
import hospitalsystem.model.Pharmacist;
import hospitalsystem.model.Medicine;
import hospitalsystem.model.Medicine.Batch;
import hospitalsystem.model.Patient;
import hospitalsystem.model.User;
import java.time.LocalDate;
import hospitalsystem.model.ReplenishmentRequest;


public class PharmacistMenu implements MenuInterface {

    private final Pharmacist pharmacist;

    // Constructor
    public PharmacistMenu(User currentUser) { 
        if (!(currentUser instanceof Pharmacist)) {
            throw new IllegalArgumentException("User must be a Doctor");
        }
        this.pharmacist = (Pharmacist) currentUser;
    }

    
    //Shift to pharmacistControl
    // Method to add a new batch to the medicine
    public void addBatch(int quantity, LocalDate expirationDate) {
        batches.add(new Batch(quantity, expirationDate));
        batches.sort(Comparator.comparing(Batch::getExpirationDate));
        System.out.println("Added batch of " + quantity + " units for " + medicineName + ", expires on " + expirationDate);
    }

    // Get batches nearing expiration
    public List<Batch> getNearingExpirationBatches(int weeksBeforeExpiration) {
        List<Batch> nearingExpiration = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (Batch batch : batches) {
            if (batch.getExpirationDate().isBefore(today.plusWeeks(weeksBeforeExpiration))) {
                nearingExpiration.add(batch);
            }
        }
        return nearingExpiration;
    }

    // Dispense a specified quantity, prioritizing batches closest to expiration
    public boolean dispense(int quantity) {
        Iterator<Batch> iterator = batches.iterator();
        int batchQuantity;
        while (iterator.hasNext() && quantity > 0) {
            Batch batch = iterator.next();
            batchQuantity = batch.getQuantity();
            if (batchQuantity <= quantity) {
                quantity -= batchQuantity;
                iterator.remove();
                System.out.println("Used up batch of " + medicineName + " with expiration date: " + batch.getExpirationDate());
            } else {
                batch.setQuantity(batchQuantity - quantity);
                System.out.println("Dispensed " + quantity + " units from batch of " + medicineName + " with expiration date: " + batch.getExpirationDate());
                quantity = 0;
            }
        }

        if (quantity > 0) {
            System.out.println("Insufficient stock to dispense " + quantity + " units of " + medicineName);
            return false;
        }
        return true;
    }

    // // View Inventory and Stock Information sub-menu
    // private void viewInventoryAndStock(Scanner sc) {
    //     System.out.println("=== View Inventory and Stock ===");
    //     System.out.println("1. View Entire Inventory");
    //     System.out.println("2. Check Medication Stock");
    //     System.out.println("3. Check Low Stock Medications");
    //     System.out.print("Select an option: ");

    //     int option = sc.nextInt();
    //     sc.nextLine(); // Consume newline
    //     switch (option) {
    //         case 1:
    //             viewInventory();
    //             break;
    //         case 2:
    //             System.out.print("Enter Medication Name: ");
    //             String medicationName = sc.nextLine();
    //             checkMedicationStock(medicationName);
    //             break;
    //         case 3:
    //             checkLowStockMedications();
    //             break;
    //         default:
    //             System.out.println("Invalid option. Returning to main menu.");
    //     }
    // }

    // // Submit and View Replenishment Requests sub-menu
    // private void manageReplenishmentRequests(Scanner sc) {
    //     System.out.println("=== Replenishment Requests ===");
    //     System.out.println("1. Submit Replenishment Request");
    //     System.out.println("2. View Replenishment Request Status");
    //     System.out.print("Select an option: ");

    //     int option = sc.nextInt();
    //     sc.nextLine(); // Consume newline
    //     switch (option) {
    //         case 1:
    //             System.out.print("Enter medication name: ");
    //             String medicineName = sc.nextLine();
    //             System.out.print("Enter quantity: ");
    //             int quantity = sc.nextInt();
    //             sc.nextLine(); // Consume newline
    //             System.out.print("Enter expiration date (YYYY-MM-DD): ");
    //             String date = sc.nextLine();
    //             submitReplenishmentRequest(medicineName, quantity, LocalDate.parse(date));
    //             break;
    //         case 2:
    //             viewReplenishmentRequestStatus();
    //             break;
    //         default:
    //             System.out.println("Invalid option. Returning to main menu.");
    //     }
    // }

   
    @Override
    public void displayMenu() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("=========================================");
            System.out.println("Pharmacist Control Menu:");
            System.out.println("1. View Appointment Outcome Record");
            System.out.println("2. Update Prescription Status");
            System.out.println("3. View Medication Inventory");
            System.out.println("4. Submit Replenishment Request");
            System.out.println("5. Logout");
            System.out.print("Enter choice: ");

            int choice = sc.nextInt();
            sc.nextLine();  // Consume newline

            switch (choice) {
                case 1 -> viewAppointmentOutcomeRecord();
                case 2 -> updatePrescriptionStatus(sc);
                case 3 -> viewInventory();
                case 4 -> submitReplenishmentRequest(sc);
                case 5 -> {
                    System.out.println("Logging out of Pharmacist Control.");
                    return;
                }
                default -> System.out.println("Invalid input. Please select an option between 1 and 5.");
            }
        }
    }

    //  need to reconfirm if we still need this 
    // Method to find a prescription by ID
    private Prescription findPrescriptionById(int id) {
        for (Prescription prescription : pharmacist.getPrescriptions()) {
            if (prescription.getId() == id) {
                return prescription;
            }
        }
        return null;
    }

    // View all appointment outcomes and prescriptions for the pharmacist
    private void viewAppointmentOutcomeRecord() {
        List<Prescription> prescriptions = pharmacist.getPrescriptions();
        System.out.println("Pharmacist's Appointment Outcome Records:");
        for (Prescription prescription : prescriptions) {
            System.out.printf("Patient ID: %s, Medicine: %s, Dosage: %d, Status: %s%n",
                    prescription.getPatientID(),
                    prescription.getMedicine().getMedicineName(),
                    prescription.getDosage(),
                    prescription.getStatus());
        }
    }

    // // View all prescriptions assigned to the pharmacist
    // public void viewPrescriptions() {
    //     List<Prescription> prescriptions = pharmacist.getPrescriptions();
    //     System.out.println("Pharmacist's Prescriptions:");
    //     for (Prescription prescription : prescriptions) {
    //         System.out.println(prescription);
    //     }
    // }

    // Update the status of a specific prescription
    private void updatePrescriptionStatus(Scanner sc) {
        System.out.print("Enter Appointment ID to update prescription status: ");
        String appointmentID = sc.nextLine();

        Appointment appointment = Database.appointmentMap.get(appointmentID);
        
        Prescription prescription = appointment.getPrescription();
        if (prescription.getMedicineList().isEmpty()) {
            System.out.println("No prescriptions found for this appointment.");
            return;
        }

        System.out.println("Prescriptions in Appointment " + appointmentID + ":");
        for (int i = 0; i < prescriptions.size(); i++) {
            Prescription prescription = prescriptions.get(i);
            System.out.printf("%d. %s (Status: %s)%n", i + 1, prescription.getMedicine().getMedicineName(), prescription.getStatus());
        }

        System.out.print("Select prescription to update: ");
        int prescriptionIndex = sc.nextInt() - 1;
        sc.nextLine();  // Consume newline
        
        if (prescriptionIndex >= 0 && prescriptionIndex < prescriptions.size()) {
            Prescription prescription = prescriptions.get(prescriptionIndex);
            System.out.print("Enter new status (e.g., PENDING, DISPENSED, REJECTED): ");
            String statusInput = sc.nextLine().toUpperCase();

            try {
                PrescriptionStatus newStatus = PrescriptionStatus.valueOf(statusInput);
                if (AppointmentControl.updatePrescriptionStatus(appointmentID, prescription.getMedicine().getMedicineName(), newStatus)) {
                    System.out.println("Prescription status updated successfully.");
                } else {
                    System.out.println("Failed to update prescription status.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid status entered.");
            }
        } else {
            System.out.println("Invalid selection.");
        }
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

    // Submit a replenishment request
    public void submitReplenishmentRequest(Scanner sc) {
        System.out.print("Enter medication name to request replenishment: ");
        String medicineName = sc.nextLine();
        System.out.print("Enter quantity for replenishment: ");
        int quantity = sc.nextInt();
        sc.nextLine();  // Consume newline

        // Create and submit the request without specifying an expiration date
        ReplenishmentRequest request = new ReplenishmentRequest(medicineName, quantity);
        inventoryControl.addReplenishmentRequest(request);

        System.out.println("Replenishment request submitted for " + medicineName + " with quantity " + quantity + ".");
    }

    // // Modify the submitReplenishmentRequest method to accept String, int, LocalDate
    // public void submitReplenishmentRequest(String medicineName, int quantity, LocalDate expirationDate) {
    //     // Existing implementation to create a ReplenishmentRequest and add it to requestMap
    //     if (inventoryControl.inventoryMap.containsKey(medicineName)) {
    //         ReplenishmentRequest request = new ReplenishmentRequest(medicineName, quantity, expirationDate);
    //         inventoryControl.requestMap.put(medicineName, request);
    //         System.out.println("Replenishment request submitted for " + medicineName + " (" + quantity + " units, expires on " + expirationDate + ").");
    //     } else {
    //         System.out.println("Error: Medication " + medicineName + " not found in inventory.");
    //     }
    // }

    
    // // Method for submitting a replenishment request
    // public void submitReplenishmentRequest(String medicineName, int quantity) {
    //     Scanner sc = new Scanner(System.in);
    //     System.out.print("Enter expiration date for replenishment (YYYY-MM-DD): ");
    //     LocalDate expirationDate = LocalDate.parse(sc.nextLine().trim());

    //     // Submit a request without directly updating inventory
    //     if (inventoryControl.inventoryMap.containsKey(medicineName)) {
    //         ReplenishmentRequest request = new ReplenishmentRequest(medicineName, quantity, expirationDate);
    //         inventoryControl.requestMap.put(medicineName, request);
    //         System.out.println("Replenishment request submitted for " + medicineName + " (" + quantity + " units, expires on " + expirationDate + ").");
    //     } else {
    //         System.out.println("Error: Medication " + medicineName + " not found in inventory.");
    //     }
    // }

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
