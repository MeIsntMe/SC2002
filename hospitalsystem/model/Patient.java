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
    private int totalAppointmentCount;

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

    public int getTotalAppointmentCount(){
        return this.totalAppointmentCount;
    }

    public void setAppointments(ArrayList<Appointment> newAppointments){
        this.appointments = newAppointments;
    }

    public void addAppointmentCount(){
        this.totalAppointmentCount++;
    }
}
