package hospitalsystem.model;
import hospitalsystem.controllers.*;
import java.util.List;

public class Patient extends User{
    private final MedicalRecord medicalRecord;
    private List<Appointment> appointments;

    public Patient(String HospitalID, String name, String gender, String password){
        super(HospitalID, name, gender, password);
        this.medicalRecord = new MedicalRecord(this);
        this.appointments = AppointmentControl.getAppointmentsByPatientID(this.patientID);
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

    public void setAppointments(List<Appointment> newAppointments){
        this.appointments = newAppointments;
    }
}
