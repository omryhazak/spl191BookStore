package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.messages.CheckVehicle;
import bgu.spl.mics.application.messages.ReturnVehicle;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ResourceService is in charge of the store resources - the delivery vehicles.
 * Holds a reference to the {@link //ResourceHolder} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link //MoneyRegister}, {@link //Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ResourceService extends MicroService{

	private ResourcesHolder resourcesHolder;
	private int duration;
	private CountDownLatch countDownLatch;


	public ResourceService(String name, int duration,  CountDownLatch countDownLatch) {
		super(name);
		resourcesHolder = ResourcesHolder.getInstance();
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

		//subscribing to the CheckVehicle event
		subscribeEvent(CheckVehicle.class, (CheckVehicle e) ->{

			//lambda implementation of Check vehicle callback

			//tries to take a vehicle, waits if needed, until there is vehicle.
			Future<DeliveryVehicle> f1 = resourcesHolder.acquireVehicle();
			complete(e, f1);
		});

		//subscribing to the Return Vehicle event
		subscribeEvent(ReturnVehicle.class, (ReturnVehicle e) ->{

			//lambda implementation of Check vehicle callback

			//return a vehicle to the resource holder
			resourcesHolder.releaseVehicle(e.getDeliveryVehicle());
		});
		this.countDownLatch.countDown();
	}
}