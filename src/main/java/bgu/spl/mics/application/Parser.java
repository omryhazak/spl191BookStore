package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;

import java.util.concurrent.Semaphore;

public class Parser {

    private BookInventoryInfo[] initialInventory;
    private Vehicles[] initialResources;
    private Services services;

    public Services getServices() {
        return services;
    }

    public BookInventoryInfo[] getInitialInventory() {
        return initialInventory;
    }

    public Vehicles[] getInitialResources() {
        return initialResources;
    }

    public void initialSemaphore(){
        for(BookInventoryInfo b : initialInventory){
            b.semaphore = new Semaphore(b.getAmountInInventory());
        }
    }
}
