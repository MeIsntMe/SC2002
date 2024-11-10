package hospitalsystem.model;

public abstract class User {
    private String userID;
    private String password;
    private String name; 
    private String gender; 

    User(String userID, String name, String gender, String password) {
        this.userID = userID;
        this.name = name; 
        this.gender = gender; 
        this.password = password;
    }
    
    public String getID() {return userID;}
    public String getName() {return name;}   
    public String getGender() {return gender;}    
    public String getPassword() {return password;}    

    public void setName(String name) {this.name = name; }
    public void setGender(String gender) {this.gender = gender;}
    public void setPassword(String password) {this.password = password;}
}
