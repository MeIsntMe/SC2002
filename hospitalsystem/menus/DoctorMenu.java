package hospitalsystem.menus;

import hospitalsystem.HMS;
import hospitalsystem.appointmentcontrol.DoctorAppointmentControl;
import hospitalsystem.model.*;
import hospitalsystem.usercontrol.DoctorUserControl;

/**
 * Represents the menu for doctor users in the Hospital Management System.
 * Provides options for doctors to manage patient records, appointments, and schedules.
 *
 * @author Gracelynn, Leo
 * @version 1.0
 * @since 2024-11-19
 *
 */
public class DoctorMenu implements MenuInterface {
    
    private final Doctor doctor;

    /*
     * Constructor
     * Ensures type-safety via checks
     */
    public DoctorMenu(User currentUser) {
        if (!(currentUser instanceof Doctor)) {
            throw new IllegalArgumentException("User must be a Doctor");
        }
        this.doctor = (Doctor) currentUser;
    }

    @Override
    public void displayMenu() {
        while (true) {
            System.out.println("=========================================");
            System.out.println("Doctor Menu");
            System.out.println("1. View Patient Medical Record");
            System.out.println("2. Update Patient Medical Record");
            System.out.println("3. View Personal Schedule");
            System.out.println("4. Set Availability");
            System.out.println("5. Accept/Decline Appointments");
            System.out.println("6. View Upcoming Appointments");
            System.out.println("7. Record Appointment Outcome");
            System.out.println("8. Logout");
            System.out.print("Enter choice: ");

            try {
                int choice = Integer.parseInt(sc.nextLine());

                switch (choice) {
                    case 1:
                        // View Patient Medical Record
                        DoctorUserControl.handleViewPatientRecord();
                        break;
                    case 2:
                        // Update Patient Medical Record
                        DoctorUserControl.handleUpdatePatientRecord(doctor);
                        break;
                    case 3:
                        // View Personal Schedule
                        DoctorAppointmentControl.displayPersonalSchedule(doctor);
                        break;
                    case 4:
                        // Set Availablity
                        DoctorAppointmentControl.handleSetAvailability(doctor);
                        break;
                    case 5:
                        // Accept/Decline Appointments
                        DoctorAppointmentControl.handleAppointmentRequests(doctor);
                        break;
                    case 6:
                        // View Upcoming Appointments
                        DoctorAppointmentControl.displayUpcomingAppointments(doctor);
                        break;
                    case 7:
                        //Record Appointment Outcomes
                        DoctorAppointmentControl.handleRecordOutcome(doctor);
                        break;
                    case 8:
                        // Logout
                        HMS.logout();
                        return;
                    default:
                        System.out.println("Invalid choice, try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number between 1-8.");
            } catch (Exception e) {
                System.out.println("An error has occurred: " + e.getMessage());
                sc.nextLine();
            }
        }
    }
}
