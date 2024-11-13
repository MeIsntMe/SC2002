package hospitalsystem.model;

import hospitalsystem.enums.PrescriptionStatus;
import java.util.HashMap;

public class Prescription {
    private final HashMap<Medicine, Integer> medicineList = new HashMap<>(); //Prescription key: medicine name, point to quantity 
    private PrescriptionStatus status;  // Status of the prescription (e.g., PENDING, DISPENSED)

    private final String patientID;              // Patient associated with this prescription
    private final String doctorID;               // Doctor that assigned prescription

    // Constructor
    public Prescription(String medicineName, String doctorID, String patientID, int dosage, PrescriptionStatus status) {
        this.patientID = patientID;
        this.status = PrescriptionStatus.PENDING;
        this.doctorID = doctorID;
    }

    // Getters and Setters methods 
    public HashMap<Medicine, Integer> getMedicineList() {
        return medicineList;
    }

    public String getPatientID() {
        return patientID;
    }

    public String getDoctorID() {
        return doctorID;
    }

    public PrescriptionStatus getStatus() {
        return status;
    }

    // Setter for status
    public void setStatus(PrescriptionStatus status) {
        this.status = status;
    }

    private String medicineListToString(){
        String res = "[";
        for (Medicine medicine:medicineList.keySet()){
            res += "[" + medicine.getMedicineName() + ", " + medicineList.get(medicine) + "]";
        }
        return res + "]";
    }

    @Override
    public String toString() {
        return "Prescription [Prescribed Medicine: " + medicineListToString() + ", Patient ID: " + patientID +
                ", Doctor ID: " + doctorID + ", Status: " + status + "]";
    }
}
