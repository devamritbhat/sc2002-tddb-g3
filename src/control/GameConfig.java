package control;

// Stores the main Stage 1 game settings.
public class GameConfig {

    private final int maxTurns;
    private final int tableCount;

    private final int minPaidCustomers;
    private final int minRevenueCents;
    private final int minAverageSatisfaction;

    private final ArrivalSchedule arrivalSchedule;

    public GameConfig() {

        maxTurns = 18;
        tableCount = 2;

        minPaidCustomers = 4;
        minRevenueCents = 4500;
        minAverageSatisfaction = 60;

        arrivalSchedule = new ArrivalSchedule();
    }

    public int getMaxTurns() {
        return maxTurns;
    }

    public int getTableCount() {
        return tableCount;
    }

    public int getMinPaidCustomers() {
        return minPaidCustomers;
    }

    public int getMinRevenueCents() {
        return minRevenueCents;
    }

    public int getMinAverageSatisfaction() {
        return minAverageSatisfaction;
    }

    public ArrivalSchedule getArrivalSchedule() {
        return arrivalSchedule;
    }
}
