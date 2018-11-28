package bgu.spl.mics.application.passiveObjects;

/**
 * Passive data-object representing a information about a certain book in the inventory.
 * You must not alter any of the given public methods of this class. 
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class BookInventoryInfo {

	//fields
	private String title;
	private int amount;
	private int price;

	//constructor
	public BookInventoryInfo(String t, int a, int p){
		title = t;
		amount = a;
		price = p;
	}


	/**
     * Retrieves the title of this book.
     * <p>
     * @return The title of this book.   
     */
	public String getBookTitle() {
		return "";
	}

	/**
     * Retrieves the amount of books of this type in the inventory.
     * <p>
     * @return amount of available books.      
     */
	public int getAmountInInventory() {
		return 0;
	}


	/**
	 * reduces the amount of books of this type in inventory by one.
	 */
	public void reduceAmountInInventory(){
		amount = amount - 1;
	}

	/**
     * Retrieves the price for  book.
     * <p>
     * @return the price of the book.
     */
	public int getPrice() {
		return 0;
	}
	
	

	
}
