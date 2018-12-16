package bgu.spl.mics.application;
import bgu.spl.mics.MessageBus;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.application.passiveObjects.*;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.util.*;

/** This is the Main class of the application. You should parse the input file, 
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class BookStoreRunner {
    public static void main(String[] args) {

        MessageBus mb = MessageBusImpl.getInstance();
        Parser parser = startParsing(args[0]);
        parser.initialBooksSemaphore();

        Inventory inventory = Inventory.getInstance();
        inventory.load(parser.getInitialInventory());

        ResourcesHolder resourcesHolder = ResourcesHolder.getInstance();
        resourcesHolder.load(parser.getInitialResources()[0].getDeliveryVehicles());

        MoneyRegister moneyRegister = MoneyRegister.getInstance();
        parser.getServices().startProgram();

        generateOutputFiles(moneyRegister,inventory, initialCustomerHashMap(parser), args[1] , args[2] , args[3], args[4]);

        System.out.println(customers2string(parser.getServices().getCustomers()));
        System.out.println(books2string(inventory.inventoryToArray()));

    }

    //creating the parsed json object
    private static Parser startParsing(String filePath) {
        Parser parser = null;
        try(JsonReader reader = new JsonReader(new FileReader(filePath))) {
            Gson gson = new Gson();
            parser = gson.fromJson(reader, Parser.class);

        } catch (FileNotFoundException e) {
        } finally {
            return parser;
        }

    }


    //private function that creates all the output files
    private static void generateOutputFiles(MoneyRegister m, Inventory inv,HashMap<Integer, Customer> customersHashMap, String s1,String s2,String s3,String s4){
        m.printOrderReceipts(s3);
        inv.printInventoryToFile(s2);
        generateOutFile(m, s4);
        generateOutFile(customersHashMap, s1);

    }

    //private function that generates the output files for the moneyRegister and the customers
    private static void generateOutFile(Object o, String filePath){
        try(FileOutputStream file = new FileOutputStream(filePath); ObjectOutputStream output = new ObjectOutputStream(file)){
            output.writeObject(o);
        }catch (Exception e){
        }
    }


    //creating the output customers hash map
    private static HashMap<Integer, Customer> initialCustomerHashMap(Parser parser){
        HashMap<Integer, Customer> customersHashMap = new HashMap();
        for (Customer c: parser.getServices().getCustomers()){
            customersHashMap.put(c.getId(), c);
        }

        return customersHashMap;
    }




    public static String customers2string(Customer[] customers) {
        String str = "";
        for (Customer customer : customers)
            str += customer2string(customer) + "\n---------------------------\n";
        return str;
    }

    public static String customer2string(Customer customer) {
        String str = "id    : " + customer.getId() + "\n";
        str += "name  : " + customer.getName() + "\n";
        str += "addr  : " + customer.getAddress() + "\n";
        str += "dist  : " + customer.getDistance() + "\n";
        str += "card  : " + customer.getCreditCardNumber() + "\n";
        str += "money : " + customer.getAvailableCreditAmount();
        return str;
    }

    public static String books2string(BookInventoryInfo[] books) {
        String str = "";
        for (BookInventoryInfo book : books)
            str += book2string(book) + "\n---------------------------\n";
        return str;
    }

    public static String book2string(BookInventoryInfo book) {
        String str = "";
        str += "title  : " + book.getBookTitle() + "\n";
        str += "amount : " + book.getAmountInInventory() + "\n";
        str += "price  : " + book.getPrice();
        return str;
    }


    public static String receipts2string(OrderReceipt[] receipts) {
        String str = "";
        for (OrderReceipt receipt : receipts)
            str += receipt2string(receipt) + "\n---------------------------\n";
        return str;
    }
    public static String receipt2string(OrderReceipt receipt) {
        String str = "";
        str += "customer   : " + receipt.getCustomerId() + "\n";
        str += "order tick : " + receipt.getOrderTick() + "\n";
        str += "id         : " + receipt.getOrderId() + "\n";
        str += "price      : " + receipt.getPrice() + "\n";
        str += "seller     : " + receipt.getSeller();
        return str;
    }

    public static void Print(String str, String filename) {
        try {
            try (PrintStream out = new PrintStream(new FileOutputStream(filename))) {
                out.print(str);
            }
        } catch (IOException e) {
            System.out.println("Exception: " + e.getClass().getSimpleName());
        }
    }






}
