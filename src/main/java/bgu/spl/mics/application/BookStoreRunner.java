package bgu.spl.mics.application;
import bgu.spl.mics.MessageBus;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

/** This is the Main class of the application. You should parse the input file, 
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class BookStoreRunner {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String s = "/users/studs/bsc/2019/nirsti/Desktop/yearB/semesterA/spl/input.json";
        MessageBus mb = MessageBusImpl.getInstance();
        startProgram(s);

    }

    private static void startProgram(String filePath){
        try{
            Gson gson = new Gson();
            JsonReader reader = new JsonReader(new FileReader(filePath));
            Parser parser = gson.fromJson(reader, Parser.class);
            parser.initialSemaphore();
            Inventory inventory = Inventory.getInstance();
            inventory.load(parser.getInitialInventory());
            parser.initialSemaphore();
            ResourcesHolder resourcesHolder = ResourcesHolder.getInstance();
            resourcesHolder.load(parser.getInitialResources()[0].getDeliveryVehicles());
            parser.getServices().initialCustomers();
            parser.getServices().startProgram();


        }catch (FileNotFoundException e){}


    }

}
