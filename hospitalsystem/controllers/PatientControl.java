package hospitalsystem.controllers;

import hospitalsystem.MainSystem;
import hospitalsystem.enums.*;
import hospitalsystem.model.*;
import hospitalsystem.model.Appointment.AppointmentOutcome;
import hospitalsystem.model.Appointment.AppointmentSlot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class PatientControl implements MenuInterface {
    private static Scanner sc;
    private final Patient patient;
    private int choice, innerChoice, tempCount;
    Map<String, Doctor> doctors = new HashMap<>();
    {{for (Map.Entry<String, User> entry : MainSystem.doctorsMap.entrySet()) {
        if (entry.getValue() instanceof Doctor) {
            doctors.put(entry.getKey(), (Doctor) entry.getValue());
        }
        }   
    }};
    ArrayList<Doctor> doctorList = new ArrayList<>();
    {{for (User user : MainSystem.doctorsMap.values()) {
        if (user instanceof Doctor) {
            doctorList.add((Doctor) user);
        }
    }}}
    Doctor selectedDoctor;

    public PatientControl(User patient) {
        this.patient = (Patient)patient;
    }

    
    @Override
    public void displayMenu(){
        boolean continueFlag = true;
        sc = new Scanner(System.in);
        System.out.println("Available Options: ");
        System.out.println("1. View Medical Record");
        System.out.println("2. Update Personal Information ");
        System.out.println("3. View Available Slots for a Doctor ");
        System.out.println("4. Schedule Appointment with a Doctor ");
        System.out.println("5. Reschedule Appointment ");
        System.out.println("6. Cancel Appointment ");
        System.out.println("7. View Scheduled Appointments ");
        System.out.println("8. Display Past Appointment Outcomes ");
        System.out.println("9. Log Out ");
        while (continueFlag){
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
                        updatePersonalInformation();
                        break;
                    case 3:
                        // View available appointment slots of a specific doctor
                        viewAppointmentSlots();
                        break;
                    case 4:
                        //Scheduling an Appointment with a specific doctor
                        scheduleAppointment();
                        break;
                    case 5:
                        //Rescheduling
                        reschduleAppointment();
                        break;               
                    case 6:
                        //Cancal appointment
                        cancelAppointment();
                        break;
                    case 7:
                        //View scheduled appointments
                        viewScheduledAppointments();
                        break;
                
                    case 8:
                        //Display past appointment outcomes
                        AppointmentControl.displayPastAppointmentOutcomes(this.patient);
                        break;
                
                    case 9:
                        //logout
                        continueFlag = false;
                        System.out.println("You have successfully logged out.");
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

    public void updatePersonalInformation(){
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
    }

    public void viewAppointmentSlots(){
        
        System.out.println("Which doctor's availability would you like to view?");
        for (int i = 0; i < doctors.size(); i++) {
            System.out.printf("%d. %s", i, doctorList.get(i).getName());
        }
        while (true) {
            innerChoice = sc.nextInt();
            if (sc.hasNext()){
                sc.skip(".*");
            }
            if (innerChoice >= doctors.size()){
                System.out.printf("Invalid Choice. Please input integer from 1 to %d", doctors.size()-1); 
                continue;
            }
            tempCount = 1;
            selectedDoctor = doctorList.get(innerChoice);
            System.out.println("Availability of Dr. " + selectedDoctor.getName());
            for (AppointmentSlot slot:selectedDoctor.getAvailableSlots()){
                System.out.printf("%d. %s\n", tempCount, slot.toString());
                tempCount += 1;
            }
            break;
        }
    }

    public void scheduleAppointment(){
        System.out.println("Which doctor would you like to schedule an appointment with?");
        for (int i = 0; i < doctors.size(); i++) {
            System.out.printf("%d. %s", i, doctorList.get(i).getName());
        }
        while (true) {
            innerChoice = sc.nextInt();
            if (sc.hasNext()){
                sc.skip(".*");
            }
            if (innerChoice >= doctors.size()){
                System.out.printf("Invalid Choice. Please input integer from 1 to %d", doctorList.size()-1); 
                continue;
            }
            tempCount = 1;
            selectedDoctor = doctorList.get(innerChoice);
            System.out.printf("Which slot of %s would you like to book?", selectedDoctor.getName());
            for (AppointmentSlot slot:selectedDoctor.getAvailableSlots()){
                System.out.printf("%d. %s\n", tempCount, slot.toString());
                tempCount += 1;
            }
            innerChoice = sc.nextInt();
            if (sc.hasNext()){
                sc.skip(".*");
            }
            if (innerChoice > tempCount || innerChoice <= 0){
                System.out.println("Invalid Choice. Please try again.");
                continue;
            }
            AppointmentControl.addAppointment(innerChoice, selectedDoctor, this.patient);
            System.out.println("Appointment Scheduled");
            break;
        }
    }

    public void reschduleAppointment(){
        System.out.println("====================================");
        System.out.println("       Scheduled Appointments       ");
        System.out.println("====================================");
        System.out.println("-----");
        System.out.println("Confirmed Appointments:");
        System.out.println("-----");
        tempCount = displayAppointmentsByType(1, AppointmentStatus.COMPLETED);
        System.out.println("-----");
        System.out.println("Pending Appointments:");
        System.out.println("-----");
        displayAppointmentsByType(tempCount, AppointmentStatus.PENDING);
        System.out.println("====================================");
        while (true){
            System.out.println("Which appointment would you like to reschedule?");
            System.out.print("Appointment Number: ");
            innerChoice = sc.nextInt();
            if (sc.hasNext()){
                sc.skip(".*");
            }
            if (getChosenAppointmentIndex(innerChoice)!=-1){
                selectedDoctor = patient.getAppointments().get(getChosenAppointmentIndex(innerChoice)).getDoctor();
                if (deleteAppointment(getChosenAppointmentIndex(innerChoice))){
                    break;
                }
            }
            System.out.println("Unable to find chosen appointment, please try again.");
        }
        while (true){
            System.out.println("Which slot would you like to change to?" + selectedDoctor.getName());
            tempCount = 1;
            for (AppointmentSlot slot:selectedDoctor.getAvailableSlots()){
                System.out.printf("%d. %s\n", tempCount, slot.toString());
                tempCount += 1;
            }
            innerChoice = sc.nextInt();
            if (sc.hasNext()){
                sc.skip(".*");
            }
            if (innerChoice > tempCount || innerChoice <= 0){
                System.out.println("Invalid Choice. Please try again.");
                continue;
            }
            AppointmentControl.addAppointment(innerChoice, selectedDoctor, this.patient);
            System.out.println("Appointment Rescheduled");
            break;
        }
    }
    
    public void cancelAppointment(){
        System.out.println("====================================");
        System.out.println("       Scheduled Appointments       ");
        System.out.println("====================================");
        System.out.println("-----");
        System.out.println("Confirmed Appointments:");
        System.out.println("-----");
        tempCount = displayAppointmentsByType(1, AppointmentStatus.COMPLETED);
        System.out.println("-----");
        System.out.println("Pending Appointments:");
        System.out.println("-----");
        displayAppointmentsByType(tempCount, AppointmentStatus.PENDING);
        System.out.println("====================================");
        while (true){
            System.out.println("Which appointment would you like to cancel?");
            System.out.print("Appointment Number: ");
            innerChoice = sc.nextInt();
            if (sc.hasNext()){
                sc.skip(".*");
            }
            if (getChosenAppointmentIndex(innerChoice)!=-1){
                if (deleteAppointment(getChosenAppointmentIndex(innerChoice))){
                    System.out.println("Appointment successfully cancelled.");
                    break;
                }
            }
            System.out.println("Unable to find chosen appointment, please try again.");
        }
    }
    
    public void viewScheduledAppointments(){
        System.out.println("====================================");
        System.out.println("       Scheduled Appointments       ");
        System.out.println("====================================");
        System.out.println("-----");
        System.out.println("Confirmed Appointments:");
        System.out.println("-----");
        tempCount = displayAppointmentsByType(1, AppointmentStatus.BOOKED);
        System.out.println("-----");
        System.out.println("Pending Appointments:");
        System.out.println("-----");
        displayAppointmentsByType(tempCount, AppointmentStatus.PENDING);
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

    public int displayAppointmentsByType(AppointmentStatus status){
        List<Appointment> appointments = patient.getAppointments();
        int appointmentCount = 0;
        for (Appointment appointment:appointments){
            if (appointment.getStatus() == status){
                System.out.println("Time: " + appointment.getSlot().toString());
                System.out.println("Doctor: " + appointment.getDoctor());
                appointmentCount += 1;
            }
        }
        return appointmentCount;
    }

    public static String buildSpaceString(int number) {
        int digitCount = Integer.toString(Math.abs(number)).length();
        return " ".repeat(digitCount);
    }

    public int displayAppointmentsByType(int initialIndex, AppointmentStatus status){
        List<Appointment> appointments = patient.getAppointments();
        int appointmentCount = initialIndex;
        for (Appointment appointment:appointments){
            if (appointment.getStatus() == status){
                System.out.printf("%d. Time: %s\n", appointmentCount, appointment.getSlot().toString());
                //Complicated function is to match the two lines indentation
                System.out.printf("%s Doctor: %s\n", " ".repeat(Integer.toString(appointmentCount).length()), appointment.getDoctor().getName()); 
                appointmentCount += 1;
            }
        }
        return appointmentCount;
    }

    public int getChosenAppointmentIndex(int index){
        int relIndex = 1;
        int actualIndex = 0;
        List<Appointment> appointments = patient.getAppointments();
        for (Appointment appointment:appointments){
            if (appointment.getStatus() == AppointmentStatus.BOOKED || appointment.getStatus() == AppointmentStatus.PENDING){ 
                if (relIndex == index){
                    return actualIndex;
                }
                relIndex += 1;
                actualIndex += 1;
            }
        }
        return -1;
    }

    public boolean updatePhoneNumber(String newNumber){
        try {
            patient.getMedicalRecord().setPhoneNumber(newNumber);
            return true;
        } catch (Exception e) {
            System.out.println("Error Occured:" + e);
            return false;
        }
    }

    public boolean updateEmail(String newEmail){
        try {
            patient.getMedicalRecord().setEmailAddress(newEmail);
            return true;
        } catch (Exception e) {
            System.out.println("Error Occured:" + e);
            return false;
        }
    }

    public boolean deleteAppointment(int appointmentIndex){
        //add doctor slot, set appointment cancelled
        try {
            Appointment chosenAppointment = patient.getAppointments().get(appointmentIndex);
            Doctor doctor = chosenAppointment.getDoctor();
            chosenAppointment.setStatus(AppointmentStatus.CANCELLED);
            AppointmentSlot cancelledSlot = chosenAppointment.getSlot();
            List<AppointmentSlot> newSlots = doctor.getAvailableSlots();
            newSlots.add(cancelledSlot);
            doctor.setAvailableSlots(newSlots);
            return true;
        } catch (Exception e){
            System.out.println("Error Occured: " + e);
            return false;
        }
    }
}
