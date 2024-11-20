package hospitalsystem.usercontrol;

import hospitalsystem.data.Database;
import hospitalsystem.enums.*;
import hospitalsystem.model.*;
import hospitalsystem.model.Prescription.MedicineSet;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages doctor-specific user operations in the hospital system.
 * Provides functionality for doctors to view and update patient medical records,
 * manage prescriptions, and record consultation outcomes.
 *
 * Implements specialized medical record management including:
 * - Patient medical history viewing
 * - Consultation notes
 * - Prescription management
 * - Appointment outcome recording
 *
 * @author Leo, Gracelynn
 * @version 1.0
 * @since 2024-11-19
 */
public class DoctorUserControl extends UserControl {

    public static void handleViewPatientRecord() {
        System.out.print("Enter Patient ID: ");
        String patientId = sc.nextLine();
        Patient patient = DoctorUserControl.findPatientById(patientId);
        if (patient != null) {
            DoctorUserControl.displayUserDetails(patient);
        }
    }

    /**
     * Displays complete patient medical details and history.
     * Shows:
     * - Basic patient information
     * - Medical history
     * - Recent appointments
     * - Current prescriptions
     *
     * @param user User object (must be Patient type)
     */
    static public void displayUserDetails(User user) {
        if (!(user instanceof Patient)) {
            System.out.println("Error: Can only display details for Patient users.");
            return;
        }
        Patient patient = (Patient) user;
        displayPatientMedicalRecord(patient);
    }

    public static void handleUpdatePatientRecord(Doctor doctor) {
        System.out.print("Enter Patient ID: ");
        String patientId = sc.nextLine();
        Patient patient = DoctorUserControl.findPatientById(patientId);
        if (patient != null) {
            DoctorUserControl.updateUserDetails(patient, doctor);
        }
    }

    /**
     * Updates patient details and medical records with doctor authorization.
     * Allows updates to:
     * - Patient medical details
     * - Consultation notes
     * - Prescriptions
     * - Medical record entries
     *
     * @param user User object (must be Patient type)
     * @param doctor Doctor performing the update
     * @throws NullPointerException if user or doctor is null
     */
    static public void updateUserDetails(User user, Doctor doctor) {
        if (!(user instanceof Patient)) {
            System.out.println("Error: Can only update details for Patient users.");
            return;
        }

        Patient patient = (Patient) user;
        System.out.println("1. Update Patient Details");
        System.out.println("2. Update Medical Record");
        System.out.print("Enter choice: ");

        try {
            int choice = Integer.parseInt(sc.nextLine());
            switch (choice) {
                case 1:
                    updatePatientDetails(patient);
                    break;
                case 2:
                    updateMedicalRecord(patient, doctor);
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }

    /**
     * Displays complete medical record for a patient.
     * Includes:
     * - Patient demographics
     * - Medical history
     * - Appointment history
     * - Prescription history
     * - Consultation notes
     *
     * @param patient Patient whose record is to be displayed
     */
    static private void displayPatientMedicalRecord(Patient patient) {
        System.out.println("================= Patient Details =================");
        System.out.println(getMedicalRecordString(patient));
    }

    /**
     * Updates medical record with new consultation data.
     * Creates new appointment record and includes:
     * - Consultation notes
     * - Prescriptions
     * - Treatment recommendations
     *
     * @param patient Patient whose record is being updated
     * @param doctor Doctor performing the update
     */
    static private void updateMedicalRecord(Patient patient, Doctor doctor) {
        // Get consultation notes
        System.out.println("Enter consultation notes (press Enter twice to finish):");
        StringBuilder notes = new StringBuilder();
        String line;
        while (!(line = sc.nextLine()).isEmpty()) {
            notes.append(line).append("\n");
        }

        // Get prescriptions
        List<Medicine.MedicineSet> prescribedMedicineList = new ArrayList<>();
        while (true) {
            System.out.print("Add prescription? (y/n): ");
            if (!sc.nextLine().toLowerCase().startsWith("y")) break;

            System.out.print("Enter medication name: ");
            String medicineName = sc.nextLine();

            System.out.print("Enter quantity: ");
            int quantity = Integer.parseInt(sc.nextLine());

            // Check if medicine exists in inventory
            Medicine medicine = Database.inventoryMap.get(medicineName);
            if (medicine == null) {
                System.out.println("Medicine not found in inventory.");
                continue;
            }

            prescribedMedicineList.add(new Medicine.MedicineSet(medicine, quantity));
        }

        // Create new prescription
        Prescription prescription = new Prescription(
                prescribedMedicineList,
                doctor.getID(),
                patient.getID(),
                PrescriptionStatus.PENDING
        );

        // Create appointment for medical record update
        String appointmentID = String.format("MR_%s_%d", patient.getID(), System.currentTimeMillis());
        LocalDateTime now = LocalDateTime.now();

        Appointment.AppointmentSlot slot = new Appointment.AppointmentSlot(
                now.getYear(),
                now.getMonthValue(),
                now.getDayOfMonth(),
                now.getHour(),
                now.getMinute()
        );

        Appointment appointment = new Appointment(appointmentID, patient, doctor, slot);
        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointment.setIsAvailable(false);
        appointment.setPrescription(prescription);
        appointment.setConsultationNotes(notes.toString());

        // Update database and patient records
        Database.appointmentMap.put(appointmentID, appointment);
        List<Appointment> patientAppointments = patient.getAppointments();
        if (patientAppointments == null) {
            patientAppointments = new ArrayList<>();
        }
        patientAppointments.add(appointment);
        patient.setAppointments(patientAppointments);

        Database.saveAppointmentsToCSV();
        System.out.println("Medical record updated successfully.");
    }

    /**
     * Updates patient details with validation.
     * Handles updates to:
     * - Blood Type
     * - Gender
     * Includes input validation and confirmation.
     *
     * @param patient Patient whose details are being updated
     */
    static private void updatePatientDetails(Patient patient) {
        System.out.println("What would you like to update?");
        System.out.println("1. Blood Type");
        System.out.println("2. Gender");
        System.out.println("3. Return to Main Menu");

        try {
            int choice = Integer.parseInt(sc.nextLine());
            switch (choice) {
                case 1:
                    System.out.printf("Current Blood Type: " + patient.getBloodType() +"\n");
                    BloodType newBloodType = selectBloodType();
                    updateBloodType(patient, newBloodType);
                    System.out.println("Blood type updated successfully.");
                    break;
                case 2:
                    System.out.printf("Current Gender: " + patient.getGender()+"\n");
                    System.out.print("Please enter new gender: ");
                    String gender = sc.next();
                    updateGender(patient, gender);
                    System.out.println("Gender updated successfully.");
                    break;
                case 3:
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }

    /**
     * Finds a patient by their ID in the system.
     *
     * @param patientId ID of the patient to find
     * @return Patient object if found, null otherwise
     */
    public static Patient findPatientById(String patientId) {
        Patient patient = (Patient) Database.patientsMap.get(patientId);
        if (patient != null) {
            return patient;
        } else {
            System.out.println("Patient not found.\n\n");
            return null;
        }
    }

    /**
     * Updates patient blood type in medical record.
     * Requires doctor authorization.
     *
     * @param patient Patient whose blood type is to be updated
     * @param bloodType New blood type value
     */
    private static void updateBloodType(Patient patient, BloodType bloodType) {
        patient.setBloodType(bloodType);
    }

    /**
     * Updates patient gender in medical record.
     * Requires doctor authorization.
     *
     * @param patient Patient whose gender is to be updated
     * @param gender New gender value
     */
    private static void updateGender(Patient patient, String gender) {
        patient.setGender(gender);
    }

    /**
     * Interactive blood type selection interface.
     * Displays available blood types and handles selection.
     *
     * @return Selected BloodType enum value
     */
    private static BloodType selectBloodType() {
        System.out.println("Select a Blood Type to change to:");
        BloodType[] bloodTypes = BloodType.values();
        for (int i = 0; i < bloodTypes.length; i++) {
            System.out.println((i + 1) + ". " + bloodTypes[i].getDisplayName());
        }

        BloodType selectedBloodType = null;
        while (selectedBloodType == null) {
            System.out.print("Choice: ");
            if (sc.hasNextInt()) {
                int choice = sc.nextInt();
                if (choice > 0 && choice <= bloodTypes.length) {
                    selectedBloodType = bloodTypes[choice - 1];
                } else {
                    System.out.println("Invalid selection. Please enter a number between 1 and " + bloodTypes.length + ".");
                }
            } else {
                System.out.println("Invalid input. Please enter a number.");
                sc.next();
            }
        }

        System.out.println("You selected: " + selectedBloodType.getDisplayName());
        return selectedBloodType;
    }
}
