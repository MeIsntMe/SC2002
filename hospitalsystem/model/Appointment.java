package hospitalsystem.model;

import hospitalsystem.enums.AppointmentStatus;
import java.util.List;
import java.util.ArrayList;


public class Appointment {
    private String appointmentID;
    private Patient patient;
    private Doctor doctor;
    private AppointmentSlot slot;
    private AppointmentStatus status;
    private String outcome;
    private boolean isAvailable;
    private String serviceType;
    private List<Prescription> prescriptions;
    private String consultationNotes;

    public Appointment(String appointmentID, Patient patient, Doctor doctor, AppointmentSlot slot, String serviceType) {
        this.appointmentID = appointmentID;
        this.patient = patient;
        this.doctor = doctor;
        this.slot = slot;
        this.status = AppointmentStatus.PENDING;
        this.isAvailable = true;
        this.serviceType = serviceType;
        this.prescriptions = null;
        this.consultationNotes = "";
    }

    // Getter and Setter methods
    public String getAppointmentID() {
        return appointmentID;
    }

    public Patient getPatient() {
        return patient;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public AppointmentSlot getSlot() {
        return slot;
    }

    public void setSlot(AppointmentSlot slot) {
        this.slot = slot;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    public String getOutcome() {
        return outcome;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public String getServiceType() {
        return serviceType;
    }

    public List<Prescription> getPrescriptions() {
        return prescriptions;
    }

    public void setPrescriptions(List<Prescription> prescriptions) {
        this.prescriptions = prescriptions;
    }

    public String getConsultationNotes() {
        return consultationNotes;
    }

    public void setConsultationNotes(String consultationNotes) {
        this.consultationNotes = consultationNotes;
    }

    // Static nested class representing appointment slots
    public static class AppointmentSlot {
        private String day;
        private String time;

        public AppointmentSlot(String day, String time) {
            this.day = day;
            this.time = time;
        }

        public String getDay() {
            return day;
        }

        public String getTime() {
            return time;
        }

        @Override
        public String toString() {
            return day + " " + time;
        }
    }
}
