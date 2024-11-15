package hospitalsystem.usercontrol;

import hospitalsystem.data.Database;
import hospitalsystem.enums.*;
import hospitalsystem.model.*;
import hospitalsystem.model.Prescription.MedicineSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DoctorUserControl extends UserControl {
    private static final Scanner sc = new Scanner(System.in);

    static public void displayUserDetails(User user) {
        if (!(user instanceof Patient)) {
            System.out.println("Error: Can only display details for Patient users.");
            return;
        }

        Patient patient = (Patient) user;
        displayPatientMedicalRecord(patient);
    }


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

    static private void displayPatientMedicalRecord(Patient patient) {
        // Display basic patient info
        System.out.println("Medical Record for Patient: " + patient.getName());
        System.out.println("Patient ID: " + patient.getID());
        System.out.println("Date of Birth: " + patient.getDOB());
        System.out.println("Age: " + patient.getAge());
        System.out.println("Gender: " + patient.getGender());
        System.out.println("Blood Type: " + patient.getBloodType());

        // Display appointment history
        List<Appointment> appointments = patient.getAppointments();
        if (appointments == null || appointments.isEmpty()) {
            System.out.println("\nNo appointments found for the patient.");
            return;
        }

        System.out.println("\nAppointment History:");
        for (Appointment apt : appointments) {
            System.out.println("----------------------------------------");
            System.out.println("Appointment ID: " + apt.getAppointmentID());
            System.out.println("Doctor: " + apt.getDoctor().getName());
            System.out.println("Date: " + apt.getSlot().getDateTime().toLocalDate());
            System.out.println("Time: " + apt.getSlot().getDateTime().toLocalTime());
            System.out.println("Status: " + apt.getStatus());
            System.out.println("Consultation Notes: " + apt.getConsultationNotes());

            // Display prescriptions if any
            Prescription prescription = apt.getPrescription();
            if (prescription != null) {
                System.out.println("Prescriptions:");
                for (Medicine medicine : prescription.getMedicineList().keySet()) {
                    System.out.println("- Medicine: " + medicine.getMedicineName());
                    System.out.println("  Quantity: " + prescription.getMedicineList().get(medicine));
                }
            }
        }
    }

    static private void updateMedicalRecord(Patient patient, Doctor doctor) {
        // Get consultation notes
        System.out.println("Enter consultation notes (press Enter twice to finish):");
        StringBuilder notes = new StringBuilder();
        String line;
        while (!(line = sc.nextLine()).isEmpty()) {
            notes.append(line).append("\n");
        }

        // Get prescriptions
        List<MedicineSet> prescribedMedicineList = new ArrayList<>();
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

            prescribedMedicineList.add(new MedicineSet(medicine, quantity));
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

    public static Patient findPatientById(String patientId) {
        Patient patient = (Patient) Database.patientsMap.get(patientId);
        if (patient != null) {
            return patient;
        } else {
            System.out.println("Patient not found.");
            return null;
        }
    }

    private static void updateBloodType(Patient patient, BloodType bloodType) {
        patient.setBloodType(bloodType);
    }

    private static void updateGender(Patient patient, String gender) {
        patient.setGender(gender);
    }

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
