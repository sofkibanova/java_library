import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Restaurant {
    private final String name;
    private final int waiterCount;
    private final int chefCount;
    private final int queueCapacity;
    private final RestaurantLogger logger;
    private final OrderManager orderManager;
    private final BlockingQueue<Order> kitchenQueue;
    private final List<Waiter> waiters;
    private final List<Thread> waiterThreads;
    private final Kitchen kitchen;
    private final RestaurantMonitor monitor;
    private volatile boolean isOpen;

    public Restaurant(String name, int waiterCount, int chefCount, int queueCapacity) {
        this.name = Objects.requireNonNull(name);
        this.waiterCount = waiterCount;
        this.chefCount = chefCount;
        this.queueCapacity = queueCapacity;
        this.logger = new ConsoleRestaurantLogger();

        this.kitchenQueue = new ArrayBlockingQueue<>(queueCapacity);
        this.orderManager = new OrderManager();
        this.waiters = new ArrayList<>();
        this.waiterThreads = new ArrayList<>();

        this.kitchen = new Kitchen(chefCount, kitchenQueue, orderManager, logger);
        this.monitor = new RestaurantMonitor(kitchenQueue, kitchen, orderManager, logger, queueCapacity);

        this.isOpen = false;

        initializeWaiters();
    }

    private void initializeWaiters() {
        for (int i = 1; i <= waiterCount; i++) {
            Waiter waiter = new Waiter("Официант-" + i, kitchenQueue, orderManager, logger);
            waiters.add(waiter);
        }
    }

    public void open() {
        if (isOpen) {
            logger.logWarning("Ресторан уже открыт");
            return;
        }

        System.out.println("Ресторан '" + name + "' открывается");
        System.out.println("Конфигурация: " + waiterCount + " официантов, " +
                chefCount + " поваров, очередь: " + queueCapacity);

        isOpen = true;

        Thread monitorThread = new Thread(monitor, "Монитор");
        monitorThread.start();

        kitchen.open();
        for (int i = 0; i < waiters.size(); i++) {
            Thread waiterThread = new Thread(waiters.get(i), "Официант-" + (i + 1));
            waiterThreads.add(waiterThread);
            waiterThread.start();
        }

        System.out.println("Ресторан '" + name + "' открыт и готов к работе");
    }

    public void close() {
        if (!isOpen) {
            logger.logWarning("Ресторан уже закрыт");
            return;
        }

        logger.logInfo("Ресторан '" + name + "' закрывается.");
        isOpen = false;

        monitor.stop();
        for (Waiter waiter : waiters) {
            waiter.stop();
        }

        for (Thread waiterThread : waiterThreads) {
            try {
                waiterThread.join(3000);
                if (waiterThread.isAlive()) {
                    waiterThread.interrupt();
                    waiterThread.join(1000);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.logError("Прерывание при ожидании официантов", e);
            }
        }

        kitchen.close();
        orderManager.cleaningWhenClose();
        logger.logInfo("Ресторан '" + name + "' закрыт");

        printFinalStatistics();
    }

    private void printFinalStatistics() {
        logger.logStatus("Статистика ресторана: " + name);

        int totalOrdersProcessed = waiters.stream().mapToInt(Waiter::getOrdersProcessed).sum();
        int totalOrdersCooked = kitchen.getTotalOrdersCooked();
        int totalCookingDelays = kitchen.getTotalCookingDelays();
        int totalBlockedQueue = waiters.stream().mapToInt(Waiter::getBlockedQueueCount).sum();

        logger.logStatus("Всего заказов принято: " + totalOrdersProcessed);
        logger.logStatus("Всего заказов приготовлено: " + totalOrdersCooked);
        logger.logStatus("Заказов в очереди при закрытии: " + kitchenQueue.size());
        logger.logStatus("Готовых заказов при закрытии: " + orderManager.readyOrdersCount());
        logger.logStatus("Всего задержек приготовления: " + totalCookingDelays);
        logger.logStatus("Всего блокировок очереди: " + totalBlockedQueue);

        System.out.println("-".repeat(60));
        logger.logStatus("Официанты:");
        waiters.forEach(w -> {
            double avgWait = w.getAverageWaitTime();
            logger.logStatus("  " + w.getName() + ": " + w.getOrdersProcessed() +
                    " заказов, среднее ожидание: " + (int)avgWait + "мс, " +
                    "блокировок: " + w.getBlockedQueueCount());
        });

        System.out.println("-".repeat(60));
        logger.logStatus("Повара:");
        kitchen.getChefs().forEach(c -> {
            double avgWork = c.getAverageWorkTime();
            logger.logStatus("  " + c.getName() + ": " + c.getOrdersCooked() +
                    " заказов, среднее время: " + (int)avgWork + "мс, " +
                    "задержек: " + c.getCookingDelays());
        });

        double efficiency = totalOrdersProcessed > 0 ?
                (double) totalOrdersCooked / totalOrdersProcessed * 100 : 0;
        logger.logStatus("Эффективность: " + String.format("%.1f", efficiency) + "%");

    }

    public void emergencyClose() {
        logger.logError("Экстренное закрытие ресторана",
                new IllegalStateException("Экстренное закрытие"));

        if (isOpen) {
            monitor.stop();
            waiters.forEach(Waiter::stop);
            waiterThreads.forEach(Thread::interrupt);
            kitchen.close();
            isOpen = false;
        }
    }

    public boolean isOpen() { return isOpen; }
    public Kitchen getKitchen() { return kitchen; }
    public List<Waiter> getWaiters() { return new ArrayList<>(waiters); }
    public int getQueueSize() { return kitchenQueue.size(); }
}
