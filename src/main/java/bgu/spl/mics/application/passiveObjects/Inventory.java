package bgu.spl.mics.application.passiveObjects;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;



/**
 * Passive data-object representing the store inventory.
 * It holds a collection of {@link BookInventoryInfo} for all the
 * books in the store.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class Inventory implements Serializable {

	private static ConcurrentHashMap<String, BookInventoryInfo> inv;

	/**
	 * Private class that holds the singelton.
	 */
	private static class Holder {
		private static Inventory instance = new Inventory();
	}

	/**
	 * Initialization code for ResourceHolder.
	 */
	private Inventory() {
		inv = new ConcurrentHashMap<>();
	}

	/**
     * Retrieves the single instance of this class.
	 *
	 * @pre: none.
	 * @post: get one of a kind new inventory.
     */
	public static Inventory getInstance() {
		return Holder.instance;
	}
	
	/**
     * Initializes the store inventory. This method adds all the items given to the store
     * inventory.
     * <p>
     * @param inventory 	Data structure containing all data necessary for initialization
     * 						of the inventory.
     * @pre: inventory != null
	 * @post: hash.size() == inventory.length
	 * @post all the books from inventory are in our hashmap
	 * */
	public void load (BookInventoryInfo[ ] inventory ) {
		if (inventory != null) {
			//add each book to the hashmap
			for(BookInventoryInfo b : inventory){
				inv.put(b.getBookTitle(),b);
			}
		}

	}
	
	/**
     * Attempts to take one book from the store.
     * <p>
     * @param book 		Name of the book to take from the store
     * @return 	an {@link Enum} with options NOT_IN_STOCK and SUCCESSFULLY_TAKEN.
     * 			The first should not change the state of the inventory while the 
     * 			second should reduce by one the number of books of the desired type.
	 * @pre: hash.isEmpty == false
	 * @pre: {@param book} != null
	 * @post: if  checkAvailabiltyAndGetPrice({@param book}) != (-1), setAmountInInventory({@param book})
     */
	public OrderResult take (String book) {
		BookInventoryInfo b = inv.get(book);
		OrderResult orderResult=null;

		if (b.semaphore.tryAcquire()) {
			b.reduceAmountInInventory();
			orderResult = OrderResult.SUCCESSFULLY_TAKEN;
		}
		else {
			orderResult = OrderResult.NOT_IN_STOCK;
		}
		return orderResult;
	}
	
	
	
	/**
     * Checks if a certain book is available in the inventory.
     * <p>
     * @param book 		Name of the book.
     * @return the price of the book if it is available, -1 otherwise
	 * @pre: hash.isEmpty == false
	 * @pre: {@param book} != null
	 * @post: none.
     */
	public int checkAvailabiltyAndGetPrice(String book) {
		BookInventoryInfo b = inv.get(book);
		AtomicInteger i=null ;
		if (b.getAmountInInventory() != 0) {
				i = new AtomicInteger(b.getPrice());
		}else{
			i = new AtomicInteger(-1);
		}

		return i.get();
	}
	
	/**
     * 
     * <p>
     * Prints to a file name @filename a serialized object HashMap<String,Integer> which is a Map of all the books in the inventory. The keys of the Map (type {@link String})
     * should be the titles of the books while the values (type {@link Integer}) should be
     * their respective available amount in the inventory. 
     * This method is called by the main method in order to generate the output.
	 * @pre: hash.isEmpty != null
     */
	public void printInventoryToFile(String filename){
		HashMap<String, Integer> booksHashMap = this.createBooksHashMap();
		try(FileOutputStream file = new FileOutputStream(filename); ObjectOutputStream output = new ObjectOutputStream(file)){
			output.writeObject(booksHashMap);
		}catch (Exception e){
			System.out.println("im in inventory");
		}
	}

	//creating the book hash map to return in output file
	private HashMap<String, Integer> createBooksHashMap(){
		HashMap<String, Integer> toReturn = new HashMap<>();
		for (BookInventoryInfo b : this.inv.values()){
			toReturn.put(b.getBookTitle(), b.getAmountInInventory());
		}
		return toReturn;

	}

	public BookInventoryInfo[] inventoryToArray(){
		int counter = 0;
		BookInventoryInfo[] b = new BookInventoryInfo[this.inv.size()];
		for(BookInventoryInfo bo : inv.values()){
			b[counter] = bo;
			counter++;
		}
		return b;
	}

}
