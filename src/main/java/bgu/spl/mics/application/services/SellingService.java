package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BookOrderEvent;
import bgu.spl.mics.application.messages.CheckAvailabilityEvent;
import bgu.spl.mics.application.messages.TakeEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;
import bgu.spl.mics.application.passiveObjects.OrderResult;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Selling service in charge of taking orders from customers.
 * Holds a reference to the {@link MoneyRegister} singleton of the store.
 * Handles {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link //ResourcesHolder}, {@link //Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class SellingService extends MicroService {

	private AtomicInteger currentTime;
	private MoneyRegister moneyRegister;
	private int duration;
	private CountDownLatch countDownLatch;


	public SellingService(String name, int duration, CountDownLatch countDownLatch) {
		super(name);
		moneyRegister = MoneyRegister.getInstance();
		this.currentTime = new AtomicInteger(1);
		this.duration = duration;
		this.countDownLatch = countDownLatch;


	}

	@Override
	protected void initialize() {

		//subscribing to the Tick Broadcast
		subscribeBroadcast(TickBroadcast.class, b -> {

			////lambda implementation of Tick Broadcast callback
			this.currentTime.set(b.getCurrentTick());
			if (b.getCurrentTick() == duration) terminate();
		});


		//subscribing to the BookOrderEvent
		subscribeEvent(BookOrderEvent.class, (BookOrderEvent e
		) -> {

			//lambda implementation of bookOrderEvent callback

			System.out.println(this.getName() + " got book order event");


			int processTick = this.currentTime.get();
			OrderReceipt toReturn = null;
			int orderTick = e.getOrderTick().get();
			boolean holdKey = false;

			//acquiring a a key to a customer
			while (!holdKey) {
				try {
					e.getCustomer().semaphore.acquire();
					holdKey = true;
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

			//check availability of the book
			Future<Integer> f1 = sendEvent(new CheckAvailabilityEvent(e.getBookTitle(), e.getCustomer()));


			int bookPrice = f1.get();


			boolean customerHasEnoughMoney = (e.getCustomer().getAvailableCreditAmount() - bookPrice >= 0);

			//check if the customer can afford the book
			if (bookPrice == -1 || !customerHasEnoughMoney) {
				complete(e, null);
			} else {
				Future<OrderResult> f2 = sendEvent(new TakeEvent(e.getBookTitle()));
				OrderResult o = f2.get();
				if (o == OrderResult.SUCCESSFULLY_TAKEN) {
					moneyRegister.chargeCreditCard(e.getCustomer(), bookPrice);
					toReturn = new OrderReceipt(0, this.getName(), e.getCustomer().getId(), e.getBookTitle(), bookPrice, this.currentTime.get(), orderTick, processTick);
					e.getCustomer().addReceipt(toReturn);
					moneyRegister.file(toReturn);
					complete(e, toReturn);
				} else complete(e, null);
			}
			e.getCustomer().semaphore.release();


		});
		this.countDownLatch.countDown();


	}
}


