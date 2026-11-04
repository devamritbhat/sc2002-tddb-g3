package boundary;

import java.util.List;
import java.util.Scanner;

import control.GameController;
import domain.Customer;
import domain.GameStats;
import domain.Order;
import domain.Restaurant;
import domain.Table;
import support.ActionResult;

// Handles all command-line input and output.
public class RestaurantUI {

    private final GameController controller;
    private final Scanner scanner;

    public RestaurantUI(GameController controller) {
        this.controller = controller;
        scanner = new Scanner(System.in);
    }

    // Runs the complete Stage 1 game.
    public void run() {

        System.out.println("================================");
        System.out.println("       RESTAURANT RUSH");
        System.out.println("================================");

        while (!controller.isGameOver()) {

            controller.startNextTurn();

            System.out.println();
            System.out.println("================================");
            System.out.println("TURN " + controller.getCurrentTurn());
            System.out.println("================================");

            // Step 1: Customer arrival.
            Customer arrival = controller.processArrival();

            if (arrival != null) {
                System.out.println(
                        "Customer " + arrival.getId()
                                + " has arrived."
                );
            }

            showState();

            // Step 2 and 3: Waiter and Chef choices.
            handleWaiterAction();
            handleChefAction();

            // Step 4: Eating and automatic payment.
            ActionResult payment =
                    controller.processEatingAndPayment();

            if (payment.isSuccess()) {
                System.out.println(payment.getMessage());
            }

            // Step 5: Satisfaction loss and abandonment.
            controller.processSatisfactionAndAbandonment();

            showEndOfTurnSummary();
        }

        showFinalResult();

        scanner.close();
    }

    // Displays the current restaurant state.
    private void showState() {

        Restaurant restaurant =
                controller.getRestaurant();

        GameStats stats =
                restaurant.getStats();

        System.out.println();
        System.out.println("----- Restaurant Status -----");

        System.out.println(
                "Revenue: "
                        + formatMoney(
                                stats.getRevenueCents()
                        )
        );

        if (stats.getPaidCount() == 0) {
            System.out.println(
                    "Average satisfaction: N/A"
            );
        } else {
            System.out.printf(
                    "Average satisfaction: %.2f%n",
                    stats.getAveragePaidSatisfaction()
            );
        }

        System.out.println();
        System.out.println("Staff Tasks Available:");
        System.out.println("Waiter: 1 task");
        System.out.println("Chef: 1 preparation unit");
        System.out.println("Cashier: automatic, up to 1 payment");

        showWaitingQueue();
        showTables();
        showCustomers();
        showOrders();
    }

    // Displays customers currently waiting for tables.
    private void showWaitingQueue() {

        List<Customer> customers =
                controller.getRestaurant()
                        .getWaitingQueue()
                        .getCustomers();

        System.out.println();
        System.out.println("Waiting Queue:");

        if (customers.isEmpty()) {
            System.out.println("None");
            return;
        }

        for (Customer customer : customers) {
            System.out.println(
                    "Customer " + customer.getId()
                            + " | Satisfaction: "
                            + customer.getSatisfaction()
            );
        }
    }

    // Displays the restaurant tables.
    private void showTables() {

        System.out.println();
        System.out.println("Tables:");

        for (Table table :
                controller.getRestaurant()
                        .getTables()) {

            if (table.isFree()) {

                System.out.println(
                        "Table " + table.getTableId()
                                + ": Free"
                );

            } else {

                System.out.println(
                        "Table " + table.getTableId()
                                + ": Customer "
                                + table.getOccupant()
                                        .getId()
                );
            }
        }
    }

    // Displays all customers and their current states.
    private void showCustomers() {

        List<Customer> customers =
                controller.getRestaurant()
                        .getCustomers();

        System.out.println();
        System.out.println("Customers:");

        if (customers.isEmpty()) {
            System.out.println("None");
            return;
        }

        for (Customer customer : customers) {

            System.out.println(
                    "Customer " + customer.getId()
                            + " | "
                            + customer.getStatus()
                            + " | Satisfaction: "
                            + customer.getSatisfaction()
            );
        }
    }

    // Displays all current orders.
    private void showOrders() {

        List<Order> orders =
                controller.getRestaurant()
                        .getOrders();

        System.out.println();
        System.out.println("Orders:");

        if (orders.isEmpty()) {
            System.out.println("None");
            return;
        }

        for (Order order : orders) {

            System.out.println(
                    "Order " + order.getOrderId()
                            + " | Customer "
                            + order.getCustomer().getId()
                            + " | "
                            + order.getItem().getName()
                            + " | "
                            + order.getStatus()
                            + " | Remaining units: "
                            + order.getRemainingPreparationUnits()
            );
        }
    }

    // Lets the manager choose one Waiter task.
    private void handleWaiterAction() {

        boolean actionComplete = false;

        while (!actionComplete) {

            System.out.println();
            System.out.println("Waiter Task:");
            System.out.println("1. Seat customer");
            System.out.println("2. Take order");
            System.out.println("3. Serve order");
            System.out.println("4. Wait");

            int choice =
                    readInt("Choose Waiter task: ");

            switch (choice) {

                case 1:
                    actionComplete =
                            chooseCustomerToSeat();
                    break;

                case 2:
                    actionComplete =
                            chooseCustomerForOrder();
                    break;

                case 3:
                    actionComplete =
                            chooseOrderToServe();
                    break;

                case 4:
                    ActionResult waiterWait =
                            controller.waiterWait();

                    System.out.println(
                            waiterWait.getMessage()
                    );

                    actionComplete =
                            waiterWait.isSuccess();

                    break;

                default:
                    System.out.println(
                            "Invalid choice. Try again."
                    );
            }
        }
    }

    // Lets the manager choose a waiting customer to seat.
    private boolean chooseCustomerToSeat() {

        List<Customer> waiting =
                controller.getRestaurant()
                        .getWaitingQueue()
                        .getCustomers();

        if (waiting.isEmpty()) {
            System.out.println(
                    "No customers are waiting for a table."
            );
            return false;
        }

        if (controller.getRestaurant()
                .findFreeTable() == null) {

            System.out.println(
                    "No free table is available."
            );
            return false;
        }

        System.out.println();
        System.out.println("Waiting customers:");

        for (Customer customer : waiting) {
            System.out.println(
                    customer.getId()
                            + ". Customer "
                            + customer.getId()
            );
        }

        int id =
                readInt(
                        "Choose customer ID to seat: "
                );

        Customer selected =
                findCustomerById(waiting, id);

        if (selected == null) {
            System.out.println(
                    "Invalid customer."
            );
            return false;
        }

        ActionResult result =
                controller.seatCustomer(selected);

        System.out.println(result.getMessage());

        return result.isSuccess();
    }

    // Lets the manager choose a customer whose order should be taken.
    private boolean chooseCustomerForOrder() {

        List<Customer> customers =
                controller.getCustomersReadyToOrder();

        if (customers.isEmpty()) {
            System.out.println(
                    "No customers are ready to order."
            );
            return false;
        }

        System.out.println();
        System.out.println("Customers ready to order:");

        for (Customer customer : customers) {
            System.out.println(
                    customer.getId()
                            + ". Customer "
                            + customer.getId()
            );
        }

        int id =
                readInt(
                        "Choose customer ID: "
                );

        Customer selected =
                findCustomerById(customers, id);

        if (selected == null) {
            System.out.println(
                    "Invalid customer."
            );
            return false;
        }

        ActionResult result =
                controller.takeOrder(selected);

        System.out.println(result.getMessage());

        return result.isSuccess();
    }

    // Lets the manager choose a ready order to serve.
    private boolean chooseOrderToServe() {

        List<Order> orders =
                controller.getReadyOrders();

        if (orders.isEmpty()) {
            System.out.println(
                    "No orders are ready to serve."
            );
            return false;
        }

        System.out.println();
        System.out.println("Ready orders:");

        for (Order order : orders) {

            System.out.println(
                    order.getOrderId()
                            + ". Order "
                            + order.getOrderId()
                            + " - Customer "
                            + order.getCustomer()
                                    .getId()
            );
        }

        int id =
                readInt(
                        "Choose order ID to serve: "
                );

        Order selected =
                findOrderById(orders, id);

        if (selected == null) {
            System.out.println(
                    "Invalid order."
            );
            return false;
        }

        ActionResult result =
                controller.serveOrder(selected);

        System.out.println(result.getMessage());

        return result.isSuccess();
    }

    // Lets the manager choose one Chef task.
    private void handleChefAction() {

        boolean actionComplete = false;

        while (!actionComplete) {

            List<Order> orders =
                    controller.getPreparableOrders();

            System.out.println();
            System.out.println("Chef Task:");
            System.out.println("1. Prepare order");
            System.out.println("2. Wait");

            int choice =
                    readInt("Choose Chef task: ");

            switch (choice) {

                case 1:

                    if (orders.isEmpty()) {
                        System.out.println(
                                "No orders can be prepared."
                        );
                        break;
                    }

                    System.out.println();
                    System.out.println(
                            "Orders available for preparation:"
                    );

                    for (Order order : orders) {

                        System.out.println(
                                order.getOrderId()
                                        + ". Order "
                                        + order.getOrderId()
                                        + " - "
                                        + order.getItem()
                                                .getName()
                                        + " - Remaining units: "
                                        + order
                                                .getRemainingPreparationUnits()
                        );
                    }

                    int id =
                            readInt(
                                    "Choose order ID: "
                            );

                    Order selected =
                            findOrderById(
                                    orders,
                                    id
                            );

                    if (selected == null) {
                        System.out.println(
                                "Invalid order."
                        );
                        break;
                    }

                    ActionResult result =
                            controller.prepareOrder(
                                    selected
                            );

                    System.out.println(
                            result.getMessage()
                    );

                    actionComplete =
                            result.isSuccess();

                    break;

                case 2:

                    ActionResult chefWait =
                            controller.chefWait();

                    System.out.println(
                            chefWait.getMessage()
                    );

                    actionComplete =
                            chefWait.isSuccess();

                    break;

                default:
                    System.out.println(
                            "Invalid choice. Try again."
                    );
            }
        }
    }

    // Displays statistics after each turn.
    private void showEndOfTurnSummary() {

        GameStats stats =
                controller.getRestaurant()
                        .getStats();

        System.out.println();
        System.out.println(
                "----- End of Turn "
                        + controller.getCurrentTurn()
                        + " -----"
        );

        System.out.println(
                "Served customers: "
                        + stats.getServedCount()
        );

        System.out.println(
                "Unhappy departures: "
                        + stats.getUnhappyDepartures()
        );

        System.out.println(
                "Paid customers: "
                        + stats.getPaidCount()
        );

        System.out.println(
                "Revenue: "
                        + formatMoney(
                                stats.getRevenueCents()
                        )
        );

        if (stats.getPaidCount() == 0) {

            System.out.println(
                    "Average satisfaction: N/A"
            );

        } else {

            System.out.printf(
                    "Average satisfaction: %.2f%n",
                    stats.getAveragePaidSatisfaction()
            );
        }
    }

    // Displays the final victory or defeat result.
    private void showFinalResult() {

        GameStats stats =
                controller.getRestaurant()
                        .getStats();

        System.out.println();
        System.out.println("================================");
        System.out.println("          FINAL RESULT");
        System.out.println("================================");

        if (controller.hasWon()) {
            System.out.println("VICTORY!");
        } else {
            System.out.println("DEFEAT!");
        }

        System.out.println(
                "Turns used: "
                        + controller.getCurrentTurn()
        );

        System.out.println(
                "Paid customers: "
                        + stats.getPaidCount()
        );

        System.out.println(
                "Served customers: "
                        + stats.getServedCount()
        );

        System.out.println(
                "Unhappy departures: "
                        + stats.getUnhappyDepartures()
        );

        System.out.println(
                "Revenue: "
                        + formatMoney(
                                stats.getRevenueCents()
                        )
        );

        if (stats.getPaidCount() == 0) {

            System.out.println(
                    "Average satisfaction: N/A"
            );

        } else {

            System.out.printf(
                    "Average satisfaction: %.2f%n",
                    stats.getAveragePaidSatisfaction()
            );
        }
    }

    // Finds a customer from a displayed list.
    private Customer findCustomerById(
            List<Customer> customers,
            int id) {

        for (Customer customer : customers) {

            if (customer.getId() == id) {
                return customer;
            }
        }

        return null;
    }

    // Finds an order from a displayed list.
    private Order findOrderById(
            List<Order> orders,
            int id) {

        for (Order order : orders) {

            if (order.getOrderId() == id) {
                return order;
            }
        }

        return null;
    }

    // Reads an integer without crashing on invalid input.
    private int readInt(String message) {

        while (true) {

            System.out.print(message);

            if (scanner.hasNextInt()) {
                int value = scanner.nextInt();
                scanner.nextLine();
                return value;
            }

            System.out.println(
                    "Please enter a valid number."
            );

            scanner.nextLine();
        }
    }

    // Formats integer cents as dollars.
    private String formatMoney(int cents) {

        return String.format(
                "$%.2f",
                cents / 100.0
        );
    }
}
