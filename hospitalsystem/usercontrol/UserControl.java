package hospitalsystem.usercontrol;

import hospitalsystem.appointmentcontrol.AppointmentControl;
import hospitalsystem.model.MedicalRecord;
import hospitalsystem.model.Patient;
import java.util.Scanner;

/**
 * Abstract base class for user management operations in the hospital system.
 * Provides common functionality for managing different types of users including
 * staff and patients.
 *
 * @author Your Name
 * @version 1.0
 * @since 2024-11-19
 */
public class UserControl{

    static final Scanner sc = new Scanner(System.in);
    /**
     * Generates a formatted string representation of a patient's medical record.
     * Includes personal information, medical history, and appointment outcomes.
     *
     * @param patient Patient whose medical record is to be formatted
     * @return Formatted string containing medical record information
     */
    public static String getMedicalRecordString(Patient patient){
        MedicalRecord mr = patient.getMedicalRecord();
        StringBuilder sb = new StringBuilder();
        sb.append("\n  ID: ").append(mr.getID());
        sb.append("\n  Name: ").append(mr.getName());
        sb.append("\n  Date of Birth: ").append(mr.getDOB());
        sb.append("\n  Gender: ").append(mr.getGender());
        sb.append("\n  Phone Number: ").append(mr.getPhoneNumber());
        sb.append("\n  Email Address: ").append(mr.getEmail());
        sb.append("\n  Blood Type: ").append(mr.getBloodType());   
        sb.append("\n").append(AppointmentControl.getAppointmentOutcomesString(patient, "  "));

        return sb.toString();
    }

}