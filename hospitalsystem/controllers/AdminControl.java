package hospitalsystem.controllers;

import java.util.InputMismatchException;
import java.util.Scanner;

import hospitalsystem.MainSystem;

public class AdminControl {
    
    public static void displayMenu(){
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
                    case 1: 
                        StaffControl.manageStaffMenu(scanner);
                        continue;
                    case 2: 
                        //call appointment control
                    case 3: 
                        InventoryControl.manageInventoryMenu();
                    case 4: 
                        //wait for pharmacist
                    case 5: 
                        MainSystem.currentUser = null; 
                    default: 
                        System.out.println("Invalid input! Please enter a number between 1-5 ");
                        break; 
                }
            } catch (InputMismatchException e) {
                System.out.println("=====================================");
                System.out.println("Invalid input!");
                scanner.nextLine();
            } catch (Exception e) {
                System.out.println("=====================================");
                System.out.println("An error has occurred: " + e);
            }
        }
    }

}

