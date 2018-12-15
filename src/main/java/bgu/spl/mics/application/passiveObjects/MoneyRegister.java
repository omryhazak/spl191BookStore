package bgu.spl.mics.application.passiveObjects;


import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;

import java.util.LinkedList;
import java.util.List;
//import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Passive object representing the store finance management. 
 * It should hold a list of receipts issued by the store.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class MoneyRegister  implements Serializable {

	//field
	private LinkedList<OrderReceipt> list;
	private int totalEarnings;

	/**
	 * Private class that holds the singelton.
	 */
	private static class Holder {
		private static MoneyRegister instance = new MoneyRegister();
	}

	/**
	 * Initialization code for ResourceHolder.
	 */
	private MoneyRegister() {
		list = new LinkedList<>();
		totalEarnings = 0;

	}


	/**
	 * Retrieves the single instance of this class.
	 */
	public static MoneyRegister getInstance() {
		return Holder.instance;
	}
	
	/**
     * Saves an order receipt in the money register.
     * <p>   
     * @param r		The receipt to save in the money register.
     */
	public void file (OrderReceipt r) {
		list.add(r);
	}
	
	/**
     * Retrieves the current total earnings of the store.  
     */
	public int getTotalEarnings() {
		return totalEarnings;
	}
	
	/**
     * Charges the credit card of the customer a certain amount of money.
     * <p>
     * @param amount 	amount to charge
     */
	public void chargeCreditCard(Customer c, int amount) {
		c.chargeCustomer(amount);
		totalEarnings = totalEarnings + amount;
	}
	
	/**
     * Prints to a file named @filename a serialized object List<OrderReceipt> which holds all the order receipts 
     * currently in the MoneyRegister
     * This method is called by the main method in order to generate the output.. 
     */
	public void printOrderReceipts(String filename) {
		LinkedList<OrderReceipt> toReturn  = new LinkedList<>();
		for(OrderReceipt o : this.list){
			toReturn.add(o);
		}
		try(FileOutputStream file = new FileOutputStream(filename)){
			ObjectOutputStream output = new ObjectOutputStream(file);
			output.writeObject(this.list);
			output.close();
		}catch (Exception e){
			System.out.println("im in receipts");
		}
	}
}
