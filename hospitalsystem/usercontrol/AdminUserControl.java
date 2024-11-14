package hospitalsystem.usercontrol;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

import hospitalsystem.HMS;
import hospitalsystem.data.Database;
import hospitalsystem.enums.BloodType;
import hospitalsystem.enums.UserType;
import hospitalsystem.model.Administrator;
import hospitalsystem.model.Doctor;
import hospitalsystem.model.Patient;
import hospitalsystem.model.Pharmacist;
import hospitalsystem.model.User;

public class AdminUserControl extends UserControl {
    
    Scanner sc = new Scanner(System.in);

    // Handles display staff list 
    public void displayStaffList() {

        // Prompt for role filter
        UserType role = getStaffRoleInput(sc);

        // Select appropriate set based on role
        Collection<? extends User> staffCollection;
        switch (role) {
            case DOCTOR -> staffCollection = Database.doctorsMap.values();
            case PHARMACIST -> staffCollection = Database.pharmsMap.values();
            case ADMINISTRATOR -> staffCollection = Database.adminsMap.values();
            case PATIENT -> { 
                System.out.println("Invalid staff role specified. Patients are not staff.");
                return;}
            case null -> {
                System.out.println("Invalid staff role specified.");
                return;} 
        }
    
        // Prompt for optional filters
        System.out.print("Enter gender (male/female), or leave blank to skip): ");
        String gender = sc.nextLine().trim();
        System.out.print("Enter minimum age (or leave blank to skip): ");
        String minAgeInput = sc.nextLine().trim();
        System.out.print("Enter maximum age (or leave blank to skip): ");
        String maxAgeInput = sc.nextLine().trim();
    
        Integer minAge = minAgeInput.isEmpty() ? null : Integer.parseInt(minAgeInput);
        Integer maxAge = maxAgeInput.isEmpty() ? null : Integer.parseInt(maxAgeInput);

        // Filter  staff based on provided criteria
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
            for (User staff : filteredStaff) 
                displayUserDetails(staff);
        }
    }

    // Displays staff details (in single row format)
    public void displayUserDetails(User staff) {
        System.out.printf("%-10s %-20s %-10s %-5d%n", 
            staff.getID(), staff.getName(), staff.getGender(), staff.getAge());
    }

    // Handles update staff details 
    public void updateStaffDetails(){
        
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

    }

    // Updates indiv staff details 
    public void updateUserDetails(User staff) {
        boolean done = false;
        while (!done) {
            System.out.println("Enter field to update (1-3): 1. Age | 2. Password | 3. Done");
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

    // Add staff
    public static void addUser(Scanner sc){
        while (true) {

            // Prompt for role and details
            UserType role = getRoleInput(sc);
            System.out.print("Enter name: "); 
            String name = sc.nextLine();
            System.out.print("Enter gender: ");
            String gender = sc.nextLine();

            // Set default details 
            String password = "password";
            int age = 0;

            // Auto-generate ID
            String userID = generateID(role);

            // Create and add user
            switch (role) {
                case PATIENT: 
                    LocalDate DOB = LocalDate.of(2000, 1, 1);
                    String email = "";
                    BloodType bloodType = BloodType.UNDEFINED; //default
                    Patient patient = new Patient(userID, name, DOB, age, gender, bloodType, email, password);
                    Database.patientsMap.put(userID, patient);
                    break;
                case DOCTOR: 
                    Doctor doc = new Doctor(userID, name, age, gender, password);
                    Database.doctorsMap.put(userID, doc);
                    break; 
                case PHARMACIST: 
                    Pharmacist pharm = new Pharmacist(userID, name, age, gender, password);
                    Database.pharmsMap.put(userID, pharm);
                    break; 
                case ADMINISTRATOR:
                    Administrator admin = new Administrator(userID, name, age, gender, password);
                    Database.adminsMap.put(userID, admin);
                    break; 
            }
            System.out.printf("%s %s added at ID %s", role, name, userID);

            // Offer option to repeat
            if (!HMS.repeat(sc)) break;
        } 
    }

    // Generate Staff ID (supporting func)
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
                userMap = Database.doctorsMap;
                break;
            case DOCTOR:
                prefix = "D";
                userMap = Database.doctorsMap;
                break;
            case PHARMACIST:
                prefix = "P";
                userMap = Database.pharmsMap;
                break;
            case ADMINISTRATOR:
                prefix = "A";
                userMap = Database.adminsMap;
                break;
            case null: 
                return ("");
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

    // Remove staff
    public static void removeUser(Scanner sc){
        while (true) {

            // Prompt for role and ID
            UserType role = getRoleInput(sc);
            System.out.print("Enter the user ID: ");
            String userID = sc.nextLine().toUpperCase();

            // Remove user
            boolean removed = false;
            switch (role) {
                case PATIENT -> removed = removeFromMap(Database.patientsMap, userID);
                case DOCTOR -> removed = removeFromMap(Database.doctorsMap, userID);
                case PHARMACIST -> removed = removeFromMap(Database.pharmsMap, userID);
                case ADMINISTRATOR -> removed = removeFromMap(Database.adminsMap, userID);
                default -> System.out.println("Invalid input. Please enter Doctor or Pharmacist.");
            }
            if (removed) {System.out.println("Staff member with ID " + userID + " has been successfully removed."); }
            else {System.out.println("Staff member with ID " + userID + " not found."); }
            
            // Offer option to repeat
            if (!HMS.repeat(sc)) break;
        }
    }

    // Remove staff from HashMap (supporting func)
    private static boolean removeFromMap(Map<String, ? extends User> userMap, String staffID) {
        return (userMap.remove(staffID) != null);
        // remove returns the removed value if it exists, or null if not found
    }

    // Get role input 
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

    // Get staff role input 
    public static UserType getStaffRoleInput(Scanner sc) {
        while (true) {
            System.out.println("Select role (1-3): 1. Doctor | 2. Pharmacist | 3. Administrator");
            int role;
            try {
                role = sc.nextInt(); 
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