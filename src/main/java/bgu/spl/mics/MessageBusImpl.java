package bgu.spl.mics;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

	//fields
	private ConcurrentHashMap<MicroService, BlockingQueue> mapOfMS;
	private ConcurrentHashMap<Event, Event> mapOfEvents;

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
	}


	/**
	 * Retrieves the single instance of this class.
	 */
	public static MessageBusImpl getInstance() {
		return messageBusImplHolder.instance;
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		// TODO Auto-generated method stub

	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		// TODO Auto-generated method stub

	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendBroadcast(Broadcast b) {
		// TODO Auto-generated method stub

	}


	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void register(MicroService m) {
		BlockingQueue<Message> q = new LinkedBlockingQueue<>();
		map.put(m, q);
	}

	@Override
	public void unregister (MicroService m){
		map.remove(m);

			//also need to unsubscribe
	}

	@Override
	public Message awaitMessage (MicroService m) throws InterruptedException { Message mes = new Message() {
	};
	try {
		mes = (Message) mapOfMS.get(m).take();
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
	return mes;
	}
}
