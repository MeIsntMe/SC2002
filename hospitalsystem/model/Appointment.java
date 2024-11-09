package hospitalsystem.model;

import java.util.List;
import java.util.ArrayList;
import java.util.Hashtable;
import hospitalsystem.enums.*;


public class Appointment {
    public class AppointmentOutcome {
        private String appointmentDate; 
        private String serviceType;
        private Hashtable<String, PrescriptionStatus> prescriptions;
        private String consultationNotes;

        public String getAppointmentDate() {
            return appointmentDate;
        }
        
        public String getServiceType() {
            return serviceType;
        }
        
        public Hashtable<String, PrescriptionStatus> getPrescriptions() {
            return prescriptions;
        }
        
        public String getConsultationNotes() {
            return consultationNotes;
        }
    }
    
    private String appointmentID;
    private Patient patient;
    private Doctor doctor;
    private AppointmentSlot slot;
    private AppointmentStatus status;
    private boolean isAvailable;
    private AppointmentOutcome outcome;
    
    

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

    public AppointmentOutcome getAppointmentOutcome() {
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
