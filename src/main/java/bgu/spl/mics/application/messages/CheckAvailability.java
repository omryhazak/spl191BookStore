package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

public class CheckAvailability implements Event<Boolean> {

    private String bookTitle;
    //private Boolean toReturn;


    public CheckAvailability(String bookTitle) {
        this.bookTitle = bookTitle;
    }
}
