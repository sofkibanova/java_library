import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class RestaurantMonitor implements Runnable {
    private final BlockingQueue<Order> kitchenQueue;
    private final Kitchen kitchen;
    private final OrderManager orderManager;
    private volatile boolean monitoring;
    private final RestaurantLogger logger;
    private final int queueCapacity;

    public RestaurantMonitor(BlockingQueue<Order> kitchenQueue, Kitchen kitchen,
                             OrderManager orderManager, RestaurantLogger logger,
                             int queueCapacity) {
        this.kitchenQueue = kitchenQueue;
        this.kitchen = kitchen;
        this.orderManager = orderManager;
        this.logger = logger;
        this.queueCapacity = queueCapacity;
        this.monitoring = true;
    }

    @Override
    public void run() {
        System.out.println("Монитор ресторана запущен");

        try {
            while (monitoring && !Thread.currentThread().isInterrupted()) {
                displayStatus();
                TimeUnit.SECONDS.sleep(3);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Монитор ресторана прерван");
        }

        System.out.println("Монитор ресторана остановлен");
    }

    private void displayStatus() {
        int queueSize = kitchenQueue.size();
        int readyOrders = orderManager.readyOrdersCount();
        int activeChefs = kitchen.getActiveChefCount();
        int queuePercentage = (int) ((double) queueSize / queueCapacity * 100);
        logger.logStatus("Очередь на кухне: " + queueSize + "/" + queueCapacity +
                " (" + queuePercentage + "%)");
        logger.logStatus("Готовых заказов: " + readyOrders);
        logger.logStatus("Активных поваров: " + activeChefs + "/" + kitchen.getChefs().size());

        if (queuePercentage >= 90) {
            logger.logStatus("Очередь почти заполнена.");
        } else if (queuePercentage >= 70) {
            logger.logStatus("Высокая загрузка очереди. Возможны задержки.");
        }

        if (readyOrders > 5) {
            logger.logStatus("Накопление готовых заказов: " + readyOrders);
        }

        if (queueSize > 0 && activeChefs == 0) {
            logger.logStatus("Повара заняты. Заказы ждут в очереди.");
        }

        if (queueSize > 0) {
            double avgWaitTime = (double) queueSize * 5000 / Math.max(activeChefs, 1);
            logger.logStatus("Примерное время ожидания: " +
                    (int) avgWaitTime / 1000 + " секунд");
        }

    }

    public void stop() { monitoring = false; }
}
