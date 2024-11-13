package hospitalsystem.model;

import hospitalsystem.enums.PrescriptionStatus;

public class Prescription {
    private static int idCounter = 0;      // Static counter for unique ID assignment
    private int id;                        // Unique ID for each prescription
    private Medicine medicine;             // Reference to a Medicine
    private String patientID;              // Patient associated with this prescription
    private int dosage;                    // Dosage of the medicine prescribed
    private PrescriptionStatus status;     // Status of the prescription (e.g., PENDING, DISPENSED)

    // Constructor
    public Prescription(Medicine medicine, String patientID, int dosage, PrescriptionStatus status) {
        this.id = ++idCounter;             // Assign a unique ID to each prescription
        this.medicine = medicine;
        this.patientID = patientID;
        this.dosage = dosage;
        this.status = status;
    }

    // Getters
    public int getId() {
        return id;
    }

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

    // Setter for status
    public void setStatus(PrescriptionStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Prescription [ID: " + id + ", Medicine: " + medicine.getMedicineName() + ", Patient ID: " + patientID +
                ", Dosage: " + dosage + "mg, Status: " + status + "]";
    }
}
