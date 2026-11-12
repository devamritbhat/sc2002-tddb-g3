import control.GameConfig;
import control.GameController;

import domain.Cashier;
import domain.CriticCustomer;
import domain.ComboMeal;
import domain.Customer;
import domain.HappyHour;
import domain.Menu;
import domain.MenuItem;
import domain.Order;
import domain.RegularCustomer;
import domain.Restaurant;
import domain.Table;
import domain.VIPCustomer;

import support.CustomerStatus;
import support.OrderStatus;

// Automated checks for the game rules.
public class GameTest {

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

        // Stage 2 customer types.
        testArrivalCustomerTypes();
        testRegularCustomerUnchanged();
        testVIPCustomer();
        testCriticCustomer();
        testDiscountLockedWhenOrdered();

        // Stage 3: Host, ComboMeal and Happy Hour.
        testComboMeals();
        testStage3FoodChoices();
        testHostSeating();
        testHappyHourDuration();
        testHappyHourPricing();
        testHappyHourRecovery();

        System.out.println();
        System.out.println("All tests passed.");
    }

    // Checks the customer type of each arrival.
    private static void testArrivalCustomerTypes() {

        GameController controller =
                new GameController(new GameConfig());

        String[] expectedTypes =
                {"Regular", "VIP", "Regular", "Regular", "Critic", "Regular"};

        int arrivals = 0;

        for (int turn = 1; turn <= 18; turn++) {

            controller.startNextTurn();

            Customer customer =
                    controller.processArrival();

            if (customer != null) {

                assertEquals(
                        expectedTypes[arrivals],
                        customer.getTypeName(),
                        "Customer " + customer.getId() + " has the wrong type."
                );

                arrivals++;
            }
        }

        assertEquals(
                6,
                arrivals,
                "There should still be exactly 6 arrivals."
        );

        System.out.println(
                "PASS: Arrival customer types are correct."
        );
    }

    // Checks that Regular customers still follow the Stage 1 rules.
    private static void testRegularCustomerUnchanged() {

        Menu menu = new Menu();

        Customer customer =
                new RegularCustomer(1, 1);

        assertEquals(
                "Salad",
                customer.chooseItem(menu).getName(),
                "Regular customer 1 should order Salad."
        );

        assertEquals(
                1500,
                customer.calculateBillCents(1500),
                "Regular customers should pay full price."
        );

        System.out.println(
                "PASS: Regular customers are unchanged."
        );
    }

    // Checks VIP satisfaction loss and the 10% discount.
    private static void testVIPCustomer() {

        Customer vip =
                new VIPCustomer(2, 4);

        vip.loseSatisfaction();

        assertEquals(
                95,
                vip.getSatisfaction(),
                "VIP should lose 5 satisfaction per turn."
        );

        assertEquals(
                810,
                vip.calculateBillCents(900),
                "VIP Salad should cost $8.10."
        );

        assertEquals(
                1350,
                vip.calculateBillCents(1500),
                "VIP Burger should cost $13.50."
        );

        assertEquals(
                1620,
                vip.calculateBillCents(1800),
                "VIP Pasta should cost $16.20."
        );

        assertEquals(
                "Burger",
                vip.chooseItem(new Menu()).getName(),
                "VIP customer 2 should follow the ID rule and order Burger."
        );

        System.out.println(
                "PASS: VIP customer works correctly."
        );
    }

    // Checks Critic satisfaction loss and Pasta preference.
    private static void testCriticCustomer() {

        Menu menu = new Menu();

        // Customer 1 would order Salad under the ID rule.
        Customer critic =
                new CriticCustomer(1, 1);

        critic.loseSatisfaction();

        assertEquals(
                88,
                critic.getSatisfaction(),
                "Critic should lose 12 satisfaction per turn."
        );

        assertEquals(
                "Pasta",
                critic.chooseItem(menu).getName(),
                "Critic should always order Pasta."
        );

        assertEquals(
                1800,
                critic.calculateBillCents(1800),
                "Critic should pay full price."
        );

        for (int turn = 0; turn < 10; turn++) {
            critic.loseSatisfaction();
        }

        assertEquals(
                0,
                critic.getSatisfaction(),
                "Critic satisfaction should not go below 0."
        );

        System.out.println(
                "PASS: Critic customer works correctly."
        );
    }

    // Checks that the VIP discount is fixed when ordering.
    private static void testDiscountLockedWhenOrdered() {

        GameController controller =
                new GameController(new GameConfig());

        // Skip to turn 4, when the VIP arrives.
        for (int turn = 1; turn <= 3; turn++) {
            controller.startNextTurn();
            controller.processArrival();
            controller.waiterWait();
            controller.chefWait();
            controller.processEatingAndPayment();
            controller.processSatisfactionAndAbandonment();
        }

        controller.startNextTurn();
        Customer vip = controller.processArrival();

        assertEquals(
                "VIP",
                vip.getTypeName(),
                "Customer 2 should be a VIP."
        );

        assertTrue(
                controller.seatCustomer(vip).isSuccess(),
                "VIP should be seated."
        );

        controller.startNextTurn();

        assertTrue(
                controller.takeOrder(vip).isSuccess(),
                "VIP order should be taken."
        );

        assertEquals(
                1350,
                vip.getOrder().getBillCents(),
                "VIP Burger bill should be locked at $13.50."
        );

        System.out.println(
                "PASS: VIP discount is locked when ordering."
        );
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

    // Checks combo prices and preparation units.
    private static void testComboMeals() {

        Menu menu = new Menu();

        assertEquals(
                6,
                menu.getItems().size(),
                "Menu should have 3 items and 3 combos."
        );

        String[] names = {
                "Salad + Burger Combo",
                "Salad + Pasta Combo",
                "Burger + Pasta Combo"
        };
        int[] prices = {2160, 2430, 2970};
        int[] units = {2, 3, 3};

        for (int i = 0; i < 3; i++) {

            MenuItem combo = menu.getItem(3 + i);

            assertTrue(
                    combo instanceof ComboMeal,
                    names[i] + " should be a ComboMeal."
            );

            assertEquals(names[i], combo.getName(), "Wrong combo name.");
            assertEquals(prices[i], combo.getPriceCents(), names[i] + " has the wrong price.");
            assertEquals(units[i], combo.getPreparationUnits(), names[i] + " has the wrong preparation units.");

            assertEquals(
                    2,
                    ((ComboMeal) combo).getComponents().size(),
                    "A combo should have two components."
            );
        }

        System.out.println(
                "PASS: Combo meals are correct."
        );
    }

    // Checks that customers cycle through all six menu choices.
    private static void testStage3FoodChoices() {

        Menu menu = new Menu();

        String[] expected = {
                "Salad", "Burger", "Pasta",
                "Salad + Burger Combo", "Salad + Pasta Combo", "Burger + Pasta Combo"
        };

        for (int id = 1; id <= 6; id++) {

            assertEquals(
                    expected[id - 1],
                    new RegularCustomer(id, 1).chooseItem(menu).getName(),
                    "Customer " + id + " chose the wrong item."
            );
        }

        assertEquals(
                "Pasta",
                new CriticCustomer(5, 13).chooseItem(menu).getName(),
                "The Critic should still order Pasta."
        );

        System.out.println(
                "PASS: Stage 3 food choices are correct."
        );
    }

    // Checks that the Host seats at most one customer per turn and the Waiter can still seat.
    private static void testHostSeating() {

        GameController controller =
                new GameController(new GameConfig());

        // Turns 1 to 4 with no seating, so Customers 1 and 2 are both waiting.
        for (int turn = 1; turn <= 4; turn++) {
            controller.startNextTurn();
            controller.processArrival();
        }

        Customer first = getCustomer(controller, 1);
        Customer second = getCustomer(controller, 2);

        assertTrue(
                controller.processHost().isSuccess(),
                "Host should seat the first waiting customer."
        );

        assertEquals(
                CustomerStatus.SEATED,
                first.getStatus(),
                "Host should seat Customer 1 first."
        );

        assertTrue(
                !controller.processHost().isSuccess(),
                "Host should seat at most one customer per turn."
        );

        assertTrue(
                controller.seatCustomer(second).isSuccess(),
                "Waiter should still be able to seat a customer."
        );

        controller.startNextTurn();

        assertTrue(
                !controller.processHost().isSuccess(),
                "Host should not seat anyone without a waiting customer and a free table."
        );

        System.out.println(
                "PASS: Host seating works correctly."
        );
    }

    // Checks that Happy Hour lasts two turns and can only be used once.
    private static void testHappyHourDuration() {

        GameController controller =
                new GameController(new GameConfig());

        HappyHour happyHour =
                controller.getRestaurant().getHappyHour();

        controller.startNextTurn();

        assertTrue(
                controller.activateHappyHour().isSuccess(),
                "Happy Hour should start."
        );

        assertEquals(2, happyHour.getRemainingTurns(), "Happy Hour should have 2 turns left.");

        controller.startNextTurn();

        assertTrue(happyHour.isActive(), "Happy Hour should still be active on the next turn.");
        assertEquals(1, happyHour.getRemainingTurns(), "Happy Hour should have 1 turn left.");

        controller.startNextTurn();

        assertTrue(!happyHour.isActive(), "Happy Hour should end after two turns.");

        assertTrue(
                !controller.activateHappyHour().isSuccess(),
                "Happy Hour can only be used once per game."
        );

        System.out.println(
                "PASS: Happy Hour duration works correctly."
        );
    }

    // Checks Happy Hour prices: VIP discount first, then 20% off, locked when ordered.
    private static void testHappyHourPricing() {

        Menu menu = new Menu();
        MenuItem burger = menu.getItem(1);
        MenuItem pasta = menu.getItem(2);
        MenuItem saladPasta = menu.getItem(4);

        HappyHour happyHour = new HappyHour();

        Customer vip = new VIPCustomer(2, 4);
        Customer regular = new RegularCustomer(3, 7);

        // Ordered before Happy Hour starts.
        Order before = new Order(1, regular, pasta, happyHour);

        happyHour.activate();

        Order vipBurger = new Order(2, vip, burger, happyHour);
        Order regularPasta = new Order(3, regular, pasta, happyHour);
        Order vipCombo = new Order(4, vip, saladPasta, happyHour);

        assertEquals(1800, before.getBillCents(), "An order taken before Happy Hour keeps its price.");
        assertEquals(1080, vipBurger.getBillCents(), "VIP Burger in Happy Hour should cost $10.80.");
        assertEquals(1440, regularPasta.getBillCents(), "Regular Pasta in Happy Hour should cost $14.40.");
        assertEquals(1750, vipCombo.getBillCents(), "VIP Salad + Pasta Combo in Happy Hour should cost $17.50.");

        assertTrue(vipBurger.isHappyHourPrice(), "Order should be marked as a Happy Hour price.");
        assertTrue(!before.isHappyHourPrice(), "Order before Happy Hour should not be marked.");

        // Happy Hour ends: new orders are full price, earlier bills stay the same.
        happyHour.nextTurn();
        happyHour.nextTurn();

        Order after = new Order(5, regular, pasta, happyHour);

        assertEquals(1800, after.getBillCents(), "An order after Happy Hour is full price.");
        assertEquals(1080, vipBurger.getBillCents(), "A Happy Hour bill stays locked.");

        System.out.println(
                "PASS: Happy Hour pricing works correctly."
        );
    }

    // Checks that only customers waiting for a table or food recover, up to 100.
    private static void testHappyHourRecovery() {

        GameController controller =
                new GameController(new GameConfig());

        // Customer 1 waits for 3 turns: 100 - 24 = 76.
        for (int turn = 1; turn <= 3; turn++) {
            controller.startNextTurn();
            controller.processArrival();
            controller.processSatisfactionAndAbandonment();
        }

        Customer waiting = getCustomer(controller, 1);

        assertEquals(76, waiting.getSatisfaction(), "Customer 1 should have 76 satisfaction.");

        controller.startNextTurn();

        assertTrue(
                !controller.processHappyHourRecovery().isSuccess(),
                "No recovery before Happy Hour starts."
        );

        controller.activateHappyHour();
        controller.processHappyHourRecovery();

        assertEquals(91, waiting.getSatisfaction(), "Waiting customer should recover 15.");

        // Customer 2 arrives with 100 and stays at 100.
        Customer vip = controller.processArrival();
        controller.processHappyHourRecovery();

        assertEquals(100, vip.getSatisfaction(), "Satisfaction should not go above 100.");

        // A customer who has left does not recover.
        GameController later =
                new GameController(new GameConfig());

        // Customer 1 waits until they leave after turn 13.
        for (int turn = 1; turn <= 13; turn++) {
            later.startNextTurn();
            later.processArrival();
            later.processSatisfactionAndAbandonment();
        }

        Customer left = getCustomer(later, 1);

        later.startNextTurn();
        later.activateHappyHour();
        later.processHappyHourRecovery();

        assertTrue(left.hasLeft(), "Customer 1 should have left.");
        assertEquals(0, left.getSatisfaction(), "A customer who left should not recover.");

        System.out.println(
                "PASS: Happy Hour recovery works correctly."
        );
    }

    // Finds a customer by ID.
    private static Customer getCustomer(
            GameController controller,
            int id) {

        for (Customer customer :
                controller.getRestaurant().getCustomers()) {

            if (customer.getId() == id) {
                return customer;
            }
        }

        throw new AssertionError("Customer " + id + " not found.");
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
