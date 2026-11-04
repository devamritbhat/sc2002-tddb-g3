package domain;

// Stores the main game statistics.
public class GameStats {

    private int revenueCents;
    private int paidCount;
    private int paidSatisfactionTotal;
    private int servedCount;
    private int unhappyDepartures;

    public GameStats() {
        revenueCents = 0;
        paidCount = 0;
        paidSatisfactionTotal = 0;
        servedCount = 0;
        unhappyDepartures = 0;
    }

    // Records a completed payment.
    public void recordPayment(Customer customer, int billCents) {
        revenueCents += billCents;
        paidCount++;
        paidSatisfactionTotal += customer.getSatisfaction();
    }

    // Records a served customer.
    public void recordServed() {
        servedCount++;
    }

    // Records a customer leaving unhappy.
    public void recordDeparture() {
        unhappyDepartures++;
    }

    public int getRevenueCents() {
        return revenueCents;
    }

    public int getPaidCount() {
        return paidCount;
    }

    public int getServedCount() {
        return servedCount;
    }

    public int getUnhappyDepartures() {
        return unhappyDepartures;
    }

    // Returns the average satisfaction of paid customers.
    public double getAveragePaidSatisfaction() {

        if (paidCount == 0) {
            return -1;
        }

        return (double) paidSatisfactionTotal / paidCount;
    }
}
