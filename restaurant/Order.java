import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class Order {
    //атомарный счетчик для генерации уникальных ID
    final AtomicInteger ID_GENERATE = new AtomicInteger(1);
    private final int id;
    private final String dishName;
    private final int cookingTimeMs;
    private final String waiterName;
    private volatile OrderStatus status;

    public Order(String dishName, int cookingTimeMs, String waiterName){
        this.id = ID_GENERATE.getAndIncrement();
        this.dishName = Objects.requireNonNull(dishName);
        this.cookingTimeMs = cookingTimeMs;
        this.waiterName = Objects.requireNonNull(waiterName);
        this.status = OrderStatus.CREATED;
    }

    public int getId() { return id; }
    public String getDishName() { return dishName; }
    public int getCookingTimeMs() { return cookingTimeMs; }
    public String getWaiterName() { return waiterName; }
    public OrderStatus getStatus() { return status; }

    public void setStatus(OrderStatus newStatus) {
        this.status = newStatus;
    }

    @Override
    public String toString() {
        return String.format("№%d: %s (%dms)", id, dishName, cookingTimeMs, waiterName);
    }
}
