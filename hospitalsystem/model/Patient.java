package hospitalsystem.model;
import hospitalsystem.appointmentcontrol.AppointmentControl;
import hospitalsystem.enums.BloodType;
import java.time.LocalDate;
import java.util.List;

public class Patient extends User{
    private String email; 
    private String phoneNumber;
    private final LocalDate DOB; 
    private final MedicalRecord medicalRecord;
    private BloodType bloodType;
    private List<Appointment> appointments;


    public Patient(String HospitalID, String name, String phoneNumber, LocalDate DOB, int age, String gender, BloodType bloodType, String email, String password){
        super(HospitalID, name, age, gender, password);
        this.phoneNumber = phoneNumber;
        this.DOB = DOB; 
        this.email = email; 
        this.bloodType = bloodType;
        this.medicalRecord = new MedicalRecord(this);
        this.appointments = AppointmentControl.getAppointmentsByPatientID(this.getID());
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
