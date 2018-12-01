package bgu.spl.mics.application.passiveObjects;
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
public class Inventory {

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
			for (int i = 0; i < inventory.length; i++) {
				inv.put(inventory[i].getBookTitle(), inventory[i]);
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
		OrderResult orderResult;
		orderResult = OrderResult.SUCCESSFULLY_TAKEN;
		if (b != null) {
			try {
				if (!b.semaphore.tryAcquire()) {
					orderResult = OrderResult.NOT_IN_STOCK;
				} else {
					b.reduceAmountInInventory();
				}
			} catch (Exception e) {
			}
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
		AtomicInteger i = null;

		try {
			b.semaphore.acquire();
			if (b.getAmountInInventory() != 0) {
				i.set(b.getPrice());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		b.semaphore.release();
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

	}
}
