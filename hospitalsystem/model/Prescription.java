package hospitalsystem.model;

import hospitalsystem.enums.PrescriptionStatus;

public class Prescription {
    private Medicine medicine;             // Reference to a Medicine
    private String patientID;              // Patient associated with this prescription
    private int dosage;                    // Dosage of the medicine prescribed
    private PrescriptionStatus status;     // Status of the prescription (e.g., PENDING, DISPENSED)

    // Constructor
    public Prescription(Medicine medicine, String patientID, int dosage, PrescriptionStatus status) {
        this.medicine = medicine;
        this.patientID = patientID;
        this.dosage = dosage;
        this.status = status;
    }

    // Getters and setters
    public Medicine getMedicine() {
        return medicine;
    }

    public String getPatientID() {
        return patientID;
    }

    public int getDosage() {
        return dosage;
    }

    public PrescriptionStatus getStatus() {
        return status;
    }

    public void setStatus(PrescriptionStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Prescription [Medicine: " + medicine.getMedicineName() + ", Patient ID: " + patientID +
                ", Dosage: " + dosage + "mg, Status: " + status + "]";
    }
}

