//интерфейс для системы логирования в ресторане
public interface RestaurantLogger {
    void logInfo(String message);
    void logError(String message, Throwable error);
    void logOrder(Order order);
    void logWarning(String message);
    void logStatus(String message);
}
