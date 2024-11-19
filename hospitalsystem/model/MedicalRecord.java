package hospitalsystem.model;

import hospitalsystem.enums.*;
import hospitalsystem.model.Appointment.AppointmentOutcome;
import java.util.ArrayList;

/**
 * Represents a patient's medical record in the Hospital Management System.
 * A medical record contains personal information, blood type, and appointment outcomes.
 *
 * @author Gracelynn
 * @version 1.0
 * @since 2024-11-19
 */
public class MedicalRecord {
    /**
     * The unique identifier of the medical record.
     */
    final private String id;

    /**
     * The name of the patient.
     */
    private String name;

    /**
     * The date of birth of the patient.
     */
    private String dOB;

    /**
     * The gender of the patient.
     */
    private String gender;

    /**
     * The phone number of the patient.
     */
    private String phoneNumber;

    /**
     * The email address of the patient.
     */
    private String email;

    /**
     * The blood type of the patient.
     */
    private BloodType bloodType;

    /**
     * The list of appointment outcomes for the patient.
     */
    private ArrayList<AppointmentOutcome> appointmentOutcomes;

    /**
     * Constructs a MedicalRecord object for the given patient.
     *
     * @param patient The patient associated with the medical record.
     */
    public MedicalRecord(Patient patient) {
        this.id = patient.getID();
        this.name = patient.getName();
        this.dOB = patient.getDOB().toString();
        this.gender = patient.getGender();
        this.email = patient.getEmail();
        this.bloodType = patient.getBloodType();
        this.appointmentOutcomes = new ArrayList<>(); // Initialize empty list
        // Only add outcomes if patient has appointments
        if (patient.getAppointments() != null) {
            for (Appointment appointment : patient.getAppointments()) {
                if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
                    this.appointmentOutcomes.add(appointment.getAppointmentOutcome());
                }
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
    
    public BloodType getBloodType() {
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
    
    public void setBloodType(BloodType bloodType) {
        this.bloodType = bloodType;
    }

    public void setAppointmentOutcomes(ArrayList<AppointmentOutcome> newAppointmentOutcomes) {
        this.appointmentOutcomes = newAppointmentOutcomes;
    }
}
