import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class OrderManager {
    //потокобезопасная коллекция для хранения ID готового заказа
    private final ConcurrentHashMap<Integer, Order> readyOrders;

    public OrderManager() {
        this.readyOrders = new ConcurrentHashMap<>();
    }

    public void toReadyOrders(Order order) {
        Objects.requireNonNull(order);
        readyOrders.put(order.getId(), order);
        //уведомляем всех официантов о новых заказах
        synchronized (readyOrders){
            readyOrders.notifyAll();
        }
    }
    //Optional - для более безопасной работы с возможным значением null
    public Optional<Order> takeReadyOrder(int idOrder) {
        Order order = readyOrders.remove(idOrder);
        return Optional.ofNullable(order);
    }

    public boolean isContainsOrder(int idOrder) {
        return readyOrders.containsKey(idOrder);
    }

    public void waitOrder(long maxTimeoutMs) throws InterruptedException {
        synchronized (readyOrders) {
            readyOrders.wait(maxTimeoutMs);
        }
    }

    public int readyOrdersCount() { return readyOrders.size(); }
    public void cleaningWhenClose () { readyOrders.clear();}
}
