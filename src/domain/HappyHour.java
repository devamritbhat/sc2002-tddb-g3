package domain;

// Happy Hour pricing policy. It can be used once and lasts two turns.
public class HappyHour {

    private static final int DURATION_TURNS = 2;
    private static final int DISCOUNT_PERCENT = 20;
    private static final int RECOVERY_POINTS = 15;

    private boolean used;
    private int remainingTurns;

    public HappyHour() {
        used = false;
        remainingTurns = 0;
    }

    public boolean canActivate() {
        return !used;
    }

    // Starts Happy Hour for this turn and the next.
    public void activate() {
        if (!used) {
            used = true;
            remainingTurns = DURATION_TURNS;
        }
    }

    public boolean isActive() {
        return remainingTurns > 0;
    }

    public boolean isUsed() {
        return used;
    }

    public int getRemainingTurns() {
        return remainingTurns;
    }

    // Counts down at the start of each new turn.
    public void nextTurn() {
        if (remainingTurns > 0) {
            remainingTurns--;
        }
    }

    // 20% off, rounded to the nearest cent.
    public int applyDiscount(int priceCents) {
        return (priceCents * (100 - DISCOUNT_PERCENT) + 50) / 100;
    }

    public int getRecoveryPoints() {
        return RECOVERY_POINTS;
    }
}
