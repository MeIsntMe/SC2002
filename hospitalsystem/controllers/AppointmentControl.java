package hospitalsystem.controllers;

import hospitalsystem.MainSystem;
import hospitalsystem.model.*;
import hospitalsystem.enums.*;
import hospitalsystem.model.Appointment.AppointmentSlot;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class AppointmentControl {
    private static HashMap<String, Appointment> allAppointments = new HashMap<>();
    private static final String CSV_HEADER = "AppointmentID,PatientID,DoctorID,Year,Month,Day,Hour,Minute,Status,IsAvailable,ConsultationNotes,Prescriptions";

    public static void loadAppointmentsFromCSV(String filePath) {
        try (Scanner fileScanner = new Scanner(new File(filePath))) {
            fileScanner.nextLine(); // Skip the first line
            while (fileScanner.hasNextLine()) {
                String[] appointmentData = fileScanner.nextLine().replaceAll("\"", "").split(",");
                try {
                    String appointmentID = appointmentData[0].trim();
                    String patientID = appointmentData[1].trim();
                    String doctorID = appointmentData[2].trim();

                    // Get Doctor and Patient from MainSystem maps
                    User doctorUser = MainSystem.doctorsMap.get(doctorID);
                    User patientUser = MainSystem.patientsMap.get(patientID);

                    Doctor doctor = null;
                    Patient patient = null;

                    if (doctorUser instanceof Doctor) {
                        doctor = (Doctor) doctorUser;
                    }
                    if (patientUser instanceof Patient) {
                        patient = (Patient) patientUser;
                    }

                    if (doctor != null && patient != null) {
                        // Parse date/time components
                        int year = Integer.parseInt(appointmentData[3].trim());
                        int month = Integer.parseInt(appointmentData[4].trim());
                        int day = Integer.parseInt(appointmentData[5].trim());
                        int hour = Integer.parseInt(appointmentData[6].trim());
                        int minute = Integer.parseInt(appointmentData[7].trim());

                        // Create appointment slot
                        AppointmentSlot slot = new AppointmentSlot(year, month, day, hour, minute);

                        // Create appointment
                        Appointment appointment = new Appointment(appointmentID, patient, doctor, slot);

                        // Set appointment properties
                        appointment.setStatus(AppointmentStatus.valueOf(appointmentData[8].trim().toUpperCase()));
                        appointment.setAvailable(Boolean.parseBoolean(appointmentData[9].trim()));
                        appointment.setConsultationNotes(appointmentData[10].trim());

                        // Handle prescriptions if they exist
                        if (appointmentData.length > 11 && !appointmentData[11].trim().isEmpty()) {
                            HashMap<String, PrescriptionStatus> prescriptions = new HashMap<>();
                            String[] prescriptionPairs = appointmentData[11].split(";");

                            for (String pair : prescriptionPairs) {
                                String[] parts = pair.split(":");
                                if (parts.length == 2) {
                                    String medicine = parts[0].trim();
                                    PrescriptionStatus status = PrescriptionStatus.valueOf(parts[1].trim());
                                    prescriptions.put(medicine, status);
                                }
                            }
                            appointment.setPrescriptions(prescriptions);
                        }

                        // Add to maps and lists
                        allAppointments.put(appointmentID, appointment);
                        doctor.addAppointment(appointment);

                        // Update patient's appointments
                        ArrayList<Appointment> patientAppointments = patient.getAppointments();
                        if (patientAppointments == null) {
                            patientAppointments = new ArrayList<>();
                        }
                        patientAppointments.add(appointment);
                        patient.setAppointments(patientAppointments);
                        patient.addAppointmentCount();

                    } else {
                        if (doctor == null) {
                            System.out.println("Doctor not found for ID: " + doctorID);
                        }
                        if (patient == null) {
                            System.out.println("Patient not found for ID: " + patientID);
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Error processing appointment line: " + String.join(",", appointmentData));
                    System.out.println("Error details: " + e.getMessage());
                }
            }
            System.out.println("Successfully loaded " + allAppointments.size() + " appointments");

        } catch (FileNotFoundException e) {
            System.out.println("An error has occurred\n" + e.getMessage());
        }
    }

    // Utility methods for managing appointments
    public static List<Appointment> getAppointmentsByDoctorID(String doctorID) {
        return allAppointments.values().stream()
                .filter(appointment -> appointment.getDoctor().getID().equals(doctorID))
                .sorted()
                .toList();
    }

    public static List<Appointment> getAppointmentsByPatientID(String patientID) {
        return allAppointments.values().stream()
                .filter(appointment -> appointment.getPatient().getID().equals(patientID))
                .sorted()
                .toList();
    }

    public static Appointment getAppointment(String appointmentID) {
        return allAppointments.get(appointmentID);
    }

    public static boolean bookSlot(String appointmentID) {
        Appointment appointment = allAppointments.get(appointmentID);
        if (appointment != null && appointment.isAvailable()) {
            appointment.setAvailable(false);
            appointment.setStatus(AppointmentStatus.BOOKED);
            allAppointments.put(appointmentID, appointment);
            appointment.getDoctor().addAppointment(appointment);
            return true;
        }
        return false;
    }

    public static boolean cancelSlot(String appointmentID) {
        Appointment appointment = allAppointments.get(appointmentID);
        if (appointment != null && !appointment.isAvailable()) {
            appointment.setAvailable(true);
            appointment.setStatus(AppointmentStatus.CANCELLED);
            allAppointments.put(appointmentID, appointment);
            appointment.getDoctor().removeAppointment(appointment);
            return true;
        }
        return false;
    }

    public static void recordOutcome(String appointmentID, String consultationNotes, HashMap<String, PrescriptionStatus> prescriptions) {
        Appointment appointment = allAppointments.get(appointmentID);
        if (appointment != null) {
            appointment.setConsultationNotes(consultationNotes);
            appointment.setPrescriptions(prescriptions);
            appointment.setStatus(AppointmentStatus.COMPLETED);
            allAppointments.put(appointmentID, appointment);
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
                appointment.setAvailable(true);

                // Add to global appointments map
                allAppointments.put(appointmentID, appointment);
            }
        }

        // Update doctor's available slots
        doctor.setAvailableSlots(slots);

        System.out.println("Generated " + slots.size() + " slots for next week for doctor " + doctor.getID());
        return slots;
    }

    private static String generateAppointmentID(String doctorID, AppointmentSlot slot) {
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

    public static void saveAppointmentsToCSV(String filePath) {
        try (FileWriter fw = new FileWriter(filePath);
             BufferedWriter bw = new BufferedWriter(fw)) {

            // Write header
            bw.write(CSV_HEADER);
            bw.newLine();

            // Write appointments sorted by ID for consistency
            allAppointments.values().stream()
                    .sorted(Comparator.comparing(Appointment::getAppointmentID))
                    .forEach(appointment -> {
                        try {
                            bw.write(formatAppointmentToCSV(appointment));
                            bw.newLine();
                        } catch (IOException e) {
                            System.out.println("Error writing appointment " + appointment.getAppointmentID() + ": " + e.getMessage());
                        }
                    });

            System.out.println("Successfully saved " + allAppointments.size() + " appointments to " + filePath);

        } catch (IOException e) {
            System.out.println("Error saving appointments to CSV: " + e.getMessage());
        }
    }

    private static String formatAppointmentToCSV(Appointment appointment) {
        StringBuilder sb = new StringBuilder();

        // Get the date components
        AppointmentSlot slot = appointment.getSlot();
        LocalDateTime dateTime = slot.getDateTime();

        // Format prescriptions if they exist
        String prescriptions = formatPrescriptions(appointment.getPrescriptions());

        // Build the CSV line with proper escaping
        sb.append(escapeCSV(appointment.getAppointmentID())).append(",");
        sb.append(escapeCSV(appointment.getPatient().getID())).append(",");
        sb.append(escapeCSV(appointment.getDoctor().getID())).append(",");
        sb.append(dateTime.getYear()).append(",");
        sb.append(dateTime.getMonthValue()).append(",");
        sb.append(dateTime.getDayOfMonth()).append(",");
        sb.append(dateTime.getHour()).append(",");
        sb.append(dateTime.getMinute()).append(",");
        sb.append(appointment.getStatus()).append(",");
        sb.append(appointment.isAvailable()).append(",");
        sb.append(escapeCSV(appointment.getConsultationNotes())).append(",");
        sb.append(escapeCSV(prescriptions));

        return sb.toString();
    }

    public static void updateSlotAvailability(String doctorID, AppointmentSlot slot, boolean makeAvailable) {
        // Find the appointment for this slot
        Appointment appointment = allAppointments.values().stream()
                .filter(apt -> apt.getDoctor().getID().equals(doctorID) &&
                        apt.getSlot().getDateTime().equals(slot.getDateTime()))
                .findFirst()
                .orElse(null);

        if (appointment != null) {
            appointment.setAvailable(makeAvailable);
            if (!makeAvailable) {
                appointment.setStatus(AppointmentStatus.BOOKED);
            } else {
                appointment.setStatus(AppointmentStatus.PENDING);
            }
            allAppointments.put(appointment.getAppointmentID(), appointment);
        }
    }

    private static String formatPrescriptions(HashMap<String, PrescriptionStatus> prescriptions) {
        if (prescriptions == null || prescriptions.isEmpty()) {
            return "";
        }

        return prescriptions.entrySet().stream()
                .map(entry -> entry.getKey() + ":" + entry.getValue())
                .reduce((a, b) -> a + ";" + b)
                .orElse("");
    }

    private static String escapeCSV(String value) {
        if (value == null) {
            return "";
        }

        // If the value contains commas, quotes, or newlines, wrap it in quotes and escape existing quotes
        boolean needsQuoting = value.contains(",") || value.contains("\"") || value.contains("\n");
        if (needsQuoting) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    // Optional: Method to backup before saving
    public static void backupAndSave(String filePath) {
        // Create backup filename with timestamp
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String backupPath = filePath.replace(".csv", "_backup_" + timestamp + ".csv");

        // Copy existing file to backup if it exists
        File originalFile = new File(filePath);
        if (originalFile.exists()) {
            try {
                Files.copy(originalFile.toPath(), new File(backupPath).toPath(), StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Backup created: " + backupPath);
            } catch (IOException e) {
                System.out.println("Warning: Could not create backup: " + e.getMessage());
            }
        }

        // Save current data
        saveAppointmentsToCSV(filePath);
    }
}