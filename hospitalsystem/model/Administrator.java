package hospitalsystem.model;

public class Administrator extends User {
    private int age;

    public Administrator(String staffID, String name, String gender, int age, String password) {
        super(staffID, name, gender, password);
        this.age = age; 
    }

    // Getter and Setter methods
    public int getAge() {return age;}
    public void setAge(int age ) {this.age = age; }

}
