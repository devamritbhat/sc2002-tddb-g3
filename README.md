# Restaurant Rush

Group Assignment for SC2002 Object Oriented Design and Programming. Restaurant Rush is a turn-based Java command-line restaurant management game.

Github Repository: [https://github.com/devamritbhat/sc2002-tddb-g3](https://github.com/devamritbhat/sc2002-tddb-g3)

## Quick Start

The game can be compiled and run from the project root folder:

```
rm -rf out && mkdir out
javac -d out $(find src -name "*.java")
java -cp out app.RestaurantRushApp
```

## Game Overview

The game will consist of 18 turns and 2 tables. On every turn, the Waiter chooses one task to be done, while the Chef can prepare one unit of the order and the Cashier processes one payment. The Customers will arrive on turns 1, 4, 7, 10, 13 and 16. In order to win the game, the player must end with at least 4 paid customers, $45.00 revenue and have an average of at least 60 paid-customer satisfaction. Failure to do so results in a DEFEAT.

|Stage|Added features                      |Code                                                                    |
|-----|------------------------------------|------------------------------------------------------------------------|
|1    |Regular customers and the basic game|[`stage-1`](https://github.com/devamritbhat/sc2002-tddb-g3/tree/stage-1)|                                                                              
|2    |VIP and Critic customers            |[`stage-2`](https://github.com/devamritbhat/sc2002-tddb-g3/tree/stage-2)|
|3    |Host, combo meals and Happy Hour    |[`stage-3`](https://github.com/devamritbhat/sc2002-tddb-g3/tree/stage-3)|

In Stage 3, the Host automatically seats the waiting customer if there is a table available. Regular and VIP customers select food based on their ID, while Critics always order Pasta. Happy Hour will only be activated once for 2 turns which will give 20% off new orders and 15 satisfaction recovery to customers who have not been seated yet or still waiting for their food. VIP will receive their 10% discount first.

## Team Members

|Member  |Main responsibilities                             |Stage 2                                 |Stage 3                                         |
|--------|--------------------------------------------------|----------------------------------------|------------------------------------------------|
|Devamrit|`GameController`, architecture and integration    |Arrival types                           |Host and Happy Hour turn flow                   |
|Evan    |`Restaurant`, `WaitingQueue`, `Table`, Waiter     |Customer type and bill display, class diagram|Host seating, UI updates and class diagram |
|Hein    |`Menu`, `MenuItem`, `Order`, Chef                 |Critic food choice                      |`ComboMeal` and the six-choice menu             |
|Keith   |`Cashier`, `GameStats`, victory/defeat and testing|VIP billing and Stage 2 test runs       |Happy Hour billing and Stage 3 test runs        |
|Saajid  |`Customer`, `RegularCustomer`, satisfaction       |`VIPCustomer` and `CriticCustomer`      |`HappyHour` and satisfaction recovery           |

## Automated Tests

Compile the application along with the tests:

```
rm -rf out && mkdir out
javac -d out $(find src test -name "*.java")
java -cp out GameTest
java -cp out SimulationTest
```

- `GameTest` checks for the game rules.
- `SimulationTest` will run through a full victory/defeat scenario.
- Only run one of the scenarios by using `java -cp out SimulationTest victory` or `java -cp out SimulationTest defeat`.

## Test Evidence

Arrival and food choices are fixed to ensure that test runs can be repeatable. The input files contains the menu choices for every test run, and the `*-run.txt` files are the turn-by-turn summaries from `SimulationTest`. Both are saved in `docs/test-runs/`.

|Stage|Run    |Paid|Revenue|Avg. satisfaction|Departures|Input file                                                           |
|-----|-------|---:|------:|----------------:|---------:|---------------------------------------------------------------------|
|1    |VICTORY|4   |$51.00 |78.00            |0         |[`victory-input.txt`](docs/test-runs/victory-input.txt)              |
|1    |DEFEAT |0   |$0.00  |N/A              |2         |[`defeat-input.txt`](docs/test-runs/defeat-input.txt)                |
|2    |VICTORY|4   |$49.50 |79.50            |0         |[`stage2-victory-input.txt`](docs/test-runs/stage2-victory-input.txt)|
|2    |DEFEAT |0   |$0.00  |N/A              |1         |[`stage2-defeat-input.txt`](docs/test-runs/stage2-defeat-input.txt)  |
|3    |VICTORY|5   |$76.50 |88.60            |0         |[`stage3-victory-input.txt`](docs/test-runs/stage3-victory-input.txt)|
|3    |DEFEAT |0   |$0.00  |N/A              |1         |[`stage3-defeat-input.txt`](docs/test-runs/stage3-defeat-input.txt)  |

To replay a run from previous stages, checkout the corresponding Git tag, compile it, then run with its input file. For example, the Stage 2 victory run:

```
git checkout stage-2
rm -rf out && mkdir out
javac -d out $(find src -name "*.java")
java -cp out app.RestaurantRushApp < docs/test-runs/stage2-victory-input.txt
```

Use the same command with another input file for other runs. The matching per-turn outputs are saved as `*-run.txt` under `docs/test-runs/` (for example, `stage2-victory-run.txt`). Run `git checkout main` to return to the latest version.

**Stage 1:** All customers are Regular. Customers 1 and 4 order Salad, 2 and 5 order Burger, and 3 and 6 order Pasta. In the defeat run, Customers 1 and 2 leave after turns 13 and 16.

**Stage 2:** Customer 2 is a VIP and pays $13.50 for a Burger. Customer 5 is a Critic and orders Pasta, which is served too late to pay in the victory run. In the defeat run, only Customer 1 leaves.

**Stage 3:** The Host seats customers as they arrive, while Customers 4 and 6 order combos. In the victory run, Happy Hour is used on turns 13–14. The Critic’s Pasta costs $14.40 and their satisfaction recovers from 88 to 100 on turn 14. Customer 6’s combo is ready on turn 18, too late to serve. In the defeat run, the Waiter and Chef wait, and Customer 1 leaves.

### Input File Format

Each line contains the next menu choice requested by the game:

- Happy Hour: `1` start, `2` not now (asked until used)
- Waiter: `1` seat, `2` take order, `3` serve (followed by customer/order ID), `4` wait
- Chef: `1` prepare (followed by order ID), `2` wait

## UML Diagrams

- [Class diagram](docs/uml/class-diagram.png)
- [Sequence diagram](docs/uml/sequence-diagram.png) (Stage 3 victory run, turns 13–18)

## Stage Transitions

The tables bellow shows the files added or changed between stages.

### Stage 1 to Stage 2
Since `Customer` was already abstract, only small changes were needed. The Stage 1 rules and tests still work.

|Change       |Files                                                                                   |Reason                                                                                          |
|-------------|----------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------|
|New          |`VIPCustomer.java`, `CriticCustomer.java`                                               |Added the two customer types                                                                    |
|Changed      |`Customer.java`, `RegularCustomer.java`                                                 |Added `chooseItem()`, `calculateBillCents()` and `getTypeName()` for customer-specific behaviour|
|Changed      |`Order.java`, `Menu.java`                                                               |Added customer-based billing and `findItem()` for the Critic’s Pasta                            |
|Changed      |`ArrivalSchedule.java`, `GameController.java`                                           |Customer creation moved to `createCustomer()`; customers now choose their food                  |
|Changed      |`RestaurantUI.java`                                                                     |Displays customer types and bills                                                               |
|Renamed      |`Stage1Test.java` → `GameTest.java`; `Stage1SimulationTest.java` → `SimulationTest.java`|Tests now cover later stages                                                                    |
|New / Changed|`docs/`                                                                                 |Stage 2 runs and class diagram                                                                  |

Compare: https://github.com/devamritbhat/sc2002-tddb-g3/compare/stage-1...stage-2

### Stage 2 to Stage 3
Stage 3 adds Host and Happy Hour steps to the turn flow. The food-choice rule now has six options, so Customers 4 and 6 order combos. One Stage 2 test was updated to use Customer 1, while the remaining earlier tests still pass.

|Change       |Files                                          |Reason                                                            |
|-------------|-----------------------------------------------|------------------------------------------------------------------|
|New          |`Host.java`, `ComboMeal.java`, `HappyHour.java`|Added Host seating, combo meals and Happy Hour                    |
|Changed      |`Menu.java`, `Order.java`, `Waiter.java`       |Six menu choices and Happy Hour pricing when an order is taken    |
|Changed      |`Customer.java`, `Restaurant.java`             |Added satisfaction recovery and stored `HappyHour` in `Restaurant`|
|Changed      |`GameController.java`, `RestaurantUI.java`     |Added Host and Happy Hour turn steps and UI messages              |
|Changed      |`Cashier.java`, `Chef.java`                    |Formatting changes only; behaviour unchanged                      |
|Changed      |`GameTest.java`, `SimulationTest.java`         |Added Stage 3 checks and simulations                              |
|New / Changed|`docs/`                                        |Stage 3 runs and updated UML diagrams                             |

Compare: https://github.com/devamritbhat/sc2002-tddb-g3/compare/stage-2...stage-3

## Video

Video link: https://drive.google.com/file/d/1tJqsyPVPj-r9mpX0enZ8SSOVLzMqxdhc/view?usp=sharing

|#|Segment                             |Presenter|Timestamp|
|-|------------------------------------|---------|---------|
|1|Game launching from the submitted project|Devamrit|00:00|
|2|Manager choices with constrained staff|Evan|01:32|
|3|Dish READY → SERVED → payment|Hein|02:44|
|4|VIP and Critic behaviour|Saajid|04:17|
|5|A Stage 3 feature, victory and defeat|Keith|05:30|
|6|Stage tags, comparisons and class diagrams|Devamrit|09:30|
|7|Use case and sequence diagram|Saajid|11:55|
