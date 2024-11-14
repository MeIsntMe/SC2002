package hospitalsystem.model;

import hospitalsystem.enums.PrescriptionStatus;
import java.util.HashMap;
import java.util.List;

public class Prescription {
    private final HashMap<Medicine, Integer> medicineList = new HashMap<>(); //Prescription key: medicine name, point to quantity 
    private PrescriptionStatus status;  // Status of the prescription (e.g., PENDING, DISPENSED)

    private final String patientID;              // Patient associated with this prescription
    private final String doctorID;               // Doctor that assigned prescription

    // Constructor
    public Prescription(List<MedicineSet> prescribedMedicine, String doctorID, String patientID, PrescriptionStatus status) {
        this.patientID = patientID;
        this.status = PrescriptionStatus.PENDING;
        this.doctorID = doctorID;
        for (MedicineSet set:prescribedMedicine){
            medicineList.put(set.getMedicine(), set.getQuantity());
        }
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

    public static class MedicineSet{
        final private Medicine medicine;
        final private int quantity;

        public MedicineSet(Medicine medicine, int quantity){
            this.medicine = medicine;
            this.quantity = quantity;
        }

        public Medicine getMedicine() {
            return medicine;
        }

        public int getQuantity() {
            return quantity;
        }
    }
}