package hospitalsystem.model;

public abstract class User {
    private final String userID;
    private String password;
    private String name; 
    private String gender; 
    private int age;

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
    public String getPassword() {return password;}
    public int getAge() {return age;}    

    public void setName(String name) {this.name = name; }
    public void setGender(String gender) {this.gender = gender;}
    public void setPassword(String password) {this.password = password;}
    public void setAge(int age) {this.age = age;}
}
