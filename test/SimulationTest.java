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
            controller.processArrival();

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

        assertTrue(
                paid >= 4,
                "Victory requires at least 4 paid customers."
        );

        assertTrue(
                revenue >= 4500,
                "Victory requires at least $45 revenue."
        );

        assertTrue(
                average >= 60,
                "Victory requires average satisfaction of at least 60."
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

    // Defines the Waiter's action for the winning strategy.
    private static void runVictoryWaiterAction(
            GameController controller,
            int turn) {

        switch (turn) {

            case 1:
                assertSuccess(
                        controller.seatCustomer(
                                getCustomer(
                                        controller,
                                        1
                                )
                        ),
                        "Turn 1: Seat Customer 1"
                );
                break;

            case 2:
                assertSuccess(
                        controller.takeOrder(
                                getCustomer(
                                        controller,
                                        1
                                )
                        ),
                        "Turn 2: Take Customer 1 order"
                );
                break;

            case 3:
                assertSuccess(
                        controller.serveOrder(
                                getOrder(
                                        controller,
                                        1
                                )
                        ),
                        "Turn 3: Serve Customer 1"
                );
                break;

            case 4:
                assertSuccess(
                        controller.seatCustomer(
                                getCustomer(
                                        controller,
                                        2
                                )
                        ),
                        "Turn 4: Seat Customer 2"
                );
                break;

            case 5:
                assertSuccess(
                        controller.takeOrder(
                                getCustomer(
                                        controller,
                                        2
                                )
                        ),
                        "Turn 5: Take Customer 2 order"
                );
                break;

            case 6:
                assertSuccess(
                        controller.serveOrder(
                                getOrder(
                                        controller,
                                        2
                                )
                        ),
                        "Turn 6: Serve Customer 2"
                );
                break;

            case 7:
                assertSuccess(
                        controller.seatCustomer(
                                getCustomer(
                                        controller,
                                        3
                                )
                        ),
                        "Turn 7: Seat Customer 3"
                );
                break;

            case 8:
                assertSuccess(
                        controller.takeOrder(
                                getCustomer(
                                        controller,
                                        3
                                )
                        ),
                        "Turn 8: Take Customer 3 order"
                );
                break;

            case 10:
                assertSuccess(
                        controller.seatCustomer(
                                getCustomer(
                                        controller,
                                        4
                                )
                        ),
                        "Turn 10: Seat Customer 4"
                );
                break;

            case 11:
                assertSuccess(
                        controller.serveOrder(
                                getOrder(
                                        controller,
                                        3
                                )
                        ),
                        "Turn 11: Serve Customer 3"
                );
                break;

            case 12:
                assertSuccess(
                        controller.takeOrder(
                                getCustomer(
                                        controller,
                                        4
                                )
                        ),
                        "Turn 12: Take Customer 4 order"
                );
                break;

            case 13:
                assertSuccess(
                        controller.serveOrder(
                                getOrder(
                                        controller,
                                        4
                                )
                        ),
                        "Turn 13: Serve Customer 4"
                );
                break;

            case 14:
                assertSuccess(
                        controller.seatCustomer(
                                getCustomer(
                                        controller,
                                        5
                                )
                        ),
                        "Turn 14: Seat Customer 5"
                );
                break;

            case 15:
                assertSuccess(
                        controller.takeOrder(
                                getCustomer(
                                        controller,
                                        5
                                )
                        ),
                        "Turn 15: Take Customer 5 order"
                );
                break;

            case 17:
                assertSuccess(
                        controller.serveOrder(
                                getOrder(
                                        controller,
                                        5
                                )
                        ),
                        "Turn 17: Serve Customer 5 order"
                );
                break;

            default:
                assertSuccess(
                        controller.waiterWait(),
                        "Waiter waits"
                );
                break;
        }
    }

    // Defines the Chef's action for the winning strategy.
    private static void runVictoryChefAction(
            GameController controller,
            int turn) {

        switch (turn) {

            case 2:
                assertSuccess(
                        controller.prepareOrder(
                                getOrder(
                                        controller,
                                        1
                                )
                        ),
                        "Turn 2: Prepare Customer 1 order"
                );
                break;

            case 5:
                assertSuccess(
                        controller.prepareOrder(
                                getOrder(
                                        controller,
                                        2
                                )
                        ),
                        "Turn 5: Prepare Customer 2 order"
                );
                break;

            case 8:
            case 9:
                assertSuccess(
                        controller.prepareOrder(
                                getOrder(
                                        controller,
                                        3
                                )
                        ),
                        "Prepare Customer 3 order"
                );
                break;

            case 12:
                assertSuccess(
                        controller.prepareOrder(
                                getOrder(
                                        controller,
                                        4
                                )
                        ),
                        "Turn 12: Prepare Customer 4 order"
                );
                break;

            // The Critic's Pasta needs 2 units.
            case 15:
            case 16:
                assertSuccess(
                        controller.prepareOrder(
                                getOrder(
                                        controller,
                                        5
                                )
                        ),
                        "Prepare Customer 5 order"
                );
                break;

            default:
                assertSuccess(
                        controller.chefWait(),
                        "Chef waits"
                );
                break;
        }
    }

    // Runs a complete game where both staff always wait.
    private static void testDefeatRun() {

        GameController controller =
                new GameController(new GameConfig());

        System.out.println();
        System.out.println("=== DEFEAT RUN ===");

        for (int turn = 1; turn <= 18; turn++) {

            controller.startNextTurn();

            controller.processArrival();

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
