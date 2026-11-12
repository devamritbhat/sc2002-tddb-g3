package domain;

import support.ActionResult;
import support.OrderStatus;

// Handles food preparation.
public class Chef {

    // Prepares one unit of an order.
    public ActionResult prepare(Order order, int currentTurn) {

        if (order == null) {
            return ActionResult.failure("No order selected.");
        }

        if (order.getStatus() != OrderStatus.PLACED && order.getStatus() != OrderStatus.PREPARING) {
            return ActionResult.failure("Order cannot be prepared in its current state.");
        }

        order.prepareOneUnit(currentTurn);

        if (order.getStatus() == OrderStatus.READY) {

            return ActionResult.success( "Order " + order.getOrderId() + " is now READY.");
        }

        return ActionResult.success("Prepared one unit of Order " + order.getOrderId() + ".");
    }
}
