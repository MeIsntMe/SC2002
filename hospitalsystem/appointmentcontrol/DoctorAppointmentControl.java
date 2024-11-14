package hospitalsystem.appointmentcontrol;

import hospitalsystem.data.Database;
import hospitalsystem.enums.*;
import hospitalsystem.model.*;
import hospitalsystem.model.Appointment.AppointmentSlot;
import java.time.LocalDateTime;
import java.util.*;

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
    public static List<AppointmentSlot> generateWeeklySlots(Doctor doctor) {
        List<AppointmentSlot> slots = new ArrayList<>();

        // Get next Monday
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextMonday = now.plusDays(1);
        while (nextMonday.getDayOfWeek().getValue() != 1) {  // 1 = Monday
            nextMonday = nextMonday.plusDays(1);
        }

        int[][] times = {{9, 0}, {10, 30}, {13, 0}, {14, 30}};

        for (int i = 0; i < 5; i++) { // Monday to Friday
            LocalDateTime currentDay = nextMonday.plusDays(i);
            for (int[] time : times) {
                // Create slot using the current day but with specified time
                AppointmentSlot slot = new AppointmentSlot(
                        currentDay.getYear(),
                        currentDay.getMonthValue(),
                        currentDay.getDayOfMonth(),
                        time[0],
                        time[1]
                );
                slots.add(slot);

                // Create a new appointment for this slot
                String appointmentID = generateAppointmentID(doctor.getID(), slot);
                Appointment appointment = new Appointment(appointmentID, null, doctor, slot);
                appointment.setStatus(AppointmentStatus.PENDING);
                appointment.setIsAvailable(true);
                // Add to global appointments map
                Database.appointmentMap.put(appointmentID, appointment);
            }
        }

        // Update doctor's available slots
        doctor.setAvailableSlots(slots);

        System.out.println("Generated " + slots.size() + " slots for next week for doctor " + doctor.getID());
        return slots;
    }

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
    public static void recordOutcome(Appointment appointment, String notes, Prescription prescription) {
        appointment.setConsultationNotes(notes);
        appointment.setPrescription(prescription);
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

    // Create a new prescription
    public static Prescription.MedicineSet createMedicineSet(Medicine medicine, int quantity) {
        return new Prescription.MedicineSet(medicine, quantity);
    }

    // Add medicine to prescription
    public static void addMedicineToPrescription(Prescription prescription, Medicine medicine, int quantity) {
        prescription.getMedicineList().put(medicine, quantity);
    }
}
