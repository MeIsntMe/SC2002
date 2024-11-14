package hospitalsystem.usercontrol;

import hospitalsystem.model.Patient;
import java.util.Scanner;

public class PatientUserControl extends UserControl {
    
    // Instance variables 
    private static Scanner sc;

    public PatientUserControl(Scanner scanner) { 
        this.sc = scanner;
    }

    

    

    // Display patient medical records 
    @Override
    public void displayUserDetails(){
        System.out.println("Temp");
    }

    @Override
    public void updateUserDetails(){
        System.out.println("Temp");
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
