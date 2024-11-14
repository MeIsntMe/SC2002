package hospitalsystem.usercontrol;

import hospitalsystem.appointmentcontrol.DoctorAppointmentControl;
import hospitalsystem.data.Database;
import hospitalsystem.model.*;
import hospitalsystem.enums.*;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class DoctorUserControl extends UserControl {

    Scanner sc = new Scanner(System.in);

    // Display patient madical records
    public void displayUserDetails() {

        // Find Patient by ID
        System.out.print("Enter Patient ID: ");
        String patientId = sc.nextLine();
        Patient patient = findPatientById(patientId);  
        if (patient == null) return;

        // Display Medical Record 
        System.out.println("Medical Record for Patient: " + patient.getName());
        System.out.println("Patient ID: " + patient.getID());
        System.out.println("Date of Birth: " + patient.getDOB());
        System.out.println("Age: " + patient.getAge());
        System.out.println("Gender: " + patient.getGender());
        System.out.println("Blood Type: " + patient.getBloodType());
        List<Appointment> patientAppointments = getPatientAppointments(patient); //change this: use Patient's appointment getting method  already has own list of appointments 

        if (patientAppointments.isEmpty()) {
            System.out.println("No appointments found for the patient.");
        } else {
            System.out.println("\nAppointment History:");
            for (Appointment apt : patientAppointments) {
                System.out.println("Appointment ID: " + apt.getAppointmentID());
                System.out.println("Doctor: " + apt.getDoctor().getName());
                System.out.println("Date: " + apt.getSlot().getDateTime().toLocalDate());
                System.out.println("Time: " + apt.getSlot().getDateTime().toLocalTime());
                System.out.println("Status: " + apt.getStatus());
                System.out.println("Consultation Notes: " + apt.getConsultationNotes());

                System.out.println("Prescriptions:");
                List<Prescription> prescriptions = apt.getPrescriptions();
                if (prescriptions.isEmpty()) {
                    System.out.println("No prescriptions found for this appointment.");
                } else {
                    for (Prescription prescription : prescriptions) {
                        System.out.println("- Medicine: " + prescription.getMedicineList().keySet());
                        System.out.println("  Quantity: " + prescription.getMedicineList().values());
                        System.out.println("  Status: " + prescription.getStatus());
                    }
                }
                System.out.println("----------------------------------------");
            }
        }
    }

    public static Patient findPatientById(String patientId) {
        Patient patient = (Patient) Database.patientsMap.get(patientId);
        if (patient != null) {
            return patient;
        } else {
            System.out.println("Patient not found.");
            return null;
        }
    }

    // Update patient medical record 
    public void updateUserDetails() {
        System.out.print("Enter Patient ID: ");
        String patientId = sc.nextLine();
        Patient patient = DoctorUserControl.findPatientById(patientId);
        if (patient == null) return;

        // Enter details of new appointment
        System.out.println("Enter consultation notes (press Enter twice to finish):");
        StringBuilder notes = new StringBuilder();
        String line;
        while (!(line = sc.nextLine()).isEmpty()) {
            notes.append(line).append("\n");
        }
        List<Prescription> prescriptions = new ArrayList<>();
        while (true) {
            System.out.print("Add prescription? (y/n): ");
            if (!sc.nextLine().toLowerCase().startsWith("y")) break;

            System.out.print("Enter medication name: ");
            String medication = sc.nextLine();

            System.out.print("Enter dosage: ");
            int dosage = Integer.parseInt(sc.nextLine());

            Prescription prescription = new Prescription(medication, doctor.getID(), patient.getID(), dosage, PrescriptionStatus.PENDING);
            prescriptions.add(prescription);
        }

        // Update medical record
        DoctorUserControl.updatePatientRecord(patient, doctor, notes.toString(), prescriptions);
        Database.saveAppointmentsToCSV(); // Save changes to CSV
    }

    //move everything into DoctorAppointmentControl 
    private static List<Appointment> getPatientAppointments(Patient patient) {
        return Database.appointmentMap.values().stream()
                .filter(apt -> apt.getPatient().getID().equals(patient.getID()))
                .sorted(Comparator.comparing(apt -> apt.getSlot().getDateTime()))
                .collect(Collectors.toList());
    }

    public static void updatePatientRecord(Patient patient, Doctor doctor, String notes, List<Prescription> prescriptions) {
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

        appointment.setPrescriptions(prescriptions);
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
        List<Appointment.AppointmentSlot> slots = DoctorAppointmentControl.generateWeeklySlots(doctor);

        // Find the maximum existing appointment ID
        int maxID = Database.appointmentMap.keySet().stream()
                .filter(id -> id.startsWith("APT"))
                .mapToInt(id -> Integer.parseInt(id.substring(3)))
                .max()
                .orElse(0);

        // Create new appointments for each generated slot and add them to the database and doctor's appointments
        for (Appointment.AppointmentSlot slot : slots) {
            String appointmentID = generateAppointmentID(++maxID);
            Appointment appointment = new Appointment(appointmentID, null, doctor, slot);
            appointment.setIsAvailable(true);
            appointment.setStatus(AppointmentStatus.PENDING);

            Database.appointmentMap.put(appointmentID, appointment);
            doctor.addAppointment(appointment);
        }

        Database.saveAppointmentsToCSV();
        System.out.println("Generated " + slots.size() + " slots for next week.");
    }

    //Helper for generateNextWeekSlots
    private static String generateAppointmentID(int id) {
        return "APT" + String.format("%03d", id);
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
        System.out.println("\nAvailable Slots (PENDING, CANCELLED):");
        for (int i = 0; i < slots.size(); i++) {
            Appointment apt = slots.get(i);
            if (apt.getStatus() == AppointmentStatus.PENDING || apt.getStatus() == AppointmentStatus.CANCELLED) {
                System.out.printf("%d. %s - Status: %s\n", i + 1, apt.getSlot().toString(), apt.getStatus());
            }
        }
    }

    public static void displayUnavailableSlots(List<Appointment> slots) {
        System.out.println("\nUnavailable Slots (UNAVAILABLE, BOOKED):");
        for (int i = 0; i < slots.size(); i++) {
            Appointment apt = slots.get(i);
            if (apt.getStatus() == AppointmentStatus.UNAVAILABLE || apt.getStatus() == AppointmentStatus.BOOKED) {
                System.out.printf("%d. %s - Status: %s\n", i + 1, apt.getSlot().toString(), apt.getStatus());
            }
        }
    }

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

    public static void displayBookedAppointments(List<Appointment> appointments) {
        System.out.println("\nBooked Appointments (BOOKED):");
        for (int i = 0; i < appointments.size(); i++) {
            Appointment apt = appointments.get(i);
            if (apt.getStatus() == AppointmentStatus.BOOKED) {
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

    private static List<Appointment> getUpcomingAppointments(Doctor doctor) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        return Database.appointmentMap.values().stream()
                .filter(apt -> apt.getDoctor().getID().equals(doctor.getID()))
                .filter(apt -> apt.getStatus() == AppointmentStatus.BOOKED)
                .filter(apt -> apt.getSlot().getDateTime().isAfter(currentDateTime))
                .sorted(Comparator.comparing(apt -> apt.getSlot().getDateTime()))
                .collect(Collectors.toList());
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

    public static void recordOutcome(Appointment appointment, String notes, List<Prescription> prescriptions) {
        appointment.setConsultationNotes(notes);
        appointment.setPrescriptions(prescriptions);
        appointment.setStatus(AppointmentStatus.COMPLETED);
        Database.appointmentMap.put(appointment.getAppointmentID(), appointment);
        System.out.println("Appointment outcome recorded successfully.");
    }
}
