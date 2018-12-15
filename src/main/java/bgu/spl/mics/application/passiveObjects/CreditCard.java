package bgu.spl.mics.application.passiveObjects;

import java.io.Serializable;

public class CreditCard implements Serializable {

    private int number;
    private int amount;

    public CreditCard(int number, int amount) {
        this.number = number;
        this.amount = amount;
    }

    public int getNumber() {
        return number;
    }

    public int getAmount() {
        return amount;
    }
}
