package hospitalsystem.usercontrol;

import hospitalsystem.data.Database;
import hospitalsystem.model.Patient;
import hospitalsystem.model.User;

/**
 * Manages patient-specific user operations in the hospital system.
 * Provides functionality for patients to view their medical records
 * and update personal information. Implements restricted access to
 * ensure patients can only view and modify appropriate information.
 *
 * @author Gracelynn
 * @version 1.0
 * @since 2024-11-19
 */
public class PatientUserControl extends UserControl {
    
    /**
     * Displays patient medical record information.
     * Shows only information accessible to patients:
     * - Personal details
     * - Appointment history
     * - Current prescriptions
     * - General medical information
     *
     * @param user User object (must be Patient type)
     * @throws IllegalArgumentException if user is not a Patient type
     */
    static public void displayPatientDetails(User user){
        Patient patient;
        if (user instanceof Patient){
            patient = (Patient) user;
        }
        else{
            System.out.println("displayPatientDetails only accepts Patient object.");
            return;
        }
        System.out.println("================= Patient Details =================");
        System.out.println(getMedicalRecordString(patient));
    }

    /**
     * Updates patient personal information through interactive interface.
     * Allows patients to update:
     * - Age
     * - Email address
     * - Phone number
     * Includes input validation and confirmation.
     *
     * @param user User object (must be Patient type)
     * @throws IllegalArgumentException if user is not a Patient type
     * @throws NullPointerException if user is null
     */
    static public void updatePatientDetails(User user){
        Patient patient;
        if (user instanceof Patient){
            patient = (Patient) user;
        }
        else{
            System.out.println("updatePatientDetails only accepts Patient object.");
            return;
        }
        while(true){
            int choice;
            System.out.println("What would you like to update?");
            System.out.println("1. Email");
            System.out.println("2. Phone Number");
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
                    System.out.println("Please enter your new email: ");
                    String newEmail = sc.nextLine();
                    PatientUserControl.updateEmail(patient, newEmail);
                    break;
                case 2:
                    System.out.println("Please enter your new phoneNumber: ");
                    String newPhoneNumber = sc.nextLine();
                    PatientUserControl.updatePhoneNumber(patient, newPhoneNumber);
                    break;
                case 3: 
                    System.out.println("Finished updating details");
                    Database.savePatientData();
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    /**
     * Updates patient email address with format validation.
     * Ensures email contains '@' symbol and proper format.
     *
     * @param patient Patient whose email is to be updated
     * @param email New email address
     */
    static public void updateEmail(Patient patient, String email){
        if (email.contains("@")){
            patient.setEmail(email);
            System.out.println("Successfully updated email.");
        }
        else{
            System.out.println("Invalid email domain.");
        }
    }

    /**
     * Updates patient phone number with format validation.
     * Ensures phone number contains only numeric characters.
     *
     * @param patient Patient whose phone number is to be updated
     * @param phoneNumber New phone number
     */
    static public void updatePhoneNumber(Patient patient, String phoneNumber){
        if (phoneNumber.matches(".*[a-zA-Z].*")) {
            System.out.println("Invalid phone number. Input contains alphanumeric characters.");
        } else {
            patient.setPhoneNumber(phoneNumber);
            System.out.println("Successfully updated phone number.");
        }
    }
}
