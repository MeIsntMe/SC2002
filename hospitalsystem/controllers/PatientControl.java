package hospitalsystem.controllers;

import hospitalsystem.enums.*;
import hospitalsystem.model.*;
import hospitalsystem.model.Appointment.AppointmentOutcome;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class PatientControl {
    private static List<String> listOfMethods = List.of("test", "test2");
    private static Scanner sc;
    private Patient patient;
    private int choice, innerChoice;

    public void displayMenu(){
        int i = 1;
        sc = new Scanner(System.in);
        System.err.println("Available Options: ");
            for (String methodString: listOfMethods) {
                System.err.printf("%d: %s\n", i, methodString);
                i++;
            }
        while (true){
            try {
                choice = sc.nextInt();
                if (sc.hasNext()){
                    sc.skip(".*");
                }
                switch (choice){
                    case 1:
                        //View Medical Record
                        displayMedicalRecord();
                        break;
                    case 2:
                        //Update Personal Information
                        //Do we want gender and name?
                        while(true){
                            System.out.println("What would you like to update?");
                            System.out.println("1. Phone Number");
                            System.out.println("2. Email");
                            System.out.println("3. Exit");
                            System.out.print("Enter choice: ");
                            innerChoice = sc.nextInt();
                            if (sc.hasNext()){
                                sc.skip(".*");
                            }
                            if (innerChoice == 1){
                                while (true){
                                    System.out.print("Please enter your number: ");
                                    String newNumber = sc.nextLine();
                                    if (newNumber.length() > 13){
                                        System.out.println("Invalid Number. Please try again.");
                                        continue;
                                    }
                                    if (updatePhoneNumber(newNumber)){
                                        System.out.println("Number succesfully updated.");
                                        break;
                                    }
                                }
                            }
                            else if (innerChoice == 2){
                                while (true){
                                    System.out.print("Please enter your email: ");
                                    String newEmail = sc.nextLine();
                                    if (!newEmail.contains("@")){
                                        System.out.println("Invalid email. Please try again.");
                                        continue;
                                    }
                                    if (updateEmail(newEmail)){
                                        System.out.println("Email succesfully updated.");
                                        break;
                                    }
                                }
                            }
                            else if (innerChoice != 3){
                                System.out.println("Invalid choice. Please try again.");
                                continue;
                            }
                            break;
                        }
                        break;
                    case 3:
                        // View available appointment slots 
                        System.out.println("View Available Appointment Slots");
                        //Print list of doctors 
                        //choose doctor to view 
                        break;
                
                    case 4:
                        System.out.println("Schedule an Appointment");
                        // Add functionality for scheduling an appointment here
                        break;
                
                    case 5:
                        System.out.println("Reschedule an Appointment");
                        // Add functionality for rescheduling an appointment here
                        break;
                
                    case 6:
                        System.out.println("Cancel an Appointment");
                        // Add functionality for canceling an appointment here
                        break;
                
                    case 7:
                        System.out.println("View Scheduled Appointments");
                        // Add functionality for viewing scheduled appointments here
                        break;
                
                    case 8:
                        System.out.println("View Past Appointment Outcome Records");
                        // Add functionality for viewing past appointment outcomes here
                        break;
                
                    case 9:
                        System.out.println("Logout");
                        // Add functionality for logout here
                        break;
                
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
        ArrayList<AppointmentOutcome> appointmentOutcomes = mr.getAppointmentOutcomes();
        int lastSlot = appointmentOutcomes.size()-1;

        System.out.println("====================================");
        System.out.println("           Medical Record           ");
        System.out.println("====================================");
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
        System.out.println("-----");
        System.out.println("List of Past Appointment Outcomes:");
        for (AppointmentOutcome outcome:appointmentOutcomes){
            System.out.println("Appointment Date: " + outcome.getAppointmentDate());
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

    public void displayPastAppointmentOutcomes(){
        ArrayList<AppointmentOutcome> appointmentOutcomes = patient.getMedicalRecord().getAppointmentOutcomes();
        int lastSlot = appointmentOutcomes.size()-1;
        System.out.println("=====================================");
        System.out.println("      Past Appointment Outcomes      ");
        System.out.println("=====================================");
        for (AppointmentOutcome outcome:appointmentOutcomes){
            System.out.println("Appointment Date: " + outcome.getAppointmentDate());
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

    public void displayConfirmedAppointments(){
        ArrayList<Appointment> appointments = patient.getAppointments();
        int lastSlot = appointments.size()-1;
        System.out.println("====================================");
        System.err.println("       Confirmed Appointments       ");
        System.out.println("====================================");
        for (Appointment appointment:appointments){
            if (appointment.getStatus() == AppointmentStatus.COMPLETED){
                System.out.println("Time: " + appointment.getSlot().toString());
                System.out.println("Doctor: " + appointment.getDoctor());
                if (appointment != appointments.get(lastSlot)){
                    System.out.println("-----");
                }
            }
        }
    }

    public void displayPendingAppointments(){
        ArrayList<Appointment> appointments = patient.getAppointments();
        int lastSlot = appointments.size()-1;
        System.out.println("====================================");
        System.err.println("        Pending Appointments        ");
        System.out.println("====================================");
        for (Appointment appointment:appointments){
            if (appointment.getStatus() == AppointmentStatus.PENDING){
                System.out.println("Time: " + appointment.getSlot().toString());
                System.out.println("Doctor: " + appointment.getDoctor());
                if (appointment != appointments.get(lastSlot)){
                    System.out.println("-----");
                }
            }
        }
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

    public boolean addAppointment(Appointment appointment){
        try {
            ArrayList<Appointment> newAppointments = patient.getAppointments();
            newAppointments.add(appointment);
            patient.setAppointments(newAppointments);
            return true;
        } catch (Exception e){
            System.err.println("Error Occured: " + e);
            return false;
        }
    }

    public boolean deleteAppointment(int appointmentIndex){
        try {
            ArrayList<Appointment> newAppointments = patient.getAppointments();
            newAppointments.remove(appointmentIndex);
            return true;
        } catch (Exception e){
            System.err.println("Error Occured: " + e);
            return false;
        }
    }
}
