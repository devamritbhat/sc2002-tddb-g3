package domain;

// Represents a VIP customer with a 10% discount.
public class VIPCustomer extends Customer {

    private static final int SATISFACTION_LOSS = 5;
    private static final int DISCOUNT_PERCENT = 10;

    public VIPCustomer(int id, int arrivalTurn) {
        super(id, arrivalTurn);
    }

    @Override
    public int getSatisfactionLossPerTurn() {
        return SATISFACTION_LOSS;
    }

    // 10% off, rounded to the nearest cent.
    @Override
    public int calculateBillCents(int priceCents) {
        return (priceCents * (100 - DISCOUNT_PERCENT) + 50) / 100;
    }

    @Override
    public String getTypeName() {
        return "VIP";
    }
}
