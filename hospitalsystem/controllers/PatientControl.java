package hospitalsystem.controllers;

import hospitalsystem.enums.PrescriptionStatus;
import hospitalsystem.model.*;
import hospitalsystem.model.Appointment.AppointmentOutcome;
import java.util.Hashtable;
import java.util.List;
import java.util.Scanner;

public class PatientControl {
    private static List<String> listOfMethods = List.of("test", "test2");
    private static Scanner sc;
    private Patient patient;

    public void displayMenu(){
        int i = 1;
        sc = new Scanner(System.in);
        System.err.println("Available Methods: ");
            for (String methodString: listOfMethods) {
                System.err.printf("%d: %s\n", i, methodString);
                i++;
            }
        while (true){
            try {
                int choice = sc.nextInt();
                if (sc.hasNext()){
                    sc.skip(".*");
                }
                switch (choice){
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    default:
                        System.out.println("=========================================");
                        System.out.println("Invalid choice, try again"); 
                }
            } 
            catch (Exception e) {
                System.out.println("=====================================");
                System.out.println("An error has occurred: " + e);
            }
        }
    }

    public void displayMedicalRecord(){
        MedicalRecord mr = patient.getMedicalRecord();
        System.out.println("=====================================");
        System.out.println("           Medical Record");
        System.out.println("=====================================");
        System.out.println("Patient ID: " + mr.getId());
        System.out.println("Name: " + mr.getName());
        System.out.println("Date of Birth: " + mr.getDOB());
        System.out.println("Gender: " + mr.getGender());
        if (mr.getPhoneNumber().equals("")){
            System.out.println("Phone Number: " + mr.getPhoneNumber());
        }
        if (mr.getEmailAddress().equals("")){
            System.out.println("Email Address: " + mr.getEmailAddress());
        }
        System.out.println("Blood Type: " + mr.getBloodType());
        for (AppointmentOutcome outcome:mr.getAppointmentOutcomes()){
            System.out.println("List of Appointment Outcomes:");
            System.out.println("-----");
            System.out.println("Appointment Date: " + outcome.getAppointmentDate());
            System.out.println("Service Type: " + outcome.getServiceType());
            
            // Print each prescription in the hashtable (assuming PrescriptionStatus is an enum or class with a toString method)
            System.out.println("Prescriptions:");
            Hashtable<String, PrescriptionStatus> prescriptions = outcome.getPrescriptions();
            for (String prescriptionName : prescriptions.keySet()) {
                System.out.println(" - " + prescriptionName + ": " + prescriptions.get(prescriptionName));
            }
            
            System.out.println("Consultation Notes: ");
            System.out.println(outcome.getConsultationNotes());
            System.out.println("-----");
        }
        System.out.println("=====================================");
    }

    public boolean updatePhoneNumber(String newNumber){
        try {
            patient.getMedicalRecord().setPhoneNumber(newNumber);
            return true;
        } catch (Exception e) {
            System.err.println("Error Occured:" + e);
            return false;
        }
    }

    public boolean updateEmail(String newEmail){
        try {
            patient.getMedicalRecord().setEmailAddress(newEmail);
            return true;
        } catch (Exception e) {
            System.err.println("Error Occured:" + e);
            return false;
        }
    }

}
