package hospitalsystem.menus;
import hospitalsystem.HMS;
import hospitalsystem.enums.UserType;
import hospitalsystem.model.*;


public class Test {
    public static void main(String[] args) {
        //testing for menu 
        Administrator p = new Administrator("111", "bob", 0, null, null);
        MenuInterface control = new AdminMenu(p); 

        HMS.loadRequiredData(UserType.ADMINISTRATOR);

        if (control != null) 
            control.displayMenu();
        
        
    }
}
