package hospitalsystem.model;

public class Pharmacist extends User {

    private int age; 

    // Constructor
    public Pharmacist(String userId, String name, String gender, int age, String password) {
        super(userId, name, gender, password);
        this.age = age;
    }

    // Getter and Setter methods
    public int getAge() {return age;}
    public void setAge(int age ) {this.age = age;}
}
