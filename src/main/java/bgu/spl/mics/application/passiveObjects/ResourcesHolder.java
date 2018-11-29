package bgu.spl.mics.application.passiveObjects;
import bgu.spl.mics.Future;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
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
	private ConcurrentHashMap<Integer, DeliveryVehicle> availableVehicles;
	private ConcurrentHashMap<Integer, DeliveryVehicle> inUseVehicles;
	private Semaphore semaphore;

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
		availableVehicles = new ConcurrentHashMap<Integer,DeliveryVehicle>();
		inUseVehicles = new ConcurrentHashMap<Integer,DeliveryVehicle>();
		semaphore = new Semaphore(availableVehicles.size());
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
		semaphore.acquire();
		Enumeration<Integer> keys = availableVehicles.keys();
		Integer key = keys.nextElement();
		DeliveryVehicle deliveryVehicle = availableVehicles.get(key);
		f.resolve(deliveryVehicle);

	} catch (Exception e) {
		semaphore.release();
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
		DeliveryVehicle deliveryVehicle = inUseVehicles.remove(vehicle);
		availableVehicles.put(deliveryVehicle.getLicense(), deliveryVehicle);
	}
	
	/**
     * Receives a collection of vehicles and stores them by license.
     * <p>
     * @param vehicles	Array of {@link DeliveryVehicle} instances to store.
     */
	public void load(DeliveryVehicle[] vehicles) {
		for (DeliveryVehicle deliveryVehicle: vehicles){
			this.availableVehicles.put(deliveryVehicle.getLicense(), deliveryVehicle);
		}
	}

}
