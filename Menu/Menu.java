package Menu;

import User.User;
import java.util.ArrayList;

public class Menu {
    public static void displayOptions(User user){
        System.err.println("Methods: ");
        int i = 1;
        for (String option:user.getAvailableMethods()){
            System.err.printf("%d: %s", i, option);
            i += 1;
        }
    }

    public static void output(ArrayList<String> information){
        for (String line:information){
            System.out.println(line);
        }
    }
}
