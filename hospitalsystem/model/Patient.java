package hospitalsystem.model;
import java.util.ArrayList;

public class Patient extends User{
    private final String patientID;
    private final MedicalRecord medicalRecord;
    private ArrayList<Appointment> appointments;
    private int totalAppointmentCount;

    public Patient(String HospitalID, String name, String gender, String password){
        super(HospitalID, name, gender, password);
        this.patientID = HospitalID;
        this.medicalRecord = new MedicalRecord(this);
    }

    public MedicalRecord getMedicalRecord(){
        return this.medicalRecord;
    }

    @Override
    public String getID(){
        return this.patientID;
    }

    public ArrayList<Appointment> getAppointments(){
        return this.appointments;
    }

    public int getTotalAppointmentCount(){
        return this.totalAppointmentCount;
    }

    public void setAppointments(ArrayList<Appointment> newAppointments){
        this.appointments = newAppointments;
    }

    public void addAppointmentCount(){
        this.totalAppointmentCount++;
    }
}
