package hospitalsystem.model;

/**
 * Represents a user in the Hospital Management System.
 * This is an abstract class that serves as a base for specific user types.
 *
 * @author An Xian
 * @version 1.0
 * @since 2024-11-19
 */
public abstract class User {
    /**
     * The unique identifier of the user.
     */
    private final String userID;

    /**
     * The password of the user.
     */
    private String password;

    /**
     * The name of the user.
     */
    private String name;

    /**
     * The gender of the user.
     */
    private String gender;

    /**
     * The age of the user.
     */
    private int age;

    /**
     * Constructs a User object with the given parameters.
     *
     * @param userID The unique identifier of the user.
     * @param name The name of the user.
     * @param age The age of the user.
     * @param gender The gender of the user.
     * @param password The password of the user.
     */
    User(String userID, String name, int age, String gender, String password) {
        this.userID = userID;
        this.name = name; 
        this.gender = gender; 
        this.password = password;
        this.age = age;
    }
    
    public String getID() {return userID;}
    public String getName() {return name;}   
    public String getGender() {return gender;}    
    public String getPassword() {System.out.println("Method called and errored");return password;}
    public int getAge() {return age;}    

    public void setName(String name) {this.name = name; }
    public void setGender(String gender) {this.gender = gender;}
    public void setPassword(String password) {this.password = password;}
    public void setAge(int age) {this.age = age;}
}
