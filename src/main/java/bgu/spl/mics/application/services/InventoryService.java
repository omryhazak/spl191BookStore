package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CheckAvailabilityEvent;
import bgu.spl.mics.application.messages.TakeEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.OrderResult;

import java.util.concurrent.CountDownLatch;
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

public class InventoryService extends MicroService {

	private Inventory inventory;
	private int duration;
	private CountDownLatch countDownLatch;

	public InventoryService(String name, int duration, CountDownLatch countDownLatch) {
		super(name);
		inventory = Inventory.getInstance();
		this.duration = duration;
		this.countDownLatch = countDownLatch;
	}

	@Override
	protected void initialize() {
		//subscribing to the Tick Broadcast
		subscribeBroadcast(TickBroadcast.class, b -> {

			////lambda implementation of Tick Broadcast callback
			if(b.getCurrentTick()==duration) terminate();
		});

		subscribeEvent(CheckAvailabilityEvent.class, (CheckAvailabilityEvent e) ->{
			complete(e, inventory.checkAvailabiltyAndGetPrice(e.getBookTitle()));
		});

		subscribeEvent(TakeEvent.class, (TakeEvent e) ->{
			complete(e, inventory.take(e.getBookTitle()));
		});

		this.countDownLatch.countDown();
	}

}