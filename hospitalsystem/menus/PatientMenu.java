package hospitalsystem.menus;

import hospitalsystem.HMS;
import hospitalsystem.appointmentcontrol.*;
import hospitalsystem.model.*;
import hospitalsystem.usercontrol.PatientUserControl;
/**
 * Manages patient interface for the hospital system.
 * Allows patients to do carry out actions such as viewing medical record,
 * updating personal information, and managing appointments. 
 *
 *
 * @author Gracelynn
 * @version 1.0
 * @since 2024-11-19
 */
public class PatientMenu implements MenuInterface {
    
    // Instance variables 
    private final Patient patient;
    private int choice;

    /*
     * Constructor
     * Ensures type-safety via checks
     */
    public PatientMenu(User currentUser) { 
        if (!(currentUser instanceof Patient)) {
            throw new IllegalArgumentException("User must be a Patient");
        }
        this.patient = (Patient) currentUser;
    }
    
    @Override
    public void displayMenu(){
        while (true) {
            System.out.println("=========================================");
            System.out.println("Patient Menu: ");
            System.out.println("1. View Medical Record");
            System.out.println("2. Update Personal Information ");
            System.out.println("3. View Available Slots for a Doctor ");
            System.out.println("4. Schedule Appointment with a Doctor ");
            System.out.println("5. Reschedule Appointment ");
            System.out.println("6. Cancel Appointment ");
            System.out.println("7. View Scheduled Appointments ");
            System.out.println("8. Display Past Appointment Outcomes ");
            System.out.println("9. Logout ");
            System.out.print("Enter choice (1-9): ");

            try {
                choice = sc.nextInt();
                sc.nextLine();
                switch (choice){
                    case 1:
                        //View Medical Record
                        PatientUserControl.displayPatientDetails(this.patient);
                        break;
                    case 2:
                        //Update Personal Information
                        PatientUserControl.updatePatientDetails(this.patient);
                        break;
                    case 3:
                        // View available appointment slots of a specific doctor
                        PatientAppointmentControl.handleViewAppointmentSlots();
                        break;
                    case 4:
                        //Scheduling an Appointment with a specific doctor
                        PatientAppointmentControl.handleScheduleAppointment(patient);
                        break;
                    case 5:
                        //Rescheduling
                        PatientAppointmentControl.handleRescheduleAppointment(patient);
                        break;               
                    case 6:
                        //Cancal appointment
                        PatientAppointmentControl.handleCancelAppointment(patient);
                        break;
                    case 7:
                        //View scheduled appointments
                        PatientAppointmentControl.handleViewScheduledAppointments(patient);
                        break;
                    case 8:
                        //Display past appointment outcomes
                        System.out.println(AppointmentControl.getAppointmentOutcomesString(patient, ""));
                        break;
                    case 9:
                        //logout
                        HMS.logout();
                        return;
                    default:
                        System.out.println("Invalid choice, try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number between 1-9.");
            } catch (Exception e) {
                System.out.println("An error has occurred: " + e.getMessage());
                sc.nextLine();
            }
        }
    }
}
