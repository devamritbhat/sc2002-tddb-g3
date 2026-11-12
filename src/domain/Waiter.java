package domain;

import support.ActionResult;
import support.CustomerStatus;
import support.OrderStatus;

// Handles waiter actions.
public class Waiter {

    // Seats a waiting customer at a free table.
    public ActionResult seatCustomer(Customer customer, Table table) {

        if (customer == null) {
            return ActionResult.failure("No customer selected.");
        }

        if (table == null || !table.isFree()) {
            return ActionResult.failure("No free table is available.");
        }

        if (customer.getStatus() != CustomerStatus.WAITING) {
            return ActionResult.failure("Customer is not waiting for a table.");
        }

        table.seat(customer);
        customer.setStatus(CustomerStatus.SEATED);

        return ActionResult.success(
                "Customer " + customer.getId()
                        + " seated at Table "
                        + table.getTableId() + "."
        );
    }

    // Takes an order from a seated customer. Happy Hour applies if it is active.
    public ActionResult takeOrder(Customer customer,
                                  MenuItem item,
                                  int orderId,
                                  HappyHour happyHour) {

        if (customer == null || item == null) {
            return ActionResult.failure("Invalid customer or menu item.");
        }

        if (customer.getStatus() != CustomerStatus.SEATED) {
            return ActionResult.failure("Customer is not ready to order.");
        }

        if (customer.getOrder() != null) {
            return ActionResult.failure("Customer already has an order.");
        }

        Order order = new Order(orderId, customer, item, happyHour);

        customer.setOrder(order);
        customer.setStatus(CustomerStatus.ORDERED);

        return ActionResult.success(
                "Customer " + customer.getId()
                        + " ordered " + item.getName() + "."
        );
    }

    // Serves an order that is ready.
    public ActionResult serveOrder(Order order, int currentTurn) {

        if (order == null) {
            return ActionResult.failure("No order selected.");
        }

        if (order.getStatus() != OrderStatus.READY) {
            return ActionResult.failure("Order is not ready to serve.");
        }

        order.markServed(currentTurn);

        Customer customer = order.getCustomer();
        customer.setStatus(CustomerStatus.SERVED);

        return ActionResult.success(
                "Order " + order.getOrderId()
                        + " served to Customer "
                        + customer.getId() + "."
        );
    }
}
