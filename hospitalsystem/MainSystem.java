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
import hospitalsystem.controllers.AdminControl;
import hospitalsystem.enums.UserType;

public class MainSystem {

    // Declare hashsets that hold instances of classes of each role
    public static HashSet <Patient> patients = new HashSet<Patient>();
    public static HashSet <Doctor> doctors = new HashSet<Doctor>(); 
    public static HashSet <Administrator> admins = new HashSet<Administrator>(); 
    public static HashSet <Pharmacist> pharms = new HashSet<Pharmacist>();

    private static final Map<Integer, HashSet<? extends User>> userRoleMap = new HashMap<>();
    static {
        userRoleMap.put(1, patients);   // 1 for Patient
        userRoleMap.put(2, doctors);    // 2 for Doctor
        userRoleMap.put(3, admins);     // 3 for Administrator
        userRoleMap.put(4, pharms);     // 4 for Pharmacist
    }
    public static User currentUser;

    //TO DO: make collection for medical supplies

    public static void main(String[] args) {

        // Add users into hashsets 
        // TO DO: replace with actual filepaths
        addPatient("patientFilePath.csv"); 
        addStaff("staffFilePath.csv"); 

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("=========================================");
            System.out.println("Welcome to the Hospital Management System");
            System.out.println("1. Login");
            System.out.println("2. Register (New User Login)");
            System.out.println("3. Close application");
            System.out.print("Enter choice: ");
            try{
                int choice = scanner.nextInt();
                int role; 
                switch (choice) {
                    case 1: 
                        role = getRoleInput(scanner);
                        if (login(role, scanner)) {
                            if (currentUser instanceof Patient) 
                                PatientControl.displayMenu();
                            else if (currentUser instanceof Doctor) 
                                DoctorControl.displayMenu();
                            else if (currentUser instanceof Pharmacist) 
                                PharmacistControl.displayMenu();
                            else 
                                AdminControl.displayMenu();
                        } 
                        continue;
                    case 2: 
                        role = getRoleInput(scanner);
                        newUserLogin(role, scanner);
                        continue;
                    case 3:
                        scanner.close();
                        return;
                    default:
                        System.out.println("=========================================");
                        System.out.println("Invalid choice, try again");
                        continue; 
                } 
            } catch (Exception e) {
                    System.out.println("=====================================");
                    System.out.println("An error has occurred: " + e);
            }
        }
    }

    public static boolean login(int role, Scanner scanner) {
        HashSet<? extends User> users = userRoleMap.get(role);

        System.out.print("Enter user ID: ");
        String inputID = scanner.nextLine();
        System.out.print("Enter password: ");
        String inputPassword = scanner.nextLine();

        for (User user : users) {
            if (user.getID().equals(inputID) && user.getPassword().equalsIgnoreCase(inputPassword)) {
                System.out.println("Login successful!");
                currentUser = user;
                return true;
            }
        }
        System.out.println("Wrong ID or password!");
        return false; 
    }

    public static void newUserLogin(int role, Scanner scanner) {
        HashSet<? extends User> users = userRoleMap.get(Integer.valueOf(role));

        System.out.print("Enter user ID: ");
        String inputID = scanner.nextLine();

        //check if userID already exists
        for (User user : users) {
            if (user.getID().equals(inputID)) {
                System.out.println("User already exists. Please log in.");
                return; 
            }
        }

        //create new user
        System.out.print("Enter name: ");
        String name = scanner.nextLine();
        switch (role) {
            case 1 -> patients.add(new Patient(inputID, name, "", "", "", "", "password"));
            case 2 -> doctors.add(new Doctor(inputID, name, "", 0, "password"));
            case 3 -> pharms.add(new Pharmacist(inputID, name, "", 0, "password"));
            case 4 -> admins.add(new Administrator(inputID, name, "", 0, "password"));
            default -> System.out.println("Invalid role.");
        }

        System.out.println("New user created. Please log in using default password: 'password'.");
        return;
    }

    public static int getRoleInput(Scanner scanner) {
        System.out.println("Select role: 1. Patient | 2. Doctor | 3. Pharmacist | 4. Admin");
        
        int role; 
        try {
            role = scanner.nextInt(); 
            if (!userRoleMap.containsKey(role)) 
                throw new IllegalArgumentException("Invalid role number specified. Please enter a number between 1 and 4.");
        } catch (Exception e) {
            System.out.println("Invalid input. Please enter a number between 1 and 4.");
            return getRoleInput(scanner);
        }
        return role;
    }

    // addPatient(): Reads and adds patients from a file to the system.
    public static void addPatient(String filePath) {
        try (Scanner scanner = new Scanner(new File(filePath))) {
            scanner.nextLine(); // Skip the first line
            while (scanner.hasNextLine()) {
                String userInformation[] = scanner.nextLine().split(",");
                String patientID = userInformation[0].trim();
                String name = userInformation[1].trim();
                String DOB = userInformation[2].trim();
                String gender = userInformation[3].trim().toLowerCase();
                String bloodType = userInformation[4].trim();
                String userEmail = userInformation[5].trim();
                String password = userInformation.length > 6 ? userInformation[6].trim() : "password"; 
                
                patients.add(new Patient(patientID, name, DOB, gender, bloodType, userEmail, password)); //instantiate patients 
            }
        } catch (FileNotFoundException e) {
            System.out.println("An error has occurred\n" + e.getMessage());
        }
    }

    // addStaff(): Reads and adds staff from a file to the system based on user type 
    public static void addStaff(String filePath) {
        try (Scanner scanner = new Scanner(new File(filePath))) {
            scanner.nextLine(); // Skip the first line
            while (scanner.hasNextLine()) {
                String userInformation[] = scanner.nextLine().split(",");
                String staffID = userInformation[0].trim();
                String name = userInformation[1].trim();
                UserType role = UserType.valueOf(userInformation[2].trim().toUpperCase());
                String gender = userInformation[3].trim();
                int age = Integer.valueOf(userInformation[4].trim());
                String password = userInformation.length > 5 ? userInformation[5].trim() : "password"; 

                switch (role) {
                    case DOCTOR -> doctors.add(new Doctor(staffID, name, gender, age, password));
                    case ADMINISTRATOR -> admins.add(new Administrator(staffID, name, gender, age, password));
                    case PHARMACIST -> pharms.add(new Pharmacist(staffID, name, gender, age, password));
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("An error has occurred\n" + e.getMessage());
        }
    }

    
}
 