package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BookOrderEvent;
import bgu.spl.mics.application.messages.CheckAvailabilityEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;

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

	public SellingService(String name) {
		super(name);
		moneyRegister = MoneyRegister.getInstance();
		currentTime.set(1);


	}

	@Override
	protected void initialize() {

		//subscribing to the Tick Broadcast
		subscribeBroadcast(TickBroadcast.class, b -> {

			////lambda implementation of Tick Broadcast callback
			this.currentTime.incrementAndGet();
		});

		//subscribing to the BookOrderEvent
		subscribeEvent(BookOrderEvent.class, (BookOrderEvent e) ->{
			
			//lambda implementation of bookOrderEvent callback
			OrderReceipt toReturn = null;
			int orderTick = e.getOrederTick().get();
			Future<Boolean> f1 = sendEvent(new CheckAvailabilityEvent(e.getBookTitle(), e.getCustomer()));
			boolean isTaken = f1.get();

			if(!isTaken){
				complete(e, null);
			}else{
				//intialize the orderReceipt
			}



		});




		
	}


//	this.name = name;
//	terminated = false;
//	eventsToSubscribe = new ConcurrentLinkedQueue<>();
//	mapOfCallbacksForBroadcasts = new ConcurrentHashMap<>();
//	mapOfCallbacksForEvents = new ConcurrentHashMap<>();
//	MB = MessageBusImpl.getInstance();
}
