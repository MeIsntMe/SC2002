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
                int role; 
                switch (choice) {
                    case 1: 
                        manageStaff();
                        continue;
                    case 2: 
                        //call appointment control  
                    case 3: 
                        //call inventory control
                    case 4: 
                        //call inventory control
                    case 5: 
                        MainSystem.currentUser = null; 

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

    public static void manageStaff(){
        while (true) {
            Scanner sc = new Scanner(System.in);
            
            System.out.println("=========================================");
            System.out.println("Staff Management: ");
            System.out.println("1. Add staff");
            System.out.println("2. Remove staff");
            System.out.println("3. Update staff details"); // TO DO: WHAT IS THIS. 
            System.out.println("4. Display filtered list of staff");
            System.out.println("5. Exit Staff Management");
            System.out.print("Enter choice: ");

            try{
                int choice = sc.nextInt();
                int role; 
                switch (choice) {
                    case 1: 
                        StaffControl.addStaff(sc);
                        continue;
                    case 2: 
                        StaffControl.removeStaff(sc);
                        continue;
                    case 3: 
                        // TO DO: WHAT IS THIS. 
                    case 4: 
                    StaffControl.displayStaffFiltered(sc);
                    case 5: 
                        sc.close();
                        return;

                }
        }
    }

    
    
}

