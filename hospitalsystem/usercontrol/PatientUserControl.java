package hospitalsystem.usercontrol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import hospitalsystem.enums.AppointmentStatus;
import hospitalsystem.enums.PrescriptionStatus;
import hospitalsystem.model.Patient;
import hospitalsystem.model.User;
import hospitalsystem.model.Appointment;
import hospitalsystem.model.Appointment.AppointmentOutcome;
import hospitalsystem.model.Appointment.AppointmentSlot;
import hospitalsystem.model.MedicalRecord;

public class PatientUserControl extends UserControl {
    
    // Instance variables 
    private static Scanner sc;
    private final Patient patient;

    //Constructor
    public PatientUserControl(User currentUser) { 
        if (!(currentUser instanceof Patient)) {
            throw new IllegalArgumentException("User must be a Doctor");
        }
        this.patient = (Patient) currentUser;
    }

    // Display patient medical records 
    @Override
    public void displayUserDetails(){
        MedicalRecord mr = patient.getMedicalRecord();
        ArrayList<AppointmentOutcome> appointmentOutcomes = mr.getAppointmentOutcomes();
        int lastSlot = appointmentOutcomes.size()-1;

        System.out.println("====================================");
        System.out.println("           Medical Record           ");
        System.out.println("====================================");
        System.out.println("Patient ID: " + mr.getId());
        System.out.println("Name: " + mr.getName());
        System.out.println("Date of Birth: " + mr.getDOB());
        System.out.println("Gender: " + mr.getGender());
        if (!mr.getEmailAddress().equals("")){
            System.out.println("Email: " + mr.getEmailAddress());
        }
        System.out.println("Blood Type: " + mr.getBloodType());
        System.out.println("-----");
        System.out.println("List of Past Appointment Outcomes:");
        for (AppointmentOutcome outcome:appointmentOutcomes){
            System.out.println("Appointment Date: " + outcome.getRecordedDate());
            System.out.println("Service Type: " + outcome.getServiceType());
            
            System.out.println("Prescriptions:");
            HashMap<String, PrescriptionStatus> prescriptions = outcome.getPrescriptions();
            for (String prescriptionName : prescriptions.keySet()) {
                System.out.println(" - " + prescriptionName + ": " + prescriptions.get(prescriptionName));
            }
            
            System.out.println("Consultation Notes: ");
            System.out.println(outcome.getConsultationNotes());
            if (outcome != appointmentOutcomes.get(lastSlot)){
                System.out.println("-----");
            }
        }
        System.out.println("=====================================");
    }

    // Update patient personal information  
    public void updateUserDetails(){
        while(true){
            System.out.println("What would you like to update?");
            System.out.println("1. Age");
            System.out.println("2. Email");
            System.out.println("3. Exit");
            System.out.print("Enter choice: ");
            int innerChoice = sc.nextInt();
            sc.nextLine();
            switch (innerChoice) {
                case 1: 
                    System.out.println("Please enter your updated age: ");
                    int newAge;
                    try{
                        newAge = sc.nextInt();
                        sc.nextLine();
                        patient.setAge(newAge);
                        System.out.println("Age Updated");
                    } catch (Exception e){
                        System.out.println("Invalid input. Please input a number.");
                    } break;
                case 2: 
                    System.out.println("Please enter your new email: ");
                    String newEmail = sc.nextLine();
                    patient.setEmail(newEmail);
                    System.out.println("Email Updated");
                    break;
                case 3: 
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    continue;
            }
        }
    }



}
