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

The project is developed collaboratively by all team members.

The following areas indicate each member's primary responsibility for design review, implementation understanding, testing and presentation:

| Member | Primary Area |
|---|---|
| Devamrit | Overall architecture, `GameController`, game integration and lifecycle |
| Evan | `Restaurant`, `WaitingQueue`, `Table`, Waiter, seating and serving |
| Hein | `Menu`, `MenuItem`, `Order`, Chef and food preparation |
| Keith | `Cashier`, `GameStats`, victory/defeat conditions and testing |
| Saajid | `Customer`, `RegularCustomer`, satisfaction and abandonment |

All members contribute collaboratively to the overall implementation.

---

# Current Implementation

The current version implements the complete basic Restaurant Rush game using regular customers and the three required menu items.

## Features

The implementation includes:

- 18-turn game
- 2 restaurant tables
- Regular customers
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

Customers choose their menu item automatically using a deterministic rule.

---

# Turn Flow

Each turn follows this sequence:

1. Customer arrival, if scheduled
2. Waiter action
3. Chef action
4. Eating progression and automatic Cashier payment
5. Satisfaction loss and abandonment
6. End-of-turn summary

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
│   │   ├── Customer.java
│   │   ├── GameStats.java
│   │   ├── Menu.java
│   │   ├── MenuItem.java
│   │   ├── Order.java
│   │   ├── RegularCustomer.java
│   │   ├── Restaurant.java
│   │   ├── Table.java
│   │   ├── Waiter.java
│   │   └── WaitingQueue.java
│   │
│   └── support/
│       ├── ActionResult.java
│       ├── CustomerStatus.java
│       └── OrderStatus.java
│
├── test/
│   ├── Stage1Test.java
│   └── Stage1SimulationTest.java
│
├── docs/
│   ├── uml/
│   │   └── class-diagram.png
│   │
│   └── test-runs/
│       ├── victory-run.txt
│       └── defeat-run.txt
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
java -cp out Stage1Test
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

## Run Full Game Simulations

Run both simulations:

```bash
java -cp out Stage1SimulationTest
```

Run only the victory scenario:

```bash
java -cp out Stage1SimulationTest victory
```

Run only the defeat scenario:

```bash
java -cp out Stage1SimulationTest defeat
```

---

# Test Evidence

Reproducible test runs are stored in:

```text
docs/test-runs/
```

## Victory Run

The automated victory simulation completes all 18 turns with:

```text
Paid customers: 4
Revenue: $51.00
Average satisfaction: 78.00
```

This satisfies all current victory requirements.

The complete output is stored in:

```text
docs/test-runs/victory-run.txt
```

## Defeat Run

The automated defeat scenario makes the Waiter and Chef wait throughout the game.

The simulation completes all 18 turns with:

```text
Paid customers: 0
Revenue: $0.00
```

and therefore results in defeat.

The complete output is stored in:

```text
docs/test-runs/defeat-run.txt
```

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
- `Order`
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

`Customer` is an abstract class.

`RegularCustomer` defines the current satisfaction-loss behaviour.

Customer-specific behaviour is kept inside customer objects rather than being placed in the UI or controller.

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

## Invalid Actions

Invalid manager choices return an `ActionResult` explaining why the action cannot be performed.

The manager may then choose again without consuming the Waiter or Chef task for that turn.

## Customer Menu Selection

Customers select their own menu item.

The current implementation uses a deterministic rule based on customer ID so that behaviour remains reproducible during testing.

---

# UML

The current class diagram is stored in:

```text
docs/uml/class-diagram.png
```

The diagram is updated together with the code as the design evolves.

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

As later stages are implemented, this section will record the changes between:

- Stage 1 → Stage 2
- Stage 2 → Stage 3

For each transition, the README will document:

- New files added
- Existing files modified
- Files removed, if any
- Reasons for the changes
- Relevant Git commits or comparisons

This section will be updated when the corresponding stages are implemented.
