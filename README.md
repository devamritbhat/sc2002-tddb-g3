# Restaurant Rush

Restaurant Rush is a turn-based restaurant management game developed in Java for the SC2002 Object-Oriented Design & Programming group assignment.

Repository: https://github.com/devamritbhat/sc2002-tddb-g3.git

The project is developed progressively across three stages. The same codebase and documentation are updated as development progresses, while each complete stage is preserved using Git tags:

- `stage-1`
- `stage-2`
- `stage-3`

This README will be updated as the project evolves.

---

## Team Members

- Devamrit
- Evan
- Hein
- Keith
- Saajid

The following areas indicate each member's primary responsibility for design review, implementation understanding, testing and presentation:

| Member | Primary Area | Stage 2 | Stage 3 |
|---|---|---|---|
| Devamrit | Overall architecture, `GameController`, game integration and lifecycle | Arrival types in `ArrivalSchedule` and `GameController` | Stage 3 turn flow in `GameController`: Host step, Happy Hour start, recovery and countdown |
| Evan | `Restaurant`, `WaitingQueue`, `Table`, Waiter, seating and serving | Customer type and bill display in `RestaurantUI`, class diagram update | `Host` seating; Host, menu and Happy Hour display in `RestaurantUI`; class diagram update |
| Hein | `Menu`, `MenuItem`, `Order`, Chef and food preparation | Critic's Pasta choice: `chooseItem()` and `Menu.findItem()` | `ComboMeal` and the six-choice menu in `Menu` |
| Keith | `Cashier`, `GameStats`, victory/defeat conditions and testing | VIP discount: `calculateBillCents()` and the bill in `Order`; Stage 2 tests and runs | Happy Hour pricing in `Order` (VIP discount first, then 20% off); Stage 3 tests and runs |
| Saajid | `Customer`, `RegularCustomer`, satisfaction and abandonment | `VIPCustomer` and `CriticCustomer`: satisfaction loss and customer types | `HappyHour` and `Customer.recoverSatisfaction()` |

---

# Current Implementation

The current version (Stage 3) implements the complete Restaurant Rush game with Regular, VIP and Critic customers, the Host, combo meals and Happy Hour.

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

| Item | Price | Preparation Units |
|---|---:|---:|
| Salad | $9.00 | 1 |
| Burger | $15.00 | 1 |
| Pasta | $18.00 | 2 |
| Salad + Burger Combo | $21.60 | 2 |
| Salad + Pasta Combo | $24.30 | 3 |
| Burger + Pasta Combo | $29.70 | 3 |

A combo's preparation units are the sum of its two items. Its price is 90% of the two prices added together, rounded to the nearest cent.

Customers choose their menu item automatically by customer ID, cycling through all six choices: Customer 1 Salad, 2 Burger, 3 Pasta, 4 Salad + Burger Combo, 5 Salad + Pasta Combo and 6 Burger + Pasta Combo. Critics always order Pasta.

### Customer Types

| Type | Satisfaction loss per waiting turn | Bill | Food choice |
|---|---:|---|---|
| Regular | 8 | Full price | By customer ID (six choices) |
| VIP | 5 | 10% off | By customer ID (six choices) |
| Critic | 12 | Full price | Always Pasta |

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

The game always runs for all 18 turns.

At the end of Turn 18, the player wins only if all three conditions are satisfied:

- At least 4 customers have paid
- Revenue is at least $45.00
- Average satisfaction of paid customers is at least 60

Otherwise, the result is defeat.

---

# Project Structure

```text
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

The same files are updated as the project progresses. Earlier versions remain available through Git tags.

---

# Building and Running

## Compile the Application

From the project root:

```bash
rm -rf out
mkdir out
javac -d out $(find src -name "*.java")
```

## Run the Game

```bash
java -cp out app.RestaurantRushApp
```

---

# Automated Tests

Automated tests are included so that the game rules can be checked without manually playing through all 18 turns.

## Compile Application and Tests

```bash
rm -rf out
mkdir out
javac -d out $(find src test -name "*.java")
```

## Run Rule-Level Tests

```bash
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

```bash
java -cp out SimulationTest
```

Run only the victory scenario:

```bash
java -cp out SimulationTest victory
```

Run only the defeat scenario:

```bash
java -cp out SimulationTest defeat
```

---

# Test Evidence

All runs use the base setting. Since the game has no randomness, the results are reproducible.

## Stage 1

Recorded at tag `stage-1`, when all customers were Regular. Customers order by ID: 1 and 4 choose Salad, 2 and 5 choose Burger, and 3 and 6 choose Pasta. Run `git checkout stage-1` to reproduce them.

### Victory Run

| Turn | Waiter | Chef |
|---|---|---|
| 1 | Seat Customer 1 | Wait |
| 2 | Take order from Customer 1 | Prepare Order 1 |
| 3 | Serve Order 1 | Wait |
| 4 | Seat Customer 2 | Wait |
| 5 | Take order from Customer 2 | Prepare Order 2 |
| 6 | Serve Order 2 | Wait |
| 7 | Seat Customer 3 | Wait |
| 8 | Take order from Customer 3 | Prepare Order 3 |
| 9 | Wait | Prepare Order 3 |
| 10 | Seat Customer 4 | Wait |
| 11 | Serve Order 3 | Wait |
| 12 | Take order from Customer 4 | Prepare Order 4 |
| 13 | Serve Order 4 | Wait |
| 14 | Seat Customer 5 | Wait |
| 15 | Take order from Customer 5 | Prepare Order 5 |
| 16–18 | Wait | Wait |

Result: **VICTORY**

```text
Paid customers: 4          (target: at least 4)
Revenue: $51.00            (target: at least $45)
Average satisfaction: 78.00 (target: at least 60)
Unhappy departures: 0
```

Reproduce:

```bash
java -cp out app.RestaurantRushApp < docs/test-runs/victory-input.txt
```

Saved output:

```text
docs/test-runs/victory-run.txt
```

### Defeat Run

The Waiter and Chef wait every turn, so no customers get seated. Waiting customers lose 8 satisfaction each turn. Customer 1 leaves after Turn 13, followed by Customer 2 after Turn 16.

Result: **DEFEAT**

```text
Paid customers: 0          (target: at least 4)
Revenue: $0.00             (target: at least $45)
Average satisfaction: N/A
Unhappy departures: 2
```

Reproduce:

```bash
java -cp out app.RestaurantRushApp < docs/test-runs/defeat-input.txt
```

Saved output:

```text
docs/test-runs/defeat-run.txt
```

## Stage 2

Recorded at tag `stage-2`. Customer 2 is a VIP and orders a Burger. Customer 5 is a Critic and orders Pasta. Run `git checkout stage-2` to reproduce them.

### Victory Run

| Turn | Waiter | Chef |
|---|---|---|
| 1 | Seat Customer 1 | Wait |
| 2 | Take order from Customer 1 | Prepare Order 1 |
| 3 | Serve Order 1 | Wait |
| 4 | Seat Customer 2 (VIP) | Wait |
| 5 | Take order from Customer 2 | Prepare Order 2 |
| 6 | Serve Order 2 | Wait |
| 7 | Seat Customer 3 | Wait |
| 8 | Take order from Customer 3 | Prepare Order 3 |
| 9 | Wait | Prepare Order 3 |
| 10 | Seat Customer 4 | Wait |
| 11 | Serve Order 3 | Wait |
| 12 | Take order from Customer 4 | Prepare Order 4 |
| 13 | Serve Order 4 | Wait |
| 14 | Seat Customer 5 (Critic) | Wait |
| 15 | Take order from Customer 5 | Prepare Order 5 |
| 16 | Wait | Prepare Order 5 |
| 17 | Serve Order 5 | Wait |
| 18 | Wait | Wait |

The VIP pays $13.50 for the Burger. The Critic's Pasta takes two Chef turns and is served on turn 17, too late to pay.

Result: **VICTORY**

```text
Paid customers: 4          (target: at least 4)
Revenue: $49.50            (target: at least $45)
Average satisfaction: 79.50 (target: at least 60)
Unhappy departures: 0
```

Reproduce:

```bash
java -cp out app.RestaurantRushApp < docs/test-runs/stage2-victory-input.txt
```

Saved output:

```text
docs/test-runs/stage2-victory-run.txt
```

### Defeat Run

The Waiter and Chef wait every turn. Customer 1 leaves after Turn 13. The VIP loses only 5 per turn, so they never leave.

Result: **DEFEAT**

```text
Paid customers: 0          (target: at least 4)
Revenue: $0.00             (target: at least $45)
Average satisfaction: N/A
Unhappy departures: 1
```

Reproduce:

```bash
java -cp out app.RestaurantRushApp < docs/test-runs/stage2-defeat-input.txt
```

Saved output:

```text
docs/test-runs/stage2-defeat-run.txt
```

## Stage 3

The Host seats every customer on the turn they arrive, so the Waiter can take their order straight away. Customers 4 and 6 order combos.

### Victory Run

| Turn | Host | Waiter | Chef |
|---|---|---|---|
| 1 | Seat Customer 1 | Take order from Customer 1 (Salad) | Prepare Order 1 |
| 2 | — | Serve Order 1 | Wait |
| 3 | — | Wait | Wait |
| 4 | Seat Customer 2 (VIP) | Take order from Customer 2 (Burger) | Prepare Order 2 |
| 5 | — | Serve Order 2 | Wait |
| 6 | — | Wait | Wait |
| 7 | Seat Customer 3 | Take order from Customer 3 (Pasta) | Prepare Order 3 |
| 8 | — | Wait | Prepare Order 3 |
| 9 | — | Serve Order 3 | Wait |
| 10 | Seat Customer 4 | Take order from Customer 4 (Salad + Burger Combo) | Prepare Order 4 |
| 11 | — | Wait | Prepare Order 4 |
| 12 | — | Serve Order 4 | Wait |
| 13 | Seat Customer 5 (Critic) | Take order from Customer 5 (Pasta) | Prepare Order 5 |
| 14 | — | Wait | Prepare Order 5 |
| 15 | — | Serve Order 5 | Wait |
| 16 | Seat Customer 6 | Take order from Customer 6 (Burger + Pasta Combo) | Prepare Order 6 |
| 17–18 | — | Wait | Prepare Order 6 |

Happy Hour is started on turn 13 and lasts turns 13 and 14. The Critic's Pasta is ordered during Happy Hour and costs $14.40 instead of $18.00. At the start of turn 14, the Critic recovers from 88 to 100 satisfaction while waiting for the food. Customer 6's combo needs 3 units, so it only becomes READY on turn 18, too late to serve.

Result: **VICTORY**

```text
Paid customers: 5          (target: at least 4)
Revenue: $76.50            (target: at least $45)
Average satisfaction: 88.60 (target: at least 60)
Unhappy departures: 0
```

Reproduce:

```bash
java -cp out app.RestaurantRushApp < docs/test-runs/stage3-victory-input.txt
```

Saved output:

```text
docs/test-runs/stage3-victory-run.txt
```

### Defeat Run

The Waiter and Chef wait every turn and Happy Hour is never started. The Host still seats Customers 1 and 2, but nobody is served. Customer 1 leaves after Turn 13, and the Host then seats Customer 3.

Result: **DEFEAT**

```text
Paid customers: 0          (target: at least 4)
Revenue: $0.00             (target: at least $45)
Average satisfaction: N/A
Unhappy departures: 1
```

Reproduce:

```bash
java -cp out app.RestaurantRushApp < docs/test-runs/stage3-defeat-input.txt
```

Saved output:

```text
docs/test-runs/stage3-defeat-run.txt
```

## Input File Format

Each line of an input file is one menu entry, in the order the game asks for them:

- Happy Hour (asked at the start of each turn until it is used): `1` start, `2` not now
- Waiter: `1` seat, `2` take order or `3` serve, followed by the customer ID or order ID; `4` wait
- Chef: `1` prepare, followed by the order ID; `2` wait

---

# Design

The implementation separates the program into boundary, control and domain responsibilities.

## Boundary

`RestaurantUI` handles command-line input and output.

It displays the restaurant state, presents manager choices and sends selected actions to the controller.

Game rules are not implemented inside the UI.

## Control

`GameController` coordinates the overall turn sequence and connects the UI to the domain objects.

`GameConfig` stores the main game settings.

`ArrivalSchedule` stores the fixed customer arrival schedule.

## Domain

Domain classes represent the restaurant and its behaviour.

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

The controller determines when actions occur, while the relevant domain classes determine how those actions are performed.

---

# Key Design Decisions

## Customer Behaviour

`Customer` is an abstract class. `RegularCustomer`, `VIPCustomer` and `CriticCustomer` each set their own satisfaction loss. `VIPCustomer` overrides the bill (10% off) and `CriticCustomer` overrides the food choice (always Pasta).

`GameController` never checks a customer's type. `ArrivalSchedule` decides which type of customer arrives.

## Order Lifecycle

Orders follow the lifecycle:

```text
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

```text
CANCELLED
```

if its customer leaves.

## Money Representation

Prices and revenue are stored as integer cents rather than floating-point monetary values.

For example:

```text
$9.00  → 900
$15.00 → 1500
$18.00 → 1800
```

The VIP discount is rounded to the nearest cent, and the bill is fixed when the order is taken.

## Invalid Actions

Invalid manager choices return an `ActionResult` explaining why the action cannot be performed.

The manager may then choose again without consuming the Waiter or Chef task for that turn.

## Customer Menu Selection

Customers select their own menu item.

Regular and VIP customers choose by customer ID so that runs are reproducible. In Stage 3 the rule cycles through all six menu choices, so Customers 4 and 6 order combos. Critics always order Pasta.

## Host

`Host` is a separate staff class. Each turn it seats the first customer in the waiting queue if a table is free. `GameController` makes sure it seats at most one customer per turn, and the Waiter can still seat customers.

## Combo Meals

`ComboMeal` extends `MenuItem`, so an order, the Chef and the bill work the same way for a combo as for a single item. The combo works out its own price and preparation units from its two items.

## Happy Hour

`HappyHour` is held by `Restaurant`. It keeps track of whether it has been used and how many turns are left. When an order is taken, `Order` applies the customer's discount first and then Happy Hour, each rounded to the nearest cent, so a VIP Burger costs $15.00 × 0.9 × 0.8 = $10.80. The bill does not change after that.

---

# UML

The current class diagram is stored in:

```text
docs/uml/class-diagram.png
```

The diagram is updated together with the code as the design evolves.

The sequence diagram shows turns 13 to 18 of the Stage 3 victory run: the manager starts Happy Hour, the Critic (Customer 5) is seated by the Host, orders Pasta at the Happy Hour price, and is served and pays. It is stored in:

```text
docs/uml/sequence-diagram.png
```

---

# Development Stages and Git Tags

The project is maintained as one evolving codebase.

Each complete runnable stage is preserved using a Git tag:

```text
stage-1
stage-2
stage-3
```

For example:

```text
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

This allows the development between stages to be compared directly through Git history.

---

# Stage Transitions

This section records the changes between stages: new files, changed or removed files, the reasons, and a compare link.

## Stage 1 → Stage 2

Compare: https://github.com/devamritbhat/sc2002-tddb-g3/compare/stage-1...stage-2

| Change | File | Reason |
|---|---|---|
| New | `VIPCustomer.java`, `CriticCustomer.java` | The two Stage 2 customer types |
| Changed | `Customer.java` | Added `chooseItem()`, `calculateBillCents()` and `getTypeName()` so each type can set its own food and bill |
| Changed | `RegularCustomer.java` | Added `getTypeName()` |
| Changed | `Order.java` | The bill comes from the customer, so the VIP discount is fixed when ordering |
| Changed | `Menu.java` | Added `findItem()` for the Critic's Pasta |
| Changed | `ArrivalSchedule.java` | Added `createCustomer()` to pick each arrival's type |
| Changed | `GameController.java` | Uses `createCustomer()` and lets the customer choose its food |
| Changed | `RestaurantUI.java` | Shows customer types and bills |
| Renamed | `Stage1Test.java` → `GameTest.java`, `Stage1SimulationTest.java` → `SimulationTest.java` | Tests now cover every stage; Stage 2 checks added |
| New / Changed | `docs/` | Stage 2 test runs and updated class diagram |

Little had to change because `Customer` was already abstract in Stage 1. The turn sequence, staff classes and Stage 1 rules are unchanged, and all Stage 1 tests still pass.

## Stage 2 → Stage 3

Compare: https://github.com/devamritbhat/sc2002-tddb-g3/compare/stage-2...stage-3

| Change | File | Reason |
|---|---|---|
| New | `Host.java` | The Host role, which seats one waiting customer per turn |
| New | `ComboMeal.java` | A combo of two menu items: preparation units added, 90% of the price |
| New | `HappyHour.java` | The Happy Hour policy: once per game, two turns, 20% off and +15 recovery |
| Changed | `Menu.java` | Adds the three combos; customers now cycle through all six choices by ID |
| Changed | `Order.java` | Applies Happy Hour after the customer's discount; the bill is still fixed when ordering |
| Changed | `Waiter.java` | `takeOrder()` passes Happy Hour to the new order |
| Changed | `Customer.java` | Added `recoverSatisfaction()` for Happy Hour |
| Changed | `Restaurant.java` | Holds the `HappyHour` |
| Changed | `GameController.java` | Host step, Happy Hour start, recovery and countdown |
| Changed | `RestaurantUI.java` | Happy Hour choice and status, Host messages, menu at the start |
| Changed | `GameTest.java`, `SimulationTest.java` | Stage 3 checks and new full-game runs |
| New / Changed | `docs/` | Stage 3 test runs, updated class diagram and the sequence diagram |

The turn sequence, staff classes, customer types and payment rules are unchanged. The only earlier rule that changed is the food choice: with six menu choices, Customers 4 and 6 now order combos, while Customers 1 to 3 and the Critic order the same as before. One Stage 2 check was updated for this (it now uses Customer 1). All other Stage 1 and Stage 2 tests still pass.
