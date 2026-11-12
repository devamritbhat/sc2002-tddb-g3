# Restaurant Rush

Restaurant Rush is a Java command-line restaurant management game developed for the SC2002 Object-Oriented Design & Programming group assignment.

Repository: [https://github.com/devamritbhat/sc2002-tddb-g3.git](https://github.com/devamritbhat/sc2002-tddb-g3.git)

The game was developed in 3 stages using the same codebase. Each completed stage is saved using Git tags so that the changes between stages can be compared:

| **Stage** | **Code at tag**                                                        | **Compare**                                                                                   |
| --------- | ---------------------------------------------------------------------- | --------------------------------------------------------------------------------------------- |
| Stage 1   | [stage-1](https://github.com/devamritbhat/sc2002-tddb-g3/tree/stage-1) | —                                                                                             |
| Stage 2   | [stage-2](https://github.com/devamritbhat/sc2002-tddb-g3/tree/stage-2) | [stage-1...stage-2](https://github.com/devamritbhat/sc2002-tddb-g3/compare/stage-1...stage-2) |
| Stage 3   | [stage-3](https://github.com/devamritbhat/sc2002-tddb-g3/tree/stage-3) | [stage-2...stage-3](https://github.com/devamritbhat/sc2002-tddb-g3/compare/stage-2...stage-3) |

---

## Team Members

- Devamrit
- Evan
- Hein
- Keith
- Saajid

The table below shows the main parts that each member worked on for the project and the different stages:

| **Member** | **Primary Area**                                                       | **Stage 2**                                                                          | **Stage 3**                                                                                |
| ---------- | ---------------------------------------------------------------------- | ------------------------------------------------------------------------------------ | ------------------------------------------------------------------------------------------ |
| Devamrit   | Overall architecture, `GameController`, game integration and lifecycle | Arrival types in `ArrivalSchedule` and `GameController`                              | Stage 3 turn flow in `GameController`: Host step, Happy Hour start, recovery and countdown |
| Evan       | `Restaurant`, `WaitingQueue`, `Table`, Waiter, seating and serving     | Customer type and bill display in `RestaurantUI`, class diagram update               | `Host` seating; Host, menu and Happy Hour display in `RestaurantUI`; class diagram update  |
| Hein       | `Menu`, `MenuItem`, `Order`, Chef and food preparation                 | Critic's Pasta choice: `chooseItem()` and `Menu.findItem()`                          | `ComboMeal` and the six-choice menu in `Menu`                                              |
| Keith      | `Cashier`, `GameStats`, victory/defeat conditions and testing          | VIP discount: `calculateBillCents()` and the bill in `Order`; Stage 2 tests and runs | Happy Hour pricing in `Order` (VIP discount first, then 20% off); Stage 3 tests and runs   |
| Saajid     | `Customer`, `RegularCustomer`, satisfaction and abandonment            | `VIPCustomer` and `CriticCustomer`: satisfaction loss and customer types             | `HappyHour` and `Customer.recoverSatisfaction()`                                           |

---

# Current Implementation

Stage 3 is the current version of the game. It includes Regular, VIP and Critic customers, the Host, combo meals and Happy Hour.

## Features

The implementation includes:

- 18-turn game
- 2 restaurant tables
- Regular, VIP and Critic customers
- Host who seats one waiting customer per turn automatically
- Three combo meals
- Happy Hour, once per game
- Scheduled customer arrivals
- Waiting queue
- Customer seating
- Order taking
- Automatic customer menu selection
- Chef food preparation
- Waiter serving
- Eating period
- Automatic Cashier payment
- Satisfaction reduction
- Customer abandonment at 0 satisfaction
- Table release after payment or abandonment
- Revenue tracking
- Paid-customer satisfaction tracking
- Victory and defeat evaluation
- Invalid action handling without consuming the staff task

### Menu

| **Item**             | **Price** | **Preparation Units** |
| -------------------- | --------- | --------------------- |
| Salad                | $9.00     | 1                     |
| Burger               | $15.00    | 1                     |
| Pasta                | $18.00    | 2                     |
| Salad + Burger Combo | $21.60    | 2                     |
| Salad + Pasta Combo  | $24.30    | 3                     |
| Burger + Pasta Combo | $29.70    | 3                     |

For combo meals, the preparation units are added together and the price is 90% of the combined price, rounded to the nearest cent.

Customers choose their menu item automatically by customer ID, cycling through all six choices: Customer 1 Salad, 2 Burger, 3 Pasta, 4 Salad + Burger Combo, 5 Salad + Pasta Combo and 6 Burger + Pasta Combo. Critics always order Pasta.

### Customer Types

| **Type** | **Satisfaction loss per waiting turn** | **Bill**   | **Food choice**              |
| -------- | -------------------------------------- | ---------- | ---------------------------- |
| Regular  | 8                                      | Full price | By customer ID (six choices) |
| VIP      | 5                                      | 10% off    | By customer ID (six choices) |
| Critic   | 12                                     | Full price | Always Pasta                 |

Customer 2 (turn 4) is a VIP and Customer 5 (turn 13) is a Critic. The other four arrivals are Regular.

### Happy Hour

- The manager can start it once per game, at the start of a turn. It lasts that turn and the next.
- Orders taken while it is on get 20% off, after the VIP discount. The bill is fixed when the order is taken.
- At the start of each Happy Hour turn, customers still waiting for a table or food recover 15 satisfaction, up to 100.
- The status shows whether it is available, active (with turns left) or used.

---

# Turn Flow

Each turn follows this sequence:

1. Happy Hour choice, while it is still available, and Happy Hour recovery if it is on
2. Customer arrival, if scheduled
3. Host seats the first waiting customer, if a table is free
4. Waiter action
5. Chef action
6. Eating progression and automatic Cashier payment
7. Satisfaction loss and abandonment
8. End-of-turn summary

The Host acts automatically and seats at most one customer per turn. The Waiter can still seat customers as well.

The Waiter may perform at most one task per turn:

- Seat a customer
- Take an order
- Serve a ready order
- Wait

The Chef may:

- Advance one order by one preparation unit
- Wait

The Cashier operates automatically and processes at most one eligible payment per turn.

---

# Victory Conditions

The game still runs for all 18 turns. To win, all 3 conditions must be met at the end of turn 18:

- At least 4 customers have paid
- Revenue is at least $45.00
- Average satisfaction of paid customers is at least 60

Otherwise, the result is defeat.

---

# Project Structure

```
sc2002-tddb-g3/
├── README.md
├── src/
│   ├── app/
│   │   └── RestaurantRushApp.java
│   │
│   ├── boundary/
│   │   └── RestaurantUI.java
│   │
│   ├── control/
│   │   ├── ArrivalSchedule.java
│   │   ├── GameConfig.java
│   │   └── GameController.java
│   │
│   ├── domain/
│   │   ├── Cashier.java
│   │   ├── Chef.java
│   │   ├── ComboMeal.java
│   │   ├── CriticCustomer.java
│   │   ├── Customer.java
│   │   ├── GameStats.java
│   │   ├── HappyHour.java
│   │   ├── Host.java
│   │   ├── Menu.java
│   │   ├── MenuItem.java
│   │   ├── Order.java
│   │   ├── RegularCustomer.java
│   │   ├── Restaurant.java
│   │   ├── Table.java
│   │   ├── VIPCustomer.java
│   │   ├── Waiter.java
│   │   └── WaitingQueue.java
│   │
│   └── support/
│       ├── ActionResult.java
│       ├── CustomerStatus.java
│       └── OrderStatus.java
│
├── test/
│   ├── GameTest.java
│   └── SimulationTest.java
│
├── docs/
│   ├── uml/
│   │   ├── class-diagram.png
│   │   └── sequence-diagram.png
│   │
│   └── test-runs/
│       ├── victory-run.txt
│       ├── victory-input.txt
│       ├── defeat-run.txt
│       ├── defeat-input.txt
│       ├── stage2-victory-run.txt
│       ├── stage2-victory-input.txt
│       ├── stage2-defeat-run.txt
│       ├── stage2-defeat-input.txt
│       ├── stage3-victory-run.txt
│       ├── stage3-victory-input.txt
│       ├── stage3-defeat-run.txt
│       └── stage3-defeat-input.txt
│
└── out/

```

The same files were updated from one stage to the next. Earlier versions can still be accessed through Git tags.

---

# Building and Running

## Compile the Application

From the project root:

```
rm -rf out
mkdir out
javac -d out $(find src -name "*.java")
```

## Run the Game

```
java -cp out app.RestaurantRushApp
```

---

# Automated Tests

Automated tests are included to check the game rules without having to play through all 18 turns each time.

## Compile Application and Tests

```
rm -rf out
mkdir out
javac -d out $(find src test -name "*.java")
```

## Run Rule-Level Tests

```
java -cp out GameTest
```

These tests currently check:

- 18-turn limit
- Arrival schedule
- Satisfaction loss
- Menu prices and preparation units
- Order preparation
- Serving
- Eating and payment timing
- Payment and table release
- Customer abandonment
- Victory conditions
- Staff task limits
- Cashier oldest-first rule
- Stage 2 customer types: arrivals, VIP discount and Critic Pasta
- Stage 3: combo prices, food choices, Host seating, and Happy Hour duration, pricing and recovery

## Run Full Game Simulations

Run both simulations:

```
java -cp out SimulationTest
```

Run only the victory scenario:

```
java -cp out SimulationTest victory
```

Run only the defeat scenario:

```
java -cp out SimulationTest defeat
```

---

# Test Evidence

All test runs use the base setting. Since there is no randomness in the game, the same results can be repeated.

## Stage 1

These test runs were recorded at `stage-1` where all customers were Regular. The food choice was based on customer ID, so Customers 1 and 4 ordered Salad, 2 and 5 ordered Burger while 3 and 6 ordered Pasta. To repeat these tests, use `git checkout stage-1`.

### Victory Run

| **Turn** | **Waiter**                 | **Chef**        |
| -------- | -------------------------- | --------------- |
| 1        | Seat Customer 1            | Wait            |
| 2        | Take order from Customer 1 | Prepare Order 1 |
| 3        | Serve Order 1              | Wait            |
| 4        | Seat Customer 2            | Wait            |
| 5        | Take order from Customer 2 | Prepare Order 2 |
| 6        | Serve Order 2              | Wait            |
| 7        | Seat Customer 3            | Wait            |
| 8        | Take order from Customer 3 | Prepare Order 3 |
| 9        | Wait                       | Prepare Order 3 |
| 10       | Seat Customer 4            | Wait            |
| 11       | Serve Order 3              | Wait            |
| 12       | Take order from Customer 4 | Prepare Order 4 |
| 13       | Serve Order 4              | Wait            |
| 14       | Seat Customer 5            | Wait            |
| 15       | Take order from Customer 5 | Prepare Order 5 |
| 16–18    | Wait                       | Wait            |

Result: **VICTORY**

```
Paid customers: 4          (target: at least 4)
Revenue: $51.00            (target: at least $45)
Average satisfaction: 78.00 (target: at least 60)
Unhappy departures: 0

```

Reproduce:

```
java -cp out app.RestaurantRushApp < docs/test-runs/victory-input.txt
```

Saved output:

```
docs/test-runs/victory-run.txt

```

### Defeat Run

For the defeat run, both the Waiter and Chef wait for all 18 turns so no customers are seated. Customers lose 8 satisfaction each turn while waiting, with Customer 1 leaving after turn 13 and Customer 2 after turn 16.

Result: **DEFEAT**

```
Paid customers: 0          (target: at least 4)
Revenue: $0.00             (target: at least $45)
Average satisfaction: N/A
Unhappy departures: 2

```

Reproduce:

```
java -cp out app.RestaurantRushApp < docs/test-runs/defeat-input.txt
```

Saved output:

```
docs/test-runs/defeat-run.txt

```

## Stage 2

For Stage 2, Customer 2 is a VIP who orders Burger and Customer 5 is a Critic who orders Pasta. These runs were recorded at `stage-2` and can be repeated using `git checkout stage-2`.

### Victory Run

| **Turn** | **Waiter**                 | **Chef**        |
| -------- | -------------------------- | --------------- |
| 1        | Seat Customer 1            | Wait            |
| 2        | Take order from Customer 1 | Prepare Order 1 |
| 3        | Serve Order 1              | Wait            |
| 4        | Seat Customer 2 (VIP)      | Wait            |
| 5        | Take order from Customer 2 | Prepare Order 2 |
| 6        | Serve Order 2              | Wait            |
| 7        | Seat Customer 3            | Wait            |
| 8        | Take order from Customer 3 | Prepare Order 3 |
| 9        | Wait                       | Prepare Order 3 |
| 10       | Seat Customer 4            | Wait            |
| 11       | Serve Order 3              | Wait            |
| 12       | Take order from Customer 4 | Prepare Order 4 |
| 13       | Serve Order 4              | Wait            |
| 14       | Seat Customer 5 (Critic)   | Wait            |
| 15       | Take order from Customer 5 | Prepare Order 5 |
| 16       | Wait                       | Prepare Order 5 |
| 17       | Serve Order 5              | Wait            |
| 18       | Wait                       | Wait            |

Customer 2 only paid $13.50 for the Burger after the VIP discount. The Critic ordered Pasta which requires 2 preparation units and was only served on turn 17, which was too late to pay.

Result: **VICTORY**

```
Paid customers: 4          (target: at least 4)
Revenue: $49.50            (target: at least $45)
Average satisfaction: 79.50 (target: at least 60)
Unhappy departures: 0

```

Reproduce:

```
java -cp out app.RestaurantRushApp < docs/test-runs/stage2-victory-input.txt
```

Saved output:

```
docs/test-runs/stage2-victory-run.txt

```

### Defeat Run

For this defeat run, the Waiter and Chef wait every turn. Customer 1 leaves after turn 13 while Customer 2, who is a VIP, only loses 5 satisfaction each turn and does not leave.

Result: **DEFEAT**

```
Paid customers: 0          (target: at least 4)
Revenue: $0.00             (target: at least $45)
Average satisfaction: N/A
Unhappy departures: 1

```

Reproduce:

```
java -cp out app.RestaurantRushApp < docs/test-runs/stage2-defeat-input.txt
```

Saved output:

```
docs/test-runs/stage2-defeat-run.txt

```

## Stage 3

For the Stage 3 victory run, the Host seated customers when they arrived so that the Waiter could take their order in the same turn. Customers 4 and 6 also ordered combos.

### Victory Run

| **Turn** | **Host**                 | **Waiter**                                        | **Chef**        |
| -------- | ------------------------ | ------------------------------------------------- | --------------- |
| 1        | Seat Customer 1          | Take order from Customer 1 (Salad)                | Prepare Order 1 |
| 2        | —                        | Serve Order 1                                     | Wait            |
| 3        | —                        | Wait                                              | Wait            |
| 4        | Seat Customer 2 (VIP)    | Take order from Customer 2 (Burger)               | Prepare Order 2 |
| 5        | —                        | Serve Order 2                                     | Wait            |
| 6        | —                        | Wait                                              | Wait            |
| 7        | Seat Customer 3          | Take order from Customer 3 (Pasta)                | Prepare Order 3 |
| 8        | —                        | Wait                                              | Prepare Order 3 |
| 9        | —                        | Serve Order 3                                     | Wait            |
| 10       | Seat Customer 4          | Take order from Customer 4 (Salad + Burger Combo) | Prepare Order 4 |
| 11       | —                        | Wait                                              | Prepare Order 4 |
| 12       | —                        | Serve Order 4                                     | Wait            |
| 13       | Seat Customer 5 (Critic) | Take order from Customer 5 (Pasta)                | Prepare Order 5 |
| 14       | —                        | Wait                                              | Prepare Order 5 |
| 15       | —                        | Serve Order 5                                     | Wait            |
| 16       | Seat Customer 6          | Take order from Customer 6 (Burger + Pasta Combo) | Prepare Order 6 |
| 17–18    | —                        | Wait                                              | Prepare Order 6 |

Happy Hour starts on turn 13 and lasts until turn 14. The Critic orders Pasta during Happy Hour and pays $14.40 instead of $18.00. On turn 14, the Critic also recovers from 88 to 100 satisfaction while waiting for food. Customer 6 orders a combo which requires 3 preparation units, so it only becomes READY on turn 18 and is too late to serve.

Result: **VICTORY**

```
Paid customers: 5          (target: at least 4)
Revenue: $76.50            (target: at least $45)
Average satisfaction: 88.60 (target: at least 60)
Unhappy departures: 0

```

Reproduce:

```
java -cp out app.RestaurantRushApp < docs/test-runs/stage3-victory-input.txt
```

Saved output:

```
docs/test-runs/stage3-victory-run.txt

```

### Defeat Run

In the defeat run, both the Waiter and Chef wait every turn and Happy Hour is never activated. The Host still seats Customers 1 and 2 but no orders are served. Customer 1 leaves after turn 13 and the Host then seats Customer 3.

Result: **DEFEAT**

```
Paid customers: 0          (target: at least 4)
Revenue: $0.00             (target: at least $45)
Average satisfaction: N/A
Unhappy departures: 1

```

Reproduce:

```
java -cp out app.RestaurantRushApp < docs/test-runs/stage3-defeat-input.txt
```

Saved output:

```
docs/test-runs/stage3-defeat-run.txt

```

## Input File Format

Each line of an input file is one menu entry, in the order the game asks for them:

- Happy Hour (asked at the start of each turn until it is used): `1` start, `2` not now
- Waiter: `1` seat, `2` take order or `3` serve, followed by the customer ID or order ID; `4` wait
- Chef: `1` prepare, followed by the order ID; `2` wait

---

# Design

The program is separated into boundary, control and domain, with each handling different tasks.

## Boundary

`RestaurantUI` handles the command-line input and output. It shows the restaurant status and lets the player choose their actions, which are then sent to `GameController`. Game rules are kept outside the UI.

## Control

`GameController` manages the game turns and connects the UI to the domain objects. `GameConfig` stores the game settings while `ArrivalSchedule` handles the customer arrival schedule.

## Domain

The domain package contains the different restaurant objects.

Examples include:

- `Customer`
- `RegularCustomer`
- `VIPCustomer`
- `CriticCustomer`
- `Order`
- `ComboMeal`
- `HappyHour`
- `Host`
- `Menu`
- `MenuItem`
- `Table`
- `WaitingQueue`
- `Restaurant`
- `Waiter`
- `Chef`
- `Cashier`
- `GameStats`

`GameController` decides when the actions are carried out while the domain classes handle their own actions.

---

# Key Design Decisions

## Customer Behaviour

`Customer` is an abstract class since the different customer types share similar attributes but have different satisfaction loss. `VIPCustomer` gives a 10% discount while `CriticCustomer` always orders Pasta. `ArrivalSchedule` decides which customer type arrives, so `GameController` does not need to check the type.

## Order Lifecycle

Orders follow the lifecycle:

```
PLACED
   ↓
PREPARING
   ↓
READY
   ↓
SERVED
   ↓
READY_TO_PAY
   ↓
PAID

```

An unfinished order may instead become:

```
CANCELLED

```

if its customer leaves.

## Money Representation

Prices and revenue are stored in cents instead of using floating-point values to avoid rounding issues.

For example:

```
$9.00  → 900
$15.00 → 1500
$18.00 → 1800

```

The VIP discount is rounded to the nearest cent, and the bill is fixed when the order is taken.

## Invalid Actions

For invalid actions, `ActionResult` shows an error message to the player. The player can then choose again without using up the Waiter or Chef task.

## Customer Menu Selection

Customers choose their own food automatically instead of the player choosing for them. For Regular and VIP customers, the food is based on their ID so that the same test runs can be repeated. Stage 3 has 6 menu choices, so Customers 4 and 6 order combos while the Critic still orders Pasta.

## Host

`Host` is a separate staff class which automatically seats the first customer in the waiting queue if there is an available table. `GameController` makes sure that the Host only seats one customer per turn while the Waiter can still seat customers.

## Combo Meals

`ComboMeal` extends `MenuItem` so that orders, food preparation and billing still work the same way as a normal menu item. The price and preparation units are calculated based on the two items in the combo.

## Happy Hour

`Restaurant` holds `HappyHour`, which keeps track of whether it has been used and how many turns are left. When an order is taken, `Order` applies the customer's discount first followed by the Happy Hour discount, with each rounded to the nearest cent. For example, a VIP Burger costs $15.00 × 0.9 × 0.8 = $10.80. The bill remains the same after the order is taken.

---

# UML

The current class diagram is stored in:

```
docs/uml/class-diagram.png

```

The class diagram was updated as changes were made to the code.

The sequence diagram shows turns 13 to 18 of the Stage 3 victory run. On turn 13, the manager starts Happy Hour while the Host seats Customer 5, who is a Critic. The Critic then orders Pasta with the Happy Hour discount and is later served and makes payment. The diagram is stored in:

```
docs/uml/sequence-diagram.png

```

---

# Development Stages and Git Tags

The same codebase was used for all 3 stages.

Each complete runnable stage is preserved using a Git tag:

```
stage-1
stage-2
stage-3

```

For example:

```
Initial repository
      ↓
Develop and update the same files
      ↓
Complete Stage 1
      ↓
Tag: stage-1
      ↓
Continue modifying the same files
      ↓
Complete Stage 2
      ↓
Tag: stage-2
      ↓
Continue modifying the same files
      ↓
Complete Stage 3
      ↓
Tag: stage-3

```

Git tags allow the changes between each stage to be compared.

---

# Stage Transitions

The tables below show which files were added or changed between stages and the reasons for the changes.

## Stage 1 → Stage 2

| **Change**    | **File**                                                                                 | **Reason**                                                                                                  |
| ------------- | ---------------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------- |
| New           | `VIPCustomer.java`, `CriticCustomer.java`                                                | The two Stage 2 customer types                                                                              |
| Changed       | `Customer.java`                                                                          | Added `chooseItem()`, `calculateBillCents()` and `getTypeName()` so each type can set its own food and bill |
| Changed       | `RegularCustomer.java`                                                                   | Added `getTypeName()`                                                                                       |
| Changed       | `Order.java`                                                                             | The bill comes from the customer, so the VIP discount is fixed when ordering                                |
| Changed       | `Menu.java`                                                                              | Added `findItem()` for the Critic's Pasta                                                                   |
| Changed       | `ArrivalSchedule.java`                                                                   | Added `createCustomer()` to pick each arrival's type                                                        |
| Changed       | `GameController.java`                                                                    | Uses `createCustomer()` and lets the customer choose its food                                               |
| Changed       | `RestaurantUI.java`                                                                      | Shows customer types and bills                                                                              |
| Renamed       | `Stage1Test.java` → `GameTest.java`, `Stage1SimulationTest.java` → `SimulationTest.java` | Tests now cover every stage; Stage 2 checks added                                                           |
| New / Changed | `docs/`                                                                                  | Stage 2 test runs and updated class diagram                                                                 |

Since `Customer` was already abstract in Stage 1, only small changes to the existing code were needed. The turn flow and staff classes remained the same, and all Stage 1 tests still pass.

## Stage 2 → Stage 3

| **Change**    | **File**                               | **Reason**                                                                              |
| ------------- | -------------------------------------- | --------------------------------------------------------------------------------------- |
| New           | `Host.java`                            | The Host role, which seats one waiting customer per turn                                |
| New           | `ComboMeal.java`                       | A combo of two menu items: preparation units added, 90% of the price                    |
| New           | `HappyHour.java`                       | The Happy Hour policy: once per game, two turns, 20% off and +15 recovery               |
| Changed       | `Menu.java`                            | Adds the three combos; customers now cycle through all six choices by ID                |
| Changed       | `Order.java`                           | Applies Happy Hour after the customer's discount; the bill is still fixed when ordering |
| Changed       | `Waiter.java`                          | `takeOrder()` passes Happy Hour to the new order                                        |
| Changed       | `Customer.java`                        | Added `recoverSatisfaction()` for Happy Hour                                            |
| Changed       | `Restaurant.java`                      | Holds the `HappyHour`                                                                   |
| Changed       | `GameController.java`                  | Host step, Happy Hour start, recovery and countdown                                     |
| Changed       | `RestaurantUI.java`                    | Happy Hour choice and status, Host messages, menu at the start                          |
| Changed       | `GameTest.java`, `SimulationTest.java` | Stage 3 checks and new full-game runs                                                   |
| New / Changed | `docs/`                                | Stage 3 test runs, updated class diagram and the sequence diagram                       |

For Stage 3, the earlier staff classes, customer types and payment rules remained the same, but the Host and Happy Hour steps were added to the turn flow. The food choice was also changed to include 6 menu items, so Customers 4 and 6 now order combos while Customers 1 to 3 and the Critic still order the same food. Because of this, one Stage 2 test was updated to use Customer 1 instead. All other Stage 1 and Stage 2 tests still pass.

---
# Video

Video link: (add link)

| **#** | **Segment**                                                                  | **Presenter** | **Timestamp** |
| ----- | ---------------------------------------------------------------------------- | ------------- | ------------- |
| 1     | The game launching from the submitted project                                |               |               |
| 2     | Meaningful manager choices, including a decision where staff are constrained |               |               |
| 3     | A dish going READY → SERVED → payment                                        |               |               |
| 4     | A VIP or Critic behaviour                                                    |               |               |
| 5     | A Stage 3 feature                                                            |               |               |
| 6     | One victory and one defeat                                                   |               |               |
| 7     | The stage tag comparisons and the diagrams                                   |               |               |
