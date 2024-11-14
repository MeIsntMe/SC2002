package hospitalsystem.appointmentcontrol;

import hospitalsystem.data.Database;
import hospitalsystem.model.*;
import hospitalsystem.enums.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class DoctorAppointmentControl extends AppointmentControl {

    // Get doctor's specific appointments
    public static List<Appointment> getAvailableSlots(Doctor doctor) {
        return Database.appointmentMap.values().stream()
                .filter(apt -> apt.getDoctor().getID().equals(doctor.getID()))
                .filter(Appointment::getIsAvailable)
                .sorted((a1, a2) -> a1.getSlot().getDateTime().compareTo(a2.getSlot().getDateTime()))
                .toList();
    }

    public static List<Appointment> getUnavailableSlots(Doctor doctor) {
        return Database.appointmentMap.values().stream()
                .filter(apt -> apt.getDoctor().getID().equals(doctor.getID()))
                .filter(apt -> !apt.getIsAvailable() && apt.getStatus() == AppointmentStatus.UNAVAILABLE)
                .sorted((a1, a2) -> a1.getSlot().getDateTime().compareTo(a2.getSlot().getDateTime()))
                .toList();
    }

    public static List<Appointment> getPendingAppointments(Doctor doctor) {
        return Database.appointmentMap.values().stream()
                .filter(apt -> apt.getDoctor().getID().equals(doctor.getID()))
                .filter(apt -> apt.getStatus() == AppointmentStatus.PENDING && !apt.getIsAvailable())
                .sorted((a1, a2) -> a1.getSlot().getDateTime().compareTo(a2.getSlot().getDateTime()))
                .toList();
    }

    public static List<Appointment> getBookedAppointments(Doctor doctor) {
        return Database.appointmentMap.values().stream()
                .filter(apt -> apt.getDoctor().getID().equals(doctor.getID()))
                .filter(apt -> apt.getStatus() == AppointmentStatus.BOOKED)
                .sorted((a1, a2) -> a1.getSlot().getDateTime().compareTo(a2.getSlot().getDateTime()))
                .toList();
    }

    // Manage doctor's slots
    public static void markSlotUnavailable(Doctor doctor, Appointment appointment) {
        appointment.setIsAvailable(false);
        appointment.setStatus(AppointmentStatus.UNAVAILABLE);
        Database.appointmentMap.put(appointment.getAppointmentID(), appointment);
        Database.saveAppointmentsToCSV();
    }

    public static void markSlotAvailable(Doctor doctor, Appointment appointment) {
        appointment.setIsAvailable(true);
        appointment.setStatus(AppointmentStatus.PENDING);
        Database.appointmentMap.put(appointment.getAppointmentID(), appointment);
        Database.saveAppointmentsToCSV();
    }

    // Handle appointment status changes
    public static void acceptAppointment(Doctor doctor, Appointment appointment) {
        appointment.setStatus(AppointmentStatus.BOOKED);
        Database.appointmentMap.put(appointment.getAppointmentID(), appointment);
        Database.saveAppointmentsToCSV();
    }

    public static void declineAppointment(Doctor doctor, Appointment appointment) {
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.setIsAvailable(true);
        Database.appointmentMap.put(appointment.getAppointmentID(), appointment);
        Database.saveAppointmentsToCSV();
    }

    // Record appointment outcomes
    public static void recordOutcome(Appointment appointment, String notes, List<Prescription> prescriptions) {
        appointment.setConsultationNotes(notes);
        appointment.setPrescriptions(prescriptions);
        appointment.setStatus(AppointmentStatus.COMPLETED);
        Database.appointmentMap.put(appointment.getAppointmentID(), appointment);
        Database.saveAppointmentsToCSV();
    }

    // Generate new appointment slots
    public static void generateNextWeekSlots(Doctor doctor) {
        List<Appointment.AppointmentSlot> slots = generateWeeklySlots(doctor);
        Database.saveAppointmentsToCSV();
    }

    // Display methods
    public static void displayAvailableSlots(List<Appointment> slots) {
        System.out.println("\nAvailable Slots:");
        for (int i = 0; i < slots.size(); i++) {
            Appointment apt = slots.get(i);
            System.out.printf("%d. %s\n", i + 1, apt.getSlot().toString());
        }
    }

    public static void displayUnavailableSlots(List<Appointment> slots) {
        System.out.println("\nUnavailable Slots:");
        for (int i = 0; i < slots.size(); i++) {
            Appointment apt = slots.get(i);
            System.out.printf("%d. %s\n", i + 1, apt.getSlot().toString());
        }
    }

    public static void displayPendingAppointments(List<Appointment> appointments) {
        System.out.println("\nPending Appointment Requests:");
        for (int i = 0; i < appointments.size(); i++) {
            Appointment apt = appointments.get(i);
            System.out.printf("%d. Patient: %s - Date: %s\n",
                    i + 1,
                    apt.getPatient().getName(),
                    apt.getSlot().toString());
        }
    }

    public static void displayBookedAppointments(List<Appointment> appointments) {
        System.out.println("\nBooked Appointments:");
        for (int i = 0; i < appointments.size(); i++) {
            Appointment apt = appointments.get(i);
            System.out.printf("%d. Patient: %s - Date: %s\n",
                    i + 1,
                    apt.getPatient().getName(),
                    apt.getSlot().toString());
        }
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

    // Create a new prescription
    public static Prescription createPrescription(String medicineName, String doctorID, String patientID, int dosage) {
        return new Prescription(medicineName, doctorID, patientID, dosage, PrescriptionStatus.PENDING);
    }

    // Add medicine to prescription
    public static void addMedicineToPrescription(Prescription prescription, Medicine medicine, int quantity) {
        prescription.getMedicineList().put(medicine, quantity);
    }

    // Add a prescription to appointment
    public static void addPrescriptionToAppointment(Appointment appointment, Medicine medicine, int quantity) {
        Prescription prescription = createPrescription(
                medicine.getMedicineName(),
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

    // Update prescription status
    public static boolean updatePrescriptionStatus(Prescription prescription, PrescriptionStatus newStatus) {
        prescription.setStatus(newStatus);
        return true;
    }

    // Get prescriptions for an appointment
    public static List<Prescription> getAppointmentPrescriptions(String appointmentID) {
        Appointment appointment = Database.appointmentMap.get(appointmentID);
        return appointment != null ? appointment.getPrescriptions() : new ArrayList<>();
    }
}
