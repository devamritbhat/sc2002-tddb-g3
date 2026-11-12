package control;

import java.util.ArrayList;
import java.util.List;

import domain.Cashier;
import domain.Chef;
import domain.Customer;
import domain.HappyHour;
import domain.Host;
import domain.MenuItem;
import domain.Order;
import domain.Restaurant;
import domain.Table;
import domain.Waiter;

import support.ActionResult;
import support.CustomerStatus;
import support.OrderStatus;

// Controls the main game flow.
public class GameController {

    private final GameConfig config;
    private final Restaurant restaurant;

    private final Waiter waiter;
    private final Host host;
    private final Chef chef;
    private final Cashier cashier;

    private int currentTurn;
    private int nextCustomerId;
    private int nextOrderId;

    private boolean waiterTaskUsed;
    private boolean hostTaskUsed;
    private boolean chefTaskUsed;

    public GameController(GameConfig config) {

        this.config = config;

        restaurant =
                new Restaurant(config.getTableCount());

        waiter = new Waiter();
        host = new Host();
        chef = new Chef();
        cashier = new Cashier();

        currentTurn = 0;
        nextCustomerId = 1;
        nextOrderId = 1;

        waiterTaskUsed = false;
        hostTaskUsed = false;
        chefTaskUsed = false;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public int getCurrentTurn() {
        return currentTurn;
    }

    public GameConfig getConfig() {
        return config;
    }

    public boolean isWaiterTaskUsed() {
        return waiterTaskUsed;
    }

    public boolean isChefTaskUsed() {
        return chefTaskUsed;
    }

    // Starts the next turn.
    public void startNextTurn() {

        if (currentTurn < config.getMaxTurns()) {

            currentTurn++;

            waiterTaskUsed = false;
            hostTaskUsed = false;
            chefTaskUsed = false;

            restaurant.getHappyHour().nextTurn();
        }
    }

    // Starts Happy Hour for this turn and the next. Only once per game.
    public ActionResult activateHappyHour() {

        HappyHour happyHour = restaurant.getHappyHour();

        if (!happyHour.canActivate()) {
            return ActionResult.failure(
                    "Happy Hour has already been used."
            );
        }

        happyHour.activate();

        return ActionResult.success(
                "Happy Hour is on for this turn and the next."
        );
    }

    // At the start of an active turn, customers waiting for a table or food recover satisfaction.
    public ActionResult processHappyHourRecovery() {

        HappyHour happyHour = restaurant.getHappyHour();

        if (!happyHour.isActive()) {
            return ActionResult.failure(
                    "Happy Hour is not active."
            );
        }

        for (Customer customer :
                restaurant.getCustomers()) {

            CustomerStatus status =
                    customer.getStatus();

            if (status == CustomerStatus.WAITING
                    || status == CustomerStatus.SEATED
                    || status == CustomerStatus.ORDERED) {

                customer.recoverSatisfaction(
                        happyHour.getRecoveryPoints()
                );
            }
        }

        return ActionResult.success(
                "Happy Hour: customers waiting for a table or food recover "
                        + happyHour.getRecoveryPoints()
                        + " satisfaction."
        );
    }

    // The Host automatically seats the first waiting customer, once per turn.
    public ActionResult processHost() {

        if (hostTaskUsed) {
            return ActionResult.failure(
                    "Host has already seated a customer this turn."
            );
        }

        Customer customer =
                restaurant.getWaitingQueue().peek();

        if (customer == null) {
            return ActionResult.failure(
                    "No customers are waiting for a table."
            );
        }

        Table table = restaurant.findFreeTable();

        if (table == null) {
            return ActionResult.failure(
                    "No free table is available."
            );
        }

        ActionResult result =
                host.seatCustomer(customer, table);

        if (result.isSuccess()) {

            restaurant.getWaitingQueue()
                    .remove(customer);

            hostTaskUsed = true;
        }

        return result;
    }

    // Creates a customer if one is scheduled to arrive.
    public Customer processArrival() {

        if (!config.getArrivalSchedule()
                .hasArrival(currentTurn)) {

            return null;
        }

        Customer customer =
                config.getArrivalSchedule()
                        .createCustomer(
                                nextCustomerId,
                                currentTurn
                        );

        nextCustomerId++;

        restaurant.addCustomer(customer);

        return customer;
    }

    // Seats a selected waiting customer.
    public ActionResult seatCustomer(Customer customer) {

        if (waiterTaskUsed) {
            return ActionResult.failure(
                    "Waiter has already completed a task this turn."
            );
        }

        if (customer == null) {
            return ActionResult.failure(
                    "No customer selected."
            );
        }

        if (!restaurant.getWaitingQueue()
                .getCustomers()
                .contains(customer)) {

            return ActionResult.failure(
                    "Customer is not in the waiting queue."
            );
        }

        Table table = restaurant.findFreeTable();

        if (table == null) {
            return ActionResult.failure(
                    "No free table is available."
            );
        }

        ActionResult result =
                waiter.seatCustomer(customer, table);

        if (result.isSuccess()) {

            restaurant.getWaitingQueue()
                    .remove(customer);

            waiterTaskUsed = true;
        }

        return result;
    }

    // Takes an order from a selected seated customer.
    public ActionResult takeOrder(Customer customer) {

        if (waiterTaskUsed) {
            return ActionResult.failure(
                    "Waiter has already completed a task this turn."
            );
        }

        if (customer == null) {
            return ActionResult.failure(
                    "No customer selected."
            );
        }

        MenuItem item =
                customer.chooseItem(
                        restaurant.getMenu()
                );

        ActionResult result =
                waiter.takeOrder(
                        customer,
                        item,
                        nextOrderId,
                        restaurant.getHappyHour()
                );

        if (result.isSuccess()) {

            restaurant.addOrder(
                    customer.getOrder()
            );

            nextOrderId++;
            waiterTaskUsed = true;
        }

        return result;
    }

    // Serves a selected ready order.
    public ActionResult serveOrder(Order order) {

        if (waiterTaskUsed) {
            return ActionResult.failure(
                    "Waiter has already completed a task this turn."
            );
        }

        if (order == null) {
            return ActionResult.failure(
                    "No order selected."
            );
        }

        if (order.getStatus() == OrderStatus.READY
                && order.getReadyTurn() >= currentTurn) {

            return ActionResult.failure(
                    "Order became ready this turn and cannot be served until the next turn."
            );
        }

        ActionResult result =
                waiter.serveOrder(
                        order,
                        currentTurn
                );

        if (result.isSuccess()) {

            restaurant.getStats()
                    .recordServed();

            waiterTaskUsed = true;
        }

        return result;
    }

    // Advances one selected order by one preparation unit.
    public ActionResult prepareOrder(Order order) {

        if (chefTaskUsed) {
            return ActionResult.failure(
                    "Chef has already completed a task this turn."
            );
        }

        ActionResult result =
                chef.prepare(
                        order,
                        currentTurn
                );

        if (result.isSuccess()) {
            chefTaskUsed = true;
        }

        return result;
    }

    // Uses the Waiter's task without performing an action.
    public ActionResult waiterWait() {

        if (waiterTaskUsed) {
            return ActionResult.failure(
                    "Waiter has already completed a task this turn."
            );
        }

        waiterTaskUsed = true;

        return ActionResult.success(
                "Waiter waits this turn."
        );
    }

    // Uses the Chef's task without preparing an order.
    public ActionResult chefWait() {

        if (chefTaskUsed) {
            return ActionResult.failure(
                    "Chef has already completed a task this turn."
            );
        }

        chefTaskUsed = true;

        return ActionResult.success(
                "Chef waits this turn."
        );
    }

    // Returns customers who can currently place an order.
    public List<Customer> getCustomersReadyToOrder() {

        List<Customer> result =
                new ArrayList<>();

        for (Customer customer :
                restaurant.getCustomers()) {

            if (customer.getStatus()
                    == CustomerStatus.SEATED) {

                result.add(customer);
            }
        }

        return result;
    }

    // Returns orders the Chef can currently prepare.
    public List<Order> getPreparableOrders() {

        List<Order> result =
                new ArrayList<>();

        for (Order order :
                restaurant.getOrders()) {

            if (order.getStatus()
                        == OrderStatus.PLACED
                    || order.getStatus()
                        == OrderStatus.PREPARING) {

                result.add(order);
            }
        }

        return result;
    }

    // Returns orders that can currently be served.
    public List<Order> getReadyOrders() {
        return restaurant.findReadyOrders();
    }

    // Advances eating and processes one automatic payment.
    public ActionResult processEatingAndPayment() {

        for (Order order :
                restaurant.getOrders()) {

            if (order.getStatus()
                        == OrderStatus.SERVED
                    && order.getServedTurn()
                        < currentTurn) {

                order.markReadyToPay(
                        currentTurn
                );

                order.getCustomer()
                        .setStatus(
                                CustomerStatus.READY_TO_PAY
                        );
            }
        }

        return cashier.processNextPayment(
                restaurant,
                currentTurn
        );
    }

    // Applies satisfaction loss and handles abandonment.
    public void processSatisfactionAndAbandonment() {

        for (Customer customer :
                restaurant.getCustomers()) {

            CustomerStatus status =
                    customer.getStatus();

            boolean losesSatisfaction =
                    status == CustomerStatus.WAITING
                    || status == CustomerStatus.SEATED
                    || status == CustomerStatus.ORDERED;

            if (!losesSatisfaction) {
                continue;
            }

            customer.loseSatisfaction();

            if (customer.getSatisfaction() > 0) {
                continue;
            }

            customer.setStatus(
                    CustomerStatus.LEFT
            );

            restaurant.getWaitingQueue()
                    .remove(customer);

            if (customer.getOrder() != null) {
                customer.getOrder().cancel();
            }

            if (customer.getTable() != null) {
                customer.getTable().release();
            }

            restaurant.getStats()
                    .recordDeparture();
        }
    }

    // Checks whether the victory conditions are met.
    public boolean hasWon() {

        int paid =
                restaurant.getStats()
                        .getPaidCount();

        int revenue =
                restaurant.getStats()
                        .getRevenueCents();

        double average =
                restaurant.getStats()
                        .getAveragePaidSatisfaction();

        return paid
                    >= config.getMinPaidCustomers()
                && revenue
                    >= config.getMinRevenueCents()
                && average
                    >= config.getMinAverageSatisfaction();
    }

    public boolean isGameOver() {
        return currentTurn
                >= config.getMaxTurns();
    }
}
