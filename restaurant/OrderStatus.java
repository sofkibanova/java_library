public enum OrderStatus {
    CREATED("Создан"),
    QUEUED("В очереди"),
    COOKING("Готовится"),
    READY("Готов"),
    DELIVERED("Доставлен"),
    CANCELLED("Отменен");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
