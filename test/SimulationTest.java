import control.GameConfig;
import control.GameController;

import domain.Customer;
import domain.Order;
import domain.Restaurant;

// Full-game simulations.
public class SimulationTest {

    public static void main(String[] args) {

        if (args.length == 0) {

            testVictoryRun();
            testDefeatRun();

            System.out.println();
            System.out.println(
                    "All simulation tests passed."
            );

        } else if (
                args[0].equalsIgnoreCase("victory")
        ) {

            testVictoryRun();

        } else if (
                args[0].equalsIgnoreCase("defeat")
        ) {

            testDefeatRun();

        } else {

            System.out.println(
                    "Usage: SimulationTest [victory|defeat]"
            );
        }
    }

    // Runs a complete 18-turn strategy that should win.
    private static void testVictoryRun() {

        GameController controller =
                new GameController(new GameConfig());

        System.out.println("=== VICTORY RUN ===");

        for (int turn = 1; turn <= 18; turn++) {

            controller.startNextTurn();

            // Happy Hour starts on turn 13 and lasts turns 13 and 14.
            if (turn == 13) {
                assertSuccess(
                        controller.activateHappyHour(),
                        "Turn 13: Start Happy Hour"
                );
            }

            controller.processHappyHourRecovery();
            controller.processArrival();
            controller.processHost();

            runVictoryWaiterAction(
                    controller,
                    turn
            );

            runVictoryChefAction(
                    controller,
                    turn
            );

            controller.processEatingAndPayment();

            controller
                    .processSatisfactionAndAbandonment();

            printTurnSummary(
                    controller,
                    turn
            );
        }

        Restaurant restaurant =
                controller.getRestaurant();

        int paid =
                restaurant.getStats()
                        .getPaidCount();

        int revenue =
                restaurant.getStats()
                        .getRevenueCents();

        double average =
                restaurant.getStats()
                        .getAveragePaidSatisfaction();

        assertTrue(
                controller.isGameOver(),
                "Victory simulation should finish all 18 turns."
        );

        assertEquals(
                2160,
                getOrder(controller, 4).getBillCents(),
                "Customer 4's Salad + Burger Combo should cost $21.60."
        );

        assertEquals(
                1440,
                getOrder(controller, 5).getBillCents(),
                "The Critic's Pasta taken in Happy Hour should cost $14.40."
        );

        assertTrue(
                controller.hasWon(),
                "The victory strategy should win."
        );

        System.out.println();
        System.out.println(
                "PASS: Full victory simulation succeeded."
        );

        System.out.println(
                "Paid customers: " + paid
        );

        System.out.printf(
                "Revenue: $%.2f%n",
                revenue / 100.0
        );

        System.out.printf(
                "Average satisfaction: %.2f%n",
                average
        );
    }

    // Waiter actions for the winning strategy. The Host seats every customer on arrival.
    private static void runVictoryWaiterAction(
            GameController controller,
            int turn) {

        switch (turn) {

            case 1:
            case 4:
            case 7:
            case 10:
            case 13:
            case 16:
                // Take the order of the customer the Host just seated.
                int customerId = (turn + 2) / 3;

                assertSuccess(
                        controller.takeOrder(
                                getCustomer(controller, customerId)
                        ),
                        "Turn " + turn + ": Take Customer " + customerId + " order"
                );
                break;

            case 2:
            case 5:
            case 9:
            case 12:
            case 15:
                int servedId =
                        turn == 2 ? 1
                        : turn == 5 ? 2
                        : turn == 9 ? 3
                        : turn == 12 ? 4
                        : 5;

                assertSuccess(
                        controller.serveOrder(
                                getOrder(controller, servedId)
                        ),
                        "Turn " + turn + ": Serve Customer " + servedId
                );
                break;

            default:
                assertSuccess(
                        controller.waiterWait(),
                        "Turn " + turn + ": Waiter waits"
                );
                break;
        }
    }

    // Chef actions for the winning strategy.
    private static void runVictoryChefAction(
            GameController controller,
            int turn) {

        int customerId;

        switch (turn) {

            case 1: customerId = 1; break;
            case 4: customerId = 2; break;
            case 7:
            case 8: customerId = 3; break;
            case 10:
            case 11: customerId = 4; break;
            case 13:
            case 14: customerId = 5; break;
            case 16:
            case 17:
            case 18: customerId = 6; break;
            default: customerId = 0; break;
        }

        if (customerId == 0) {
            assertSuccess(
                    controller.chefWait(),
                    "Turn " + turn + ": Chef waits"
            );
            return;
        }

        assertSuccess(
                controller.prepareOrder(
                        getOrder(controller, customerId)
                ),
                "Turn " + turn + ": Prepare Customer " + customerId + " order"
        );
    }

    // Runs a complete game where the Waiter and Chef always wait.
    private static void testDefeatRun() {

        GameController controller =
                new GameController(new GameConfig());

        System.out.println();
        System.out.println("=== DEFEAT RUN ===");

        for (int turn = 1; turn <= 18; turn++) {

            controller.startNextTurn();

            controller.processHappyHourRecovery();
            controller.processArrival();

            // The Host still seats customers automatically.
            controller.processHost();

            assertSuccess(
                    controller.waiterWait(),
                    "Waiter waits"
            );

            assertSuccess(
                    controller.chefWait(),
                    "Chef waits"
            );

            controller.processEatingAndPayment();

            controller
                    .processSatisfactionAndAbandonment();

            printTurnSummary(
                    controller,
                    turn
            );
        }

        Restaurant restaurant =
                controller.getRestaurant();

        assertTrue(
                controller.isGameOver(),
                "Defeat simulation should still run all 18 turns."
        );

        assertTrue(
                !controller.hasWon(),
                "Waiting every turn should result in defeat."
        );

        assertEquals(
                0,
                restaurant.getStats()
                        .getPaidCount(),
                "No customers should have paid."
        );

        assertEquals(
                0,
                restaurant.getStats()
                        .getRevenueCents(),
                "Revenue should be $0."
        );

        System.out.println();
        System.out.println(
                "PASS: Full defeat simulation succeeded."
        );
    }

    // Finds a customer by ID.
    private static Customer getCustomer(
            GameController controller,
            int id) {

        for (Customer customer :
                controller.getRestaurant()
                        .getCustomers()) {

            if (customer.getId() == id) {
                return customer;
            }
        }

        throw new AssertionError(
                "Customer " + id + " not found."
        );
    }

    // Finds the order belonging to a customer.
    private static Order getOrder(
            GameController controller,
            int customerId) {

        Customer customer =
                getCustomer(
                        controller,
                        customerId
                );

        if (customer.getOrder() == null) {
            throw new AssertionError(
                    "Customer "
                            + customerId
                            + " has no order."
            );
        }

        return customer.getOrder();
    }

    // Prints a short summary for each simulated turn.
    private static void printTurnSummary(
            GameController controller,
            int turn) {

        Restaurant restaurant =
                controller.getRestaurant();

        System.out.printf(
                "Turn %2d | Paid: %d | Revenue: $%.2f | Departures: %d%n",
                turn,
                restaurant.getStats()
                        .getPaidCount(),
                restaurant.getStats()
                        .getRevenueCents()
                        / 100.0,
                restaurant.getStats()
                        .getUnhappyDepartures()
        );
    }

    // Checks that an attempted action succeeded.
    private static void assertSuccess(
            support.ActionResult result,
            String message) {

        if (!result.isSuccess()) {

            throw new AssertionError(
                    message
                            + " failed: "
                            + result.getMessage()
            );
        }
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
}
