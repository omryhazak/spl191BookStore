package bgu.spl.mics.application.passiveObjects;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Passive data-object representing a customer of the store.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class Customer {

	//fields
	private int id;
	private String name;
	private String address;
	private int distance;


	//may change due to concurrency..
	private List<OrderReceipt> receiptList;
	private int creditCard;
	private int availableAmountInCard;

	//constructor
	public Customer(int id, String name, String address, int distance, List<OrderReceipt> receiptList, int creditCard, int availableAmountInCard){
		this.id = id;
		this.name = name;
		this.address = address;
		this.distance = distance;
		this.receiptList = receiptList;
		this.creditCard = creditCard;
		this.availableAmountInCard =availableAmountInCard;

	}

	/**
     * Retrieves the name of the customer.
     */
	public String getName() {
		return name;
	}

	/**
     * Retrieves the ID of the customer  . 
     */
	public int getId() {
		return id;
	}
	
	/**
     * Retrieves the address of the customer.  
     */
	public String getAddress() {
		return address;
	}
	
	/**
     * Retrieves the distance of the customer from the store.  
     */
	public int getDistance() {
		return distance;
	}

	/**
	 * Retrieves a list of receipts for the purchases this customer has made.
	 */
	public List<OrderReceipt> getCustomerReceiptList() {
		return receiptList;
	}
	
	/**
	 * Adds a receipt to the list of receipts for the purchases this customer has made.
     * <p>
     * @return A list of receipts.
     */
	public void addReceipt(OrderReceipt o) {
		receiptList.add(o);
	}

	/**
     * Retrieves the amount of money left on this customers credit card.
     * <p>
     * @return Amount of money left.   
     */
	public int getAvailableCreditAmount() {

		return availableAmountInCard;
	}
	
	/**
     * Retrieves this customers credit card serial number.    
     */
	public int getCreditNumber() {
		return creditCard;
	}

	/**
	 * Sets the amount of money left on this customers credit card.
	 */
	public  void chargeCustomer(int charge) {
		synchronized ((Integer)availableAmountInCard) {
			availableAmountInCard = availableAmountInCard - charge;
		}
	}
	
}
