package hospitalsystem.menus;

import hospitalsystem.HMS;
import hospitalsystem.appointmentcontrol.*;
import hospitalsystem.data.Database;
import hospitalsystem.model.*;
import hospitalsystem.usercontrol.PatientUserControl;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
/**
 * Manages patient interface for the hospital system.
 * Allows patients to do carry out actions such as viewing medical record,
 * updating personal information, and managing appointments. 
 *
 *
 * @author Gracelynn
 * @version 1.0
 * @since 2024-03-16
 */

public class PatientMenu implements MenuInterface {
    
    // Instance variables 
    private static Scanner sc;
    private final Patient patient;
    private int choice;

    /*
     * Constructor
     * Ensures type-safety via checks
     */
    public PatientMenu(User currentUser) { 
        if (!(currentUser instanceof Patient)) {
            throw new IllegalArgumentException("User must be a Patient");
        }
        this.patient = (Patient) currentUser;
        this.sc = new Scanner(System.in);
    }
    
    @Override
    public void displayMenu(){
        boolean continueFlag = true;
        sc = new Scanner(System.in);
        System.out.println("=========================================");
        System.out.println("Patient Menu: ");
        System.out.println("1. View Medical Record");
        System.out.println("2. Update Personal Information ");
        System.out.println("3. View Available Slots for a Doctor ");
        System.out.println("4. Schedule Appointment with a Doctor ");
        System.out.println("5. Reschedule Appointment ");
        System.out.println("6. Cancel Appointment ");
        System.out.println("7. View Scheduled Appointments ");
        System.out.println("8. Display Past Appointment Outcomes ");
        System.out.println("9. Logout ");
        System.out.print("Enter choice (1-9): ");
        while (continueFlag){
            try {
                choice = sc.nextInt();
                if (sc.hasNext()){
                    sc.skip(".*");
                }
                switch (choice){
                    case 1:
                        //View Medical Record
                        handleDisplayMedicalRecord();
                        break;
                    case 2:
                        //Update Personal Information
                        handleUpdatePersonalInformation();
                        break;
                    case 3:
                        // View available appointment slots of a specific doctor
                        handleViewAppointmentSlots();
                        break;
                    case 4:
                        //Scheduling an Appointment with a specific doctor
                        handleScheduleAppointment();
                        break;
                    case 5:
                        //Rescheduling
                        handleRescheduleAppointment();
                        break;               
                    case 6:
                        //Cancal appointment
                        handleCancelAppointment();
                        break;
                    case 7:
                        //View scheduled appointments
                        handleViewScheduledAppointments();
                        break;
                
                    case 8:
                        //Display past appointment outcomes
                        handleDisplayPastAppointmentOutcomes();
                        break;
                
                    case 9:
                        //logout
                        continueFlag = false;
                        HMS.logout();
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

    public void handleDisplayMedicalRecord(){
        PatientUserControl.displayUserDetails(this.patient);
    }

    public void handleUpdatePersonalInformation(){
        PatientUserControl.updateUserDetails(this.patient);
    }

    public List<Appointment> handleViewAppointmentSlots(){
        List<User> doctorList = new ArrayList<>(Database.doctorsMap.values());
        //hides main choice field to prevent overriding main loop
        int choice;
        System.out.println("Which doctor you want to select?");
        int i;
        for (i = 0; i < doctorList.size(); i++) {
            System.out.println((i + 1) + ". " + doctorList.get(i).getName());
        }
        System.out.print("Enter choice: ");
        while (true) { 
            try {
                choice = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input, only numbers are accepted.");
                continue;
            }
            if (choice >= doctorList.size() || choice <= 0){
                System.out.println("Invalid choice.");
                continue;
            }
            Doctor selectedDoctor = (Doctor) doctorList.get(choice);
            List<Appointment> availableSlots = PatientAppointmentControl.getAvailableSlots(selectedDoctor);
            for (i = 0; i < availableSlots.size(); i++){
                System.out.println((i + 1) + ". " + availableSlots.get(i).getSlot());
            }
            return availableSlots;
        }
    }

    public void handleScheduleAppointment(){
        //hides main choice field to prevent overriding main loop
        int choice;
        List<Appointment> availableSlots = handleViewAppointmentSlots();
        System.out.print("Which slot would you like to schedule your new appointment for: ");
        while (true) { 
            try {
                choice = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input, only numbers are accepted.");
                continue;
            }
            if (choice >= availableSlots.size() || choice <= 0){
                System.out.println("Invalid choice.");
                continue;
            }
            Appointment chosenSlot = availableSlots.get(choice);
            chosenSlot.setPatient(patient);
            chosenSlot.setIsAvailable(false);
            Database.appointmentMap.put(chosenSlot.getAppointmentID(), chosenSlot);
            System.err.println("Successfully scheduled appointment. Pending Doctor's approval.");
            break;
        }
    }

    public List<Appointment> handleViewScheduledAppointments(){
        List<Appointment> scheduledAppointments = PatientAppointmentControl.getScheduledSlots(patient);
        int i;
        for (i = 0; i < scheduledAppointments.size(); i++){
            System.out.println((i + 1) + ". " + scheduledAppointments.get(i));
        }
        return scheduledAppointments;
    }

    public void handleCancelAppointment(){
        List<Appointment> scheduledAppointments = handleViewScheduledAppointments();
        System.out.print("Which slot would you like to cancel: ");
        while (true) { 
            try {
                choice = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input, only numbers are accepted.");
                continue;
            }
            if (choice >= scheduledAppointments.size() || choice <= 0){
                System.out.println("Invalid choice.");
                continue;
            }
            Appointment chosenSlot = scheduledAppointments.get(choice);
            chosenSlot.setPatient(null);
            chosenSlot.setIsAvailable(true);
            Database.appointmentMap.put(chosenSlot.getAppointmentID(), chosenSlot);
            System.err.println("Successfully canceled appointment.");
            break;
        }
    }

    public void handleRescheduleAppointment(){
        handleCancelAppointment();
        handleScheduleAppointment();
    }

    public void handleDisplayPastAppointmentOutcomes(){
        System.out.println(AppointmentControl.getAppointmentOutcomesString(patient, ""));
    }

}
