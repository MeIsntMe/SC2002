package hospitalsystem.appointmentcontrol;

import java.util.List;

import hospitalsystem.HMS;
import hospitalsystem.data.*;
import hospitalsystem.enums.*;
import hospitalsystem.model.*;

/**
 * Manages pharmacist-specific appointment operations in the hospital system.
 * Handles prescription management, dispensing, and related appointment functions.
 *
 * @author An Xian, Gracelynn
 * @version 1.0
 * @since 2024-11-19
 */

public class PharmacistAppointmentControl extends AppointmentControl{
    
    public static void viewCompletedAppointments(){
        List<Appointment> completedAppointmentList = AppointmentControl.getCompletedAppointments();
        for (Appointment appointment : completedAppointmentList) {
            if (appointment.getPatient() == null){
                continue;
            }
            System.out.println(appointment);
            System.out.println(appointment.getAppointmentOutcome());
        }
    }


    public static void updatePrescriptionStatus(){
        while (true) {
           
            // Get appointment ID
            System.out.println("Enter appointment ID to dispense prescription for: ");
            String appointmentID = scanner.nextLine();

            if (!Database.appointmentMap.containsKey(appointmentID)){
                System.out.println("Appointment does not exist.");
                continue;
            }

            // Update prescription status to dispensed 
            Appointment appointment = Database.appointmentMap.get(appointmentID);
            Prescription prescription = appointment.getPrescription(); 
            
            appointment.getAppointmentOutcome().getPrescription().setStatus(PrescriptionStatus.DISPENSED);
            System.out.println("Prescription has been dispensed.");
            System.out.println(prescription);
            
            // Option to repeat 
            if (!HMS.repeat(scanner)) return;
        }
    }

    public static boolean updatePrescriptionStatus(Prescription prescription, PrescriptionStatus newStatus) {
        prescription.setStatus(newStatus);
        return true;
    }
} 
