package bgu.spl.mics.application.services;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Selling service in charge of taking orders from customers.
 * Holds a reference to the {@link MoneyRegister} singleton of the store.
 * Handles {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class SellingService extends MicroService{
	private AtomicInteger currentTime;
	private MoneyRegister moneyRegister;

	public SellingService() {
		super("Change_This_Name");
		moneyRegister = MoneyRegister.getInstance();


	}

	@Override
	protected void initialize() {
//		subscribeBroadcast(TickBroadcast.class, Broadcast b -> {
//			this.currentTime++;
//		});


		
	}


//	this.name = name;
//	terminated = false;
//	eventsToSubscribe = new ConcurrentLinkedQueue<>();
//	mapOfCallbacksForBroadcasts = new ConcurrentHashMap<>();
//	mapOfCallbacksForEvents = new ConcurrentHashMap<>();
//	MB = MessageBusImpl.getInstance();
}
