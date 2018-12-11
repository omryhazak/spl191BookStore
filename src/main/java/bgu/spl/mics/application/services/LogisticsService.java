package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CheckVehicle;
import bgu.spl.mics.application.messages.DeliveryEvent;
import bgu.spl.mics.application.messages.ReturnVehicle;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

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

	private AtomicInteger currentTime;

	public LogisticsService(String name) {
		super(name);
		currentTime = new AtomicInteger(1);
	}

	@Override
	protected void initialize() {
		//subscribing to the Tick Broadcast
		subscribeBroadcast(TickBroadcast.class, b -> {

			////lambda implementation of Tick Broadcast callback
			this.currentTime.incrementAndGet();
		});

		subscribeEvent(DeliveryEvent.class, (DeliveryEvent e) ->{

			//sending event checkVehicle in order to get vehicle from resource service and gets future that will be resolved later into vehicle
			Future<Future<DeliveryVehicle>> f1 = sendEvent(new CheckVehicle());

			//after vehicle was resolved, sending the vehicle with deliver
			DeliveryVehicle deliveryVehicle = f1.get().get();
			deliveryVehicle.deliver(e.getCustomer().getAddress(), e.getCustomer().getDistance());

			//after delivery was done, releasing the vehicle using returnVehicle event.
			sendEvent(new ReturnVehicle(deliveryVehicle));
		});
	}
}