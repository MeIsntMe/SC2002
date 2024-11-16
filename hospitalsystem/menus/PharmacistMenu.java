package hospitalsystem.menus;

import java.util.InputMismatchException;
import java.util.Scanner;

import hospitalsystem.HMS;
import hospitalsystem.inventorycontrol.InventoryControl;
import hospitalsystem.usercontrol.PharmacistUserControl;

public class PharmacistMenu implements MenuInterface {
    // can remove this 
    //private final PharmacistUserControl pharmacistUserControl;

    // Constructor
    public PharmacistMenu() {
        // can remove this as all methods are static
        //this.pharmacistUserControl = new PharmacistUserControl();
    }

    /**
     * Displays the Pharmacist Menu and processes user choices.
     */
    @Override
    public void displayMenu() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("=========================================");
            System.out.println("Pharmacist Menu:");
            System.out.println("1. View Appointment Outcome Records");
            System.out.println("2. Update Prescription Status");
            System.out.println("3. View Medication Inventory");
            System.out.println("4. Submit Replenishment Request");
            System.out.println("5. Logout");
            System.out.println("=========================================");
            System.out.print("Enter your choice (1-5): ");

            try {
                int choice = sc.nextInt();
                sc.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        PharmacistUserControl.viewAppointmentOutcomeRecord(sc);
                        break;
                    case 2:
                        PharmacistUserControl.updatePrescriptionStatus(sc);
                        break;
                    case 3:
                        InventoryControl.displayInventory();
                        break;
                    case 4:
                        PharmacistUserControl.submitReplenishmentRequest(sc);
                        break;
                    case 5:
                        System.out.println("You have successfully logged out.");
                        HMS.logout();
                        return;
                    default:
                        System.out.println("Invalid choice! Please select a valid option.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input! Please enter a number between 1 and 5.");
                sc.nextLine(); // Clear the invalid input
            }
        }
    }
}
