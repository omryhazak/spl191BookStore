package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.CreditCard;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;
import bgu.spl.mics.application.services.*;

public class Services {

    private TimeService time;
    private int selling;
    private int inventoryService;
    private int logistics;
    private int resourcesService;
    private Customer[] customers;



    public void intialCustomers(){
        for (int i=0; i<customers.length; i++){
            customers[i].intialCustomer();
        }
    }

    public void startProgram(){

        this.setTime();

        for(int i=1; i <= selling; i++){
            SellingService toRun = new SellingService("sellingService" + i);
            toRun.run();
        }

        for(int i=1; i <= inventoryService; i++){
            InventoryService toRun = new InventoryService("inventoryService" + i);
            toRun.run();
        }

        for(int i=1; i <= logistics; i++){
            LogisticsService toRun = new LogisticsService("logisticsService" + i);
            toRun.run();
        }

        for(int i=1; i <= resourcesService; i++){
            ResourceService toRun = new ResourceService("resourceService" + i);
            toRun.run();
        }

        time.run();
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

    public void setTime(){
        this.time.setTimeService();
    }

}
