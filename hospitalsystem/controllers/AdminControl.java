package hospitalsystem.controllers;

import java.util.InputMismatchException;
import java.util.Scanner;

import hospitalsystem.MainSystem;

public class AdminControl implements MenuInterface {
    
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
                    case 4 -> AdminInventoryControl.approveRequests(sc);
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

}

