package domain;

import support.OrderStatus;

// Represents a customer's food order.
public class Order {

    private final int orderId;
    private final Customer customer;
    private final MenuItem item;

    private OrderStatus status;
    private int remainingPreparationUnits;
    private final int billCents;

    private int readyTurn;
    private int servedTurn;
    private int readyToPayTurn;

    public Order(int orderId, Customer customer, MenuItem item) {
        this.orderId = orderId;
        this.customer = customer;
        this.item = item;

        this.status = OrderStatus.PLACED;
        this.remainingPreparationUnits = item.getPreparationUnits();
        this.billCents = item.getPriceCents();

        this.readyTurn = -1;
        this.servedTurn = -1;
        this.readyToPayTurn = -1;
    }

    public int getOrderId() {
        return orderId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public MenuItem getItem() {
        return item;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public int getRemainingPreparationUnits() {
        return remainingPreparationUnits;
    }

    public int getBillCents() {
        return billCents;
    }

    public int getReadyTurn() {
        return readyTurn;
    }

    public int getServedTurn() {
        return servedTurn;
    }

    public int getReadyToPayTurn() {
        return readyToPayTurn;
    }

    // Advances the order by one preparation unit.
    public void prepareOneUnit(int currentTurn) {

        if (status == OrderStatus.PLACED) {
            status = OrderStatus.PREPARING;
        }

        if (status != OrderStatus.PREPARING) {
            return;
        }

        remainingPreparationUnits--;

        if (remainingPreparationUnits <= 0) {
            remainingPreparationUnits = 0;
            status = OrderStatus.READY;
            readyTurn = currentTurn;
        }
    }

    // Marks the order as served.
    public void markServed(int currentTurn) {

        if (status == OrderStatus.READY) {
            status = OrderStatus.SERVED;
            servedTurn = currentTurn;
        }
    }

    // Marks the customer as ready to pay.
    public void markReadyToPay(int currentTurn) {

        if (status == OrderStatus.SERVED) {
            status = OrderStatus.READY_TO_PAY;
            readyToPayTurn = currentTurn;
        }
    }

    // Marks the order as paid.
    public void markPaid() {

        if (status == OrderStatus.READY_TO_PAY) {
            status = OrderStatus.PAID;
        }
    }

    // Cancels an unfinished order.
    public void cancel() {

        if (status != OrderStatus.PAID) {
            status = OrderStatus.CANCELLED;
        }
    }
}
