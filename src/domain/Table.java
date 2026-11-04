package domain;

// Represents a table in the restaurant.
public class Table {

    private final int tableId;
    private Customer occupant;

    public Table(int tableId) {
        this.tableId = tableId;
        this.occupant = null;
    }

    public int getTableId() {
        return tableId;
    }

    public Customer getOccupant() {
        return occupant;
    }

    public boolean isFree() {
        return occupant == null;
    }

    // Assigns a customer to the table.
    public void seat(Customer customer) {
        occupant = customer;
        customer.setTable(this);
    }

    // Frees the table.
    public void release() {

        if (occupant != null) {
            occupant.setTable(null);
        }

        occupant = null;
    }
}
