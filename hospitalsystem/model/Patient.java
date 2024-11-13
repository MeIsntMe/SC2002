package hospitalsystem.model;
import hospitalsystem.apptcontrol.AppointmentControl;
import hospitalsystem.menus.*;

import java.time.LocalDate;
import hospitalsystem.controllers.*;
import hospitalsystem.enums.BloodType;
import java.util.List;

public class Patient extends User{
    private String email; 
    private LocalDate DOB; 
    private final MedicalRecord medicalRecord;
    private final BloodType bloodType;
    private List<Appointment> appointments;


    public Patient(String HospitalID, String name, LocalDate DOB, int age, String gender, BloodType bloodType, String email, String password){
        super(HospitalID, name, age, gender, password);
        this.DOB = DOB; 
        this.email = email; 
        this.bloodType = bloodType;
        this.medicalRecord = new MedicalRecord(this);
        this.appointments = AppointmentControl.getAppointmentsByPatientID(this.getID());
    }

    public Patient(String HospitalID, String name){}

    public LocalDate getDOB() {
        return DOB;
    }
    public BloodType getBloodType() {
        return this.bloodType;
    }
    public String getEmail() {
        return email;
    }
    public MedicalRecord getMedicalRecord() {
        return this.medicalRecord;
    }
    public List<Appointment> getAppointments(){
        return this.appointments;
    }
    public int getTotalAppointmentCount(){
        return this.appointments.size();
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setAppointments(List<Appointment> newAppointments){
        this.appointments = newAppointments;
    }
}
