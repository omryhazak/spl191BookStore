package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BookOrderEvent;
import bgu.spl.mics.application.messages.DeliveryEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;
//import javafx.util.Pair;
import org.graalvm.util.Pair;


import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

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
	private LinkedList<Pair<String, Integer>> orderSchedule;
	private AtomicInteger currentTime;
	private AtomicInteger duration;

	public APIService(String name, Customer customer, int duration, List<Pair<String, Integer>> orderSchedule) {
		super(name);
		currentTime = new AtomicInteger(1);
		this.customer = customer;
		this.duration.set(duration);
		this.orderSchedule = new LinkedList<>();
		this.orderSchedule.addAll(orderSchedule);
	}

	@Override
	protected void initialize() {
		//sorting the order schedule by time the books should be ordered.
		Collections.sort(orderSchedule, Comparator.comparing(p -> -p.getRight()));

		//subscribing to the Tick Broadcast
		//lambda implementation of Tick Broadcast callback
		subscribeBroadcast(TickBroadcast.class, b -> {

			//service gets the tick.
			this.currentTime.incrementAndGet();

			//checks if there is an order should be sent by going over the sorted list.
			boolean toStop = false;

				while(!toStop){

				//checks if it is the tick we need to order the book.
				if (currentTime.get() == orderSchedule.getFirst().getRight()) {

					//if it is the tick, we will create new orderBook event and take out the pair from the sorted schedule.
					//we will keep on doing it until the first book which it is not his time to be ordered.
					//i changed the first argument of the new book order event
					Future<OrderReceipt> f1 = sendEvent(new BookOrderEvent(orderSchedule.getFirst().getLeft(), customer, currentTime.get()));
					orderSchedule.poll();

					//checking the result from the book order event.
					//according to that, decide if send delivery event
					if(f1 != null) { //than the book order was successfully executed
						Future f2 = sendEvent(new DeliveryEvent(customer));

					}
				}
				else {
					toStop = true;
				}
				}

			//terminate if he got the last tick.
			if (currentTime.equals(duration)) {
				this.terminate();
			}
		});

	}
}
