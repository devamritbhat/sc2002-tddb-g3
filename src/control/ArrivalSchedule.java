package control;

import domain.CriticCustomer;
import domain.Customer;
import domain.RegularCustomer;
import domain.VIPCustomer;

// Stores customer arrival turns and types.
public class ArrivalSchedule {

    private static final int VIP_CUSTOMER_ID = 2;
    private static final int CRITIC_CUSTOMER_ID = 5;

    private final int[] arrivalTurns;

    public ArrivalSchedule() {
        arrivalTurns = new int[] {1, 4, 7, 10, 13, 16};
    }

    // Checks whether a customer arrives on this turn.
    public boolean hasArrival(int turn) {

        for (int arrivalTurn : arrivalTurns) {
            if (arrivalTurn == turn) {
                return true;
            }
        }

        return false;
    }

    public int[] getArrivalTurns() {
        return arrivalTurns.clone();
    }

    // Customer 2 is a VIP, customer 5 is a Critic, the rest are Regular.
    public Customer createCustomer(int customerId, int turn) {

        if (customerId == VIP_CUSTOMER_ID) {
            return new VIPCustomer(customerId, turn);
        }

        if (customerId == CRITIC_CUSTOMER_ID) {
            return new CriticCustomer(customerId, turn);
        }

        return new RegularCustomer(customerId, turn);
    }
}
