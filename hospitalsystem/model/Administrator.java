package hospitalsystem.model;

import hospitalsystem.enums.UserType;

public class Administrator extends User {

    private int age;
    private UserType role; 

    public Administrator(String staffID, String name, String gender, int age, String password) {
        super(staffID, name, gender, password);
        this.role = UserType.ADMINISTRATOR;
        this.age = age; 
    }
}
