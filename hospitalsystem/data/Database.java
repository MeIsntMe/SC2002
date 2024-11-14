package hospitalsystem.data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

import hospitalsystem.enums.AppointmentStatus;
import hospitalsystem.enums.BloodType;
import hospitalsystem.enums.PrescriptionStatus;
import hospitalsystem.enums.UserType;
import hospitalsystem.model.*;
import hospitalsystem.model.Appointment.AppointmentSlot;

public class Database {

    // HashMaps

    public static Map<String, User> patientsMap = new HashMap<>();  //userID -> Patient
    public static Map<String, User> doctorsMap = new HashMap<>();   //userID -> Doctor
    public static Map<String, User> adminsMap = new HashMap<>();    //userID -> Administrator
    public static Map<String, User> pharmsMap = new HashMap<>();    //userID -> Pharmacist

    public static Map<String, Medicine> inventoryMap = new HashMap<>();             // medicineName -> Medicine
    public static Map<String, ReplenishmentRequest> requestMap = new HashMap<>();   // medicineName -> ReplenishmentRequest

    public static HashMap<String, Appointment> appointmentMap = new HashMap<>();    // AppointmentID -> Appointment
    
    // CSV Variables
    private static final String APPOINTMENT_CSV_HEADER = "AppointmentID,PatientID,DoctorID,Year,Month,Day,Hour,Minute,Status,IsAvailable,ConsultationNotes,Prescriptions";
    private static final String APPOINTMENT_CSV_PATH = "hospitalsystem/data/Appointment.csv";

    private static final String PATIENT_CSV_HEADER = "Patient ID,Name,Date of Birth,Gender,Blood Type,Contact Information";
    private static final String PATIENT_CSV_PATH = "hospitalsystem/data/Patient_List.csv";

    private static final String STAFF_CSV_HEADER = "Staff ID,Name,Role,Gender,Age";
    private static final String STAFF_CSV_PATH = "hospitalsystem/data/Staff_List.csv";

    private static final String INVENTORY_CSV_HEADER = "Medicine Name,Initial Stock,Low Stock Level Alert";
    private static final String INVENTORY_CSV_PATH = "hospitalsystem/data/Medicine_List.csv";
    // Methods to load from CSV into Hashmap 

    public static void loadPatientfromCSV (String filePath) {
        try (Scanner scanner = new Scanner(new File(filePath))) {
            scanner.nextLine(); // Skip the first line
            while (scanner.hasNextLine()) {
                String patientData[] = scanner.nextLine().split(",");
                String patientID = patientData[0].trim();
                String name = patientData[1].trim();
                LocalDate DOB = LocalDate.parse(patientData[2].trim());
                int age = Integer.valueOf(patientData[3].trim());
                String gender = patientData[4].trim().toLowerCase();
                BloodType bloodType = BloodType.valueOf(patientData[5].trim());
                String email = patientData[6].trim();
                String password = patientData.length > 6 ? patientData[7].trim() : "password"; 

                //have to change
                Patient patient = new Patient(patientID, name, phoneNumber, DOB, age, gender, bloodType, email, password); 
                patientsMap.put(patientID, patient);
            }
        } catch (FileNotFoundException e) {
            System.out.println("An error has occurred\n" + e.getMessage());
        }
    }

    public static void loadStaffFromCSV(String filePath) {
        try (Scanner scanner = new Scanner(new File(filePath))) {
            scanner.nextLine(); // Skip the first line
            while (scanner.hasNextLine()) {
                String staffData[] = scanner.nextLine().split(",");
                String staffID = staffData[0].trim();
                String name = staffData[1].trim();
                UserType role = UserType.valueOf(staffData[2].trim().toUpperCase());
                String gender = staffData[3].trim();
                int age = Integer.valueOf(staffData[4].trim());
                String password = staffData.length > 5 ? staffData[5].trim() : "password"; 

                switch (role) {
                    case DOCTOR: 
                        Doctor doc = new Doctor(staffID, name, gender, age, password);
                        doctorsMap.put(staffID, doc);
                        break; 
                    case ADMINISTRATOR: 
                        Administrator admin = new Administrator(staffID, name, gender, age, password);
                        adminsMap.put(staffID, admin);
                        break; 
                    case PHARMACIST: 
                        Pharmacist pharm = new Pharmacist(staffID, name, gender, age, password);
                        pharmsMap.put(staffID, pharm);
                        break;
                    case PATIENT:
                        break; //csv does not contain patient
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("An error has occurred\n" + e.getMessage());
        }
    }

    public static void loadInventoryFromCSV(String filePath) {
        try (Scanner scanner = new Scanner(new File(filePath))) {
            scanner.nextLine(); // Skip the header line if there is one
            while (scanner.hasNextLine()) {
                String[] inventoryData = scanner.nextLine().split(",");
                String medicineName = inventoryData[0].trim();
                int initialStock = Integer.parseInt(inventoryData[1].trim());
                int lowStockAlert = Integer.parseInt(inventoryData[2].trim());
                LocalDate expirationDate = LocalDate.parse(inventoryData[3].trim());

                // Check if the medicine exists; if not, create a new one
                Medicine medicine = inventoryMap.get(medicineName);
                if (medicine == null) {
                    medicine = new Medicine(medicineName, lowStockAlert);
                    inventoryMap.put(medicineName, medicine);
                }
                
                // Add batch to the medicine
                medicine.addBatch(initialStock, expirationDate);
            }
            System.out.println("Inventory loaded successfully from CSV.");
        } catch (FileNotFoundException e) {
            System.out.println("CSV file not found: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Error parsing number from CSV: " + e.getMessage());
        }
    }

    public static void loadAppointmentsFromCSV(String filePath) {
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

    private static String formatPrescription(Prescription prescription) {
        if (prescription == null || prescription.getMedicineList().isEmpty()) {
            return "";
        }

        return prescription.getMedicineList().entrySet().stream()
                .map(entry -> entry.getKey().getMedicineName() + ":" + entry.getValue())
                .collect(Collectors.joining(";"));
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
                            int totalStock = medicine.getBatches().stream()
                                    .mapToInt(batch -> batch.getQuantity())
                                    .sum();

                            String line = String.format("%s,%d,%d",
                                    escapeCSV(medicine.getMedicineName()),
                                    totalStock,
                                    medicine.getLowStockAlert()
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
}
