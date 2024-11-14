package hospitalsystem.appointmentcontrol;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import hospitalsystem.data.Database;
import hospitalsystem.enums.AppointmentStatus;
import hospitalsystem.enums.PrescriptionStatus;
import hospitalsystem.model.Appointment;
import hospitalsystem.model.Appointment.AppointmentSlot;
import hospitalsystem.model.Doctor;
import hospitalsystem.model.Medicine;
import hospitalsystem.model.Patient;
import hospitalsystem.model.Prescription;

public class unusedcode {
    //unused code, idk if it might be needed
    private static List<Prescription> parsePrescriptions(String prescriptionStr) {
        List<Prescription> prescriptions = new ArrayList<>();
        if (prescriptionStr == null || prescriptionStr.isEmpty()) {
            return prescriptions;
        }

        String[] prescriptionPairs = prescriptionStr.split(";");
        for (String pair : prescriptionPairs) {
            String[] parts = pair.split(":");
            if (parts.length == 2) {
                String medicine = parts[0].trim();
                PrescriptionStatus status = PrescriptionStatus.valueOf(parts[1].trim());
                prescriptions.add(new Prescription(medicine, status));
            }
        }
        return prescriptions;
    }

    
    public static void updateSlotAvailability(String doctorID, AppointmentSlot slot, boolean makeAvailable) {
        // Find the appointment for this slot
        Appointment appointment = Database.appointmentMap.values().stream()
                .filter(apt -> apt.getDoctor().getID().equals(doctorID) &&
                        apt.getSlot().getDateTime().equals(slot.getDateTime()))
                .findFirst()
                .orElse(null);

        if (appointment != null) {
            appointment.setIsAvailable(makeAvailable);
            if (!makeAvailable) {
                appointment.setStatus(AppointmentStatus.BOOKED);
            } else {
                appointment.setStatus(AppointmentStatus.PENDING);
            }
            Database.appointmentMap.put(appointment.getAppointmentID(), appointment);
        }
    }

    // Add a prescription to appointment
    public static void addPrescriptionToAppointment(Appointment appointment, Medicine medicine, int quantity) {
        Prescription prescription = createPrescription(
                medicine,
                appointment.getDoctor().getID(),
                appointment.getPatient().getID(),
                quantity
        );
        addMedicineToPrescription(prescription, medicine, quantity);

        List<Prescription> prescriptions = new ArrayList<>(appointment.getPrescriptions());
        prescriptions.add(prescription);
        appointment.setPrescriptions(prescriptions);

        Database.appointmentMap.put(appointment.getAppointmentID(), appointment);
        Database.saveAppointmentsToCSV();
    }

    
    public static boolean updatePrescriptionStatus(String appointmentID, String medicineName, PrescriptionStatus newStatus) {
        Appointment appointment = Database.appointmentMap.get(appointmentID);
        if (appointment != null) {
            HashMap<String, PrescriptionStatus> prescriptions = appointment.getPrescriptions();
            if (prescriptions.containsKey(medicineName)) {
                prescriptions.put(medicineName, newStatus);
                appointment.setPrescriptions(prescriptions);
                Database.appointmentMap.put(appointmentID, appointment);
                return true;
            }
        }
        return false;
    }

    public static boolean addPrescription(String appointmentID, String medicineName, PrescriptionStatus status) {
        Appointment appointment = Database.appointmentMap.get(appointmentID);
        if (appointment != null) {
            HashMap<String, PrescriptionStatus> prescriptions = appointment.getPrescriptions();
            prescriptions.put(medicineName, status);
            appointment.setPrescriptions(prescriptions);
            Database.appointmentMap.put(appointmentID, appointment);
            return true;
        }
        return false;
    }

    public static boolean removePrescription(String appointmentID, String medicineName) {
        Appointment appointment = Database.appointmentMap.get(appointmentID);
        if (appointment != null) {
            HashMap<String, PrescriptionStatus> prescriptions = appointment.getPrescriptions();
            if (prescriptions.remove(medicineName) != null) {
                appointment.setPrescriptions(prescriptions);
                Database.appointmentMap.put(appointmentID, appointment);
                return true;
            }
        }
        return false;
    }

    public static List<Prescription> getAppointmentPrescriptions(String appointmentID) {
        Appointment appointment = Database.appointmentMap.get(appointmentID);
        return appointment != null ? appointment.getPrescriptions() : new ArrayList<>();
    }

    // Helper method for medical record updates
    public static String createMedicalRecordAppointment(Patient patient, Doctor doctor,
                                                        String notes, List<Prescription> prescriptions) {
        String appointmentID = String.format("MR_%s_%d", patient.getID(), System.currentTimeMillis());
        LocalDateTime now = LocalDateTime.now();

        Appointment.AppointmentSlot slot = new Appointment.AppointmentSlot(
                now.getYear(), now.getMonthValue(), now.getDayOfMonth(),
                now.getHour(), now.getMinute()
        );

        Appointment appointment = new Appointment(appointmentID, patient, doctor, slot);
        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointment.setIsAvailable(false);

        recordOutcome(appointment, notes, prescriptions);
        return appointmentID;
    }

}
