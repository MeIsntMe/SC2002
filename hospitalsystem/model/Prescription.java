package hospitalsystem.model;

import hospitalsystem.enums.PrescriptionStatus;
import java.util.HashMap;
import java.util.List;

/**
 * Represents a prescription in the Hospital Management System.
 * A prescription contains a list of prescribed medicines, patient information, and status.
 *
 * @author Gracelynn, Shaivi
 * @version 1.0
 * @since 2024-11-19
 */
public class Prescription {
    /**
     * The list of prescribed medicines and their quantities.
     */
    private final HashMap<Medicine, Integer> medicineList = new HashMap<>();

    /**
     * The status of the prescription.
     */
    private PrescriptionStatus status;

    /**
     * The unique identifier of the patient associated with the prescription.
     */
    private final String patientID;

    /**
     * The unique identifier of the doctor who assigned the prescription.
     */
    private final String doctorID;

    /**
     * Constructs a Prescription object with the given parameters.
     *
     * @param prescribedMedicine The list of prescribed medicines and their quantities.
     * @param doctorID The unique identifier of the doctor who assigned the prescription.
     * @param patientID The unique identifier of the patient associated with the prescription.
     * @param status The status of the prescription.
     */
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

    /**
     * Returns a string representation of the prescription.
     *
     * @return A string representation of the prescription.
     */
    @Override
    public String toString() {
        return 
            "  - Prescribed Medicine: " + medicineListToString() + "\n" +
            "  - Patient ID: " + patientID + "\n" +
            "  - Doctor ID: " + doctorID + "\n" +
            "  - Status: " + status + "\n";
    }


    /**
     * Represents a set of prescribed medicine and its quantity.
     */
    public static class MedicineSet {
        /**
         * The prescribed medicine.
         */
        final private Medicine medicine;

        /**
         * The quantity of the prescribed medicine.
         */
        final private int quantity;

        /**
         * Constructs a MedicineSet object with the given medicine and quantity.
         *
         * @param medicine The prescribed medicine.
         * @param quantity The quantity of the prescribed medicine.
         */
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