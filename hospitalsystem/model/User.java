package hospitalsystem.model;

public abstract class User {
    private static String userID;
    private static String password;
    private static String name; 
    private static String gender; 

    User(String userID, String name, String gender, String password) {
        this.userID = userID;
        this.name = name; 
        this.gender = gender; 
        this.password = password;
    }

    public String getID() {return userID;}
    public String getPassword() {return password;}    
    public String getName() {return name;}   
    public String getGender() {return gender;}

    //do the set stuff also
}
