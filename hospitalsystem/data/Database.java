package hospitalsystem.data;

import hospitalsystem.enums.*;
import hospitalsystem.model.*;
import hospitalsystem.model.Appointment.AppointmentSlot;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
 * @author An Xian, Gracelynn, Leo
 * @version 1.0
 * @since 2024-11-19
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

    private static final String STAFF_CSV_HEADER = "Staff ID,Name,Role,Gender,Age,Password";
    private static final String STAFF_CSV_PATH = "hospitalsystem/data/Staff_List.csv";

    private static final String INVENTORY_CSV_HEADER = "Medicine Name,Initial Stock,Low Stock Level Alert,Batches Quantity,Batches Expiry Date";
    private static final String INVENTORY_CSV_PATH = "hospitalsystem/data/Medicine_List.csv";

    private static final String REQUEST_CSV_HEADER = "RequestID,MedicineName,RequestedQuantity,Status";
    private static final String REQUEST_CSV_PATH = "hospitalsystem/data/Replenishment_Requests.csv";
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
        loadRequestsFromCSV();
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
            saveRequestsToCSV();
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
            scanner.nextLine(); // Skip header
            while (scanner.hasNextLine()) {
                String[] data = scanner.nextLine().split(",");
                String id = data[0].trim();
                String name = data[1].trim();
                LocalDate dob = LocalDate.parse(data[2].trim());
                String gender = data[3].trim();
                BloodType bloodType = BloodType.valueOf(data[4].trim().replace("+", "_POSITIVE").replace("-", "_NEGATIVE"));
                String phone = data[5].trim();
                String email = data[6].trim();
                String password = data[7].trim();

                int age = LocalDate.now().getYear() - dob.getYear();

                Patient patient = new Patient(id, name, phone, dob, age, gender, bloodType, email, password);
                patientsMap.put(id, patient);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error loading patient data: " + e.getMessage());
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
            scanner.nextLine(); // Skip header
            while (scanner.hasNextLine()) {
                String[] data = scanner.nextLine().split(",");
                String medicineName = data[0].trim();
                int initialStock = Integer.parseInt(data[1].trim());
                int minStockLevel = Integer.parseInt(data[2].trim());
                String[] quantities = data[3].trim().split("\\|");
                String[] dates = data[4].trim().split("\\|");

                Medicine medicine = new Medicine(medicineName, minStockLevel, "");
                List<Medicine.Batch> batches = new ArrayList<>();

                for (int i = 0; i < quantities.length; i++) {
                    int quantity = Integer.parseInt(quantities[i]);
                    LocalDate expiryDate = LocalDate.parse(dates[i]);
                    batches.add(medicine.new Batch(quantity, expiryDate));
                }

                medicine.setBatch(batches);
                inventoryMap.put(medicineName, medicine);
            }
            System.out.println("Successfully loaded " + inventoryMap.size() + " medicines");
        } catch (FileNotFoundException e) {
            System.out.println("CSV file not found: " + e.getMessage());
        }
    }

    /**
     * Loads replenishment request data from CSV into requestMap.
     */
    public static void loadRequestsFromCSV() {
        requestMap.clear(); // Clear existing requests first

        try (Scanner scanner = new Scanner(new File(REQUEST_CSV_PATH))) {
            // Debug print
            System.out.println("Loading requests from: " + REQUEST_CSV_PATH);

            scanner.nextLine(); // Skip header
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] data = line.split(",");

                // Debug print
                System.out.println("Processing line: " + line);

                try {
                    int requestID = Integer.parseInt(data[0].trim());
                    String medicineName = data[1].trim();
                    int requestedQuantity = Integer.parseInt(data[2].trim());
                    RequestStatus status = RequestStatus.valueOf(data[3].trim());

                    // Get medicine from inventory
                    Medicine medicine = inventoryMap.get(medicineName);
                    if (medicine == null) {
                        System.out.println("Warning: Medicine " + medicineName + " not found for request " + requestID);
                        continue;
                    }

                    // Create and store request
                    ReplenishmentRequest request = new ReplenishmentRequest(requestID, medicine, requestedQuantity);
                    request.setStatus(status);
                    requestMap.put(requestID, request);

                    // Debug print
                    //System.out.println("Added request: " + request);
                } catch (Exception e) {
                    System.out.println("Error processing request line: " + String.join(",", data));
                    System.out.println("Error details: " + e.getMessage());
                }
            }
            System.out.println("Successfully loaded " + requestMap.size() + " replenishment requests");

            // Debug print map contents
            /*
            if (!requestMap.isEmpty()) {
                System.out.println("Current requests in map:");
                requestMap.forEach((id, req) -> System.out.println(id + ": " + req));
            }
            */

        } catch (FileNotFoundException e) {
            System.out.println("Replenishment requests CSV file not found: " + REQUEST_CSV_PATH);
        } catch (Exception e) {
            System.out.println("Error loading replenishment requests: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Loads appointment data from specified CSV file into appointmentMap.
     * Links appointments with doctors and patients, handles prescriptions.
     *
     * @param filePath path to the appointment CSV file
     */
    private static void loadAppointmentsFromCSV(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String header = reader.readLine(); // Skip header
            String line;

            while ((line = reader.readLine()) != null) {
                // Combine any split lines that might exist
                StringBuilder fullLine = new StringBuilder(line);
                while (line.endsWith("\"") && !line.endsWith("\"\"")) {
                    line = reader.readLine();
                    if (line != null) {
                        fullLine.append("\n").append(line);
                    }
                }

                line = fullLine.toString();
                System.out.println("Raw line: " + line);  // Debug

                try {
                    // Split the line, preserving quoted values
                    List<String> fields = new ArrayList<>();
                    StringBuilder field = new StringBuilder();
                    boolean inQuotes = false;

                    for (int i = 0; i < line.length(); i++) {
                        char c = line.charAt(i);
                        if (c == '"') {
                            inQuotes = !inQuotes;
                        } else if (c == ',' && !inQuotes) {
                            fields.add(field.toString().trim());
                            field = new StringBuilder();
                        } else {
                            field.append(c);
                        }
                    }
                    fields.add(field.toString().trim());

                    // Remove quotes from fields
                    fields = fields.stream()
                            .map(f -> f.replaceAll("^\"|\"$", ""))
                            .collect(Collectors.toList());

                    System.out.println("Parsed fields: " + fields);  // Debug

                    if (fields.size() < 10) {
                        System.out.println("Skipping invalid appointment data: insufficient fields");
                        continue;
                    }

                    // Create appointment
                    String appointmentID = fields.get(0);
                    String patientID = fields.get(1);
                    String doctorID = fields.get(2);

                    User doctorUser = doctorsMap.get(doctorID);
                    User patientUser = patientsMap.get(patientID);

                    if (!(doctorUser instanceof Doctor)) {
                        System.out.println("Invalid doctor for appointment: " + appointmentID);
                        continue;
                    }

                    Doctor doctor = (Doctor) doctorUser;
                    Patient patient = (patientUser instanceof Patient) ? (Patient) patientUser : null;

                    int year = Integer.parseInt(fields.get(3));
                    int month = Integer.parseInt(fields.get(4));
                    int day = Integer.parseInt(fields.get(5));
                    int hour = Integer.parseInt(fields.get(6));
                    int minute = Integer.parseInt(fields.get(7));

                    AppointmentSlot slot = new AppointmentSlot(year, month, day, hour, minute);
                    Appointment appointment = new Appointment(appointmentID, patient, doctor, slot);

                    appointment.setStatus(AppointmentStatus.valueOf(fields.get(8).toUpperCase()));
                    appointment.setIsAvailable(Boolean.parseBoolean(fields.get(9)));

                    // Handle consultation notes
                    String consultationNotes = fields.size() > 10 ? fields.get(10) : "";
                    appointment.setConsultationNotes(consultationNotes);
                    System.out.println("Set consultation notes: " + consultationNotes);

                    // Handle prescriptions
                    if (fields.size() > 11 && !fields.get(11).isEmpty()) {
                        String prescriptionData = fields.get(11);
                        System.out.println("Processing prescription data: " + prescriptionData);

                        String[] prescriptionParts = prescriptionData.split(":");
                        if (prescriptionParts.length == 2) {
                            String medicineName = prescriptionParts[0].trim();
                            int quantity = Integer.parseInt(prescriptionParts[1].trim());

                            Medicine medicine = inventoryMap.get(medicineName);
                            if (medicine != null) {
                                List<Prescription.MedicineSet> medicineSets = new ArrayList<>();
                                medicineSets.add(new Prescription.MedicineSet(medicine, quantity));

                                Prescription prescription = new Prescription(
                                        medicineSets,
                                        doctor.getID(),
                                        patient.getID(),
                                        PrescriptionStatus.PENDING
                                );

                                appointment.setPrescription(prescription);
                                System.out.println("Created prescription: " + prescription);
                            } else {
                                System.out.println("WARNING: Medicine not found in inventory: " + medicineName);
                                System.out.println("Available medicines: " + String.join(", ", inventoryMap.keySet()));
                            }
                        }
                    }

                    if (appointment.getStatus() == AppointmentStatus.COMPLETED && patient != null) {
                        patient.getMedicalRecord().getAppointmentOutcomes().add(appointment.getAppointmentOutcome());
                    }

                    appointmentMap.put(appointmentID, appointment);
                    doctor.addAppointment(appointment);

                    if (patient != null) {
                        List<Appointment> patientAppointments = patient.getAppointments();
                        if (patientAppointments == null) {
                            patientAppointments = new ArrayList<>();
                        }
                        patientAppointments.add(appointment);
                        patient.setAppointments(patientAppointments);
                    }

                } catch (Exception e) {
                    System.out.println("Error processing appointment line: " + line);
                    System.out.println("Error details: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            System.out.println("Successfully loaded " + appointmentMap.size() + " appointments");

        } catch (IOException e) {
            System.out.println("Error reading appointments file: " + e.getMessage());
            e.printStackTrace();
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

        AppointmentSlot slot = appointment.getSlot();
        LocalDateTime dateTime = slot.getDateTime();

        String prescriptions = formatPrescription(appointment.getPrescription());

        sb.append(escapeCSV(appointment.getAppointmentID())).append(",");
        sb.append(appointment.getPatient() != null ? escapeCSV(appointment.getPatient().getID()) : "").append(",");
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
                            String line = String.format("%s,%s,%s,%s,%s,%s,%s,%s",
                                    escapeCSV(patient.getID()),
                                    escapeCSV(patient.getName()),
                                    patient.getDOB().toString(),
                                    escapeCSV(patient.getGender()),
                                    patient.getBloodType().toString(),
                                    escapeCSV(patient.getPhoneNumber()),
                                    escapeCSV(patient.getEmail()),
                                    escapeCSV(patient.getPassword())
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
            allStaff.sort(Comparator.comparing(User::getID));

            // Write staff data with updated passwords
            for (User staff : allStaff) {
                String newPassword = staff.getPassword(); // Get the updated password
                staff.setPassword(newPassword); // Set the updated password back to the staff object
                System.out.print(staff.getPassword());
                System.out.print(staff.getID());

                String role = determineRole(staff);
                String line = String.format("%s,%s,%s,%s,%d,%s",
                        escapeCSV(staff.getID()),
                        escapeCSV(staff.getName()),
                        role,
                        escapeCSV(staff.getGender()),
                        staff.getAge(),
                        escapeCSV(staff.getPassword())
                );
                bw.write(line);
                bw.newLine();
            }

            System.out.println("Successfully saved " + allStaff.size() + " staff members to " + STAFF_CSV_PATH);

        } catch (IOException e) {
            System.out.println("Error saving staff to CSV: " + e.getMessage());
            throw new RuntimeException("Failed to save staff data", e);
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

            // Sort medicines by name and write data
            inventoryMap.values().stream()
                    .sorted(Comparator.comparing(Medicine::getMedicineName))
                    .forEach(medicine -> {
                        try {
                            // Format batch quantities and dates
                            List<Medicine.Batch> batches = medicine.getBatches();

                            // Handle quantities
                            String batchesQuantity = batches.stream()
                                    .map(batch -> String.valueOf(batch.getQuantity()))
                                    .collect(Collectors.joining("|"));

                            // Handle expiry dates
                            String batchesDates = batches.stream()
                                    .map(batch -> batch.getExpirationDate().toString())
                                    .collect(Collectors.joining("|"));

                            // Format the complete line
                            String line = String.format("%s,%d,%d,%s,%s",
                                    escapeCSV(medicine.getMedicineName()),
                                    medicine.getTotalQuantity(),       // Initial stock is total quantity
                                    medicine.getMinStockLevel(),      // Low stock alert level
                                    batchesQuantity,                  // Batch quantities joined by |
                                    batchesDates                      // Batch dates joined by |
                            );

                            // Write the line
                            bw.write(line);
                            bw.newLine();

                        } catch (IOException e) {
                            System.out.println("Error writing medicine " + medicine.getMedicineName() + ": " + e.getMessage());
                        }
                    });

            System.out.println("Successfully saved " + inventoryMap.size() + " medicines to " + INVENTORY_CSV_PATH);

        } catch (IOException e) {
            System.out.println("Error saving inventory to CSV: " + e.getMessage());
            throw new RuntimeException("Failed to save inventory data", e);
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

    /**
     * Saves replenishment request data to CSV file.
     */
    public static void saveRequestsToCSV() {
        try (FileWriter fw = new FileWriter(REQUEST_CSV_PATH);
             BufferedWriter bw = new BufferedWriter(fw)) {

            // Write header
            bw.write(REQUEST_CSV_HEADER);
            bw.newLine();

            // Write requests sorted by ID
            requestMap.values().stream()
                    .sorted(Comparator.comparingInt(ReplenishmentRequest::getRequestID))
                    .forEach(request -> {
                        try {
                            String line = String.format("%d,%s,%d,%s",
                                    request.getRequestID(),
                                    escapeCSV(request.getMedicine().getMedicineName()),
                                    request.getRequestedQuantity(),
                                    request.getStatus()
                            );
                            bw.write(line);
                            bw.newLine();
                        } catch (IOException e) {
                            System.out.println("Error writing request " + request.getRequestID() + ": " + e.getMessage());
                        }
                    });

            System.out.println("Successfully saved " + requestMap.size() + " replenishment requests to " + REQUEST_CSV_PATH);

        } catch (IOException e) {
            System.out.println("Error saving replenishment requests to CSV: " + e.getMessage());
            throw new RuntimeException("Failed to save replenishment request data", e);
        }
    }

    /**
     * Parses a CSV line handling quoted values properly.
     * @param line The CSV line to parse
     * @return List of parsed values
     */
    private static List<String> parseCSVLine(String line) {
        List<String> result = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder currentValue = new StringBuilder();

        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result.add(currentValue.toString());
                currentValue = new StringBuilder();
            } else {
                currentValue.append(c);
            }
        }
        result.add(currentValue.toString());

        return result;
    }

    private static String determineRole(User staff) {
        if (staff instanceof Doctor) return "DOCTOR";
        if (staff instanceof Administrator) return "ADMINISTRATOR";
        if (staff instanceof Pharmacist) return "PHARMACIST";
        return "UNKNOWN";
    }

    public static void updatePassword(User user, String newPassword) {
        user.setPassword(newPassword);

        // Update the user in the appropriate map
        if (user instanceof Doctor) {
            Database.doctorsMap.put(user.getID(), user);
        } else if (user instanceof Administrator) {
            Database.adminsMap.put(user.getID(), user);
        } else if (user instanceof Pharmacist) {
            Database.pharmsMap.put(user.getID(), user);
        } else if (user instanceof Patient) {
            Database.patientsMap.put(user.getID(), user);
        }

        System.out.println("Password updated!");
    }

}
