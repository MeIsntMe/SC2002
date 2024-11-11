package hospitalsystem;
// Provides login and redirects users to respective menus

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;

import hospitalsystem.model.Patient;
import hospitalsystem.model.Doctor;
import hospitalsystem.model.Administrator;
import hospitalsystem.model.Pharmacist;
import hospitalsystem.model.User;
import hospitalsystem.model.Medicine;
import hospitalsystem.controllers.*;
import hospitalsystem.enums.UserType;

public class MainSystem {

    public static User currentUser;

    // HashMap to store each role
    public static Map<String, User> patientsMap = new HashMap<>();
    public static Map<String, User> doctorsMap = new HashMap<>();
    public static Map<String, User> adminsMap = new HashMap<>();
    public static Map<String, User> pharmsMap = new HashMap<>();

    public static void main(String[] args) {

        loadPatientfromCSV("patientFilePath.csv");
        loadStaffFromCSV("staffFilePath.csv"); 

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
                        //going to need to initialise and call, not static 
                        role = login(scanner); 
                        switch (role) {
                            case PATIENT -> PatientControl.displayMenu();
                            case DOCTOR -> DoctorControl.displayMenu();
                            case PHARMACIST -> PharmacistControl.displayMenu();
                            case ADMINISTRATOR -> AdminControl.displayMenu();
                            default -> {continue;}
                        }
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

    // Log in: returns role type if login successful, else returns null 
    public static UserType login(Scanner sc) {
        
        UserType role = getRoleInput(sc); // Get role

        System.out.print("Enter user ID: ");
        String inputID = sc.nextLine();
        System.out.print("Enter password: ");
        String inputPassword = sc.nextLine();

        // Determine correct map based on role and retrieve user by ID
        User user = null;
        switch (role) {
            case UserType.PATIENT -> user = patientsMap.get(inputID);
            case UserType.DOCTOR -> user = doctorsMap.get(inputID);
            case UserType.PHARMACIST -> user = pharmsMap.get(inputID);
            case UserType.ADMINISTRATOR -> user = adminsMap.get(inputID);
        }
        // Validate credentials
        if (user != null && user.getPassword().equals(inputPassword)) {
            System.out.printf("Login successful. Welcome %s!", currentUser.getName());
            currentUser = user;

            // Reset password for first time log in
            resetPassword(currentUser, sc);

            return role;
        } 
        else {
            System.out.println("Invalid ID or password.");
            return null;
        }
    }

    // Reset password for first time log in 
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
    
    public static void loadPatientfromCSV (String filePath) {
        try (Scanner scanner = new Scanner(new File(filePath))) {
            scanner.nextLine(); // Skip the first line
            while (scanner.hasNextLine()) {
                String patientData[] = scanner.nextLine().split(",");
                String patientID = patientData[0].trim();
                String name = patientData[1].trim();
                String DOB = patientData[2].trim();
                String gender = patientData[3].trim().toLowerCase();
                String bloodType = patientData[4].trim();
                String userEmail = patientData[5].trim();
                String password = patientData.length > 6 ? patientData[6].trim() : "password"; 
                
                Patient patient = new Patient(patientID, name, DOB, gender, bloodType, userEmail, password); 
                patientsMap.put(patientID, patient);
            }
        } catch (FileNotFoundException e) {
            System.out.println("An error has occurred\n" + e.getMessage());
        }
    }

    public static void loadStaffFromCSV(String filePath) {
        try (Scanner scanner = new Scanner(new File(filePath))) {
            scanner.nextLine(); // Skip the first line
            while (scanner.hasNextLine()) {
                String staffData[] = scanner.nextLine().split(",");
                String staffID = staffData[0].trim();
                String name = staffData[1].trim();
                UserType role = UserType.valueOf(staffData[2].trim().toUpperCase());
                String gender = staffData[3].trim();
                int age = Integer.valueOf(staffData[4].trim());
                String password = staffData.length > 5 ? staffData[5].trim() : "password"; 

                switch (role) {
                    case DOCTOR: 
                        Doctor doc = new Doctor(staffID, name, gender, age, password);
                        doctorsMap.put(staffID, doc);
                        break; 
                    case ADMINISTRATOR: 
                        Administrator admin = new Administrator(staffID, name, gender, age, password);
                        adminsMap.put(staffID, admin);
                        break; 
                    case PHARMACIST: 
                        Pharmacist pharm = new Pharmacist(staffID, name, gender, age, password);
                        pharmsMap.put(staffID, pharm);
                        break;
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("An error has occurred\n" + e.getMessage());
        }
    }

    // SUPPORTING FUNC: allows users to choose 1 to continue repeat whatever action
    public static boolean getRepeatChoice(Scanner scanner) {
        while (true) {
            System.out.print("Click 1 to continue and 2 to exit: ");
            String input = scanner.nextLine().trim();

            if (input.equals("1")) {
                return true;  // User chose to continue
            } else if (input.equals("2")) {
                return false; // User chose to exit
            } else {
                System.out.println("Invalid input. Please enter 1 to continue or 2 to exit.");
            }
        }
    }

    // SUPPORTING FUNC: get role type
    public static UserType getRoleInput(Scanner scanner) {
        while (true) {
            System.out.println("Select role (1-4): 1. Patient | 2. Doctor | 3. Pharmacist | 4. Admin");
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

    // 
}