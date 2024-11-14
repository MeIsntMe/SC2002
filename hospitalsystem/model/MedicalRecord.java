package hospitalsystem.model;

import hospitalsystem.enums.*;
import hospitalsystem.model.Appointment.AppointmentOutcome;
import java.util.ArrayList;

public class MedicalRecord {
    final private String id;
    private String name;
    private String dOB;
    private String gender;
    private String phoneNumber;
    private String email;
    private BloodType bloodType;
    private ArrayList<AppointmentOutcome> appointmentOutcomes;

    //Requires update when appointment is finished
    public MedicalRecord(Patient patient){
        this.id = patient.getID();
        ArrayList<Appointment> appointmentList = new ArrayList<>(patient.getAppointments());
        for (Appointment appointment: appointmentList){
            if (appointment.getStatus() == AppointmentStatus.COMPLETED){
                this.appointmentOutcomes.add(appointment.getAppointmentOutcome());
            }
        }
    }

    public String getID() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDOB() {
        return dOB;
    }
    
    public String getGender() {
        return gender;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public String getEmail() {
        return email;
    }
    
    public String getBloodType() {
        return bloodType;
    }
    
    public ArrayList<AppointmentOutcome> getAppointmentOutcomes() {
        return appointmentOutcomes;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public void setDOB(String dOB) {
        this.dOB = dOB;
    }
    
    public void setGender(String gender) {
        this.gender = gender;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public void setEmailAddress(String emailAddress) {
        this.email = emailAddress;
    }
    
    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public void setAppointmentOutcomes(ArrayList<AppointmentOutcome> newAppointmentOutcomes) {
        this.appointmentOutcomes = newAppointmentOutcomes;
    }
}
