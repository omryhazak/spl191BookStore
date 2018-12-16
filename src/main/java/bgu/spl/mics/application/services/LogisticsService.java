package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CheckVehicle;
import bgu.spl.mics.application.messages.DeliveryEvent;
import bgu.spl.mics.application.messages.ReturnVehicle;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Logistic service in charge of delivering books that have been purchased to customers.
 * Handles {@link DeliveryEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link //ResourcesHolder}, {@link //MoneyRegister}, {@link //Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LogisticsService extends MicroService {

	private int duration;
	private CountDownLatch countDownLatch;
	private int tick;
	int speed;



	public LogisticsService(String name, int duration, int speed, CountDownLatch countDownLatch) {
		super(name);
		this.duration = duration;
		this.countDownLatch = countDownLatch;
		this.speed = speed;
	}

	@Override
	protected void initialize() {
		//subscribing to the Tick Broadcast
		subscribeBroadcast(TickBroadcast.class, b -> {

			System.out.println(this.getName() + " got a tick " + b.getCurrentTick());

			tick = b.getCurrentTick();

			////lambda implementation of Tick Broadcast callback
			if(b.getCurrentTick()==duration) terminate();
		});

		subscribeEvent(DeliveryEvent.class, (DeliveryEvent e) ->{

			//sending event checkVehicle in order to get vehicle from resource service and gets future that will be resolved later into vehicle

			Future<Future<DeliveryVehicle>> f1 = sendEvent(new CheckVehicle());

			System.out.println(this.getName() + " send a vehicle in time " + tick);

			//after vehicle was resolved, sending the vehicle with deliver
			DeliveryVehicle deliveryVehicle = null;
			Future<DeliveryVehicle> f2 = null;
			if (f1 != null) {
				f2 = f1.get();
				if(f2 != null && f2.isDone()) {
					deliveryVehicle = f2.get((duration - tick) * speed, TimeUnit.MILLISECONDS);
				}
			}

			System.out.println(this.getName() + " got a vehicle in time " + tick);


			if(f1 != null && f2 != null && deliveryVehicle != null) {

				deliveryVehicle.deliver(e.getCustomer().getAddress(), e.getCustomer().getDistance());

				System.out.println(e.getCustomer().getName() + " book was delievered");

//				//after delivery was done, releasing the vehicle using returnVehicle event.
				sendEvent(new ReturnVehicle(deliveryVehicle));

			}
		});

		this.countDownLatch.countDown();
	}
}