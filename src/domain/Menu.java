package domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Stores the available menu items.
public class Menu {

    private final List<MenuItem> items;

    public Menu() {

        items = new ArrayList<>();

        // Base menu items.
        items.add(new MenuItem("Salad", 900, 1));
        items.add(new MenuItem("Burger", 1500, 1));
        items.add(new MenuItem("Pasta", 1800, 2));
    }

    public List<MenuItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public MenuItem getItem(int index) {
        return items.get(index);
    }

    // Chooses a menu item deterministically for a customer.
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
