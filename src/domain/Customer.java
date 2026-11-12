package domain;

import support.CustomerStatus;

// Represents a customer in the restaurant.
public abstract class Customer {

    private final int id;
    private final int arrivalTurn;

    private int satisfaction;
    private CustomerStatus status;

    private Table table;
    private Order order;

    public Customer(int id, int arrivalTurn) {
        this.id = id;
        this.arrivalTurn = arrivalTurn;

        satisfaction = 100;
        status = CustomerStatus.WAITING;

        table = null;
        order = null;
    }

    public int getId() {
        return id;
    }

    public int getArrivalTurn() {
        return arrivalTurn;
    }

    public int getSatisfaction() {
        return satisfaction;
    }

    public CustomerStatus getStatus() {
        return status;
    }

    public void setStatus(CustomerStatus status) {
        this.status = status;
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    // Reduces satisfaction when the customer is still waiting for service.
    public void loseSatisfaction() {
        satisfaction -= getSatisfactionLossPerTurn();

        if (satisfaction < 0) {
            satisfaction = 0;
        }
    }

    // Recovers satisfaction during Happy Hour, up to 100.
    public void recoverSatisfaction(int points) {
        satisfaction += points;

        if (satisfaction > 100) {
            satisfaction = 100;
        }
    }

    public boolean hasLeft() {
        return status == CustomerStatus.LEFT;
    }

    // Chooses food by customer ID by default.
    public MenuItem chooseItem(Menu menu) {
        return menu.chooseItemFor(this);
    }

    // Charges full price by default.
    public int calculateBillCents(int priceCents) {
        return priceCents;
    }

    // Each customer type defines its own satisfaction loss.
    public abstract int getSatisfactionLossPerTurn();

    // Customer type shown in the game.
    public abstract String getTypeName();
}
