package bgu.spl.mics.application.passiveObjects;

import java.io.Serializable;

public class Order implements Serializable {

    private String bookTitle;
    private int tick;

    public String getBookTitle() {
        return bookTitle;
    }

    public int getTick() {
        return tick;
    }
}
