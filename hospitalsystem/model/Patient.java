package hospitalsystem.model;
import hospitalsystem.controllers.*;

import java.time.LocalDate;
import java.util.List;

public class Patient extends User{
    private LocalDate DOB; 
    private String bloodType; 
    private String email;
    private final MedicalRecord medicalRecord;
    private List<Appointment> appointments;

    // Constructor
    public Patient(String patientID, String name, LocalDate DOB, String gender, String bloodType, String email, String password){
        super(patientID, name, gender, password);
        this.DOB = DOB; 
        this.bloodType = bloodType;
        this.email = email;
        this.medicalRecord = new MedicalRecord(this);
        this.appointments = AppointmentControl.getAppointmentsByPatientID(patientID);
    }

    public MedicalRecord getMedicalRecord() {return this.medicalRecord;}

    public List<Appointment> getAppointments() {return this.appointments;}

    public int getTotalAppointmentCount() {return this.appointments.size();}

    public void setAppointments(List<Appointment> newAppointments) {this.appointments = newAppointments;}
}
