package domain;

import java.util.List;

// A combo made of two existing menu items.
public class ComboMeal extends MenuItem {

    private static final int COMBO_PRICE_PERCENT = 90;

    private final MenuItem firstItem;
    private final MenuItem secondItem;

    public ComboMeal(MenuItem firstItem, MenuItem secondItem) {
        super(
                firstItem.getName() + " + " + secondItem.getName() + " Combo",
                comboPriceCents(firstItem, secondItem),
                firstItem.getPreparationUnits() + secondItem.getPreparationUnits()
        );

        this.firstItem = firstItem;
        this.secondItem = secondItem;
    }

    public List<MenuItem> getComponents() {
        return List.of(firstItem, secondItem);
    }

    // 90% of the combined price, rounded to the nearest cent.
    private static int comboPriceCents(MenuItem firstItem, MenuItem secondItem) {
        int combined = firstItem.getPriceCents() + secondItem.getPriceCents();
        return (combined * COMBO_PRICE_PERCENT + 50) / 100;
    }
}
