package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Customer;

public class CheckAvailabilityEvent implements Event<Boolean> {

    private String bookTitle;
    private Customer customer;


    public CheckAvailabilityEvent(String bookTitle, Customer customer) {
        this.customer = customer;
        this.bookTitle = bookTitle;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public Customer getCustomer() {
        return customer;
    }
}
