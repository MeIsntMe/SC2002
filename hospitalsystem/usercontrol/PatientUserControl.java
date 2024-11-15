package hospitalsystem.usercontrol;

import hospitalsystem.model.Patient;
import hospitalsystem.model.User;
import java.util.Scanner;

public class PatientUserControl extends UserControl {
    
    static private Scanner sc = new Scanner(System.in);

    // Display patient medical records 
    static public void displayUserDetails(User user){
        Patient patient;
        if (user instanceof Patient){
            patient = (Patient) user;
        }
        else{
            System.out.println("displayUserDetails only accepts Patient object.");
            return;
        }
        System.out.println(PatientUserControl.getMedicalRecordString(patient));
    }

    static public void updateUserDetails(User user){
        Patient patient;
        if (user instanceof Patient){
            patient = (Patient) user;
        }
        else{
            System.out.println("updateUserDetails only accepts Patient object.");
            return;
        }
        while(true){
            int choice;
            System.out.println("What would you like to update?");
            System.out.println("1. Age");
            System.out.println("2. Email");
            System.out.println("3. Exit");
            System.out.print("Enter choice: ");
            try {
                choice = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid number.");
                continue;
            }
            switch (choice) {
                case 1: 
                    System.out.println("Please enter your updated age: ");
                    int newAge = -1;
                    try {
                        newAge = Integer.parseInt(sc.nextLine());
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input.");
                    }
                    PatientUserControl.updateAge(patient, newAge);
                    break;
                case 2: 
                    System.out.println("Please enter your new email: ");
                    String newEmail = sc.nextLine();
                    PatientUserControl.updateEmail(patient, newEmail);
                    System.out.println("Email Updated");
                    break;
                case 3:
                    System.out.println("Please enter your new phoneNumber: ");
                    String newPhoneNumber = sc.nextLine();
                    PatientUserControl.updatePhoneNumber(patient, newPhoneNumber);
                    break;
                case 4: 
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    // Update patient personal information  
    static public void updateAge(Patient patient, int age){
        if (age > 0){
            patient.setAge(age);
            System.out.println("Successfully updated age.");
        }
        else {
            System.out.println("Age must be a positive number.");
        }
    }

    static public void updateEmail(Patient patient, String email){
        if (email.contains("@")){
            patient.setEmail(email);
            System.out.println("Successfully updated email.");
        }
        else{
            System.out.println("Invalid email domain.");
        }
    }

    static public void updatePhoneNumber(Patient patient, String phoneNumber){
        if (phoneNumber.matches(".*[a-zA-Z].*")) {
            System.out.println("String contains alphanumeric characters.");
        } else {
            patient.setPhoneNumber(phoneNumber);
            System.out.println("Successfully updated phone number.");
        }
    }
}
