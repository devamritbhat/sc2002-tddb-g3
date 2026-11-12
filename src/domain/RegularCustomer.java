package domain;

// Represents a regular customer.
public class RegularCustomer extends Customer {

    private static final int SATISFACTION_LOSS = 8;

    public RegularCustomer(int id, int arrivalTurn) {
        super(id, arrivalTurn);
    }

    @Override
    public int getSatisfactionLossPerTurn() {
        return SATISFACTION_LOSS;
    }

    @Override
    public String getTypeName() {
        return "Regular";
    }
}
