package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;

import java.util.concurrent.atomic.AtomicInteger;

//***************need to be changed also at nir!!!******************************************************
public class BookOrderEvent implements Event {


    private String bookTitle;
    //private OrderReceipt orderReceiptToReturn;
    private Customer customer;
    private AtomicInteger orderTick;


    public BookOrderEvent(String bookTitle, Customer customer, int orderTick) {

        this.bookTitle = bookTitle;
        this.customer = customer;
        this.orderTick = new AtomicInteger(orderTick);

    }


    public String getBookTitle() {
        return bookTitle;
    }

    public Customer getCustomer() {
        return customer;
    }

    public AtomicInteger getOrderTick() {
        return orderTick;
    }
}
