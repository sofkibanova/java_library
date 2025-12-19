public class Main {
    public static void main(String[] args) {
        Restaurant restaurant = new Restaurant("Пинчи", 5, 2, 5);
        try {
            restaurant.open();
            Thread.sleep(45000);

            restaurant.close();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Работа прервана");
            restaurant.emergencyClose();
        }
    }
}
