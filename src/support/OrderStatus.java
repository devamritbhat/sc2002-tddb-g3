package support;

// Represents the current state of an order.
public enum OrderStatus {
    PLACED,
    PREPARING,
    READY,
    SERVED,
    READY_TO_PAY,
    PAID,
    CANCELLED
}
