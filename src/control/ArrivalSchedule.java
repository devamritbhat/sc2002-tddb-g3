package control;

// Stores the turns on which customers arrive.
public class ArrivalSchedule {

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
}
