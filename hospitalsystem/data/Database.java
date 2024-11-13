package hospitalsystem.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import hospitalsystem.enums.AppointmentStatus;
import hospitalsystem.enums.PrescriptionStatus;
import hospitalsystem.enums.UserType;
import hospitalsystem.model.Administrator;
import hospitalsystem.model.Appointment;
import hospitalsystem.model.Appointment.AppointmentSlot;
import hospitalsystem.model.Doctor;
import hospitalsystem.model.Medicine;
import hospitalsystem.model.Patient;
import hospitalsystem.model.Pharmacist;
import hospitalsystem.model.ReplenishmentRequest;
import hospitalsystem.model.User;

public class Database {

    // HashMaps

    public static Map<String, User> patientsMap = new HashMap<>();  //userID -> Patient
    public static Map<String, User> doctorsMap = new HashMap<>();   //userID -> Doctor
    public static Map<String, User> adminsMap = new HashMap<>();    //userID -> Administrator
    public static Map<String, User> pharmsMap = new HashMap<>();    //userID -> Pharmacist

    public static Map<String, Medicine> inventoryMap = new HashMap<>();             // medicineName -> Medicine
    public static Map<String, ReplenishmentRequest> requestMap = new HashMap<>();   // medicineName -> ReplenishmentRequest

    public static HashMap<String, Appointment> appointmentMap = new HashMap<>();    // AppointmentID -> Appointment
  
    // Methods to load from CSV into Hashmap 

    public static void loadPatientfromCSV (String filePath) {
        try (Scanner scanner = new Scanner(new File(filePath))) {
            scanner.nextLine(); // Skip the first line
            while (scanner.hasNextLine()) {
                String patientData[] = scanner.nextLine().split(",");
                String patientID = patientData[0].trim();
                String name = patientData[1].trim();
                LocalDate DOB = LocalDate.parse(patientData[2].trim());
                String gender = patientData[3].trim().toLowerCase();
                String bloodType = patientData[4].trim();
                String email = patientData[5].trim();
                String password = patientData.length > 6 ? patientData[6].trim() : "password"; 

                Patient patient = new Patient(patientID, name, DOB, gender, bloodType, email, password); 
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

}
