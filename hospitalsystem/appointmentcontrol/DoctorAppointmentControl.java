package hospitalsystem.appointmentcontrol;

import hospitalsystem.data.Database;
import hospitalsystem.enums.*;
import hospitalsystem.model.*;
import hospitalsystem.model.Appointment.AppointmentSlot;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controls and manages doctor-specific appointment operations in the hospital system.
 * This class provides functionality for handling doctor appointments, including slot generation,
 * appointment status management, and schedule viewing.
 *
 * @author Gracelynn, Leo
 * @version 1.0
 * @since 2024-11-19
 */
public class DoctorAppointmentControl extends AppointmentControl {

    /**
     * Retrieves all available appointment slots for a specific doctor.
     *
     * @param doctor the doctor whose available slots are to be retrieved
     * @return a sorted list of available appointments for the specified doctor
     */
    public static List<Appointment> getAvailableSlots(Doctor doctor) {
        return Database.appointmentMap.values().stream()
                .filter(apt -> apt.getDoctor().getID().equals(doctor.getID()))
                .filter(Appointment::getIsAvailable)
                .sorted((a1, a2) -> a1.getSlot().getDateTime().compareTo(a2.getSlot().getDateTime()))
                .toList();
    }

    /**
     * Retrieves all unavailable appointment slots for a specific doctor.
     *
     * @param doctor the doctor whose unavailable slots are to be retrieved
     * @return a sorted list of unavailable appointments for the specified doctor
     */
    public static List<Appointment> getUnavailableSlots(Doctor doctor) {
        return Database.appointmentMap.values().stream()
                .filter(apt -> apt.getDoctor().getID().equals(doctor.getID()))
                .filter(apt -> !apt.getIsAvailable() && apt.getStatus() == AppointmentStatus.UNAVAILABLE)
                .sorted((a1, a2) -> a1.getSlot().getDateTime().compareTo(a2.getSlot().getDateTime()))
                .toList();
    }

    /**
     * Retrieves all pending appointments for a specific doctor.
     *
     * @param doctor the doctor whose pending appointments are to be retrieved
     * @return a sorted list of pending appointments for the specified doctor
     */
    public static List<Appointment> getPendingAppointments(Doctor doctor) {
        return Database.appointmentMap.values().stream()
                .filter(apt -> apt.getDoctor().getID().equals(doctor.getID()))
                .filter(apt -> apt.getStatus() == AppointmentStatus.PENDING && !apt.getIsAvailable())
                .sorted((a1, a2) -> a1.getSlot().getDateTime().compareTo(a2.getSlot().getDateTime()))
                .toList();
    }

    /**
     * Retrieves all booked appointments for a specific doctor.
     *
     * @param doctor the doctor whose booked appointments are to be retrieved
     * @return a sorted list of booked appointments for the specified doctor
     */
    public static List<Appointment> getBookedAppointments(Doctor doctor) {
        return Database.appointmentMap.values().stream()
                .filter(apt -> apt.getDoctor().getID().equals(doctor.getID()))
                .filter(apt -> apt.getStatus() == AppointmentStatus.BOOKED)
                .sorted((a1, a2) -> a1.getSlot().getDateTime().compareTo(a2.getSlot().getDateTime()))
                .toList();
    }

    /**
     * Retrieves all upcoming appointments for a specific doctor from the current date/time.
     *
     * @param doctor the doctor whose upcoming appointments are to be retrieved
     * @return a sorted list of upcoming appointments for the specified doctor
     */
    public static List<Appointment> getUpcomingAppointments(Doctor doctor) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        return Database.appointmentMap.values().stream()
                .filter(apt -> apt.getDoctor().getID().equals(doctor.getID()))
                .filter(apt -> apt.getStatus() == AppointmentStatus.BOOKED)
                .filter(apt -> apt.getSlot().getDateTime().isAfter(currentDateTime))
                .sorted(Comparator.comparing(apt -> apt.getSlot().getDateTime()))
                .collect(Collectors.toList());
    }

    /**
     * Generates appointment slots for the next week for a specific doctor.
     * Slots are generated for Monday through Friday at specific times:
     * 9:00, 10:30, 13:00, and 14:30.
     *
     * @param doctor the doctor for whom slots are to be generated
     */
    public static void generateNextWeekSlots(Doctor doctor) {
        List<AppointmentSlot> slots = new ArrayList<>();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextMonday = now.plusDays(1);
        while (nextMonday.getDayOfWeek().getValue() != 1) {
            nextMonday = nextMonday.plusDays(1);
        }

        int[][] times = {{9, 0}, {10, 30}, {13, 0}, {14, 30}};

        for (int i = 0; i < 5; i++) {
            LocalDateTime currentDay = nextMonday.plusDays(i);
            for (int[] time : times) {
                AppointmentSlot slot = new AppointmentSlot(
                        currentDay.getYear(),
                        currentDay.getMonthValue(),
                        currentDay.getDayOfMonth(),
                        time[0],
                        time[1]
                );
                slots.add(slot);
            }
        }

        int maxID = Database.appointmentMap.keySet().stream()
                .filter(id -> id.startsWith("APT"))
                .mapToInt(id -> Integer.parseInt(id.substring(3)))
                .max()
                .orElse(0);

        for (AppointmentSlot slot : slots) {
            String appointmentID = String.format("APT%03d", ++maxID);
            Appointment appointment = new Appointment(appointmentID, null, doctor, slot);
            appointment.setIsAvailable(true);
            appointment.setStatus(AppointmentStatus.PENDING);
            Database.appointmentMap.put(appointmentID, appointment);
        }

        doctor.setAvailableSlots(slots);
        Database.saveAppointmentsToCSV();
        System.out.println("Generated " + slots.size() + " slots for next week.");
    }

    /**
     * Displays available appointment slots with their status.
     * Shows only slots with PENDING or CANCELLED status.
     *
     * @param slots the list of appointment slots to display
     */
    public static void displayAvailableSlots(List<Appointment> slots) {
        System.out.println("\nAvailable Slots (PENDING, CANCELLED):");
        for (int i = 0; i < slots.size(); i++) {
            Appointment apt = slots.get(i);
            if (apt.getStatus() == AppointmentStatus.PENDING || apt.getStatus() == AppointmentStatus.CANCELLED) {
                System.out.printf("%d. %s - Status: %s\n", i + 1, apt.getSlot().toString(), apt.getStatus());
            }
        }
    }

    /**
     * Displays unavailable appointment slots with their status.
     * Shows only slots with UNAVAILABLE or BOOKED status.
     *
     * @param slots the list of appointment slots to display
     */
    public static void displayUnavailableSlots(List<Appointment> slots) {
        System.out.println("\nUnavailable Slots (UNAVAILABLE, BOOKED):");
        for (int i = 0; i < slots.size(); i++) {
            Appointment apt = slots.get(i);
            if (apt.getStatus() == AppointmentStatus.UNAVAILABLE || apt.getStatus() == AppointmentStatus.BOOKED) {
                System.out.printf("%d. %s - Status: %s\n", i + 1, apt.getSlot().toString(), apt.getStatus());
            }
        }
    }

    /**
     * Displays pending appointment requests with patient details.
     *
     * @param appointments the list of appointments to display
     */
    public static void displayPendingAppointments(List<Appointment> appointments) {
        System.out.println("\nPending Appointment Requests (PENDING):");
        for (int i = 0; i < appointments.size(); i++) {
            Appointment apt = appointments.get(i);
            if (apt.getStatus() == AppointmentStatus.PENDING) {
                System.out.printf("%d. Patient: %s - Date: %s - Time: %02d:%02d - Status: %s\n",
                        i + 1,
                        apt.getPatient().getName(),
                        apt.getSlot().getDateTime().toLocalDate(),
                        apt.getSlot().getDateTime().getHour(),
                        apt.getSlot().getDateTime().getMinute(),
                        apt.getStatus());
            }
        }
    }

    /**
     * Displays booked appointments with patient details.
     *
     * @param appointments the list of appointments to display
     */
    public static void displayBookedAppointments(List<Appointment> appointments) {
        System.out.println("\nBooked Appointments (BOOKED):");
        for (int i = 0; i < appointments.size(); i++) {
            Appointment apt = appointments.get(i);
            System.out.printf("%d. Patient: %s - Date: %s - Time: %02d:%02d - Status: %s\n",
                    i + 1,
                    apt.getPatient().getName(),
                    apt.getSlot().getDateTime().toLocalDate(),
                    apt.getSlot().getDateTime().getHour(),
                    apt.getSlot().getDateTime().getMinute(),
                    apt.getStatus());
        }
    }

    /**
     * Displays a doctor's complete schedule including all appointments and their status.
     *
     * @param doctor the doctor whose schedule is to be displayed
     */
    public static void displayPersonalSchedule(Doctor doctor) {
        List<Appointment> allAppointments = new ArrayList<>(Database.appointmentMap.values());
        List<Appointment> doctorAppointments = allAppointments.stream()
                .filter(apt -> apt.getDoctor().getID().equals(doctor.getID()))
                .sorted((a1, a2) -> a1.getSlot().getDateTime().compareTo(a2.getSlot().getDateTime()))
                .toList();

        System.out.println("\nPersonal Schedule:");
        for (Appointment apt : doctorAppointments) {
            System.out.printf("- %s (%s) - %s\n",
                    apt.getSlot().toString(),
                    apt.getStatus(),
                    apt.getIsAvailable() ? "Available" : "Unavailable");
        }
    }

    /**
     * Displays upcoming appointments for a doctor with patient details.
     *
     * @param doctor the doctor whose upcoming appointments are to be displayed
     */
    public static void displayUpcomingAppointments(Doctor doctor) {
        List<Appointment> upcomingAppointments = getUpcomingAppointments(doctor);

        if (upcomingAppointments.isEmpty()) {
            System.out.println("\nNo upcoming appointments found.");
        } else {
            System.out.println("\nUpcoming Appointments:");
            for (int i = 0; i < upcomingAppointments.size(); i++) {
                Appointment apt = upcomingAppointments.get(i);
                System.out.printf("%d. Patient: %s - Date: %s - Time: %02d:%02d - Status: %s\n",
                        i + 1,
                        apt.getPatient().getName(),
                        apt.getSlot().getDateTime().toLocalDate(),
                        apt.getSlot().getDateTime().getHour(),
                        apt.getSlot().getDateTime().getMinute(),
                        apt.getStatus());
            }
        }
    }

    /**
     * Marks a specific appointment slot as unavailable.
     * Updates the appointment status in the database.
     *
     * @param doctor the doctor whose slot is being marked
     * @param appointment the appointment to be marked as unavailable
     */
    public static void markSlotUnavailable(Doctor doctor, Appointment appointment) {
        appointment.setIsAvailable(false);
        appointment.setStatus(AppointmentStatus.UNAVAILABLE);
        Database.appointmentMap.put(appointment.getAppointmentID(), appointment);
        Database.saveAppointmentsToCSV();
        System.out.println("Slot marked as unavailable successfully.");
    }

    /**
     * Marks a specific appointment slot as available.
     * Updates the appointment status in the database.
     *
     * @param doctor the doctor whose slot is being marked
     * @param appointment the appointment to be marked as available
     */
    public static void markSlotAvailable(Doctor doctor, Appointment appointment) {
        appointment.setIsAvailable(true);
        appointment.setStatus(AppointmentStatus.PENDING);
        Database.appointmentMap.put(appointment.getAppointmentID(), appointment);
        Database.saveAppointmentsToCSV();
        System.out.println("Slot marked as available successfully.");
    }

    /**
     * Accepts a pending appointment request.
     * Changes the appointment status to BOOKED.
     *
     * @param doctor the doctor accepting the appointment
     * @param appointment the appointment to be accepted
     */
    public static void acceptAppointment(Doctor doctor, Appointment appointment) {
        appointment.setStatus(AppointmentStatus.BOOKED);
        Database.appointmentMap.put(appointment.getAppointmentID(), appointment);
        Database.saveAppointmentsToCSV();
        System.out.println("Appointment accepted successfully.");
    }

    /**
     * Declines a pending appointment request.
     * Changes the appointment status to CANCELLED and marks the slot as available.
     *
     * @param doctor the doctor declining the appointment
     * @param appointment the appointment to be declined
     */
    public static void declineAppointment(Doctor doctor, Appointment appointment) {
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.setIsAvailable(true);
        Database.appointmentMap.put(appointment.getAppointmentID(), appointment);
        Database.saveAppointmentsToCSV();
        System.out.println("Appointment declined successfully.");
    }

    /**
     * Records the outcome of a completed appointment including consultation notes and prescription.
     * Changes the appointment status to COMPLETED.
     *
     * @param appointment the appointment whose outcome is being recorded
     * @param notes the consultation notes
     * @param prescription the prescription issued during the appointment
     */
    public static void recordOutcome(Appointment appointment, String notes, Prescription prescription) {
        // Sanitize notes - replace any remaining newlines with spaces
        String sanitizedNotes = notes.replaceAll("[\n\r]", " ").trim();

        appointment.setConsultationNotes(sanitizedNotes);
        if (prescription != null) {
            appointment.setPrescription(prescription);
        }
        appointment.setStatus(AppointmentStatus.COMPLETED);

        // Update database
        Database.appointmentMap.put(appointment.getAppointmentID(), appointment);
        Database.saveAppointmentsToCSV();

        // Update patient's medical record if needed
        Patient patient = appointment.getPatient();
        if (patient != null) {
            ArrayList<Appointment.AppointmentOutcome> outcomes = patient.getMedicalRecord().getAppointmentOutcomes();
            if (outcomes == null) {
                outcomes = new ArrayList<>();
                patient.getMedicalRecord().setAppointmentOutcomes(outcomes);
            }
            outcomes.add(appointment.getAppointmentOutcome());
        }
    }

    /**
     * Creates a new medicine set with specified quantity for a prescription.
     *
     * @param medicine the medicine to be prescribed
     * @param quantity the quantity of medicine to be prescribed
     * @return a new MedicineSet object containing the medicine and quantity
     */
    public static Medicine.MedicineSet createMedicineSet(Medicine medicine, int quantity) {
        return new Medicine.MedicineSet(medicine, quantity);
    }

    /**
     * Adds a medicine with specified quantity to an existing prescription.
     *
     * @param prescription the prescription to which medicine is to be added
     * @param medicine the medicine to be added
     * @param quantity the quantity of medicine to be added
     */
    public static void addMedicineToPrescription(Prescription prescription, Medicine medicine, int quantity) {
        prescription.getMedicineList().put(medicine, quantity);
    }
}
