package hospitalsystem.appointmentcontrol;

import hospitalsystem.data.*;
import hospitalsystem.enums.*;
import hospitalsystem.model.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages patient-specific appointment operations in the hospital system.
 * Provides functionality for patients to view and manage their appointments.
 *
 * @author Gracelynn
 * @version 1.0
 * @since 2024-11-19
 */
public class PatientAppointmentControl extends AppointmentControl{
    // move majority of patient appointment related stuff here
    /**
     * Retrieves all scheduled (non-completed) appointments for a specific patient.
     * Includes appointments that are booked or pending but excludes completed ones.
     *
     * @param patient the patient whose scheduled appointments are to be retrieved
     * @return a list of active appointments sorted by date and time
     */
    public static List<Appointment> getScheduledSlots(Patient patient) {
        String patientID = patient.getID();
        return Database.appointmentMap.values().stream()
                .filter(appointment -> (appointment.getPatient() != null? appointment.getPatient().getID().equals(patientID):false))
                .filter(apt -> !apt.getIsAvailable() && apt.getStatus() != AppointmentStatus.COMPLETED)
                .sorted((a1, a2) -> a1.getSlot().getDateTime().compareTo(a2.getSlot().getDateTime()))
                .toList();
    }

    public static List<Appointment> handleViewAppointmentSlots(){
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
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input, only numbers are accepted.");
                continue;
            }
            if (choice > doctorList.size() || choice <= 0){
                System.out.println("Invalid choice.");
                continue;
            }
            Doctor selectedDoctor = (Doctor) doctorList.get(choice-1);
            List<Appointment> availableSlots = getAvailableSlots(selectedDoctor);
            for (i = 0; i < availableSlots.size(); i++){
                System.out.println((i + 1) + ". " + availableSlots.get(i).getSlot());
            }
            return availableSlots;
        }
    }

    public static void handleScheduleAppointment(Patient patient){
        //hides main choice field to prevent overriding main loop
        int choice;
        List<Appointment> availableSlots = handleViewAppointmentSlots();
        System.out.print("Which slot would you like to schedule your new appointment for: ");
        while (true) { 
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input, only numbers are accepted.");
                continue;
            }
            if (choice > availableSlots.size() || choice <= 0){
                System.out.println("Invalid choice.");
                continue;
            }
            Appointment chosenSlot = availableSlots.get(choice-1);
            chosenSlot.setPatient(patient);
            chosenSlot.setIsAvailable(false);
            Database.appointmentMap.put(chosenSlot.getAppointmentID(), chosenSlot);
            System.out.println("Successfully scheduled appointment. Pending Doctor's approval.");
            Database.saveAppointmentData();
            break;
        }
    }

    public static List<Appointment> handleViewScheduledAppointments(Patient patient){
        List<Appointment> scheduledAppointments = getScheduledSlots(patient);
        int i;
        for (i = 0; i < scheduledAppointments.size(); i++){
            System.out.println((i + 1) + ". " + scheduledAppointments.get(i));
        }
        return scheduledAppointments;
    }

    public static void handleCancelAppointment(Patient patient){
        List<Appointment> scheduledAppointments = handleViewScheduledAppointments(patient);
        System.out.print("Which slot would you like to cancel: ");
        int choice; 
        while (true) { 
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input, only numbers are accepted.");
                continue;
            }
            if (choice > scheduledAppointments.size() || choice <= 0){
                System.out.println("Invalid choice.");
                continue;
            }
            Appointment chosenSlot = scheduledAppointments.get(choice-1);
            chosenSlot.setPatient(null);
            chosenSlot.setIsAvailable(true);
            Database.appointmentMap.put(chosenSlot.getAppointmentID(), chosenSlot);
            System.err.println("Successfully canceled appointment.");
            Database.saveAppointmentData();
            break;
        }
    }

    public static void handleRescheduleAppointment(Patient patient){
        handleCancelAppointment(patient);
        handleScheduleAppointment(patient);
    }

}
