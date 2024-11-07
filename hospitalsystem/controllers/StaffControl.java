package hospitalsystem.controllers;

import java.util.HashSet;
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

    // ADD STAFF 
    public static void addStaff(Scanner sc){
        int choice;
        do {
            System.out.println("=========================================");  
            System.out.println("Staff Management > Add Staff");
            UserType role = getRoleInput(sc);

            System.out.println("Enter name:");
            String name = sc.nextLine();
            System.out.println("Enter gender:");
            String gender = sc.nextLine();
            System.out.println("Enter age:");
            int age = sc.nextInt();
            sc.nextLine();
            String staffID = generateStaffID(role);

            switch (role) {
                case DOCTOR: 
                    MainSystem.doctors.add(new Doctor(staffID, name, gender, age, "password"));
                    System.out.printf("Doctor %s added at ID %s", name, staffID);
                    break; 
                case PHARMACIST: 
                    MainSystem.pharms.add(new Pharmacist(staffID, name, gender, age, "password"));
                    System.out.printf("Pharmacist %s added at ID %s", name, staffID);
                    break; 
            }
            
            System.out.println("Click 1 to continue adding and 2 to exit:");
            choice = sc.nextInt();
            sc.nextLine();
        } while (choice == 1); 
    }

    // SUPPORTING FUNC: Generate Staff ID
    private static String generateStaffID(UserType role) {
        String prefix;
        int maxId = 0;
        if (role == UserType.DOCTOR) {
            prefix = "D";
            for (Doctor doc : MainSystem.doctors) {
                int idNumber = Integer.parseInt(doc.getID().substring(1));
                if (idNumber > maxId) 
                    maxId = idNumber;
            }
        } else {
            prefix = "P";
            for (Pharmacist pharm : MainSystem.pharms) {
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
        int choice;
        do {
            System.out.println("=========================================");  
            System.out.println("Staff Management > Remove Staff");
            UserType role = getRoleInput(sc);

            System.out.print("Enter the staff ID: ");
            String staffID = sc.nextLine().toUpperCase();

            boolean removed = false;
            switch (role) {
                case DOCTOR -> removed = removeFromSet(MainSystem.doctors, staffID);
                case PHARMACIST -> removed = removeFromSet(MainSystem.pharms, staffID);
            }

            if (removed) {
                System.out.println("Staff member with ID " + staffID + " has been successfully removed.");
            } else
                System.out.println("Staff member with ID " + staffID + " not found.");
            
            System.out.println("Click 1 to continue adding and 2 to exit:");
            choice = sc.nextInt();
            sc.nextLine();
        } while (choice == 1);
    }

    // SUPPORTING FUNC: remove staff from HashSet
    private static boolean removeFromSet(HashSet<? extends User> staffSet, String staffID) {
        return staffSet.removeIf(staff -> staff.getID().equals(staffID));
        //removeIf removes the element if the condition is true and returns true
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
        HashSet<? extends User> staffSet;
        if (role == UserType.DOCTOR) 
            staffSet = MainSystem.doctors;
        else
            staffSet = MainSystem.pharms;

        // Filter the staff based on provided criteria
        var filteredStaff = staffSet.stream()
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
