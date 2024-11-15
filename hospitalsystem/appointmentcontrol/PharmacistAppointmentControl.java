package hospitalsystem.appointmentcontrol;

import hospitalsystem.HMS;
import hospitalsystem.data.*;
import hospitalsystem.enums.*;
import hospitalsystem.model.*;
import java.util.Scanner;

public class PharmacistAppointmentControl extends AppointmentControl{
    Scanner sc = new Scanner(System.in); 
    public static void viewAppointmentsWithPendingPrescription(){
        
        boolean foundPendingPrescriptions = false; 
        System.out.println("Searching for appointments with pending prescriptions...");

        for (Appointment appointment : Database.appointmentMap.values()) {
            
            // Display appointments with pending prescriptions 
            if (appointment.getPrescription().getStatus() == PrescriptionStatus.PENDING){
                foundPendingPrescriptions = true;
                System.out.println("-------------------------------------------------");
                System.out.println(appointment);
                System.out.println(appointment.getAppointmentOutcome());
            }
        }
        if (!foundPendingPrescriptions) 
            System.out.println("No appointments with pending prescriptions found.");
    }

    public static void updatePrescriptionStatus(Scanner sc){
        while (true) {
           
            // Get appointment ID
            System.out.println("Enter appointment ID to dispense prescription for: ");
            String appointmentID = sc.nextLine();

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
            if (!HMS.repeat(sc)) return;
        }
    }

    public static boolean updatePrescriptionStatus(Prescription prescription, PrescriptionStatus newStatus) {
        prescription.setStatus(newStatus);
        return true;
    }
} 