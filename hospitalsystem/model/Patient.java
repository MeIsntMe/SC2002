package hospitalsystem.model;
import hospitalsystem.appointmentcontrol.AppointmentControl;
import hospitalsystem.enums.BloodType;
import java.util.List;

public class Patient extends User{
    private final MedicalRecord medicalRecord;
    private final BloodType bloodType;
    private List<Appointment> appointments;
    private String email;
    private String phoneNumber;

    public Patient(String HospitalID, String name, int age, String gender, String password, BloodType bloodType, String email, String phoneNumber){
        super(HospitalID, name, age, gender, password);
        this.medicalRecord = new MedicalRecord(this);
        this.appointments = AppointmentControl.getAppointmentsByPatientID(this.getID());
        this.bloodType = bloodType;
        this.email = email;
        this.phoneNumber = phoneNumber;
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
