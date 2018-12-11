package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.application.messages.BookOrderEvent;
import bgu.spl.mics.application.messages.DeliveryEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;
import bgu.spl.mics.application.passiveObjects.OrderSchedule;


import java.util.*;

/**
 * APIService is in charge of the connection between a client and the store.
 * It informs the store about desired purchases using {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link //ResourcesHolder}, {@link //MoneyRegister}, {@link //Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class APIService extends MicroService {

	private Customer customer;
	private int currentTime;
	private int duration;

	public APIService(String name, Customer customer, int duration) {
		super(name);
		currentTime = 1;
		this.customer = customer;
		this.duration = duration;

	}

	@Override
	protected void initialize() {
		//sorting the order schedule by time the books should be ordered.
		Arrays.sort(customer.getOrderSchedule(), Comparator.comparing(OrderSchedule::getTick));

		//subscribing to the Tick Broadcast
		//lambda implementation of Tick Broadcast callback
		subscribeBroadcast(TickBroadcast.class, b -> {


			//service gets the tick.
			this.currentTime++;

			//checks if there is an order should be sent by going over the sorted list.
			boolean toStop = false;

				while(!toStop){

				//checks if it is the tick we need to order the book.
				if (customer.getOrderSchedule().length != 0 && currentTime == customer.getOrderScheduleList().getFirst().getTick()) {

					//if it is the tick, we will create new orderBook event and take out the pair from the sorted schedule.
					//we will keep on doing it until the first book which it is not his time to be ordered.
					//i changed the first argument of the new book order event
					Future<OrderReceipt> f1 = sendEvent(new BookOrderEvent(customer.getOrderScheduleList().getFirst().getBookTitle() , customer, currentTime));
					customer.getOrderScheduleList().poll();
					OrderReceipt orderReceipt = f1.get();

					//checking the result from the book order event.
					//according to that, decide if send delivery event
					if(orderReceipt != null) { //than the book order was successfully executed
						Future f2 = sendEvent(new DeliveryEvent(customer));

					}
				}
				else {
					toStop = true;
				}
				}

			//terminate if he got the last tick.
			if (currentTime == duration) {
				this.terminate();
			}
		});

	}
}
