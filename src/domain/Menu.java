package domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Stores the available menu items
public class Menu {

    private final List<MenuItem> items;

    public Menu() {

        items = new ArrayList<>();

        // Base menu items
        MenuItem salad = new MenuItem("Salad", 900, 1);
        MenuItem burger = new MenuItem("Burger", 1500, 1);
        MenuItem pasta = new MenuItem("Pasta", 1800, 2);

        items.add(salad);
        items.add(burger);
        items.add(pasta);

        // Combos made from two base items.
        items.add(new ComboMeal(salad, burger));
        items.add(new ComboMeal(salad, pasta));
        items.add(new ComboMeal(burger, pasta));
    }

    public List<MenuItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public MenuItem getItem(int index) {
        return items.get(index);
    }

    // Customers cycle through all six choices by customer ID.
    public MenuItem chooseItemFor(Customer customer) {

        int index = (customer.getId() - 1) % items.size();

        return items.get(index);
    }

    // Finds a menu item by name.
    public MenuItem findItem(String name) {

        for (MenuItem item : items) {
            if (item.getName().equalsIgnoreCase(name)) {
                return item;
            }
        }

        return null;
    }
}
