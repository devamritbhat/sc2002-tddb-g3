import control.GameConfig;
import control.GameController;

import domain.Cashier;
import domain.Customer;
import domain.Menu;
import domain.MenuItem;
import domain.Order;
import domain.RegularCustomer;
import domain.Restaurant;
import domain.Table;

import support.CustomerStatus;
import support.OrderStatus;

// Automated checks for Stage 1.
public class Stage1Test {

    public static void main(String[] args) {

        test18Turns();
        testArrivalSchedule();
        testSatisfactionLoss();
        testMenuPreparationUnits();
        testOrderPreparation();
        testServing();
        testEatingAndPaymentDelay();
        testPaymentAndTableRelease();
        testAbandonment();
        testVictoryConditions();
        testStaffTaskLimits();
        testCashierOldestFirst();

        System.out.println();
        System.out.println("All Stage 1 tests passed.");
    }

    // Checks that the game runs exactly 18 turns.
    private static void test18Turns() {

        GameController controller =
                new GameController(new GameConfig());

        for (int i = 0; i < 18; i++) {
            controller.startNextTurn();
        }

        assertEquals(
                18,
                controller.getCurrentTurn(),
                "Game should reach turn 18."
        );

        assertTrue(
                controller.isGameOver(),
                "Game should be over after turn 18."
        );

        controller.startNextTurn();

        assertEquals(
                18,
                controller.getCurrentTurn(),
                "Game should not advance beyond turn 18."
        );

        System.out.println(
                "PASS: 18-turn limit works correctly."
        );
    }

    // Checks the six required customer arrival turns.
    private static void testArrivalSchedule() {

        GameController controller =
                new GameController(new GameConfig());

        int[] expectedTurns =
                {1, 4, 7, 10, 13, 16};

        int expectedIndex = 0;
        int arrivals = 0;

        for (int turn = 1; turn <= 18; turn++) {

            controller.startNextTurn();

            Customer customer =
                    controller.processArrival();

            if (customer != null) {

                assertEquals(
                        expectedTurns[expectedIndex],
                        controller.getCurrentTurn(),
                        "Customer arrived on incorrect turn."
                );

                expectedIndex++;
                arrivals++;
            }
        }

        assertEquals(
                6,
                arrivals,
                "There should be exactly 6 arrivals."
        );

        System.out.println(
                "PASS: Arrival schedule works correctly."
        );
    }

    // Checks regular customer satisfaction loss.
    private static void testSatisfactionLoss() {

        Customer customer =
                new RegularCustomer(1, 1);

        assertEquals(
                100,
                customer.getSatisfaction(),
                "Customer should start with satisfaction 100."
        );

        customer.loseSatisfaction();

        assertEquals(
                92,
                customer.getSatisfaction(),
                "Regular customer should lose 8 satisfaction."
        );

        for (int i = 0; i < 20; i++) {
            customer.loseSatisfaction();
        }

        assertEquals(
                0,
                customer.getSatisfaction(),
                "Satisfaction should never fall below 0."
        );

        System.out.println(
                "PASS: Satisfaction loss works correctly."
        );
    }

    // Checks menu prices and preparation units.
    private static void testMenuPreparationUnits() {

        Menu menu = new Menu();

        MenuItem salad = menu.getItem(0);
        MenuItem burger = menu.getItem(1);
        MenuItem pasta = menu.getItem(2);

        assertEquals(
                1,
                salad.getPreparationUnits(),
                "Salad should require 1 preparation unit."
        );

        assertEquals(
                1,
                burger.getPreparationUnits(),
                "Burger should require 1 preparation unit."
        );

        assertEquals(
                2,
                pasta.getPreparationUnits(),
                "Pasta should require 2 preparation units."
        );

        assertEquals(
                900,
                salad.getPriceCents(),
                "Salad should cost 900 cents."
        );

        assertEquals(
                1500,
                burger.getPriceCents(),
                "Burger should cost 1500 cents."
        );

        assertEquals(
                1800,
                pasta.getPriceCents(),
                "Pasta should cost 1800 cents."
        );

        System.out.println(
                "PASS: Menu values are correct."
        );
    }

    // Checks that preparation units correctly move an order to READY.
    private static void testOrderPreparation() {

        Customer customer =
                new RegularCustomer(1, 1);

        MenuItem pasta =
                new MenuItem("Pasta", 1800, 2);

        Order order =
                new Order(1, customer, pasta);

        assertEquals(
                OrderStatus.PLACED,
                order.getStatus(),
                "New order should start as PLACED."
        );

        order.prepareOneUnit(1);

        assertEquals(
                OrderStatus.PREPARING,
                order.getStatus(),
                "Pasta should be PREPARING after one unit."
        );

        assertEquals(
                1,
                order.getRemainingPreparationUnits(),
                "Pasta should have 1 unit remaining."
        );

        order.prepareOneUnit(2);

        assertEquals(
                OrderStatus.READY,
                order.getStatus(),
                "Pasta should become READY after two units."
        );

        assertEquals(
                0,
                order.getRemainingPreparationUnits(),
                "Ready order should have 0 units remaining."
        );

        assertEquals(
                2,
                order.getReadyTurn(),
                "Ready turn should be recorded."
        );

        System.out.println(
                "PASS: Order preparation works correctly."
        );
    }

    // Checks that serving cannot happen on the same turn an order becomes READY.
    private static void testServing() {

        GameController controller =
                new GameController(new GameConfig());

        // Turn 1: arrival and seating.
        controller.startNextTurn();

        Customer customer =
                controller.processArrival();

        assertTrue(
                controller.seatCustomer(customer).isSuccess(),
                "Customer should be seated."
        );

        // Turn 2: take order and prepare it.
        controller.startNextTurn();

        assertTrue(
                controller.takeOrder(customer).isSuccess(),
                "Order should be taken."
        );

        Order order =
                customer.getOrder();

        assertTrue(
                controller.prepareOrder(order).isSuccess(),
                "Chef should prepare the order."
        );

        assertEquals(
                OrderStatus.READY,
                order.getStatus(),
                "Order should be READY."
        );

        assertTrue(
                !controller.serveOrder(order).isSuccess(),
                "Order should not be served on the turn it becomes READY."
        );

        // Turn 3: serving is now allowed.
        controller.startNextTurn();

        assertTrue(
                controller.serveOrder(order).isSuccess(),
                "Ready order should be served on the next turn."
        );

        assertEquals(
                OrderStatus.SERVED,
                order.getStatus(),
                "Order should become SERVED."
        );

        assertEquals(
                CustomerStatus.SERVED,
                customer.getStatus(),
                "Customer should become SERVED."
        );

        System.out.println(
                "PASS: Serving timing works correctly."
        );
    }

    // Checks SERVED -> READY_TO_PAY -> payment timing.
    private static void testEatingAndPaymentDelay() {

        GameController controller =
                createServedCustomer();

        Customer customer =
                controller.getRestaurant()
                        .getCustomers()
                        .get(0);

        Order order =
                customer.getOrder();

        int servedTurn =
                controller.getCurrentTurn();

        assertEquals(
                OrderStatus.SERVED,
                order.getStatus(),
                "Order should currently be SERVED."
        );

        // Same turn: should not become ready to pay.
        controller.processEatingAndPayment();

        assertEquals(
                OrderStatus.SERVED,
                order.getStatus(),
                "Customer should not finish eating on serving turn."
        );

        // Next turn: finishes eating.
        controller.startNextTurn();

        controller.processEatingAndPayment();

        assertEquals(
                OrderStatus.READY_TO_PAY,
                order.getStatus(),
                "Customer should become READY_TO_PAY next turn."
        );

        assertEquals(
                servedTurn + 1,
                order.getReadyToPayTurn(),
                "READY_TO_PAY turn should be one turn after serving."
        );

        assertEquals(
                0,
                controller.getRestaurant()
                        .getStats()
                        .getPaidCount(),
                "Customer must not pay immediately."
        );

        // Following turn: payment allowed.
        controller.startNextTurn();

        controller.processEatingAndPayment();

        assertEquals(
                OrderStatus.PAID,
                order.getStatus(),
                "Customer should pay on following turn."
        );

        System.out.println(
                "PASS: Eating and payment timing works correctly."
        );
    }

    // Checks that payment records revenue and frees the table.
    private static void testPaymentAndTableRelease() {

        GameController controller =
                createServedCustomer();

        Customer customer =
                controller.getRestaurant()
                        .getCustomers()
                        .get(0);

        Table table =
                customer.getTable();

        int bill =
                customer.getOrder()
                        .getBillCents();

        controller.startNextTurn();
        controller.processEatingAndPayment();

        controller.startNextTurn();
        controller.processEatingAndPayment();

        assertEquals(
                1,
                controller.getRestaurant()
                        .getStats()
                        .getPaidCount(),
                "Exactly one customer should have paid."
        );

        assertEquals(
                bill,
                controller.getRestaurant()
                        .getStats()
                        .getRevenueCents(),
                "Revenue should equal the customer's bill."
        );

        assertTrue(
                table.isFree(),
                "Table should be free after payment."
        );

        assertEquals(
                CustomerStatus.PAID,
                customer.getStatus(),
                "Customer should be PAID."
        );

        System.out.println(
                "PASS: Payment and table release work correctly."
        );
    }

    // Checks abandonment at satisfaction 0.
    private static void testAbandonment() {

        GameController controller =
                new GameController(new GameConfig());

        controller.startNextTurn();

        Customer customer =
                controller.processArrival();

        assertTrue(
                controller.seatCustomer(customer).isSuccess(),
                "Customer should be seated."
        );

        controller.startNextTurn();

        assertTrue(
                controller.takeOrder(customer).isSuccess(),
                "Order should be taken."
        );

        Table table =
                customer.getTable();

        Order order =
                customer.getOrder();

        for (int i = 0; i < 13; i++) {
            controller.processSatisfactionAndAbandonment();
        }

        assertEquals(
                0,
                customer.getSatisfaction(),
                "Customer satisfaction should reach 0."
        );

        assertEquals(
                CustomerStatus.LEFT,
                customer.getStatus(),
                "Customer should leave."
        );

        assertEquals(
                OrderStatus.CANCELLED,
                order.getStatus(),
                "Unfinished order should be cancelled."
        );

        assertTrue(
                table.isFree(),
                "Table should be freed after abandonment."
        );

        assertEquals(
                1,
                controller.getRestaurant()
                        .getStats()
                        .getUnhappyDepartures(),
                "Departure should be recorded."
        );

        System.out.println(
                "PASS: Abandonment works correctly."
        );
    }

    // Checks victory thresholds.
    private static void testVictoryConditions() {

        GameConfig config =
                new GameConfig();

        GameController controller =
                new GameController(config);

        Restaurant restaurant =
                controller.getRestaurant();

        for (int i = 1; i <= 4; i++) {

            Customer customer =
                    new RegularCustomer(i, 1);

            MenuItem burger =
                    new MenuItem(
                            "Burger",
                            1500,
                            1
                    );

            Order order =
                    new Order(
                            i,
                            customer,
                            burger
                    );

            customer.setOrder(order);

            restaurant.getStats()
                    .recordPayment(
                            customer,
                            order.getBillCents()
                    );
        }

        assertTrue(
                controller.hasWon(),
                "4 paid customers, $60 revenue and 100 average satisfaction should win."
        );

        System.out.println(
                "PASS: Victory conditions work correctly."
        );
    }

    // Checks that Waiter and Chef can each act only once per turn.
    private static void testStaffTaskLimits() {

        GameController controller =
                new GameController(new GameConfig());

        controller.startNextTurn();

        Customer customer =
                controller.processArrival();

        assertTrue(
                controller.seatCustomer(customer).isSuccess(),
                "First Waiter action should succeed."
        );

        assertTrue(
                !controller.takeOrder(customer).isSuccess(),
                "Second Waiter action in the same turn should fail."
        );

        controller.startNextTurn();

        assertTrue(
                controller.takeOrder(customer).isSuccess(),
                "Waiter should be available again next turn."
        );

        Order order =
                customer.getOrder();

        assertTrue(
                controller.prepareOrder(order).isSuccess(),
                "First Chef action should succeed."
        );

        assertTrue(
                !controller.prepareOrder(order).isSuccess(),
                "Second Chef action in the same turn should fail."
        );

        System.out.println(
                "PASS: Staff task limits work correctly."
        );
    }

    // Checks that the Cashier processes the oldest eligible customer first.
    private static void testCashierOldestFirst() {

        Restaurant restaurant =
                new Restaurant(2);

        Customer first =
                new RegularCustomer(1, 1);

        Customer second =
                new RegularCustomer(2, 1);

        MenuItem burger =
                new MenuItem(
                        "Burger",
                        1500,
                        1
                );

        Order firstOrder =
                new Order(
                        1,
                        first,
                        burger
                );

        Order secondOrder =
                new Order(
                        2,
                        second,
                        burger
                );

        first.setOrder(firstOrder);
        second.setOrder(secondOrder);

        restaurant.addCustomer(first);
        restaurant.addCustomer(second);

        restaurant.addOrder(firstOrder);
        restaurant.addOrder(secondOrder);

        firstOrder.prepareOneUnit(1);
        secondOrder.prepareOneUnit(1);

        firstOrder.markServed(2);
        secondOrder.markServed(2);

        firstOrder.markReadyToPay(3);
        secondOrder.markReadyToPay(3);

        first.setStatus(
                CustomerStatus.READY_TO_PAY
        );

        second.setStatus(
                CustomerStatus.READY_TO_PAY
        );

        Cashier cashier =
                new Cashier();

        cashier.processNextPayment(
                restaurant,
                4
        );

        assertEquals(
                OrderStatus.PAID,
                firstOrder.getStatus(),
                "Oldest eligible customer should pay first."
        );

        assertEquals(
                OrderStatus.READY_TO_PAY,
                secondOrder.getStatus(),
                "Second customer should still be waiting to pay."
        );

        System.out.println(
                "PASS: Cashier oldest-first rule works correctly."
        );
    }

    // Creates one customer who has just been served.
    private static GameController createServedCustomer() {

        GameController controller =
                new GameController(new GameConfig());

        // Turn 1: seat customer.
        controller.startNextTurn();

        Customer customer =
                controller.processArrival();

        controller.seatCustomer(customer);

        // Turn 2: take order and prepare it.
        controller.startNextTurn();

        controller.takeOrder(customer);

        Order order =
                customer.getOrder();

        controller.prepareOrder(order);

        // Turn 3: serve ready order.
        controller.startNextTurn();

        controller.serveOrder(order);

        return controller;
    }

    private static void assertTrue(
            boolean condition,
            String message) {

        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void assertEquals(
            int expected,
            int actual,
            String message) {

        if (expected != actual) {

            throw new AssertionError(
                    message
                            + " Expected: "
                            + expected
                            + ", Actual: "
                            + actual
            );
        }
    }

    private static void assertEquals(
            Object expected,
            Object actual,
            String message) {

        if (!expected.equals(actual)) {

            throw new AssertionError(
                    message
                            + " Expected: "
                            + expected
                            + ", Actual: "
                            + actual
            );
        }
    }
}
