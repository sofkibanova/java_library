import java.util.Objects;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class Chef implements Runnable {
    private final String name;
    private final BlockingQueue<Order> orderQueue;
    private final OrderManager orderManager;
    private final RestaurantLogger logger;
    private volatile boolean isWorking;
    private final Random random;
    private int ordersCooked;
    private int cookingDelays;
    private long totalWorkTime;

    public Chef(String name, BlockingQueue<Order> orderQueue,
                OrderManager orderManager, RestaurantLogger logger) {
        this.name = Objects.requireNonNull(name);
        this.orderQueue = Objects.requireNonNull(orderQueue);
        this.orderManager = Objects.requireNonNull(orderManager);
        this.logger = Objects.requireNonNull(logger);
        this.random = new Random();
        this.isWorking = true;
        this.ordersCooked = 0;
        this.cookingDelays = 0;
        this.totalWorkTime = 0;
    }

    @Override
    public void run() {
        Thread.currentThread().setName(name);

        logger.logInfo("начал работу");

        try {
            while (isWorking && !Thread.currentThread().isInterrupted()) {
                processOrder();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.logInfo("был прерван");
        } finally {
            logger.logInfo("закончил работу. " +
                    "Приготовлено: " + ordersCooked +
                    ", Задержек: " + cookingDelays +
                    ", Среднее время работы: " + getAverageWorkTime() + "мс");
        }
    }

    private void processOrder() throws InterruptedException {
        try {
            Order order = orderQueue.poll(100, TimeUnit.MILLISECONDS);
            if (order != null) {
                cookOrder(order);
            }
        } catch (Exception e) {
            logger.logError("Ошибка при обработке заказа", e);
        }
    }

    private void cookOrder(Order order) throws InterruptedException {
        order.setStatus(OrderStatus.COOKING);

        int queueSize = orderQueue.size();
        int slowdown = calculateSlowdown(queueSize);// добавляем время, если кухня заполнена

        int actualCookTime = order.getCookingTimeMs() + slowdown;

        if (slowdown > 0) {
            logger.logWarning("Загрузка кухни! Готовлю заказ №" + order.getId() +
                    " медленнее (+" + slowdown + "мс). Очередь: " + queueSize);
            cookingDelays++;
        }

        logger.logInfo("Готовлю заказ №" + order.getId() + ": " + order.getDishName() +
                " (время: " + actualCookTime + "мс, очередь: " + queueSize + ")");
        logger.logOrder(order);

        Thread.sleep(actualCookTime);
        totalWorkTime += actualCookTime;

        order.setStatus(OrderStatus.READY);
        orderManager.toReadyOrders(order);
        ordersCooked++;

        if (orderManager.readyOrdersCount() > 3) {
            logger.logWarning("Накопление готовых заказов: " +
                    orderManager.readyOrdersCount() +
                    " (официанты должны забрать)");
        }

        logger.logInfo("Приготовил заказ №" + order.getId());
        logger.logOrder(order);
    }

    private int calculateSlowdown(int queueSize) {
        //чем больше очередь, тем больше замедление
        if (queueSize > 15) return 5000;
        if (queueSize > 10) return 3000;
        if (queueSize > 5) return 1000;
        return 0;
    }

    public void stop() { isWorking = false; }
    public String getName() { return name; }
    public int getOrdersCooked() { return ordersCooked; }
    public int getCookingDelays() { return cookingDelays; }
    public long getTotalWorkTime() { return totalWorkTime; }
    public boolean isWorking() { return isWorking; }
    public double getAverageWorkTime() { return ordersCooked > 0 ? (double) totalWorkTime / ordersCooked : 0; }
}
