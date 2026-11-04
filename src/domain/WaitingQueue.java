package domain;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

// Stores customers waiting for a table.
public class WaitingQueue {

    private final Queue<Customer> customers;

    public WaitingQueue() {
        customers = new LinkedList<>();
    }

    public void add(Customer customer) {
        customers.add(customer);
    }

    public Customer peek() {
        return customers.peek();
    }

    public Customer remove() {
        return customers.poll();
    }

    public boolean remove(Customer customer) {
        return customers.remove(customer);
    }

    public boolean isEmpty() {
        return customers.isEmpty();
    }

    public int size() {
        return customers.size();
    }

    // Returns a copy for displaying the queue.
    public List<Customer> getCustomers() {
        return new ArrayList<>(customers);
    }
}
