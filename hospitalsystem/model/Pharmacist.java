package hospitalsystem.model;

/**
 * Represents a pharmacist in the Hospital Management System.
 *
 * @author Shaivi
 * @version 1.0
 * @since 2024-11-19
 */
public class Pharmacist extends User {
    /**
     * Constructs a Pharmacist object with the given parameters.
     *
     * @param userId The unique identifier of the pharmacist.
     * @param name The name of the pharmacist.
     * @param age The age of the pharmacist.
     * @param gender The gender of the pharmacist.
     * @param password The password of the pharmacist.
     */
    public Pharmacist(String userId, String name, int age, String gender, String password) {
        super(userId, name, age, gender, password);
    }
}
