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
        System.out.println("starting the program");

        //initial the customers
        initialCustomers();

        //initial the time service
        TimeService timeService = new TimeService(this.time.getSpeed(), this.time.getDuration());
        setTime(timeService);

        //creating the micro service based Threads and run them
        for(int i=1; i <= selling; i++){
            Thread t = new Thread( new SellingService("sellingService" + i, (int) timeService.getDuration(), countDownLatch));
            threadsArray[counter] = t;
            counter++;
            t.start();
        }

        for(int i=1; i <= inventoryService; i++){
            Thread t  = new Thread(new InventoryService("inventoryService" + i, (int) timeService.getDuration(), countDownLatch));
            threadsArray[counter] = t;
            counter++;
            t.start();

        }


        for(int i=1; i <= logistics; i++){
           Thread t = new Thread(new LogisticsService("logisticsService" + i, (int) timeService.getDuration(), countDownLatch));
            threadsArray[counter] = t;
            counter++;
            t.start();

        }

        for(int i=1; i <= resourcesService; i++){
            Thread t = new Thread(new ResourceService("resourceService" + i, (int) timeService.getDuration(), countDownLatch));
            threadsArray[counter] = t;
            counter++;
            t.start();

        }

        for(int i=0; i < customers.length; i++){
            Thread t = new Thread(new APIService("apiService" + (i+1), customers[i], (int) timeService.getDuration(), countDownLatch));
            threadsArray[counter] = t;
            counter++;
            t.start();
        }



        //create the timeService thread
        //we want the time service to start ticking only after all other threads initialized
        // so we force the main thread to wait until they all subscribe before starting the time service
        Thread t2 = new Thread(timeService);
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            System.out.println("NOT EVERYONE WAS SUBSCRIBED");
        }
        t2.start();
        try {
            t2.join();
        } catch (InterruptedException e) {}
        for(int j=0; j<threadsArray.length; j++){
            try {
                threadsArray[j].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
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

    public void setTime(TimeService t){
        t.setTimeService();
    }

}
