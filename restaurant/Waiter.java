import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class Waiter implements Runnable {
    private static final List<String> DISHES = List.of(
            "Стейк", "Паста Карбонара", "Салат Цезарь", "Борщ",
            "Пицца Маргарита", "Роллы Филадельфия", "Паста Болоньезе", "Суп-пюре куриный",
            "Салат Оливье", "Рыба на пару", "Тирамису", "Чизкейк"
    );

    private final String name;
    private final BlockingQueue<Order> kitchenQueue;
    private final OrderManager orderManager;
    private final RestaurantLogger logger;
    private volatile boolean isWorking;
    private final Random random;
    private int ordersProcessed;
    private long totalWaitTime;
    private int blockedQueueCount;

    public Waiter(String name, BlockingQueue<Order> kitchenQueue,
                  OrderManager orderManager, RestaurantLogger logger) {
        this.name = Objects.requireNonNull(name);
        this.kitchenQueue = Objects.requireNonNull(kitchenQueue);
        this.orderManager = Objects.requireNonNull(orderManager);
        this.logger = Objects.requireNonNull(logger);
        this.random = new Random();
        this.isWorking = true;
        this.ordersProcessed = 0;
        this.totalWaitTime = 0;
        this.blockedQueueCount = 0;
    }

    @Override
    public void run() {
        logger.logInfo("начал работу");

        try {
            while (isWorking && !Thread.currentThread().isInterrupted()) {
                processOrder();
                Thread.sleep(random.nextInt(800) + 200);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.logInfo("был прерван");
        } finally {
            logger.logInfo("закончил работу. " +
                    "Обработано заказов: " + ordersProcessed +
                    ", Среднее время ожидания: " + getAverageWaitTime() + "мс" +
                    ", Блокировок очереди: " + blockedQueueCount);
        }
    }

    private void processOrder() throws InterruptedException {
        try {
            Order order = createOrder();
            sendToKitchen(order);
            waitForOrder(order);
            deliverOrder(order);
            ordersProcessed++;
        } catch (Exception e) {
            logger.logError("Ошибка при обработке заказа", e);
        }
    }

    private Order createOrder() {
        String dish = DISHES.get(random.nextInt(DISHES.size()));
        int preparationTime = (random.nextInt(4) + 2) * 1000;
        Order order = new Order(dish, preparationTime, name);
        order.setStatus(OrderStatus.CREATED);

        logger.logInfo("Принял заказ: " + dish);
        logger.logOrder(order);
        return order;
    }

    private void sendToKitchen(Order order) throws InterruptedException {
        order.setStatus(OrderStatus.QUEUED);

        int queueSize = kitchenQueue.size();
        int queueCapacity = kitchenQueue.remainingCapacity() + queueSize;
        // проверяем, есть ли свободные места в очереди на кухню
        if (kitchenQueue.remainingCapacity() == 0) {
            logger.logWarning("Очередь на кухню заполнена. Жду освобождения места. " +
                    "Размер очереди: " + queueSize + "/" + queueCapacity);
            blockedQueueCount++;
        }

        long startTime = System.currentTimeMillis();
        // попытка добавить заказ в очередь (ждем 3 сек)
        if (!kitchenQueue.offer(order, 3, TimeUnit.SECONDS)) {
            order.setStatus(OrderStatus.CANCELLED);
            logger.logError("Не удалось добавить заказ в очередь (перерыв 3с)",
                    new IllegalStateException("Очередь переполнена"));
            return;
        }

        long waitTime = System.currentTimeMillis() - startTime;
        totalWaitTime += waitTime;

        if (waitTime > 1000) {
            logger.logWarning("Долго ждал места в очереди: " + waitTime + "мс");
        }

        logger.logInfo("Отправил заказ №" + order.getId() + " на кухню (очередь: " +
                queueSize + "/" + queueCapacity + ")");
        logger.logOrder(order);
    }

    private void waitForOrder(Order order) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        final long TIMEOUT_MS = 30000;//чтобы официант не ждал вечно (максимум - 30 сек)

        while (isWorking && !Thread.currentThread().isInterrupted()) {
            if (orderManager.isContainsOrder(order.getId())) {
                long waitTime = System.currentTimeMillis() - startTime;
                totalWaitTime += waitTime;
                order.setStatus(OrderStatus.READY);

                if (waitTime > order.getCookingTimeMs() * 1.5) {
                    logger.logWarning("Долгое ожидание заказа №" + order.getId() +
                            ": " + waitTime + "мс");
                }
                break;
            }

            if (System.currentTimeMillis() - startTime > TIMEOUT_MS) {
                logger.logError("Таймаут ожидания заказа №" + order.getId(),
                        new IllegalStateException("Заказ не приготовлен за 30с"));
                break;
            }

            orderManager.waitOrder(1000);
        }
    }

    private void deliverOrder(Order order) {
        try {
            Optional<Order> readyOrder = orderManager.takeReadyOrder(order.getId());
            if (readyOrder.isPresent()) {
                order.setStatus(OrderStatus.DELIVERED);
                logger.logInfo("Доставил заказ №" + order.getId() + ": " + order.getDishName());
                logger.logOrder(order);
            } else {
                logger.logWarning("Заказ №" + order.getId() + " не найден для доставки");
            }
        } catch (Exception e) {
            logger.logError("Ошибка при доставке заказа №" + order.getId(), e);
        }
    }

    public void stop() {
        isWorking = false;
        synchronized (orderManager) {
            orderManager.notifyAll();
        }
    }
    public String getName() { return name; }
    public int getOrdersProcessed() { return ordersProcessed; }
    public long getTotalWaitTime() { return totalWaitTime; }
    public double getAverageWaitTime() { return ordersProcessed > 0 ? (double) totalWaitTime / ordersProcessed : 0;}
    public int getBlockedQueueCount() { return blockedQueueCount; }
}
