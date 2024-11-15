package hospitalsystem.model;

import hospitalsystem.enums.*; 
import java.time.LocalDateTime;

public class Appointment implements Comparable<Appointment> {
    private final String appointmentID;
    private Patient patient;
    private final Doctor doctor;
    private final AppointmentSlot slot;
    private AppointmentStatus status;
    private AppointmentOutcome outcome;
    private Boolean isAvailable = true;

    @Override
    public int compareTo(Appointment appointment) {
        return this.slot.getDateTime().compareTo(appointment.slot.getDateTime());
    }

    public Appointment(String appointmentID, Patient patient, Doctor doctor, AppointmentSlot slot) {
        this.appointmentID = appointmentID;
        this.patient = patient;
        this.doctor = doctor;
        this.slot = slot;
        this.status = AppointmentStatus.PENDING;
        this.outcome = new AppointmentOutcome(this, "", null, "", "");
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

    public AppointmentStatus getStatus() {
        return status;
    }

    public boolean getIsAvailable(){
        return this.isAvailable;
    }

    public void setPatient(Patient patient){
        this.patient = patient;
    }

    public void setIsAvailable(boolean newValue){
        this.isAvailable = newValue;
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

    public Prescription getPrescription() {
        return this.outcome.getPrescription();
    }

    public void setPrescription(Prescription prescription) {
        this.outcome.setPrescription(prescription);
    }

    public String getConsultationNotes() {
        return this.outcome.getConsultationNotes();
    }

    public void setConsultationNotes(String consultationNotes) {
        this.outcome.setConsultationNotes(consultationNotes);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Appointment Details {");
        sb.append("\n  Appointment ID: ").append(appointmentID);
        sb.append("\n  Patient ID: ").append(patient.getID());
        sb.append("\n  Doctor ID: ").append(doctor.getID());
        sb.append("\n  Slot: ").append(slot.toString());
        sb.append("\n  Status: ").append(status);
        sb.append("\n}");
        
        return sb.toString();
    }

    // Static nested class representing appointment slots
    public static class AppointmentSlot {
        private final LocalDateTime dateTime;

        public AppointmentSlot(int year, int month, int day, int hour, int minute) {
            this.dateTime = LocalDateTime.of(year, month, day, hour, minute);
        }

        public LocalDateTime getDateTime() {
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

    // Non-static inner class
    public class AppointmentOutcome {
        private final Appointment appointment;
        private Prescription prescription;
        private String serviceType;
        private String recordedDate;
        private String consultationNotes;

        public AppointmentOutcome(Appointment appointment, String consultationNotes, Prescription prescription, String recordedDate, String serviceType) {
            this.appointment = appointment;
            this.consultationNotes = consultationNotes;
            this.prescription = prescription;
            this.recordedDate = recordedDate;
            this.serviceType = serviceType;
        }

        public Prescription getPrescription() {
            return prescription;
        }

        public String getConsultationNotes() {
            return consultationNotes;
        }

        public Appointment getAppointment() {
            return appointment;
        }

        public String getRecordedDate(){
            return this.recordedDate;
        }

        public String getServiceType(){
            return this.serviceType;
        }

        public void setRecordedDate(String newRecordedDate){
            this.recordedDate = newRecordedDate;
        }

        public void setServiceType(String newServiceType){
            this.serviceType = newServiceType;
        }

        public void setPrescription(Prescription prescription) {
            this.prescription = prescription;
        }

        public void setConsultationNotes(String consultationNotes) {
            this.consultationNotes = consultationNotes;
        }

        @Override
        public String toString() {
            return "Appointment Outcome:\n" +
                "  - Prescription: " + (prescription != null ? "\n    " + prescription.toString().replace("\n", "\n    ") : "No prescription") + "\n" +
                "  - Service Type: " + serviceType + "\n" +
                "  - Recorded Date: " + recordedDate + "\n" +
                "  - Consultation Notes: " + consultationNotes + "\n";
        }
    }
}
