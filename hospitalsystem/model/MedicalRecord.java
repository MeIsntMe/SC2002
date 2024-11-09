package hospitalsystem.model;

import hospitalsystem.model.Appointment.AppointmentOutcome;
import java.util.ArrayList;

public class MedicalRecord {
    final private String id;
    private String name;
    private String dOB;
    private String gender;
    private String phoneNumber;
    private String emailAdress;
    private String bloodType;
    private ArrayList<AppointmentOutcome> appointments;

    public MedicalRecord(Patient patient){
        this.id = patient.getID();
        ArrayList<Appointment> appointmentList = patient.getAppointments();
        for (Appointment appointment: appointmentList){
            this.appointments.add(appointment.getAppointmentOutcome());
        }
    }

    public String getId() {
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
    
    public String getEmailAddress() {
        return emailAdress;
    }
    
    public String getBloodType() {
        return bloodType;
    }
    
    public ArrayList<AppointmentOutcome> getAppointmentOutcomes() {
        return appointments;
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
        this.emailAdress = emailAddress;
    }
    
    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
}
