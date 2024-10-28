package User;
import java.util.Scanner;
import javax.naming.AuthenticationException;
import Patient.Patient;

public abstract class User {
    static private String hospitalID;
    static private String password;

    static final public User login(Scanner scanner) throws AuthenticationException{
        System.err.print("Please input your ID:");
        hospitalID = scanner.nextLine();
        if (!checkExists(hospitalID)){
            throw new AuthenticationException("No such user exists");
        }
        System.err.print("Please input your password:");
        password = scanner.nextLine();
        if (!(password.equals(getPassword(hospitalID)))){
            throw new AuthenticationException("Wrong password");
        }
        return createUser(hospitalID);
    }

    static private boolean checkExists(String hospitalID){
        //retrieve from csv file
        boolean exists = true;
        return exists;
    }

    static private String getPassword(String hospitalID){
        //retrieve from csv if exists, else return default password
        return "password";
    }
    
    static private User createUser(String hospitalID, String name, String contactInfo){
        //retrieve role
        return new User();
    }
}
