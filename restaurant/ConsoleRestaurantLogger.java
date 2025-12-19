public class ConsoleRestaurantLogger implements RestaurantLogger {
    @Override
    public void logInfo(String message) {
        System.out.println(Thread.currentThread().getName() + ": " + message);
    }

    @Override
    public void logError(String message, Throwable error) {
        System.err.println(Thread.currentThread().getName() + ": ERROR: " +
                message + " - " + error.getMessage());
    }

    @Override
    public void logOrder(Order order) {
        System.out.println(Thread.currentThread().getName() + ": " + order +
                " - " + order.getStatus().getDescription());
    }

    @Override
    public void logWarning(String message) {
        System.out.println(Thread.currentThread().getName() + ": WARNING: " + message);
    }

    @Override
    public void logStatus(String message) {
        System.out.println(message);
    }
}
