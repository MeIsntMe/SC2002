package hospitalsystem.model;

import hospitalsystem.enums.*; 
import java.time.LocalDateTime;

/**
 * Represents an appointment in the Hospital Management System.
 * An appointment is associated with a patient, doctor, time slot, and outcome details.
 *
 * @author Gracelynn, Leo
 * @version 1.0
 * @since 2024-11-19
 */
public class Appointment implements Comparable<Appointment> {
    /**
     * The unique identifier of the appointment.
     */
    private final String appointmentID;

    /**
     * The patient associated with the appointment.
     */
    private Patient patient;

    /**
     * The doctor associated with the appointment.
     */
    private final Doctor doctor;

    /**
     * The pharmacist associated with the appointment.
     */
    private Pharmacist pharmacist;

    /**
     * The time slot of the appointment.
     */
    private final AppointmentSlot slot;

    /**
     * The status of the appointment.
     */
    private AppointmentStatus status;

    /**
     * The outcome of the appointment.
     */
    private AppointmentOutcome outcome;

    /**
     * Indicates whether the appointment slot is available.
     */
    private Boolean isAvailable = true;

    /**
     * Compares this appointment with another appointment based on their time slots.
     *
     * @param appointment The appointment to compare with.
     * @return A negative integer, zero, or a positive integer as this appointment is less than, equal to, or greater than the specified appointment.
     */
    @Override
    public int compareTo(Appointment appointment) {
        return this.slot.getDateTime().compareTo(appointment.slot.getDateTime());
    }


    /**
     * Constructs an Appointment object with the given parameters.
     *
     * @param appointmentID The unique identifier of the appointment.
     * @param patient The patient associated with the appointment.
     * @param doctor The doctor associated with the appointment.
     * @param slot The time slot of the appointment.
     */
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
    public Pharmacist getPharmacist() {
        return pharmacist;
    }

    public void setPharmacist(Pharmacist pharmacist) {
        this.pharmacist = pharmacist;
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

    /**
     * Returns a string representation of the appointment.
     *
     * @return A string representation of the appointment.
     */
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

    /**
     * Represents an appointment slot with a specific date and time.
     */
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

    /**
     * Represents an appointment slot with a specific date and time.
     */
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
            return " Appointment "+appointment.getAppointmentID()+"'s Outcome:\n" +
                "   - Prescription: " + (prescription != null ? "\n    " + prescription.toString().replace("\n", "\n    ") : "No prescription") + "\n" +
                "   - Service Type: " + ((!serviceType.equals("")) ? "\n    " + serviceType : "NIL") + "\n" +
                "   - Recorded Date: " + ((!recordedDate.equals("")) ? "\n    " + recordedDate : "NIL") + "\n" +
                "   - Consultation Notes: " + ((!consultationNotes.equals("")) ? "\n    " + consultationNotes : "NIL") + "\n";
        }
    }
}
