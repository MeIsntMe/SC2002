package hospitalsystem.data;

import hospitalsystem.enums.AppointmentStatus;
import hospitalsystem.enums.BloodType;
import hospitalsystem.enums.PrescriptionStatus;
import hospitalsystem.enums.UserType;
import hospitalsystem.model.*;
import hospitalsystem.model.Appointment.AppointmentSlot;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Database management class for the hospital system.
 * Handles all data persistence operations including loading from and saving to CSV files,
 * and maintains in-memory data structures for various hospital system entities.
 *
 * This class provides centralized data management for:
 * - Users (Patients, Doctors, Administrators, Pharmacists)
 * - Medical Inventory
 * - Appointments
 * - Replenishment Requests
 *
 * @author Your Name
 * @version 1.0
 * @since 2024-03-16
 */
public class Database {

    // HashMaps
    /** Maps user IDs to Patient objects */
    public static Map<String, User> patientsMap = new HashMap<>();
    /** Maps user IDs to Doctor objects */
    public static Map<String, User> doctorsMap = new HashMap<>();
    /** Maps user IDs to Administrator objects */
    public static Map<String, User> adminsMap = new HashMap<>();
    /** Maps user IDs to Pharmacist objects */
    public static Map<String, User> pharmsMap = new HashMap<>();
    /** Maps medicine names to Medicine objects */
    public static Map<String, Medicine> inventoryMap = new HashMap<>();
    /** Maps request IDs to ReplenishmentRequest objects */
    public static Map<Integer, ReplenishmentRequest> requestMap = new HashMap<>();
    /** Maps appointment IDs to Appointment objects */
    public static HashMap<String, Appointment> appointmentMap = new HashMap<>();

    // CSV Constants
    private static final String APPOINTMENT_CSV_HEADER = "AppointmentID,PatientID,DoctorID,Year,Month,Day,Hour,Minute,Status,IsAvailable,ConsultationNotes,Prescriptions";
    private static final String APPOINTMENT_CSV_PATH = "hospitalsystem/data/Appointment.csv";

    private static final String PATIENT_CSV_HEADER = "Patient ID,Name,Date of Birth,Gender,Blood Type,Phone Number,Email,Password";
    private static final String PATIENT_CSV_PATH = "hospitalsystem/data/Patient_List.csv";

    private static final String STAFF_CSV_HEADER = "Staff ID,Name,Role,Gender,Age";
    private static final String STAFF_CSV_PATH = "hospitalsystem/data/Staff_List.csv";

    private static final String INVENTORY_CSV_HEADER = "Medicine Name,Initial Stock,Low Stock Level Alert,Batches Quantity,Batches Expiry Date";
    private static final String INVENTORY_CSV_PATH = "hospitalsystem/data/Medicine_List.csv";

    // Public interface methods for loading data
    /**
     * Loads all data from CSV files into the system.
     * This includes staff, patient, inventory, and appointment data.
     */
    public static void loadAllData() {
        loadStaffData();
        loadPatientData();
        loadInventoryData();
        loadAppointmentData();
    }

    /**
     * Loads staff data from CSV into respective staff maps.
     * Populates doctorsMap, adminsMap, and pharmsMap.
     */
    public static void loadStaffData() {
        loadStaffFromCSV(STAFF_CSV_PATH);
    }

    /**
     * Loads patient data from CSV into patientsMap.
     */
    public static void loadPatientData() {
        loadPatientfromCSV(PATIENT_CSV_PATH);
    }

    /**
     * Loads inventory data from CSV into inventoryMap.
     */
    public static void loadInventoryData() {
        loadInventoryFromCSV(INVENTORY_CSV_PATH);
    }

    /**
     * Loads appointment data from CSV into appointmentMap.
     */
    public static void loadAppointmentData() {
        loadAppointmentsFromCSV(APPOINTMENT_CSV_PATH);
    }

    // Public interface methods for saving data
    /**
     * Saves all system data to respective CSV files.
     * Handles errors for each save operation independently.
     */
    public static void saveAllData() {
        System.out.println("Saving all data...");
        try {
            saveAppointmentData();
            savePatientData();
            saveStaffData();
            saveInventoryData();
            System.out.println("All data saved successfully!");
        } catch (Exception e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }

    /**
     * Saves current appointment data to CSV file.
     * @throws RuntimeException if there is an error saving the data
     */
    public static void saveAppointmentData() {
        try {
            saveAppointmentsToCSV();
            System.out.println("Appointments saved successfully.");
        } catch (Exception e) {
            System.out.println("Error saving appointments: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Saves current patient data to CSV file.
     * @throws RuntimeException if there is an error saving the data
     */
    public static void savePatientData() {
        try {
            savePatientToCSV();
            System.out.println("Patient data saved successfully.");
        } catch (Exception e) {
            System.out.println("Error saving patient data: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Saves current staff data to CSV file.
     * @throws RuntimeException if there is an error saving the data
     */
    public static void saveStaffData() {
        try {
            saveStaffToCSV();
            System.out.println("Staff data saved successfully.");
        } catch (Exception e) {
            System.out.println("Error saving staff data: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Saves current inventory data to CSV file.
     * @throws RuntimeException if there is an error saving the data
     */
    public static void saveInventoryData() {
        try {
            saveInventoryToCSV();
            System.out.println("Inventory data saved successfully.");
        } catch (Exception e) {
            System.out.println("Error saving inventory data: " + e.getMessage());
            throw e;
        }
    }

    // Private Methods to load from CSV into Hashmap
    /**
     * Loads patient data from specified CSV file into patientsMap.
     * Handles data validation and error logging.
     *
     * @param filePath path to the patient CSV file
     */
    private static void loadPatientfromCSV(String filePath) {
        try (Scanner scanner = new Scanner(new File(filePath))) {
            scanner.nextLine(); // Skip the header
            while (scanner.hasNextLine()) {
                String[] patientData = scanner.nextLine().split(",");
                try {
                    String patientID = patientData[0].trim();
                    String name = patientData[1].trim();
                    LocalDate DOB = LocalDate.parse(patientData[2].trim());
                    String gender = patientData[3].trim().toLowerCase();
                    BloodType bloodType = BloodType.valueOf(patientData[4].trim());
                    String phoneNumber = patientData[5].trim();
                    String email = patientData[6].trim();
                    String password = patientData[7].trim();
                    
                    int age = (int)ChronoUnit.YEARS.between(DOB, LocalDate.now());

                    Patient patient = new Patient(patientID, name, phoneNumber, DOB, age, gender, bloodType, email, password);
                    patientsMap.put(patientID, patient);
                } catch (Exception e) {
                    System.out.println("Error processing patient line: " + String.join(",", patientData));
                    System.out.println("Error details: " + e.getMessage());
                }
            }
            System.out.println("Successfully loaded " + patientsMap.size() + " patients");
        } catch (FileNotFoundException e) {
            System.out.println("An error has occurred\n" + e.getMessage());
        }
    }

    /**
     * Loads staff data from specified CSV file into respective staff maps.
     * Categorizes staff by role and handles data validation.
     *
     * @param filePath path to the staff CSV file
     */
    private static void loadStaffFromCSV(String filePath) {
        try (Scanner scanner = new Scanner(new File(filePath))) {
            scanner.nextLine(); // Skip the header
            while (scanner.hasNextLine()) {
                String[] staffData = scanner.nextLine().split(",");
                try {
                    String staffID = staffData[0].trim();
                    String name = staffData[1].trim();
                    UserType role = UserType.valueOf(staffData[2].trim().toUpperCase());
                    String gender = staffData[3].trim();
                    int age = Integer.parseInt(staffData[4].trim());
                    String password = staffData.length > 5 ? staffData[5].trim() : "password";

                    switch (role) {
                        case DOCTOR:
                            Doctor doc = new Doctor(staffID, name, age, gender, password);
                            doctorsMap.put(staffID, doc);
                            break;
                        case ADMINISTRATOR:
                            Administrator admin = new Administrator(staffID, name, age, gender, password);
                            adminsMap.put(staffID, admin);
                            break;
                        case PHARMACIST:
                            Pharmacist pharm = new Pharmacist(staffID, name, age, gender, password);
                            pharmsMap.put(staffID, pharm);
                            break;
                        case PATIENT:
                            break; // CSV does not contain patients
                    }
                } catch (Exception e) {
                    System.out.println("Error processing staff line: " + String.join(",", staffData));
                    System.out.println("Error details: " + e.getMessage());
                }
            }
            System.out.println("Successfully loaded " +
                    (doctorsMap.size() + adminsMap.size() + pharmsMap.size()) + " staff members");
        } catch (FileNotFoundException e) {
            System.out.println("An error has occurred\n" + e.getMessage());
        }
    }

    /**
     * Loads inventory data from specified CSV file into inventoryMap.
     * Handles batch information and stock levels.
     *
     * @param filePath path to the inventory CSV file
     */
    private static void loadInventoryFromCSV(String filePath) {
        try (Scanner scanner = new Scanner(new File(filePath))) {
            scanner.nextLine(); // Skip the header
            while (scanner.hasNextLine()) {
                String[] inventoryData = scanner.nextLine().split(",");
                try {
                    // Parse basic medicine data
                    String medicineName = inventoryData[0].trim();
                    int minimumStockLevel = Integer.parseInt(inventoryData[1].trim());
                    String instructions = inventoryData[2].trim();

                    // Parse batch data
                    String batchesQuantity = inventoryData[3].trim();
                    String batchesDates = inventoryData[4].trim();

                    // Create medicine if it doesn't exist
                    Medicine medicine = inventoryMap.get(medicineName);
                    if (medicine == null) {
                        medicine = new Medicine(medicineName, minimumStockLevel, instructions);
                        inventoryMap.put(medicineName, medicine);
                    }

                    // Add batch to the medicine
                    List<Medicine.Batch> batches = loadBatchesFromCSVData(medicine, batchesQuantity, batchesDates);
                    medicine.setBatch(batches);

                } catch (Exception e) {
                    System.out.println("Error processing inventory line: " + String.join(",", inventoryData));
                    System.out.println("Error details: " + e.getMessage());
                }
            }
            System.out.println("Successfully loaded " + inventoryMap.size() + " medicines");
        } catch (FileNotFoundException e) {
            System.out.println("CSV file not found: " + e.getMessage());
        }
    }

    private static List<Medicine.Batch> loadBatchesFromCSVData(Medicine medicine, String batchesQuantity, String batchesDates){
        List<Medicine.Batch> batches = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        if (!batchesQuantity.equals("")){
            int[] quantities = Arrays.stream(batchesQuantity.split("\\|")).mapToInt(Integer::parseInt).toArray();
            LocalDate[] expiryDates = Arrays.stream(batchesDates.split("\\|")).map(dateStr -> LocalDate.parse(dateStr, formatter)).toArray(LocalDate[]::new);
            int batchSize = quantities.length;
            for (int i = 0; i < batchSize; i++) {
                batches.add(medicine.new Batch(quantities[i], expiryDates[i]));
            }
        }
        return batches;
    }

    /**
     * Loads appointment data from specified CSV file into appointmentMap.
     * Links appointments with doctors and patients, handles prescriptions.
     *
     * @param filePath path to the appointment CSV file
     */
    private static void loadAppointmentsFromCSV(String filePath) {
        try (Scanner scanner = new Scanner(new File(filePath))) {
            scanner.nextLine(); // Skip the first line
            while (scanner.hasNextLine()) {
                String[] appointmentData = scanner.nextLine().replaceAll("\"", "").split(",");
                try {
                    String appointmentID = appointmentData[0].trim();
                    String patientID = appointmentData[1].trim();
                    String doctorID = appointmentData[2].trim();

                    // Get Doctor and Patient from MainSystem maps
                    User doctorUser = doctorsMap.get(doctorID);
                    User patientUser = patientsMap.get(patientID);

                    Doctor doctor = null;
                    Patient patient = null;

                    if (doctorUser instanceof Doctor)
                        doctor = (Doctor) doctorUser;
                    if (patientUser instanceof Patient)
                        patient = (Patient) patientUser;

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
                        appointment.setIsAvailable(Boolean.parseBoolean(appointmentData[9].trim()));
                        appointment.setConsultationNotes(appointmentData[10].trim());

                        // Handle prescriptions if they exist
                        if (appointmentData.length > 11 && !appointmentData[11].trim().isEmpty()) {
                            List<Prescription.MedicineSet> medicineSets = new ArrayList<>();
                            String[] prescriptionPairs = appointmentData[11].split(";");

                            for (String pair : prescriptionPairs) {
                                String[] parts = pair.split(":");
                                if (parts.length == 2) {
                                    String medicineName = parts[0].trim();
                                    int quantity = Integer.parseInt(parts[1].trim());

                                    Medicine medicine = inventoryMap.get(medicineName);
                                    if (medicine != null) {
                                        medicineSets.add(new Prescription.MedicineSet(medicine, quantity));
                                    } else {
                                        System.out.println("Medicine not found: " + medicineName);
                                    }
                                }
                            }

                            if (!medicineSets.isEmpty()) {
                                Prescription prescription = new Prescription(
                                        medicineSets,
                                        doctor.getID(),
                                        patient.getID(),
                                        PrescriptionStatus.PENDING
                                );
                                appointment.setPrescription(prescription);
                            }
                        }

                        // Add to maps and lists
                        appointmentMap.put(appointmentID, appointment);
                        doctor.addAppointment(appointment);

                        // Update patient's appointments
                        List<Appointment> patientAppointments = patient.getAppointments();
                        if (patientAppointments == null) {
                            patientAppointments = new ArrayList<>();
                        }
                        patientAppointments.add(appointment);
                        patient.setAppointments(patientAppointments);

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
            System.out.println("Successfully loaded " + appointmentMap.size() + " appointments");

        } catch (FileNotFoundException e) {
            System.out.println("An error has occurred\n" + e.getMessage());
        }
    }

    /**
     * Formats prescription data for CSV storage.
     * Converts prescription details into semicolon-separated string format.
     *
     * @param prescription the Prescription object to format
     * @return formatted string representation of the prescription
     */
    private static String formatPrescription(Prescription prescription) {
        if (prescription == null || prescription.getMedicineList().isEmpty()) {
            return "";
        }

        return prescription.getMedicineList().entrySet().stream()
                .map(entry -> entry.getKey().getMedicineName() + ":" + entry.getValue())
                .collect(Collectors.joining(";"));
    }

    /**
     * Escapes special characters in CSV values.
     * Handles commas, quotes, and newlines in data fields.
     *
     * @param value the string to escape
     * @return escaped string safe for CSV storage
     */
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

    /**
     * Formats appointment data for CSV storage.
     * Converts all appointment details into comma-separated format.
     *
     * @param appointment the Appointment object to format
     * @return formatted string representation of the appointment
     */
    private static String formatAppointmentToCSV(Appointment appointment) {
        StringBuilder sb = new StringBuilder();

        // Get the date components
        AppointmentSlot slot = appointment.getSlot();
        LocalDateTime dateTime = slot.getDateTime();

        // Format prescriptions if they exist
        String prescriptions = formatPrescription(appointment.getPrescription());

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
        sb.append(appointment.getIsAvailable()).append(",");
        sb.append(escapeCSV(appointment.getConsultationNotes())).append(",");
        sb.append(escapeCSV(prescriptions));

        return sb.toString();
    }

    /**
     * Saves current patient data to CSV file.
     * Sorts patients by ID and writes data in specified format.
     */
    public static void savePatientToCSV() {
        try (FileWriter fw = new FileWriter(PATIENT_CSV_PATH);
             BufferedWriter bw = new BufferedWriter(fw)) {

            // Write header
            bw.write(PATIENT_CSV_HEADER);
            bw.newLine();

            // Write patients sorted by ID for consistency
            patientsMap.values().stream()
                    .map(user -> (Patient) user)
                    .sorted(Comparator.comparing(User::getID))
                    .forEach(patient -> {
                        try {
                            String line = String.format("%s,%s,%s,%s,%s,%s",
                                    escapeCSV(patient.getID()),
                                    escapeCSV(patient.getName()),
                                    patient.getDOB().toString(),
                                    escapeCSV(patient.getGender()),
                                    patient.getBloodType().toString(),
                                    escapeCSV(patient.getEmail())
                            );
                            bw.write(line);
                            bw.newLine();
                        } catch (IOException e) {
                            System.out.println("Error writing patient " + patient.getID() + ": " + e.getMessage());
                        }
                    });

            System.out.println("Successfully saved " + patientsMap.size() + " patients to " + PATIENT_CSV_PATH);

        } catch (IOException e) {
            System.out.println("Error saving patients to CSV: " + e.getMessage());
        }
    }

    /**
     * Saves current staff data to CSV file.
     * Combines all staff types and sorts by ID before saving.
     */
    public static void saveStaffToCSV() {
        try (FileWriter fw = new FileWriter(STAFF_CSV_PATH);
             BufferedWriter bw = new BufferedWriter(fw)) {

            // Write header
            bw.write(STAFF_CSV_HEADER);
            bw.newLine();

            // Combine all staff maps and sort by ID
            List<User> allStaff = new ArrayList<>();
            allStaff.addAll(doctorsMap.values());
            allStaff.addAll(adminsMap.values());
            allStaff.addAll(pharmsMap.values());

            allStaff.stream()
                    .sorted(Comparator.comparing(User::getID))
                    .forEach(staff -> {
                        try {
                            String role = "Doctor";
                            if (staff instanceof Administrator) role = "Administrator";
                            if (staff instanceof Pharmacist) role = "Pharmacist";

                            String line = String.format("%s,%s,%s,%s,%d",
                                    escapeCSV(staff.getID()),
                                    escapeCSV(staff.getName()),
                                    role,
                                    escapeCSV(staff.getGender()),
                                    staff.getAge()
                            );
                            bw.write(line);
                            bw.newLine();
                        } catch (IOException e) {
                            System.out.println("Error writing staff " + staff.getID() + ": " + e.getMessage());
                        }
                    });

            System.out.println("Successfully saved " + allStaff.size() + " staff members to " + STAFF_CSV_PATH);

        } catch (IOException e) {
            System.out.println("Error saving staff to CSV: " + e.getMessage());
        }
    }

    /**
     * Saves current inventory data to CSV file.
     * Includes stock levels and batch information.
     */
    public static void saveInventoryToCSV() {
        try (FileWriter fw = new FileWriter(INVENTORY_CSV_PATH);
             BufferedWriter bw = new BufferedWriter(fw)) {

            // Write header
            bw.write(INVENTORY_CSV_HEADER);
            bw.newLine();

            // Write inventory sorted by medicine name
            inventoryMap.values().stream()
                    .sorted(Comparator.comparing(Medicine::getMedicineName))
                    .forEach(medicine -> {
                        try {
                            // Calculate total stock from all batches
                            List<Medicine.Batch> batches = medicine.getBatches();
                            String batchesQuantity = batches.stream().map(batch -> String.valueOf(batch.getQuantity())).collect(Collectors.joining("\\|"));
                            String batchesDates = batches.stream().map(batch -> batch.getExpirationDate().toString()).collect(Collectors.joining("\\|"));

                            String line = String.format("%s,%d,%d,%s,%s",
                                    escapeCSV(medicine.getMedicineName()),
                                    medicine.getMinStockLevel(),
                                    medicine.getIsLowStock(),
                                    batchesQuantity, 
                                    batchesDates
                            );
                            bw.write(line);
                            bw.newLine();
                        } catch (Exception e) {
                            System.out.println("Error processing medicine " + medicine.getMedicineName() + ": " + e.getMessage());
                        }
                    });

            System.out.println("Successfully saved inventory to " + INVENTORY_CSV_PATH);

        } catch (IOException e) {
            System.out.println("Error saving inventory to CSV: " + e.getMessage());
        }
    }

    /**
     * Saves current appointment data to CSV file.
     * Includes all appointment details and related prescriptions.
     */
    public static void saveAppointmentsToCSV() {
        try (FileWriter fw = new FileWriter(APPOINTMENT_CSV_PATH);
            BufferedWriter bw = new BufferedWriter(fw)) {

            // Write header
            bw.write(APPOINTMENT_CSV_HEADER);
            bw.newLine();

            // Write appointments sorted by ID for consistency
            Database.appointmentMap.values().stream()
                    .sorted(Comparator.comparing(Appointment::getAppointmentID))
                    .forEach(appointment -> {
                        try {
                            bw.write(formatAppointmentToCSV(appointment));
                            bw.newLine();
                        } catch (IOException e) {
                            System.out.println("Error writing appointment " + appointment.getAppointmentID() + ": " + e.getMessage());
                        }
                    });

            System.out.println("Successfully saved " + Database.appointmentMap.size() + " appointments to " + APPOINTMENT_CSV_PATH);

        } catch (IOException e) {
            System.out.println("Error saving appointments to CSV: " + e.getMessage());
        }
    }
}
