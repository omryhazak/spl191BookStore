package bgu.spl.mics.application;
import bgu.spl.mics.MessageBus;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.application.passiveObjects.*;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.ObjectOutputStream;
import java.util.*;

/** This is the Main class of the application. You should parse the input file, 
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class BookStoreRunner {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        MessageBus mb = MessageBusImpl.getInstance();
        Parser parser = startParsing(args[0]);
        parser.initialBooksSemaphore();
        Inventory inventory = Inventory.getInstance();
        inventory.load(parser.getInitialInventory());
        ResourcesHolder resourcesHolder = ResourcesHolder.getInstance();
        resourcesHolder.load(parser.getInitialResources()[0].getDeliveryVehicles());
        MoneyRegister moneyRegister = MoneyRegister.getInstance();
        HashMap<Integer, Customer> customersHashMap = initialCustomerHashMap(parser);
        parser.getServices().startProgram();
        generateOutputFiles(moneyRegister,inventory, customersHashMap, args[1] , args[2] , args[3], args[4]);
        moneyRegister.printOrderReceipts(args[1]);
        inventory.printInventoryToFile(args[2]);



    }

    //creating the parsed json object
    private static Parser startParsing(String filePath) {
        Parser parser = null;
        try {
            Gson gson = new Gson();
            JsonReader reader = new JsonReader(new FileReader(filePath));
            parser = gson.fromJson(reader, Parser.class);

        } catch (FileNotFoundException e) {
        } finally {
            return parser;
        }

    }

    //creating the output customers hash map
    private static HashMap<Integer, Customer> initialCustomerHashMap(Parser parser){
        HashMap<Integer, Customer> customersHashMap = new HashMap();
        for (int i=0; i<parser.getServices().getCustomers().length; i++){
            customersHashMap.put(parser.getServices().getCustomers()[i].getId(),parser.getServices().getCustomers()[i] );
        }
        return customersHashMap;
    }


    //private function that creates all the output file
    private static void generateOutputFiles(MoneyRegister m, Inventory inv,HashMap<Integer, Customer> customersHashMap, String s1,String s2,String s3,String s4){
        m.printOrderReceipts(s1);
        inv.printInventoryToFile(s2);
        generateFile(s3, m);
        generateFile(s4, customersHashMap);

    }
    //private function that creates output file for a given object
    private static void generateFile(String s, Object o){
        try{
            FileOutputStream file = new FileOutputStream(s);
            ObjectOutputStream output = new ObjectOutputStream(file);
            output.writeObject(o);
            output.close();
            file.close();
        }catch (Exception e){ }

    }
}
