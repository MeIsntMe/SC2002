package hospitalsystem.model;
import hospitalsystem.controllers.*;
import hospitalsystem.enums.BloodType;
import java.util.List;

public class Patient extends User{
    private final MedicalRecord medicalRecord;
    private final BloodType bloodType;
    private List<Appointment> appointments;

    public Patient(String HospitalID, String name, int age, String gender, String password, BloodType bloodType){
        super(HospitalID, name, age, gender, password);
        this.medicalRecord = new MedicalRecord(this);
        this.appointments = AppointmentControl.getAppointmentsByPatientID(this.getID());
        this.bloodType = bloodType;
    }

    public MedicalRecord getMedicalRecord(){
        return this.medicalRecord;
    }

    public List<Appointment> getAppointments(){
        return this.appointments;
    }

    public int getTotalAppointmentCount(){
        return this.appointments.size();
    }

    public BloodType getBloodType(){
        return this.bloodType;
    }

    public void setAppointments(List<Appointment> newAppointments){
        this.appointments = newAppointments;
    }
}
