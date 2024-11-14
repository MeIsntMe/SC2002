package hospitalsystem.usercontrol;

import hospitalsystem.appointmentcontrol.AppointmentControl;
import hospitalsystem.data.Database;
import hospitalsystem.enums.UserType;
import hospitalsystem.model.MedicalRecord;
import hospitalsystem.model.Patient;
import hospitalsystem.model.User;
import java.util.Scanner;


public abstract class UserControl {

    Scanner sc = new Scanner(System.in);

    // Abstract method
    // Implemented by DoctorUserControl: Display patient details (medical record)
    // Implemented by AdminUserControl: Display staff details 
    // Implemented by PatientUserControl: Display own details (medical record)
    public abstract void displayUserDetails(User user);

    // Abstract method
    // Implemented by DoctorUserControl: Update patient medical record
    // Implemented by AdminUserControl: Update staff details  
    // Implemented by PatientUserControl: Update own personal details 
    public abstract void updateUserDetails(User user);
 
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
    
    public static String getMedicalRecordString(Patient patient){
        MedicalRecord mr = patient.getMedicalRecord();
        StringBuilder sb = new StringBuilder();
        sb.append("\n  ID: ").append(mr.getID());
        sb.append("\n  Name: ").append(mr.getName());
        sb.append("\n  Date of Birth: ").append(mr.getDOB());
        sb.append("\n  Gender: ").append(mr.getGender());
        sb.append("\n  Phone Number: ").append(mr.getPhoneNumber());
        sb.append("\n  Email Address: ").append(mr.getEmail());
        sb.append("\n  Blood Type: ").append(mr.getBloodType());
        sb.append("\n}").append(AppointmentControl.getAppointmentOutcomesString(patient, "  "));

        return sb.toString();
    }

}