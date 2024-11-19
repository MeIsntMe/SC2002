package hospitalsystem.model;
import hospitalsystem.appointmentcontrol.AppointmentControl;
import hospitalsystem.enums.BloodType;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

/**
 * Represents a patient in the Hospital Management System.
 * A patient has personal information, medical records, and a list of appointments.
 *
 * @author Gracelynn, An Xian
 * @version 1.0
 * @since 2024-11-19
 */
public class Patient extends User{
    /**
     * The email address of the patient.
     */
    private String email;

    /**
     * The phone number of the patient.
     */
    private String phoneNumber;

    /**
     * The date of birth of the patient.
     */
    private final LocalDate DOB;

    /**
     * The medical record of the patient.
     */
    private final MedicalRecord medicalRecord;

    /**
     * The blood type of the patient.
     */
    private BloodType bloodType;

    /**
     * The list of appointments for the patient.
     */
    private List<Appointment> appointments;

    /**
     * Constructs a Patient object with the given parameters.
     *
     * @param HospitalID The unique identifier of the patient.
     * @param name The name of the patient.
     * @param phoneNumber The phone number of the patient.
     * @param DOB The date of birth of the patient.
     * @param age The age of the patient.
     * @param gender The gender of the patient.
     * @param bloodType The blood type of the patient.
     * @param email The email address of the patient.
     * @param password The password of the patient.
     */
    public Patient(String HospitalID, String name, String phoneNumber, LocalDate DOB, int age, String gender, BloodType bloodType, String email, String password) {
        super(HospitalID, name, age, gender, password);
        this.phoneNumber = phoneNumber;
        this.DOB = DOB;
        this.email = email;
        this.bloodType = bloodType;
        this.appointments = new ArrayList<>(); // Initialize empty list
        this.medicalRecord = new MedicalRecord(this); // Create medical record after initializing other fields
    }

    public LocalDate getDOB() {
        return DOB;
    }
    public BloodType getBloodType() {
        return this.bloodType;
    }
    public String getEmail() {
        return email;
    }
    public String getPhoneNumber() {
        return phoneNumber;
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
    public void setBloodType(BloodType bloodType){
        this.bloodType = bloodType;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public void setAppointments(List<Appointment> newAppointments){
        this.appointments = newAppointments;
    }
}
