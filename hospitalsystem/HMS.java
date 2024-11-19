package hospitalsystem;

import hospitalsystem.data.Database;
import hospitalsystem.enums.UserType;
import hospitalsystem.menus.*;
import hospitalsystem.model.*;
import hospitalsystem.usercontrol.AdminUserControl;
import java.util.Scanner;

/**
 * The main class for the Hospital Management System (HMS).
 * It provides the entry point and handles user interactions, login, and data management.
 *
 * @author An Xian, Gracelynn, Leo
 * @version 1.0
 * @since 2024-11-19
 */
public class HMS {
    /**
     * The currently logged-in user.
     */
    public static User currentUser;

    /**
     * The main method that serves as the entry point of the HMS application.
     * It displays the main menu, handles user input, and manages the application flow.
     *
     * @param args The command-line arguments.
     */
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
                        Database.loadAllData();
                        role = login(scanner);
                        if (role == null) {
                            clearLoadedData(); //Clear data
                        } else {
                            MenuInterface control = null;
                            switch (role) {
                                case PATIENT -> control = new PatientMenu(currentUser);
                                case DOCTOR -> control = new DoctorMenu(currentUser);
                                case PHARMACIST -> control = new PharmacistMenu(currentUser);
                                case ADMINISTRATOR -> control = new AdminMenu(currentUser);
                            }
                            control.displayMenu();
                        }
                        break;
                    case 2:
                        scanner.close();
                        return;
                    default:
                        System.out.println("Invalid choice. Please input either 1 or 2.");
                }

            } catch (Exception e) {
                    System.out.println("Invalid input. Please input either 1 or 2.");
                    scanner.nextLine(); // Consume the invalid input
            }
        }
    }

    /**
     * Handles the user login process.
     * Prompts the user for their role, user ID, and password.
     * Validates the provided credentials and sets the currentUser if login is successful.
     *
     * @param sc The Scanner object for user input.
     * @return The UserType if login is successful, or null if login fails.
     */
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
            case PATIENT -> user = Database.patientsMap.get(inputID);
            case DOCTOR -> user = Database.doctorsMap.get(inputID);
            case PHARMACIST -> user = Database.pharmsMap.get(inputID);
            case ADMINISTRATOR -> user = Database.adminsMap.get(inputID);
        }

        //System.out.println("user created");
        //System.out.println(Database.patientsMap);
        //System.out.printf("Password: %s", user.getPassword());

        // Validate credentials
        if (user != null && user.getPassword().equals(inputPassword)) {
            currentUser = user;
            System.out.printf("Login successful. Welcome %s!\n", currentUser.getName());
            loadRequiredData(role);

            // If first time log in, reset password 
            resetPassword(currentUser, sc);

            return role;
        } else {
            System.out.println("Invalid ID or password.");
            clearLoadedData(); // Clear loaded data on failed login
            return null;
        }
    }

    /**
     * Handles the user logout process.
     * Saves all data, clears loaded data, and sets currentUser to null.
     */
    public static void logout() {
        try {
            Database.saveAllData(); // Save all changes before logout
            clearLoadedData();
            currentUser = null;
            System.out.println("Logout successful!");
        } catch (Exception e) {
            System.out.println("Error during logout: " + e.getMessage());
        }
    }

    /**
     * Prompts the user to reset their password if the default password is detected.
     *
     * @param user The User object representing the currently logged-in user.
     * @param sc The Scanner object for user input.
     */
    public static void resetPassword(User user, Scanner sc) {
        // Check if first time log in
        if (user.getPassword().equals("password")) {
            System.out.println("=========================================");
            System.out.println("Default password detected. Please reset your password.");

            while (true) {
                System.out.println("Password requirements:");
                System.out.println("- At least 6 characters long");
                System.out.println("- Cannot contain commas");
                System.out.print("Enter new password: ");
                String newPW = sc.nextLine();

                // Validate password
                if (newPW.isEmpty() || newPW.length() < 6) {
                    System.out.println("Password must be at least 6 characters long.");
                    continue;
                }
                if (newPW.contains(",")) {
                    System.out.println("Password cannot contain commas.");
                    continue;
                }
                if (newPW.equals("password")) {
                    System.out.println("New password cannot be the same as default password.");
                    continue;
                }

                // If all validation passes, update the password
                user.setPassword(newPW);
                Database.updatePassword(user, newPW);
                Database.saveAllData();
                System.out.println("Password updated!");
                break;
            }
        }
    }


    /**
     * Prompts the user to confirm if they want to repeat an action.
     *
     * @param sc The Scanner object for user input.
     * @return true if the user wants to repeat the action, false otherwise.
     */
    public static boolean repeat(Scanner sc) {
        System.out.print("Would you like to repeat? (y/n): ");
        while (true) {
            try {
                char choice = sc.nextLine().charAt(0);
                if (choice == 'y') return true;
                else if (choice == 'n') return false;
                else System.out.println("Invalid input. Please input 'y' or 'n'. ");
            } catch (Exception e) {
                System.out.println("Invalid input. Please input 'y' or 'n'. ");
            }
        }
    }

    /**
     * Loads the required data based on the user type.
     *
     * @param userType The UserType representing the user's role.
     */
    public static void loadRequiredData(UserType userType) {
        clearLoadedData(); // Clear the minimal staff data first

        switch (userType) {
            case DOCTOR:
                Database.loadStaffData();
                Database.loadPatientData();
                Database.loadInventoryData();
                Database.loadAppointmentData();
                break;

            case ADMINISTRATOR:
                Database.loadStaffData();
                Database.loadPatientData();
                Database.loadInventoryData();
                Database.loadAppointmentData();
                break;

            case PHARMACIST:
                Database.loadStaffData();
                Database.loadPatientData();
                Database.loadInventoryData();
                Database.loadAppointmentData();
                break;

            case PATIENT:
                Database.loadPatientData();
                Database.loadStaffData();
                Database.loadAppointmentData();
                break;
        }
    }

    /**
     * Clears all loaded data from the local database.
     */
    private static void clearLoadedData() {
        Database.patientsMap.clear();
        Database.doctorsMap.clear();
        Database.adminsMap.clear();
        Database.pharmsMap.clear();
        Database.inventoryMap.clear();
        Database.appointmentMap.clear();
        Database.requestMap.clear();
    }
}