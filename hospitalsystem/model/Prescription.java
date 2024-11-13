package hospitalsystem.model;

import java.util.HashMap;

import hospitalsystem.enums.PrescriptionStatus;

public class Prescription {

    private HashMap<String, Integer> prescriptionList = new HashMap<>(); // Prescription key: medicine name, point to quantity 
    private PrescriptionStatus status;  // Status of the prescription (e.g., PENDING, DISPENSED)

    private static int idCounter = 0;      // Static counter for unique ID assignment
    private int id;                        // Unique ID for each prescription
    private Medicine medicine;             // Reference to a Medicine
    private String patientID;              // Patient associated with this prescription
    private int dosage;                    // Dosage of the medicine prescribed

    // Constructor
    public Prescription(String medicineName, String patientID, int dosage, PrescriptionStatus status) {
        this.id = ++idCounter;             // Assign a unique ID to each prescription
        this.medicine = medicine;
        this.patientID = patientID;
        this.dosage = dosage;
        this.status = status;
    }

    // Getters and Setters methods 
    public HashMap getPrescriptionList() {return prescriptionList;}
    public void setPrescriptionList(String medicineName, int dosage) {
        prescriptionList.put(medicineName, dosage);
    }
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
