package bgu.spl.mics;
import java.util.LinkedList;
import java.util.concurrent.*;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

	//fields

	//holding queues of message and future for every micro service registered in the system
	private ConcurrentHashMap<MicroService, BlockingQueue<Message>> mapOfMS;

	//holding queues of subscribed micro service per event
	private ConcurrentHashMap<Object, BlockingQueue<MicroService>> mapOfEvents;

	//holding arrays f subscribed micro service per broadcast
	private ConcurrentHashMap<Object, LinkedList<MicroService>> mapOfBroadcasts;

	//holding all the pairs of future and message
	private ConcurrentHashMap<Message, Future> mapOfFutures;

	/**
	 * Private class that holds the singelton.
	 */
	private static class messageBusImplHolder {

		private static MessageBusImpl instance = new MessageBusImpl();
	}

	/**
	 * Initialization code for ResourceHolder.
	 */
	private MessageBusImpl() {
		mapOfMS = new ConcurrentHashMap<>();
		mapOfEvents = new ConcurrentHashMap<>();
		mapOfBroadcasts = new ConcurrentHashMap<>();
		mapOfFutures = new ConcurrentHashMap<>();
	}


	/**
	 * Retrieves the single instance of this class.
	 */
	public static MessageBusImpl getInstance() {
		return messageBusImplHolder.instance;
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		if (mapOfEvents.get(type) == null) {
			BlockingQueue q = new LinkedBlockingQueue<MicroService>();
			try {
				mapOfEvents.put(type, q);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			mapOfEvents.get(type).add(m);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		if (mapOfBroadcasts.get(type) == null) {
			LinkedList q = new LinkedList();
			try {
				mapOfBroadcasts.put(type, q);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			mapOfBroadcasts.get(type).add(m);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	@Override
	public <T> void complete(Event<T> e, T result) {
		mapOfFutures.get(e).resolve(result);
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		for (MicroService m : mapOfBroadcasts.get(b.getClass())) {
			mapOfMS.get(m).add(b);
		}
	}


	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		Future<T> f = new Future<T>();
		if (mapOfEvents.get(e) != null) {
			MicroService m = mapOfEvents.get(e).poll();
			mapOfMS.get(m).add(e);
			mapOfFutures.put(e, f);
			return f;
		} else {
			return null;
		}
	}

	@Override
	public void register(MicroService m) {

		//create m's messages blocking queue
		BlockingQueue q = new LinkedBlockingQueue<Message>();
		mapOfMS.put(m, q);
	}

	@Override
	public void unregister(MicroService m) {

		//delelte m's queue
		mapOfMS.remove(m);

		//delete m's event subscription
		for (BlockingQueue<MicroService> b : mapOfEvents.values()) {
			b.remove(m);
		}

		//delete m's broadcast subscription
		for (LinkedList<MicroService> l : mapOfBroadcasts.values()) {
			l.remove(m);
		}

	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		try {
			return mapOfMS.get(m).take();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


}


