package hospitalsystem.controllers;

import java.util.List;

public class PatientControl {
    private static List<String> listOfMethods = List.of("test", "test2");

    static void displayMenu(){
        int i = 1;
        System.err.println("Available Methods: ");
        for (String methodString: listOfMethods) {
            System.err.printf("%d: %s\n", i, methodString);
            i++;
        }
    }

    

}
