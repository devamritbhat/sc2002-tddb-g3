package domain;

import java.util.List;

import support.ActionResult;
import support.CustomerStatus;
import support.OrderStatus;

// Handles automatic customer payments.
public class Cashier {

    // Processes the oldest eligible customer payment.
    public ActionResult processNextPayment(Restaurant restaurant,
                                           int currentTurn) {

        List<Customer> customers =
                restaurant.findReadyToPayCustomers();

        for (Customer customer : customers) {

            Order order = customer.getOrder();

            if (order.getReadyToPayTurn() < currentTurn) {

                order.markPaid();
                customer.setStatus(CustomerStatus.PAID);

                restaurant.getStats().recordPayment(
                        customer,
                        order.getBillCents()
                );

                if (customer.getTable() != null) {
                    customer.getTable().release();
                }

                return ActionResult.success(
                        "Customer " + customer.getId()
                                + " paid $"
                                + String.format(
                                        "%.2f",
                                        order.getBillCents() / 100.0
                                )
                                + "."
                );
            }
        }

        return ActionResult.failure(
                "No customer is ready to pay."
        );
    }
}
