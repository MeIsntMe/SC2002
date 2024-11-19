package hospitalsystem.menus;

import java.util.InputMismatchException;
import java.util.Scanner;

import hospitalsystem.HMS;
import hospitalsystem.appointmentcontrol.AdminAppointmentControl;
import hospitalsystem.inventorycontrol.AdminInventoryControl;
import hospitalsystem.model.Administrator;
import hospitalsystem.model.User;
import hospitalsystem.usercontrol.AdminUserControl;

/**
 * Represents the menu for administrator users in the Hospital Management System.
 * Provides options for administrators to manage staff, patients, appointments, and inventory.
 *
 * @author An Xian
 * @version 1.0
 * @since 2024-11-19
 *
 */
public class AdminMenu implements MenuInterface {
    
    // Instance variables 
    private final Scanner sc;
    private final Administrator admin;

    // Constructor
    public AdminMenu(User currentUser) { 
        if (!(currentUser instanceof Administrator)) {
            throw new IllegalArgumentException("User must be a Administrator ");
        }
        this.admin = (Administrator) currentUser;
        this.sc = new Scanner(System.in);
    }


    @Override
    public void displayMenu(){
        while (true) {
            System.out.println("=========================================");
            System.out.println("Administrator Menu:");
            System.out.println("1. View and Manage Staff and Patients");
            System.out.println("2. View All Appointments");
            System.out.println("3. View and Manage Medication Inventory");
            System.out.println("4. Approve Replenishment Requests");
            System.out.println("5. Logout");
            System.out.print("Enter choice (1-5): ");
            try{
                int choice = Integer.parseInt(sc.nextLine());
                switch (choice) {
                    case 1:
                        AdminMenu.manageUserMenu(sc);
                        break;
                    case 2: 
                        AdminAppointmentControl.viewAllAppointments(); 
                        break;
                    case 3:
                        AdminMenu.manageInventoryMenu();
                        break;
                    case 4:
                        AdminInventoryControl.manageRequests(sc);
                        break;
                    case 5:
                        HMS.logout();
                        return;
                    default: 
                        System.out.println("Invalid input. Please enter a number between 1-5 ");
                        break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            } catch (Exception e) {
                System.out.println("An error has occurred: " + e.getMessage());
                sc.nextLine();
            }
        }
    }

    // change Staff Control to User control and let admin crud patient 

    // MANAGE STAFF MENU
    public static void manageUserMenu(Scanner sc) {
        while (true) {            
            System.out.println("=========================================");
            System.out.println("Staff/Patient Management: ");
            System.out.println("1. Add staff or patient");
            System.out.println("2. Remove staff or patient");
            System.out.println("3. Update staff details");  
            System.out.println("4. Display filtered list of staff");
            System.out.println("5. Exit Staff Management");
            System.out.print("Enter choice: ");

            try{
                int choice = Integer.parseInt(sc.nextLine());
                switch (choice) {
                    case 1 -> AdminUserControl.addUser(sc);
                    case 2 -> AdminUserControl.removeUser(sc);
                    case 3 -> AdminUserControl.updateStaffDetails(sc);
                    case 4 -> AdminUserControl.displayStaffList(sc);
                    case 5 -> {return;}
                    default -> System.out.println("Invalid input! Please enter a number between 1-5 ");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a number between 1-5");
            } catch (Exception e) {
                System.out.println("An error has occurred: " + e.getMessage());
                sc.nextLine();
            }
        }
    }


    public static void manageInventoryMenu() {
        while (true) {
            Scanner sc = new Scanner(System.in);

            System.out.println("=========================================");
            System.out.println("Inventory Management: ");
            System.out.println("1. Display inventory");
            System.out.println("2. Manage medicine stock");
            System.out.println("3. Update low stock level alert line");
            System.out.println("4. Exit Inventory Management");
            System.out.print("Enter choice: "); 

            try {
                int choice = Integer.parseInt(sc.nextLine());
                switch (choice) {
                    case 1 -> AdminInventoryControl.displayInventory();
                    case 2 -> AdminInventoryControl.manageStock(sc);
                    case 3 -> AdminInventoryControl.updateLowStockAlert(sc);
                    case 4 -> {return; }
                    default -> System.out.println("Invalid input! Please enter a number between 1-7.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a number between 1-7.");
            } catch (Exception e) {
                System.out.println("An error has occurred: " + e.getMessage());
                sc.nextLine();
            }
        }
    }

}

