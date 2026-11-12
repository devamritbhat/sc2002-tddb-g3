package domain;

import support.ActionResult;
import support.CustomerStatus;

// Seats waiting customers, in addition to the Waiter.
public class Host {

    // Seats a waiting customer at a free table.
    public ActionResult seatCustomer(Customer customer, Table table) {

        if (customer == null) {
            return ActionResult.failure("No customer is waiting for a table.");
        }

        if (table == null || !table.isFree()) {
            return ActionResult.failure("No free table is available.");
        }

        if (customer.getStatus() != CustomerStatus.WAITING) {
            return ActionResult.failure("Customer is not waiting for a table.");
        }

        table.seat(customer);
        customer.setStatus(CustomerStatus.SEATED);

        return ActionResult.success("Host seated Customer " + customer.getId()+ " at Table " + table.getTableId() + ".");
    }
}
