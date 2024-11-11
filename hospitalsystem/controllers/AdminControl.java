package hospitalsystem.controllers;

import java.util.InputMismatchException;
import java.util.Scanner;

import hospitalsystem.MainSystem;

public class AdminControl implements MenuInterface {
    
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
                    case 1 -> manageStaffMenu(scanner);
                    case 2 -> //call appointment control
                    case 3 -> manageInventoryMenu();
                    case 4 -> //wait for pharmacist
                    case 5 -> {MainSystem.currentUser = null; return;}
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
                    case 1 -> UserControl.addStaff(sc);
                    case 2 -> UserControl.removeStaff(sc);
                    case 3 -> UserControl.updateStaffDetails(sc);
                    case 4 -> UserControl.displayStaffFiltered(sc);
                    case 5 -> {sc.close(); return;}
                    default -> System.out.println("Invalid input! Please enter a number between 1-5 ");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input! Please enter a number between 1-5");
            }
        }
    }

    public static void manageInventoryMenu(){
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
            System.out.println("6. Exit Medical Inventory Management");
            System.out.print("Enter choice: "); 

            try{
                int choice = sc.nextInt();
                switch (choice) {
                    case 1 -> InventoryControl.displayInventory();
                    case 2 -> InventoryControl.addMedicine(sc);  
                    case 3 -> InventoryControl.removeMedicine(sc);
                    case 4 -> InventoryControl.updateStockLevel(sc);
                    case 5 -> InventoryControl.updateLowStockAlarmLine(sc);
                    case 6 -> { sc.close(); return; }
                    default -> System.out.println("Invalid input! Please enter a number between 1-5 ");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input! Please enter a number between 1-5");
            }
        }
    }

}

