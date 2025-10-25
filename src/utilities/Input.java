package utilities;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Input {
    private static final Scanner scanner = new Scanner(System.in);

    public static String getString(){
        String user_input = scanner.nextLine().trim();
        return user_input;
    }

    public static String getOptionalString() {
        String user_input = scanner.nextLine().trim();
        if (user_input.isEmpty()) {
            return "";
        } else {
            return user_input;
        }
    }

    public static int getInt(){
        while (true){
            try{
                int user_input = scanner.nextInt();
                scanner.nextLine();
                return user_input;
            } catch (InputMismatchException e){
                System.out.println("Неправильно число!");
            }
        }
    }

    public static Integer getOptionalYear(){
        while (true){
            String user_input = scanner.nextLine().trim();
            if (user_input.isEmpty()){
                return null;
            }
            try{
                int num = Integer.parseInt(user_input);
                if (0 < num && num < java.time.Year.now().getValue()){
                    return num;
                } else {
                    System.out.println("Год должен быть в диапазоне от 0 до + " + java.time.Year.now().getValue() + "!");
                }
            } catch (NumberFormatException e){
                System.out.println("Неправильное число!");
            }
        }
    }

    public static int getYear(){
        while (true) {
            try {
                int user_input = getInt();
                if (0 < user_input && user_input < java.time.Year.now().getValue()) {
                    return user_input;
                } else {
                    System.out.println("Год должен быть в диапазоне от 0 до + " + java.time.Year.now().getValue() + "!");
                }
            } catch (InputMismatchException e) {
                System.out.println("Неправильное число!");
            }
        }
    }

    public static void close(){
        scanner.close();
    }
}
