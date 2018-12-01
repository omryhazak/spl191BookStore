package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;

public class BookOrderEvent implements Event<OrderReceipt> {


    private String bookTitle;
    //private OrderReceipt orderReceiptToReturn;
    private Customer customer;


    public BookOrderEvent(String bookTitle, Customer customer) {
        this.bookTitle = bookTitle;
        this.customer = customer;
       // this.orderReceiptToReturn = null;
    }




}
