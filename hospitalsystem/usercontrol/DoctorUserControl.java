package hospitalsystem.usercontrol;

import hospitalsystem.data.Database;
import hospitalsystem.model.*;
import hospitalsystem.enums.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

public class DoctorUserControl {

    public static Patient findPatientById(String patientId) {
        User user = Database.patientsMap.get(patientId);
        if (user instanceof Patient) {
            return (Patient) user;
        }
        return null;
    }

    public static void updatePatientRecord(Patient patient, Doctor doctor, String notes, List<AppointmentControl.Prescription> prescriptions) {
        String appointmentID = String.format("MR_%s_%d", patient.getID(), System.currentTimeMillis());
        LocalDateTime now = LocalDateTime.now();

        // Create slot with current time
        Appointment.AppointmentSlot slot = new Appointment.AppointmentSlot(
                now.getYear(),
                now.getMonthValue(),
                now.getDayOfMonth(),
                now.getHour(),
                now.getMinute()
        );

        // Create and configure appointment
        Appointment appointment = new Appointment(appointmentID, patient, doctor, slot);
        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointment.setIsAvailable(false);

        // Convert prescriptions to HashMap for storage
        HashMap<String, PrescriptionStatus> prescriptionMap = new HashMap<>();
        for (AppointmentControl.Prescription prescription : prescriptions) {
            prescriptionMap.put(prescription.getMedicineName(), prescription.getStatus());
        }
        appointment.setPrescriptions(prescriptionMap);
        appointment.setConsultationNotes(notes);

        // Add to Database
        Database.appointmentMap.put(appointmentID, appointment);
        doctor.addAppointment(appointment);

        // Update patient's appointments
        List<Appointment> patientAppointments = patient.getAppointments();
        if (patientAppointments == null) {
            patientAppointments = new ArrayList<>();
        }
        patientAppointments.add(appointment);
        patient.setAppointments(patientAppointments);

        System.out.println("Medical record updated successfully.");
        Database.saveAppointmentsToCSV();
    }

    public static void generateNextWeekSlots(Doctor doctor) {
        // Generate slots using AppointmentControl's logic
        List<Appointment.AppointmentSlot> slots = AppointmentControl.generateWeeklySlots(doctor);

        // Add generated appointments to Database
        for (Appointment appointment : Database.appointmentMap.values()) {
            if (appointment.getDoctor().getID().equals(doctor.getID()) &&
                    appointment.getIsAvailable()) {
                doctor.addAppointment(appointment);
            }
        }

        Database.saveAppointmentsToCSV();
        System.out.println("Generated " + slots.size() + " slots for next week.");
    }

    public static void markSlotUnavailable(Doctor doctor, Appointment appointment) {
        appointment.setIsAvailable(false);
        appointment.setStatus(AppointmentStatus.UNAVAILABLE);
        Database.appointmentMap.put(appointment.getAppointmentID(), appointment);
        Database.saveAppointmentsToCSV();
        System.out.println("Slot marked as unavailable successfully.");
    }

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

    public static void markSlotAvailable(Doctor doctor, Appointment appointment) {
        appointment.setIsAvailable(true);
        appointment.setStatus(AppointmentStatus.PENDING);
        Database.appointmentMap.put(appointment.getAppointmentID(), appointment);
    }

    public static void acceptAppointment(Doctor doctor, Appointment appointment) {
        appointment.setStatus(AppointmentStatus.BOOKED);
        Database.appointmentMap.put(appointment.getAppointmentID(), appointment);
        System.out.println("Appointment accepted successfully.");
    }

    public static void declineAppointment(Doctor doctor, Appointment appointment) {
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.setIsAvailable(true);
        Database.appointmentMap.put(appointment.getAppointmentID(), appointment);
        System.out.println("Appointment declined.");
    }

    public static void recordOutcome(Appointment appointment, String notes, List<AppointmentControl.Prescription> prescriptions) {
        // Convert prescriptions to HashMap
        HashMap<String, PrescriptionStatus> prescriptionMap = new HashMap<>();
        for (AppointmentControl.Prescription prescription : prescriptions) {
            prescriptionMap.put(prescription.getMedicineName(), prescription.getStatus());
        }

        appointment.setConsultationNotes(notes);
        appointment.setPrescriptions(prescriptionMap);
        appointment.setStatus(AppointmentStatus.COMPLETED);
        Database.appointmentMap.put(appointment.getAppointmentID(), appointment);
        System.out.println("Appointment outcome recorded successfully.");
    }
}
