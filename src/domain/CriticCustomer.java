package domain;

// Represents a critic who always orders Pasta.
public class CriticCustomer extends Customer {

    private static final int SATISFACTION_LOSS = 12;
    private static final String PREFERRED_ITEM = "Pasta";

    public CriticCustomer(int id, int arrivalTurn) {
        super(id, arrivalTurn);
    }

    @Override
    public int getSatisfactionLossPerTurn() {
        return SATISFACTION_LOSS;
    }

    @Override
    public MenuItem chooseItem(Menu menu) {
        return menu.findItem(PREFERRED_ITEM);
    }

    @Override
    public String getTypeName() {
        return "Critic";
    }
}
