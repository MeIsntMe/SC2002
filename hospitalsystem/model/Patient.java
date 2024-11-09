package hospitalsystem.model;
import java.io.Console;
import java.util.ArrayList;
import java.util.Scanner;

import hospitalsystem.model.Appointment.AppointmentSlot;

public class Patient {
    private final String patientID;
    private Scanner sc;
    private MedicalRecord medicalRecord;
    //Requires deletion upon finishing appointment
    private ArrayList<Appointment> appointments;

    public Patient(String HospitalID, Scanner scanner){
        this.patientID = HospitalID;
        this.sc = scanner;
        MedicalRecord medicalRecord = new MedicalRecord(this.patientID);
    }

    public MedicalRecord getMedicalRecord(){
        return this.medicalRecord;
    }

    public String getID(){
        return this.patientID;
    }

    public ArrayList<Appointment> getAppointments(){
        return this.appointments;
    }

    public void setAppointments(ArrayList<Appointment> newAppointments){
        this.appointments = newAppointments;
    }

    public int viewScheduledAppointments(){
        int appoinmentNumber = 0;
        //print appnts
        //count and return number
        return appoinmentNumber;
    }

    public ArrayList<AppointmentSlot> viewDoctorAvailabilities(String doctorID){
        Doctor doctor = new Doctor(doctorID);
        ArrayList<AppointmentSlot> slots = doctor.getAvailableSlots();
        return slots;
        /*int availabilitiesNumber = 0;
        if (availabilitiesNumber == 0){
            System.err.println("No available slots for this doctor. Please choose another.");
        }
        else{
            //print Doctor avails
        }
        //count and return
        return availabilitiesNumber;*/
    }

    public int scheduleAppointment(String doctorID, int choice){
        Doctor doctor = new Doctor(doctorID);
        int result = doctor.setAvailability(choice, "reserved");
        //destruct doctor
        return result;
        //list available 
        /*int slots = viewDoctorAvailabilities(DoctorID);
        if (slots == 0){
            return -1; //error for no avail slots
        }
        else {
            System.err.println("Please choose your desired slot: ");
            int choice = -1;
            while (choice == -1){
            if (sc.hasNextInt()){
                choice = sc.nextInt();
            }
            sc.next(".*");
            }
        }*/
        //handle choice
        return 1;
    }

    public void deleteAppointment(){
        int appnts = viewScheduledAppointments();
        if (appnts == 0){
            System.err.println("No scheduled appointments.");
        }
        else {
            //print ap
        }
    }
}
