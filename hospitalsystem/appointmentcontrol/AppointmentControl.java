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

    public static void recordOutcome(String appointmentID, String consultationNotes, Prescription prescription) {
        Appointment appointment = Database.appointmentMap.get(appointmentID);
        if (appointment != null) {
            appointment.setConsultationNotes(consultationNotes);

            appointment.setPrescription(prescription);

            appointment.setStatus(AppointmentStatus.COMPLETED);
            Database.appointmentMap.put(appointmentID, appointment);
        }
    }

    // Generate available slots for a week
    

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
            System.out.println("Serce Type: " + outcome.getServiceType());
            
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
    
}