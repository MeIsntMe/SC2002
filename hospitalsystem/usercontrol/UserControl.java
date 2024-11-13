package hospitalsystem.usercontrol;

import java.util.Collection;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

import hospitalsystem.HMS;
import hospitalsystem.data.Database;
import hospitalsystem.enums.UserType;
import hospitalsystem.model.Administrator;
import hospitalsystem.model.Doctor;
import hospitalsystem.model.Patient;
import hospitalsystem.model.Pharmacist;
import hospitalsystem.model.User;


public abstract class UserControl {

    Scanner sc = new Scanner(System.in);

    public abstract void displayDetails();

    public abstract void updateDetails();

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
 
    public static void updateStaffDetails(Scanner sc) {
        System.out.println("=========================================");
        System.out.println("Staff Management > Update Staff Details");

        // Prompt for role and ID 
        UserType role = getStaffRoleInput(sc);
        System.out.print("Enter the staff ID: ");
        String staffID = sc.nextLine().trim().toUpperCase();

        // Retrieve staff
        User staff = null;
        switch (role) {
            case DOCTOR -> staff = Database.doctorsMap.get(staffID);
            case PHARMACIST -> staff = Database.pharmsMap.get(staffID);
            case ADMINISTRATOR -> staff = Database.adminsMap.get(staffID); 
        }
        if (staff == null) { //if not found
            System.out.println("No " + role + " found with ID " + staffID);
            return;
        }

        // Display current details and select field to update
        System.out.println("Current Details:");
        System.out.printf("ID: %s, Name: %s, Gender: %s, Age: %d%n",
                staff.getID(), staff.getName(), staff.getGender(), staff.getAge());

        boolean done = false;
        while (!done) {
            System.out.println("Enter field to update (1-5): 1. Age | 2. Password | 3. Done");
            int choice = sc.nextInt();
            sc.nextLine(); // Consume the newline

            switch (choice) { 
                case 1 -> {
                    System.out.print("Enter new age: ");
                    int age = sc.nextInt();
                    sc.nextLine();
                    staff.setAge(age);
                    System.out.println("Age updated.");
                }
                case 2 -> {
                    System.out.print("Enter new password: ");
                    String password = sc.nextLine().trim();
                    staff.setPassword(password);
                    System.out.println("Password updated.");
                }
                case 3 -> done = true;  // Exit the update loop
                default -> System.out.println("Invalid choice. Please enter a number from 1 to 5.");
            }
        }
    }

    public static UserType getRoleInput(Scanner scanner) {
        while (true) {
            System.out.println("Select role: 1. Patient | 2. Doctor | 3. Pharmacist | 4. Admin");
            int role;
            try {
                role = scanner.nextInt(); 
                switch (role) {
                    case 1 -> {return UserType.PATIENT;}
                    case 2 -> {return UserType.DOCTOR;}
                    case 3 -> {return UserType.PHARMACIST;}
                    case 4 -> {return UserType.ADMINISTRATOR;}
                    default -> System.out.println("Invalid role number specified. Please enter a number between 1 and 4.");
                }
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a number between 1 and 4.");
            }
        }
    }

    public static UserType getStaffRoleInput(Scanner scanner) {
        while (true) {
            System.out.println("Select role (1-3): 1. Doctor | 2. Pharmacist | 3. Administrator");
            int role;
            try {
                role = scanner.nextInt(); 
                switch (role) {
                    case 1 -> {return UserType.DOCTOR;}
                    case 2 -> {return UserType.PHARMACIST;}
                    case 3 -> {return UserType.ADMINISTRATOR;}
                    default -> System.out.println("Invalid selection. Enter a number between 1 and 4.");
                }
            } catch (Exception e) {
                System.out.println("Invalid input. Enter a number between 1 and 4.");
            }
        }
    }

}