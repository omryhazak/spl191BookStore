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
	private Queue<DeliveryVehicle> availableVehicles;
	private Queue<Future<DeliveryVehicle>> futureVehicles;
	private Semaphore samAvailableVehicles;
	private Semaphore semFutureVehicles;

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
		availableVehicles = new ConcurrentLinkedQueue<>();
		futureVehicles = new ConcurrentLinkedQueue<>();
		semFutureVehicles = new Semaphore(0);
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
		//creates a future that will be later resolved to a vehicle.
		Future f = new Future<DeliveryVehicle>();

		//checks if there is a vehicle that can resolve the future immediately
		if(samAvailableVehicles.tryAcquire()){
			f.resolve(availableVehicles.poll());
		}

		//if there is no vehicle available, add the future to the futures list that will be resolved later
		//when another vehicle will be returned.
		else{
			futureVehicles.add(f);
			semFutureVehicles.release();
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
		//checks if there is a future that waits to be resolved.
		if(semFutureVehicles.tryAcquire()){

			//if so, resolving the vehicle immediately
			futureVehicles.poll().resolve(vehicle);
		}
		else {
			//else, returns the vehicle to the list of the available vehicles.
			availableVehicles.add(vehicle);
			samAvailableVehicles.release();
		}
	}

	/**
	 * Receives a collection of vehicles and stores them by license.
	 * <p>
	 * @param vehicles	Array of {@link DeliveryVehicle} instances to store.
	 */
	public void load(DeliveryVehicle[] vehicles) {
		availableVehicles.addAll(Arrays.asList(vehicles));
		samAvailableVehicles = new Semaphore(vehicles.length);
	}

}