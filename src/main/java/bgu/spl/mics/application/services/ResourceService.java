package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.messages.CheckVehicle;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ResourceService is in charge of the store resources - the delivery vehicles.
 * Holds a reference to the {@link ResourceHolder} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ResourceService extends MicroService{

	private ResourcesHolder resourcesHolder;
	private AtomicInteger currentTime;

	public ResourceService(String name) {
		super(name);
		resourcesHolder = ResourcesHolder.getInstance();
		currentTime.set(1);
	}

	@Override
	protected void initialize() {

		//subscribing to the Tick Broadcast
		subscribeBroadcast(TickBroadcast.class, b -> {

			////lambda implementation of Tick Broadcast callback
			this.currentTime.incrementAndGet();
		});

		//subscribing to the Check Vehicle
		subscribeEvent(CheckVehicle.class, (CheckVehicle e) ->{

			//lambda implementation of Check vehicle callback

			//tries to take a vehicle, waits if needed, until there is vehicle.
			Future<DeliveryVehicle> f1 = resourcesHolder.acquireVehicle();
			DeliveryVehicle deliveryVehicle = f1.get();

			//makes the delivery.

			//**********maybe we need to split it to the way to the house and the way back... dont know yet********
			deliveryVehicle.deliver(e.getCustomer().getAddress(),  e.getCustomer().getDistance());

			//return the vehicle.
			resourcesHolder.releaseVehicle(deliveryVehicle);

			//resolving the future object, and announcing that delivery was sent successfully.
			complete(e, true);

		});
	}
}
