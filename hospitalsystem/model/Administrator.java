package hospitalsystem.model;

/**
 * Represents an administrator in the Hospital Management System.
 * An administrator has access to manage staff, patients, appointments, and inventory.
 *
 * @author Gracelynn
 * @version 1.0
 * @since 2024-11-19
 */
public class Administrator extends User {

    /**
     * Constructs an Administrator object with the given parameters.
     *
     * @param staffID The unique identifier of the administrator.
     * @param name The name of the administrator.
     * @param age The age of the administrator.
     * @param gender The gender of the administrator.
     * @param password The password of the administrator.
     */
    public Administrator(String staffID, String name, int age, String gender, String password) {
        super(staffID, name, age, gender, password);
    }
    
}
