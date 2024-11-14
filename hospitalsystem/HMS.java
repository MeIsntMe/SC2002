package hospitalsystem;

import java.util.Scanner;

import hospitalsystem.data.Database;
import hospitalsystem.usercontrol.AdminUserControl;
import hospitalsystem.model.User;
import hospitalsystem.enums.UserType;
import hospitalsystem.menus.AdminMenu;
import hospitalsystem.menus.DoctorMenu;
import hospitalsystem.menus.PatientMenu;
import hospitalsystem.menus.PharmacistMenu;
import hospitalsystem.menus.MenuInterface;

public class HMS {

    public static User currentUser;
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("=========================================");
            System.out.println("Welcome to the Hospital Management System");
            System.out.println("1. Login");
            System.out.println("2. Close application");
            System.out.print("Enter choice: ");
            try{
                int choice = scanner.nextInt();
                UserType role; 
                switch (choice) {
                    case 1: 
                        role = login(scanner);
                        MenuInterface control;
                        switch (role) {
                            case PATIENT -> control = new PatientMenu(currentUser);
                            case DOCTOR -> control = new DoctorMenu(currentUser);
                            case PHARMACIST -> control = new PharmacistMenu(currentUser);
                            case ADMINISTRATOR -> control = new AdminMenu(currentUser);
                            case null -> {control = null;}
                        }
                        if (control != null) 
                            control.displayMenu();
                    case 2:
                        scanner.close();
                        return;
                    default:
                        System.out.println("Invalid choice. Please input either 1 or 2.");
                } 
            } catch (Exception e) {
                    System.out.println("Invalid input. Please input either 1 or 2.");
            }
        }
    }

    public static UserType login(Scanner sc) {  
        //Returns UserType if login successful, else returns null 

        // Get login details 
        UserType role = AdminUserControl.getRoleInput(sc);
        System.out.print("Enter user ID: ");
        String inputID = sc.nextLine();
        System.out.print("Enter password: ");
        String inputPassword = sc.nextLine();

        // Retrieve user from database by ID 
        User user = null;
        switch (role) {
            case UserType.PATIENT -> user = Database.patientsMap.get(inputID);
            case UserType.DOCTOR -> user = Database.doctorsMap.get(inputID);
            case UserType.PHARMACIST -> user = Database.pharmsMap.get(inputID);
            case UserType.ADMINISTRATOR -> user = Database.adminsMap.get(inputID);
        }

        // Validate credentials
        if (user != null && user.getPassword().equals(inputPassword)) {
            System.out.printf("Login successful. Welcome %s!", currentUser.getName());
            currentUser = user;

            // If first time log in, reset password 
            resetPassword(currentUser, sc);

            return role;
        } else {
            System.out.println("Invalid ID or password.");
            return null;
        }
    }

    public static void resetPassword(User user, Scanner sc){

        // Check if first time log in
        if (user.getPassword().equals("password")) {
            System.out.println("=========================================");
            System.out.println("Default password detected. Please reset your password.");
            System.out.println("Enter new password: ");
            String newPW = sc.nextLine(); 

            user.setPassword(newPW);
            System.out.println("Password updated!");
        }
    }

    // Function used throughout system to allow users to repeat action 
    public static boolean repeat(Scanner sc) {
        System.out.println("Would you like to repeat? (y/n): ");
        while (true) {
            try {
                char choice = sc.next().charAt(0);
                if (choice == 'y') return true;
                else if (choice == 'n') return false;
                else System.out.println("Invalid input. Please input 'y' or 'n'. ");
            } catch (Exception e) {
                System.out.println("Invalid input. Please input 'y' or 'n'. ");
            }
        }
    }
}