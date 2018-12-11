package bgu.spl.mics.application;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.services.*;


import java.util.LinkedList;

public class Services {

    private TimeService time;
    private int selling;
    private int inventoryService;
    private int logistics;
    private int resourcesService;
    private Customer[] customers;
    private LinkedList<MicroService> microServices = new LinkedList<>();



    public void initialCustomers(){
        for (int i=0; i<customers.length; i++){
            customers[i].initialCustomer();
        }
    }

    public void startProgram(){

        TimeService timeService = new TimeService(this.time.getSpeed(), this.time.getDuration());
        setTime(timeService);


        for(int i=1; i <= selling; i++){
            SellingService toRun = new SellingService("sellingService" + i, (int) timeService.getDuration());
            microServices.add(toRun);
        }

        for(int i=1; i <= inventoryService; i++){
            InventoryService toRun = new InventoryService("inventoryService" + i, (int) timeService.getDuration());
            microServices.add(toRun);

        }


        for(int i=1; i <= logistics; i++){
            LogisticsService toRun = new LogisticsService("logisticsService" + i, (int) timeService.getDuration());
            microServices.add(toRun);

        }

        for(int i=1; i <= resourcesService; i++){
            ResourceService toRun = new ResourceService("resourceService" + i, (int) timeService.getDuration());
            microServices.add(toRun);

        }

        for(int i=0; i < customers.length; i++){
            APIService toRun = new APIService("apiService" + (i+1), customers[i], (int) timeService.getDuration());
            microServices.add(toRun);
        }

        //microServices.add(time);
//        Iterator<MicroService> iter = microServices.iterator();
//        while(iter.hasNext()){
//           Thread t = new Thread(iter.next());
//            t.start();
//            System.out.println("fun in run");
//        }
        boolean isNull = false;
        while(!isNull){
            MicroService m = microServices.pollFirst();
            if(m != null){
                Thread t = new Thread(m);
                t.start();

            }
            else isNull = true;
        }

        Thread t2 = new Thread(timeService);
        t2.start();
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
