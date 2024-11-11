package hospitalsystem.model;
import hospitalsystem.controllers.*;
import java.util.List;

public class Patient extends User{
    private final String patientID;
    private final MedicalRecord medicalRecord;
    private List<Appointment> appointments;

    public Patient(String HospitalID, String name, String gender, String password){
        super(HospitalID, name, gender, password);
        this.patientID = HospitalID;
        this.medicalRecord = new MedicalRecord(this);
        this.appointments = AppointmentControl.getAppointmentsByPatientID(this.patientID);
    }

    public MedicalRecord getMedicalRecord(){
        return this.medicalRecord;
    }

    @Override
    public String getID(){
        return this.patientID;
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
