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
import hospitalsystem.model.Pharmacist;
import hospitalsystem.model.User;


public class StaffControl {

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
                    case 3 -> //display 
                    case 4 -> displayStaffFiltered(sc);
                    case 5 -> {sc.close(); return;}
                    default -> System.out.println("Invalid input! Please enter a number between 1-5 ");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input! ");
            }
        }
    }

    // ADD STAFF 
    public static void addStaff(Scanner sc){
        boolean repeat;
        do {
            System.out.println("=========================================");  
            System.out.println("Staff Management > Add Staff");
            UserType role = getRoleInput(sc);

            System.out.print("Enter name: "); 
            String name = sc.nextLine();
            System.out.print("Enter gender: ");
            String gender = sc.nextLine();
            System.out.print("Enter age: ");
            int age = sc.nextInt();
            sc.nextLine();
            String staffID = generateStaffID(role);

            switch (role) {
                case DOCTOR: 
                    Doctor doc = new Doctor(staffID, name, gender, age, "password");
                    MainSystem.doctorsMap.put(staffID, doc);
                    System.out.printf("Doctor %s added at ID %s", name, staffID);
                    break; 
                case PHARMACIST: 
                    Pharmacist pharm = new Pharmacist(staffID, name, gender, age, "password");
                    MainSystem.pharmsMap.put(staffID, doc);
                    System.out.printf("Pharmacist %s added at ID %s", name, staffID);
                    break; 
                default:
                    System.out.println("Invalid input. Please enter Doctor/Pharmacist.");
                    break; 
            }
            
            repeat = MainSystem.getRepeatChoice(sc);
        } while (repeat);
    }

    // SUPPORTING FUNC: Generate Staff ID
    private static String generateStaffID(UserType role) {
        String prefix;
        int maxId = 0;
        if (role == UserType.DOCTOR) {
            prefix = "D";
            for (User doc : MainSystem.doctorsMap.values()) {
                int idNumber = Integer.parseInt(doc.getID().substring(1));
                if (idNumber > maxId) 
                    maxId = idNumber;
            }
        } else {
            prefix = "P";
            for (User pharm : MainSystem.pharmsMap.values()) {
                int idNumber = Integer.parseInt(pharm.getID().substring(1));
                if (idNumber > maxId) 
                    maxId = idNumber;
            }
        }
        int nextIdNumber = maxId + 1;
        return String.format("%s%03d", prefix, nextIdNumber); 
    }

    // REMOVE STAFF
    public static void removeStaff(Scanner sc){
        boolean repeat;
        do {
            System.out.println("=========================================");  
            System.out.println("Staff Management > Remove Staff");
            UserType role = getRoleInput(sc);

            System.out.print("Enter the staff ID: ");
            String staffID = sc.nextLine().toUpperCase();

            boolean removed = false;
            switch (role) {
                case DOCTOR -> removed = removeFromMap(MainSystem.doctorsMap, staffID);
                case PHARMACIST -> removed = removeFromMap(MainSystem.pharmsMap, staffID);
                default -> System.out.println("Invalid input. Please enter Doctor or Pharmacist.");
            }

            if (removed) {
                System.out.println("Staff member with ID " + staffID + " has been successfully removed.");
            } else
                System.out.println("Staff member with ID " + staffID + " not found.");
            
            repeat = MainSystem.getRepeatChoice(sc);
        } while (repeat);
    }

    // SUPPORTING FUNC: remove staff from HashSet
    private static boolean removeFromMap(Map<String, ? extends User> staffMap, String staffID) {
        return (staffMap.remove(staffID) != null);
        // remove returns the removed value if it exists, or null if not found
    }

    // DISPLAY STAFF 
    public static void displayStaffFiltered(Scanner sc) {
        System.out.println("=========================================");
        System.out.println("Staff Management > Display and Filter Staff");

        // Prompt for role filter
        UserType role = getRoleInput(sc);

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
        if (role == UserType.DOCTOR) {
            staffCollection = MainSystem.doctorsMap.values();
        } else if (role == UserType.PHARMACIST) {
            staffCollection = MainSystem.pharmsMap.values();
        } else {
            System.out.println("Invalid role specified.");
            return;
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
            System.out.println("Filtered Staff List:");
            for (User staff : filteredStaff) {
                System.out.printf("ID: %s, Name: %s, Gender: %s, Age: %d%n",
                        staff.getID(), staff.getName(), staff.getGender(), staff.getAge());
            }
        }
    }

    public static void updateStaffDetails(Scanner sc) {
        System.out.println("=========================================");
        System.out.println("Staff Management > Update Staff Details");

        UserType role = getRoleInput(sc);

        System.out.print("Enter the staff ID: ");
        String staffID = sc.nextLine().trim().toUpperCase();

        User staff = null;

        // Find the staff member based on role and ID
        if (role == UserType.DOCTOR) 
            staff = MainSystem.doctorsMap.get(staffID);
        else 
            staff = MainSystem.pharmsMap.get(staffID);

        // If the staff member is not found
        if (staff == null) {
            System.out.println("No " + role + " found with ID " + staffID);
            return;
        }

        // Display current details and select field to update 
        System.out.println("Current Details:");
        System.out.printf("ID: %s, Name: %s, Gender: %s, Age: %d%n",
                staff.getID(), staff.getName(), staff.getGender(), staff.getAge());

        boolean done = false;
        while (!done) {
            System.out.println("Enter field to update (1-4): 1. Name | 2. Gender | 3. Age | 4. Password | 5. Done");
            int choice = sc.nextInt();
            sc.nextLine(); // Consume the newline

            switch (choice) {
                case 1 -> {
                    System.out.print("Enter new name: ");
                    String name = sc.nextLine().trim();
                    staff.setName(name);
                    System.out.println("Name updated.");
                }
                case 2 -> {
                    System.out.print("Enter new gender: ");
                    String gender = sc.nextLine().trim();
                    staff.setGender(gender);
                    System.out.println("Gender updated.");
                }
                case 3 -> {
                    System.out.print("Enter new age: ");
                    int age = sc.nextInt();
                    sc.nextLine();
                    staff.setAge(age);
                    System.out.println("Age updated.");
                }
                case 4 -> {
                    System.out.print("Enter new password: ");
                    String password = sc.nextLine().trim();
                    staff.setPassword(password);
                    System.out.println("Password updated.");
                }
                case 5 -> done = true;  // Exit the update loop
                default -> System.out.println("Invalid choice. Please enter a number from 1 to 5.");
            }
        }
    }

    // SUPPORTING FUNC: Get role (either doctor or pharmacist)
    public static UserType getRoleInput(Scanner sc) {
        while (true) {
            System.out.println("Enter role (Doctor/Pharmacist):");
            try {
                UserType role = UserType.valueOf(sc.nextLine().trim().toUpperCase());
                if (role == UserType.DOCTOR || role == UserType.PHARMACIST)
                    return role;
                else
                    System.out.println("Invalid role. Please enter 'Doctor' or 'Pharmacist'.");
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid input. Please enter 'Doctor' or 'Pharmacist'.");
            }
        }
    }
}