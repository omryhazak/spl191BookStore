package bgu.spl.mics.application.services;

import bgu.spl.mics.application.messages.CheckAvailabilityEvent;
import bgu.spl.mics.application.messages.TakeEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.OrderResult;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * InventoryService is in charge of the book inventory and stock.
 * Holds a reference to the {@link Inventory} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */

public class InventoryService extends MicroService{

	private AtomicInteger currentTime;
	private Inventory inventory;

	public InventoryService(String name) {
		super(name);
		currentTime = new AtomicInteger(1);
		inventory = Inventory.getInstance();
	}

	@Override
	protected void initialize() {
		//subscribing to the Tick Broadcast
		subscribeBroadcast(TickBroadcast.class, b -> {

			////lambda implementation of Tick Broadcast callback
			this.currentTime.incrementAndGet();
		});

		subscribeEvent(CheckAvailabilityEvent.class, (CheckAvailabilityEvent e) ->{
			int bookPrice =inventory.checkAvailabiltyAndGetPrice(e.getBookTitle());
			complete(e, bookPrice);
		});

		subscribeEvent(TakeEvent.class, (TakeEvent e) ->{
			OrderResult toreturn  = inventory.take(e.getBookTitle());
			complete(e, toreturn);
		});


	}

}