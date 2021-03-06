package bgu.spl.mics.application.passiveObjects;

//import org.graalvm.util.Pair;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Passive data-object representing a customer of the store.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class Customer implements Serializable {

	//fields
	private String name;
	private String address;
	private int id;
	private int distance;
	private int availableAmountInCard;
	private int creditCardNumber;
	private CreditCard creditCard;
	private LinkedList<OrderReceipt> receiptList;
	private LinkedList<Order> orderScheduleList;
	private Order[] orderSchedule;
	public Semaphore semaphore;


	//constructor
	public Customer(int id, String name, String address, int distance, CreditCard creditCard,  LinkedList<OrderReceipt> receiptList, int creditCardNumber, int availableAmountInCard, Order[] orderSchedule){
		this.id = id;
		this.name = name;
		this.address = address;
		this.distance = distance;
		this.receiptList = receiptList;
		this.creditCardNumber = creditCardNumber;
		this.availableAmountInCard =availableAmountInCard;
		this.creditCard = creditCard;
		this.orderSchedule = orderSchedule;
		this.orderScheduleList = new LinkedList<>();
		semaphore = new Semaphore(1);

	}

	public LinkedList<Order> getOrderScheduleList() {
		return orderScheduleList;
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
	public int getCreditCardNumber() {
		return creditCardNumber;
	}

	/**
	 * Sets the amount of money left on this customers credit card.
	 */
	public  void chargeCustomer(int charge) {
			availableAmountInCard = availableAmountInCard - charge;
	}

	public CreditCard getCreditCard() {
		return creditCard;
	}

	// initial the customer in case the json file created, instead of regular constructor
	public void initialCustomer(){
		this.receiptList = new LinkedList<>();
		this.semaphore = new Semaphore(1);
		this.creditCardNumber =this.creditCard.getNumber();
		this.availableAmountInCard = this.creditCard.getAmount();
		this.orderScheduleList = new LinkedList<>();
		for (int i = 0; i<this.getOrderSchedule().length; i++){
			this.orderScheduleList.add(this.orderSchedule[i]);
		}
		this.creditCard = null;
	}

	public Order[] getOrderSchedule(){
		return orderSchedule;
	}


}
