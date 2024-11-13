package hospitalsystem.appointmentcontrol;

import hospitalsystem.*;
import hospitalsystem.enums.*;
import hospitalsystem.model.*;
import hospitalsystem.data.*;
import hospitalsystem.inventorycontrol.*;
import hospitalsystem.usercontrol.*;
import hospitalsystem.menus.*;
import hospitalsystem.model.Appointment.*;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class AppointmentControl {

    // Hashmap storing all appointments 
    

    // Utility methods for managing appointments
    public static List<Appointment> getAppointmentsByDoctorID(String doctorID) {
        return Database.appointmentMap.values().stream()
                .filter(appointment -> appointment.getDoctor().getID().equals(doctorID))
                .sorted()
                .toList();
    }

    public static List<Appointment> getAppointmentsByPatientID(String patientID) {
        return Database.appointmentMap.values().stream()
                .filter(appointment -> appointment.getPatient().getID().equals(patientID))
                .sorted()
                .toList();
    }

    public static Appointment getAppointmentByAppointmentID(String appointmentID) {
        return Database.appointmentMap.get(appointmentID);
    }

    public static boolean bookSlot(String appointmentID) {
        Appointment appointment = Database.appointmentMap.get(appointmentID);
        if (appointment != null) {
            appointment.setStatus(AppointmentStatus.BOOKED);
            Database.appointmentMap.put(appointmentID, appointment);
            appointment.getDoctor().addAppointment(appointment);
            return true;
        }
        return false;
    }

    public static boolean cancelSlot(String appointmentID) {
        Appointment appointment = Database.appointmentMap.get(appointmentID);
        if (appointment != null && !appointment.getIsAvailable()) {
            appointment.setIsAvailable(true);
            appointment.setStatus(AppointmentStatus.CANCELLED);
            Database.appointmentMap.put(appointmentID, appointment);
            appointment.getDoctor().removeAppointment(appointment);
            return true;
        }
        return false;
    }

    public static void recordOutcome(String appointmentID, String consultationNotes, List<Prescription> prescriptions) {
        Appointment appointment = Database.appointmentMap.get(appointmentID);
        if (appointment != null) {
            appointment.setConsultationNotes(consultationNotes);

            appointment.setPrescriptions(prescriptions);

            appointment.setStatus(AppointmentStatus.COMPLETED);
            Database.appointmentMap.put(appointmentID, appointment);
        }
    }

    // Generate available slots for a week
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

    public static String generateAppointmentID(String doctorID, AppointmentSlot slot) {
        LocalDateTime dt = slot.getDateTime();
        return String.format("APT_%s_%d%02d%02d%02d%02d",
                doctorID,
                dt.getYear(),
                dt.getMonthValue(),
                dt.getDayOfMonth(),
                dt.getHour(),
                dt.getMinute()
        );
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

    //unused
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

    public static List<Prescription> getAppointmentPrescriptions(String appointmentID) {
        Appointment appointment = Database.appointmentMap.get(appointmentID);
        if (appointment != null) {
            HashMap<String, PrescriptionStatus> prescriptionMap = appointment.getPrescriptions();
            return prescriptionMap.entrySet().stream()
                    .map(entry -> new Prescription(entry.getKey(), entry.getValue()))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
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

    

    public static boolean addAppointment(int slotIndex, Doctor doctor, Patient patient){
        try {
            List<AppointmentSlot> newSlots = doctor.getAvailableSlots();
            AppointmentSlot selectedslot = newSlots.remove(slotIndex);
            doctor.setAvailableSlots(newSlots);
            Appointment newAppointment = new Appointment(AppointmentControl.generateAppointmentID(doctor.getID(), selectedslot), patient, doctor, selectedslot);
            doctor.addAppointment(newAppointment);

            List<Appointment> newAppointments = patient.getAppointments();
            newAppointments.add(newAppointment);
            patient.setAppointments(newAppointments);

            Database.appointmentMap.put(patient.getID(), newAppointment);
            Database.appointmentMap.put(doctor.getID(), newAppointment);

            return true;
        } catch (Exception e){
            System.out.println("Error Occured: " + e);
            return false;
        }
    }

    public static void displayPastAppointmentOutcomes(Patient patient){
        ArrayList<AppointmentOutcome> appointmentOutcomes = patient.getMedicalRecord().getAppointmentOutcomes();
        int lastSlot = appointmentOutcomes.size()-1;
        System.out.println("=====================================");
        System.out.println("      Past Appointment Outcomes      ");
        System.out.println("=====================================");
        for (AppointmentOutcome outcome:appointmentOutcomes){
            System.out.println("Appointment Date: " + outcome.getRecordedDate());
            System.out.println("Service Type: " + outcome.getServiceType());
            
            System.out.println("Prescriptions:");
            HashMap<String, PrescriptionStatus> prescriptions = outcome.getPrescriptions();
            for (String prescriptionName : prescriptions.keySet()) {
                System.out.println(" - " + prescriptionName + ": " + prescriptions.get(prescriptionName));
            }
            
            System.out.println("Consultation Notes: ");
            System.out.println(outcome.getConsultationNotes());
            if (outcome != appointmentOutcomes.get(lastSlot)){
                System.out.println("-----");
            }
        }
        System.out.println("=====================================");
    }


    // Optional: Method to backup before saving
    public static void backupAndSave() {  // Removed path parameter
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String backupPath = CSV_PATH.replace(".csv", "_backup_" + timestamp + ".csv");

        File originalFile = new File(CSV_PATH);
        if (originalFile.exists()) {
            try {
                Files.copy(originalFile.toPath(), new File(backupPath).toPath(), StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Backup created: " + backupPath);
            } catch (IOException e) {
                System.out.println("Warning: Could not create backup: " + e.getMessage());
            }
        }
 
        saveAppointmentsToCSV();
    }
}