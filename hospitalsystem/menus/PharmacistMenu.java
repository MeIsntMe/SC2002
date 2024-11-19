package hospitalsystem.menus;

import hospitalsystem.HMS;
import hospitalsystem.inventorycontrol.PharmacistInventoryControl;
import hospitalsystem.model.Pharmacist;
import hospitalsystem.model.User;
import hospitalsystem.usercontrol.PharmacistUserControl;
import java.util.Scanner;

public class PharmacistMenu implements MenuInterface {
    private final Pharmacist pharmacist;

    /**
     * Displays the Pharmacist Menu and processes user choices.
     * Ensures type-safety via checks
     */
    public PharmacistMenu(User currentUser) { 
        if (!(currentUser instanceof Pharmacist)) {
            throw new IllegalArgumentException("User must be a Pharmacist.");
        }
        this.pharmacist = (Pharmacist) currentUser;
    }

    @Override
    public void displayMenu() {
        while (true) {

            Scanner sc = new Scanner(System.in);
            System.out.println("=========================================");
            System.out.println("Pharmacist Menu:");
            System.out.println("1. View Appointment Outcome Records");
            System.out.println("2. Update Prescription Status");
            System.out.println("3. View Medication Inventory");
            System.out.println("4. Submit Replenishment Request");
            System.out.println("5. Submit Replenishment Request for All Low Stock Medicines");
            System.out.println("6. View All Replenishment Requests");
            System.out.println("7. Logout");
            System.out.println("=========================================");
            System.out.print("Enter your choice (1-6): ");

            try {
                int choice = Integer.parseInt(sc.nextLine());

                switch (choice) {
                    case 1:
                        //View all appointment outcome records
                        PharmacistUserControl.viewAppointmentOutcomeRecord(sc);
                        break;
                    case 2:
                        //updates prescription status
                        PharmacistUserControl.updatePrescriptionStatus(sc);
                        break;
                    case 3:
                        // Views medication inventory
                        PharmacistInventoryControl.displayInventory();
                        break;
                    case 4:
                        // Submit replenishment request (manual selection of medicine)
                        PharmacistInventoryControl.submitReplenishmentRequest(sc);
                        break;
                    case 5:
                        // Submit replenishment request automatically for all low stock medicines 
                        PharmacistInventoryControl.autoRequestLowStockReplenishment(); 
                        break;
                    case 6: 
                        //View all replenishment requests 
                        PharmacistInventoryControl.displayAllRequests();
                        break;
                    case 7:
                        HMS.logout();
                        sc.close();
                        return;
                    default:
                        System.out.println("Invalid choice! Please select a valid option.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number between 1 and 6.");
            } catch (Exception e) {
                System.out.println("An unexpected error occurred: " + e.getMessage());
            }
        }
    }
}
