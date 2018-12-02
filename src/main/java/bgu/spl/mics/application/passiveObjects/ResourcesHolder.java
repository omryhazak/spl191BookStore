package bgu.spl.mics.application.passiveObjects;
import bgu.spl.mics.Future;
import java.util.concurrent.*;
import java.util.*;


/**
 * Passive object representing the resource manager.
 * You must not alter any of the given public ` of this class.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class ResourcesHolder {

	//field
	private BlockingQueue<DeliveryVehicle> availableVehicles;

	/**
	 * Private class that holds the singelton.
	 */
	private static class Holder {
		private static ResourcesHolder instance = new ResourcesHolder();
	}

	/**
	 * Initialization code for ResourceHolder.
	 */
	private ResourcesHolder() {
		availableVehicles = new LinkedBlockingQueue<>();
	}


	/**
     * Retrieves the single instance of this class.
     */
	public static ResourcesHolder getInstance() {
		return Holder.instance;
	}


	/**
     * Tries to acquire a vehicle and gives a future object which will
     * resolve to a vehicle.
     * <p>
     * @return 	{@link Future<DeliveryVehicle>} object which will resolve to a 
     * 			{@link DeliveryVehicle} when completed.   
     */
public Future<DeliveryVehicle> acquireVehicle() {
	Future f = new Future<DeliveryVehicle>();
	try {
		DeliveryVehicle deliveryVehicle = availableVehicles.take();
		f.resolve(deliveryVehicle);
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
	return f;
	}
	
	/**
     * Releases a specified vehicle, opening it again for the possibility of
     * acquisition.
     * <p>
     * @param vehicle	{@link DeliveryVehicle} to be released.
     */
		public void releaseVehicle(DeliveryVehicle vehicle) {
		availableVehicles.add(vehicle);
	}
	
	/**
     * Receives a collection of vehicles and stores them by license.
     * <p>
     * @param vehicles	Array of {@link DeliveryVehicle} instances to store.
     */
	public void load(DeliveryVehicle[] vehicles) {
		availableVehicles.addAll(Arrays.asList(vehicles));
	}

}
