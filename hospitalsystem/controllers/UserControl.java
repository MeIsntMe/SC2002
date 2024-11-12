package hospitalsystem.controllers;

import java.util.Collection;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

import hospitalsystem.MainSystem;
import hospitalsystem.enums.UserType;
import hospitalsystem.model.Administrator;
import hospitalsystem.model.Doctor;
import hospitalsystem.model.Patient;
import hospitalsystem.model.Pharmacist;
import hospitalsystem.model.User;


public class UserControl {

    Scanner sc = new Scanner(System.in);

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

    // ADD STAFF 
    public static void addStaff(Scanner sc){
        boolean repeat;
        do {
            System.out.println("=========================================");  
            System.out.println("Staff Management > Add Staff");

            // Prompt for role and details
            UserType role = MainSystem.getRoleInput(sc);
            System.out.print("Enter name: "); 
            String name = sc.nextLine();
            System.out.print("Enter gender: ");
            String gender = sc.nextLine();
            String password = "password";
            String nil = "";
            int age = 0;

            // Auto-generate ID
            String userID = generateID(role);

            // Create and add user
            switch (role) {
                case PATIENT: 
                    Patient patient = new Patient(userID, name, nil, gender, nil, nil, password);
                    MainSystem.patientsMap.put(userID, patient);
                    break;
                case DOCTOR: 
                    Doctor doc = new Doctor(userID, name, gender, age, password);
                    MainSystem.doctorsMap.put(userID, doc);
                    break; 
                case PHARMACIST: 
                    Pharmacist pharm = new Pharmacist(userID, name, gender, age, password);
                    MainSystem.pharmsMap.put(userID, doc);
                    break; 
                case ADMINISTRATOR:
                    Administrator admin = new Administrator(userID, name, gender, age, password);
                    MainSystem.adminsMap.put(userID, admin);
                    break; 
            }
            System.out.printf("%s %s added at ID %s", role, name, userID);

            // Offer option to repeat
            repeat = MainSystem.getRepeatChoice(sc);
        } while (repeat);
    }

    // SUPPORTING FUNC: Generate Staff ID
    private static String generateID(UserType role) {
        String prefix;
        int maxID = 0;
        int parseIDIndex = 1;
        int nextID; 
        Map<String, User> userMap;
        
        // Determine correct prefix and map    
        switch (role){
            case PATIENT:
                prefix = "P1";
                parseIDIndex = 2;
                userMap = MainSystem.doctorsMap;
                break;
            case DOCTOR:
                prefix = "D";
                userMap = MainSystem.doctorsMap;
                break;
            case PHARMACIST:
                prefix = "P";
                userMap = MainSystem.pharmsMap;
                break;
            case ADMINISTRATOR:
                prefix = "A";
                userMap = MainSystem.adminsMap;
                break;
            }

        // Find next available ID in that map
        for (User user : userMap.values()) {
            int idNumber = Integer.parseInt(user.getID().substring(parseIDIndex));
            if (idNumber > maxID) 
                maxID = idNumber;
        }
        nextID = maxID + 1;

        // Format ID
        return String.format("%s%03d", prefix, nextID); 
    }

    // REMOVE STAFF
    public static void removeStaff(Scanner sc){
        boolean repeat;
        do {
            System.out.println("=========================================");  
            System.out.println("Staff Management > Remove Staff");

            // Prompt for role and ID
            UserType role = MainSystem.getRoleInput(sc);
            System.out.print("Enter the user ID: ");
            String userID = sc.nextLine().toUpperCase();

            // Remove user
            boolean removed = false;
            switch (role) {
                case PATIENT -> removed = removeFromMap(MainSystem.patientsMap, userID);
                case DOCTOR -> removed = removeFromMap(MainSystem.doctorsMap, userID);
                case PHARMACIST -> removed = removeFromMap(MainSystem.pharmsMap, userID);
                case ADMINISTRATOR -> removed = removeFromMap(MainSystem.adminsMap, userID);
                default -> System.out.println("Invalid input. Please enter Doctor or Pharmacist.");
            }
            if (removed) {System.out.println("Staff member with ID " + userID + " has been successfully removed."); }
            else {System.out.println("Staff member with ID " + userID + " not found."); }
            
            // Offer option to repeat
            repeat = MainSystem.getRepeatChoice(sc);
        } while (repeat);
    }

    // SUPPORTING FUNC: remove staff from HashSet
    private static boolean removeFromMap(Map<String, ? extends User> userMap, String staffID) {
        return (userMap.remove(staffID) != null);
        // remove returns the removed value if it exists, or null if not found
    }

    // DISPLAY STAFF 
    public static void displayStaffFiltered(Scanner sc) {
        System.out.println("=========================================");
        System.out.println("Staff Management > Display and Filter Staff");

        // Prompt for role filter
        UserType role = getStaffRoleInput(sc);

        // Prompt for optional filters
        System.out.print("Enter gender (male/female, or leave blank): ");
        String gender = sc.nextLine().trim();
        System.out.print("Enter minimum age (or leave blank): ");
        String minAgeInput = sc.nextLine().trim();
        System.out.print("Enter maximum age (or leave blank): ");
        String maxAgeInput = sc.nextLine().trim();

        Integer minAge = minAgeInput.isEmpty() ? null : Integer.parseInt(minAgeInput);
        Integer maxAge = maxAgeInput.isEmpty() ? null : Integer.parseInt(maxAgeInput);

        // Select the appropriate set based on role
        Collection<? extends User> staffCollection;
        switch (role) {
            case DOCTOR -> staffCollection = MainSystem.doctorsMap.values();
            case PHARMACIST -> staffCollection = MainSystem.pharmsMap.values();
            case ADMINISTRATOR -> staffCollection = MainSystem.adminsMap.values();
            case PATIENT -> { 
                System.out.println("Invalid staff role specified.");
                return;}
        }

        // Filter the staff based on provided criteria
        var filteredStaff = staffCollection.stream()
                .filter(staff -> (gender.isEmpty() || staff.getGender().equalsIgnoreCase(gender)))
                .filter(staff -> (minAge == null || staff.getAge() >= minAge))
                .filter(staff -> (maxAge == null || staff.getAge() <= maxAge))
                .collect(Collectors.toList());

        // Display the filtered results
        if (filteredStaff.isEmpty()) {
            System.out.println("No staff found matching your criteria.");
        } else {
            System.out.printf("Filtered %s List:", role.toString().toLowerCase());
            System.out.printf("%-10s %-20s %-10s %-5s%n", "ID", "Name", "Gender", "Age"); // Headers
            System.out.println("----------------------------------------------------");
            
            for (User staff : filteredStaff) {
                System.out.printf("%-10s %-20s %-10s %-5d%n", 
                        staff.getID(), staff.getName(), staff.getGender(), staff.getAge());
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
            case DOCTOR -> staff = MainSystem.doctorsMap.get(staffID);
            case PHARMACIST -> staff = MainSystem.pharmsMap.get(staffID);
            case ADMINISTRATOR -> staff = MainSystem.adminsMap.get(staffID); 
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