package bgu.spl.mics.application;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.services.*;


import java.util.LinkedList;
import java.util.concurrent.CountDownLatch;

public class Services {

    private TimeService time;
    private int selling;
    private int inventoryService;
    private int logistics;
    private int resourcesService;
    private int numOfServices;
    private Customer[] customers;
    private MicroService[] microServices;
    private Thread[] threadsArray;
    private CountDownLatch countDownLatch;



    public void initialCustomers(){
        for (int i=0; i<customers.length; i++){
            customers[i].initialCustomer();
        }
    }

    public void startProgram(){

        this.numOfServices = selling+inventoryService+logistics+resourcesService+customers.length;
        this.threadsArray = new Thread[numOfServices];
        countDownLatch = new CountDownLatch(numOfServices);
        int counter = 0;

        //initial the customers
        initialCustomers();

        //initial the time service



        //creating the micro service based Threads and run them
        // also adding them into the threads array
        for(int i=1; i <= selling; i++){
            Thread t = new Thread( new SellingService("sellingService" + i, (int) time.getDuration(), countDownLatch));
            threadsArray[counter] = t;
            counter++;
            t.start();
        }

        for(int i=1; i <= inventoryService; i++){
            Thread t  = new Thread(new InventoryService("inventoryService" + i, (int) time.getDuration(), countDownLatch));
            threadsArray[counter] = t;
            counter++;
            t.start();

        }


        for(int i=1; i <= logistics; i++){
           Thread t = new Thread(new LogisticsService("logisticsService" + i, (int) time.getDuration(), countDownLatch));
            threadsArray[counter] = t;
            counter++;
            t.start();

        }

        for(int i=1; i <= resourcesService; i++){
            Thread t = new Thread(new ResourceService("resourceService" + i, (int) time.getDuration(), countDownLatch));
            threadsArray[counter] = t;
            counter++;
            t.start();

        }

        for(int i=0; i < customers.length; i++){
            Thread t = new Thread(new APIService("apiService" + (i+1), customers[i], (int) time.getDuration(), countDownLatch));
            threadsArray[counter] = t;
            counter++;
            t.start();
        }



        //create the timeService thread
        //we want the time service to start ticking only after all other threads initialized
        // so we force the main thread to wait until they all subscribe before starting the time service
        Thread t2 = new Thread(new TimeService(this.time.getSpeed(), this.time.getDuration()));
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            System.out.println("NOT EVERYONE WAS SUBSCRIBED");
        }
        t2.start();
        try {
            t2.join();
        } catch (InterruptedException e) {
            t2.interrupt();
        }
        for(int j=0; j<threadsArray.length; j++){
            try {
                threadsArray[j].join();
            } catch (InterruptedException e) {
                threadsArray[j].interrupt();
            }
        }


    }


    public TimeService getTime() {
        return time;
    }

    public int getSelling() {
        return selling;
    }

    public int getInventoryService() {
        return inventoryService;
    }

    public int getLogistics() {
        return logistics;
    }

    public int getResourcesService() {
        return resourcesService;
    }

    public Customer[] getCustomers() {
        return customers;
    }



}
