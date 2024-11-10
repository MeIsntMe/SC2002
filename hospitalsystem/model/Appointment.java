package hospitalsystem.model;

import hospitalsystem.enums.*;
import java.time.LocalDateTime;
import java.util.HashMap;


public class Appointment implements Comparable<Appointment>{
    
    
    private String appointmentID;
    private Patient patient;
    private Doctor doctor;
    private AppointmentSlot slot;
    private AppointmentStatus status;
    private boolean isAvailable;
    private AppointmentOutcome outcome;
    
    @Override
    public int compareTo(Appointment appointment){
        return this.slot.getDateTime().compareTo(appointment.slot.getDateTime());
    }
    
    //Does appointment need to initialise with outcome?
    //Also, serviceType is what again? Isit enum?
    //What is appointmentID? Defining it by patientID + totalAppointmentCount
    public Appointment(String appointmentID, Patient patient, Doctor doctor, AppointmentSlot slot, String serviceType) {
        this.appointmentID = appointmentID;
        this.patient = patient;
        this.doctor = doctor;
        this.slot = slot;
        this.status = AppointmentStatus.PENDING;
        this.isAvailable = true;
        this.outcome.serviceType = serviceType;
        this.outcome.prescriptions = null;
        this.outcome.consultationNotes = ""; 
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

    public void setOutcome(AppointmentOutcome outcome) {
        this.outcome = outcome;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public String getServiceType() {
        return this.outcome.serviceType;
    }

    public HashMap<String, PrescriptionStatus> getPrescriptions() {
        return this.outcome.prescriptions;
    }

    public void setPrescriptions(HashMap<String, PrescriptionStatus> prescriptions) {
        this.outcome.setPrescriptions(prescriptions);
    }

    public String getConsultationNotes() {
        return this.outcome.consultationNotes;
    }

    public void setConsultationNotes(String consultationNotes) {
        this.outcome.setConsultationNotes(consultationNotes);
    }

    // Static nested class representing appointment slots
    public static class AppointmentSlot {
        private final LocalDateTime dateTime;

        public AppointmentSlot(int year, int month, int day, int hour, int minute) {
            this.dateTime = LocalDateTime.of(year, month, day, hour, minute);
        }

        public LocalDateTime getDateTime(){
            return this.dateTime;
        }

        public String getDate() {
            return dateTime.toString().split("T")[0];
        }

        public String getTime() {
            return dateTime.toString().split("T")[1];
        }

        @Override
        public String toString() {
            return getDate() + ' ' + getTime();
        }
    }

    //Non-static inner class
    public class AppointmentOutcome {
        private String appointmentDate; 
        private String serviceType;
        private HashMap<String, PrescriptionStatus> prescriptions;
        private String consultationNotes;

        public String getAppointmentDate() {
            return appointmentDate;
        }
        
        public String getServiceType() {
            return serviceType;
        }
        
        public HashMap<String, PrescriptionStatus> getPrescriptions() {
            return prescriptions;
        }
        
        public String getConsultationNotes() {
            return consultationNotes;
        }

        public void setAppointmentDate(String appointmentDate) {
            this.appointmentDate = appointmentDate;
        }
        
        public void setServiceType(String serviceType) {
            this.serviceType = serviceType;
        }
        
        public void setPrescriptions(HashMap<String, PrescriptionStatus> prescriptions) {
            this.prescriptions = prescriptions;
        }
        
        public void setConsultationNotes(String consultationNotes) {
            this.consultationNotes = consultationNotes;
        }
    }
}
