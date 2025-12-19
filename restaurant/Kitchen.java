import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Kitchen {
    private final List<Chef> chefs;
    private final ExecutorService chefPool;
    private final RestaurantLogger logger;
    private volatile boolean isOpen;

    public Kitchen(int chefCount, BlockingQueue<Order> orderQueue,
                   OrderManager orderManager, RestaurantLogger logger) {
        this.logger = Objects.requireNonNull(logger);

        this.chefs = new ArrayList<>();
        this.chefPool = Executors.newFixedThreadPool(chefCount);

        for (int i = 1; i <= chefCount; i++) {
            Chef chef = new Chef("Повар-" + i, orderQueue, orderManager, logger);
            chefs.add(chef);
        }

        this.isOpen = false;
    }

    public void open() {
        if (isOpen) {
            logger.logWarning("Кухня уже открыта");
            return;
        }

        isOpen = true;
        for (Chef chef : chefs) {
            chefPool.execute(chef);
        }
        System.out.println("Кухня открыта. Поваров: " + chefs.size());
    }

    public void close() {
        if (!isOpen) {
            logger.logWarning("Кухня закрыта");
            return;
        }

        isOpen = false;

        for (Chef chef : chefs) {
            chef.stop();
        }

        chefPool.shutdown();
        try {
            if (!chefPool.awaitTermination(5, TimeUnit.SECONDS)) {
                chefPool.shutdownNow();
                if (!chefPool.awaitTermination(3, TimeUnit.SECONDS)) {
                    logger.logError("Повара не смогли закончить работу вовремя",
                            new IllegalStateException("Таймаут завершения"));
                }
            }
        } catch (InterruptedException e) {
            // если поток был прерван во время ожидания
            chefPool.shutdownNow();
            Thread.currentThread().interrupt();// сообщаем, что поток был прерван
            logger.logError("Прерывание при закрытии кухни", e);
        }

        logger.logInfo("Кухня закрыта");
    }

    public boolean isOpen() { return isOpen; }
    public int getActiveChefCount() { return (int) chefs.stream().filter(Chef::isWorking).count(); }
    public List<Chef> getChefs() { return new ArrayList<>(chefs); }
    public int getTotalOrdersCooked() { return chefs.stream().mapToInt(Chef::getOrdersCooked).sum(); }
    public int getTotalCookingDelays() { return chefs.stream().mapToInt(Chef::getCookingDelays).sum(); }
}
