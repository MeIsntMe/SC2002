package hospitalsystem.menus;

import java.util.InputMismatchException;
import java.util.Scanner;

import hospitalsystem.HMS;
import hospitalsystem.data.Database;
import hospitalsystem.inventorycontrol.InventoryControl;
import hospitalsystem.model.Administrator;
import hospitalsystem.model.Doctor;
import hospitalsystem.model.Patient;
import hospitalsystem.model.Pharmacist;
import hospitalsystem.model.User;
import hospitalsystem.usercontrol.UserControl;

public class AdminMenu implements MenuInterface {
    
    private final Administrator admin; 

    // Constructor
    public AdminMenu(User currentUser) { 
        if (!(currentUser instanceof Doctor)) {
            throw new IllegalArgumentException("User must be a Doctor");
        }
        this.admin = (Administrator) currentUser;
    }


    @Override
    public void displayMenu(){
        while (true) {
            Scanner scanner = new Scanner(System.in);

            System.out.println("=========================================");
            System.out.println("Administrator Portal");
            System.out.println("1. View and Manage Hospital Staff");
            System.out.println("2. View Appointment details");
            System.out.println("3. View and Manage Medication Inventory");
            System.out.println("4. Approve Replenishment Requests");
            System.out.println("5. Logout");
            System.out.print("Enter choice: ");
            try{
                int choice = scanner.nextInt();
                switch (choice) {
                    case 1 -> UserControl.manageStaffMenu(scanner);
                    case 2 -> //call appointment control
                    case 3 -> AdminInventoryControl.manageInventoryMenu();
                    case 4 -> AdminInventoryControl.approveRequests(scanner);
                    case 5 -> {
                        HMS.logout();
                        return;
                    }
                    default -> System.out.println("Invalid input! Please enter a number between 1-5 ");
                }
            } catch (InputMismatchException e) {
                System.out.println("=====================================");
                System.out.println("Invalid input! Please enter a number between 1-5");
                scanner.nextLine();
            }
        }
    }

    // change Staff Control to User control and let admin crud patient 

    // MANAGE STAFF MENU
    public static void manageStaffMenu(Scanner sc) {
        while (true) {            
            System.out.println("=========================================");
            System.out.println("Staff Management: ");
            System.out.println("1. Add staff");
            System.out.println("2. Remove staff");
            System.out.println("3. Update staff details"); // TO DO: WHAT IS THIS
            System.out.println("4. Display filtered list of staff");
            System.out.println("5. Exit Staff Management");
            System.out.print("Enter choice: ");

            try{
                int choice = sc.nextInt();
                switch (choice) {
                    case 1 -> addStaff(sc);
                    case 2 -> removeStaff(sc);
                    case 3 -> updateStaffDetails(sc);
                    case 4 -> displayStaffFiltered(sc);
                    case 5 -> {sc.close(); return;}
                    default -> System.out.println("Invalid input! Please enter a number between 1-5 ");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input! Please enter a number between 1-5");
            }
        }
    }


    public static void manageInventoryMenu() {
        while (true) {
            Scanner sc = new Scanner(System.in);
            InventoryControl.loadInventoryFromCSV("inventoryFilePath.csv");
            
            System.out.println("=========================================");
            System.out.println("Inventory Management: ");
            System.out.println("1. Display inventory");
            System.out.println("2. Add medicine");
            System.out.println("3. Remove medicine");
            System.out.println("4. Update stock level"); 
            System.out.println("5. Update low stock level alert line");
            System.out.println("6. View and Approve Replenishment Requests");
            System.out.println("7. Exit Medical Inventory Management");
            System.out.print("Enter choice: "); 

            try {
                int choice = sc.nextInt();
                sc.nextLine();  // Consume newline
                switch (choice) {
                    case 1 -> displayInventory();
                    case 2 -> addMedicine(sc);  
                    case 3 -> removeMedicine(sc);
                    case 4 -> updateStockLevel(sc);
                    case 5 -> updateLowStockAlert(sc);
                    case 6 -> approveRequests(sc);
                    case 7 -> { sc.close(); return; }
                    default -> System.out.println("Invalid input! Please enter a number between 1-7.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input! Please enter a number between 1-7.");
            }
        }
    }

}

