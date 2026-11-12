package domain;

import java.util.ArrayList;
import java.util.List;

import support.OrderStatus;

// Stores the current state of the restaurant.
public class Restaurant {

    private final WaitingQueue waitingQueue;
    private final List<Table> tables;
    private final List<Customer> customers;
    private final List<Order> orders;
    private final Menu menu;
    private final GameStats stats;
    private final HappyHour happyHour;

    public Restaurant(int tableCount) {

        waitingQueue = new WaitingQueue();
        tables = new ArrayList<>();
        customers = new ArrayList<>();
        orders = new ArrayList<>();

        menu = new Menu();
        stats = new GameStats();
        happyHour = new HappyHour();

        // Creates the required restaurant tables.
        for (int i = 1; i <= tableCount; i++) {
            tables.add(new Table(i));
        }
    }

    // Adds a newly arrived customer.
    public void addCustomer(Customer customer) {
        customers.add(customer);
        waitingQueue.add(customer);
    }

    // Adds a newly created order.
    public void addOrder(Order order) {
        orders.add(order);
    }

    public WaitingQueue getWaitingQueue() {
        return waitingQueue;
    }

    public List<Table> getTables() {
        return new ArrayList<>(tables);
    }

    public List<Customer> getCustomers() {
        return new ArrayList<>(customers);
    }

    public List<Order> getOrders() {
        return new ArrayList<>(orders);
    }

    public Menu getMenu() {
        return menu;
    }

    public GameStats getStats() {
        return stats;
    }

    public HappyHour getHappyHour() {
        return happyHour;
    }

    // Finds the first available table.
    public Table findFreeTable() {

        for (Table table : tables) {
            if (table.isFree()) {
                return table;
            }
        }

        return null;
    }

    // Returns all orders that are ready to serve.
    public List<Order> findReadyOrders() {

        List<Order> readyOrders = new ArrayList<>();

        for (Order order : orders) {
            if (order.getStatus() == OrderStatus.READY) {
                readyOrders.add(order);
            }
        }

        return readyOrders;
    }

    // Returns all customers waiting to pay.
    public List<Customer> findReadyToPayCustomers() {

        List<Customer> readyCustomers = new ArrayList<>();

        for (Customer customer : customers) {

            Order order = customer.getOrder();

            if (order != null
                    && order.getStatus() == OrderStatus.READY_TO_PAY) {

                readyCustomers.add(customer);
            }
        }

        return readyCustomers;
    }
}
