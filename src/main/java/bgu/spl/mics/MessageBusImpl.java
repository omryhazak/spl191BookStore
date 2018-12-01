package bgu.spl.mics;
import org.graalvm.util.Pair;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.*;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

	//fields
	private ConcurrentHashMap<MicroService, BlockingQueue<Pair<Message,Future>>> mapOfMS;
	private ConcurrentHashMap<Object, BlockingQueue<MicroService>> mapOfEvents;
	private  ConcurrentHashMap<Object, BlockingQueue<MicroService>> mapOfBroadcasts;

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
	}


	/**
	 * Retrieves the single instance of this class.
	 */
	public static MessageBusImpl getInstance() {
		return messageBusImplHolder.instance;
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		generalSubscribe(mapOfEvents, type, m);
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		generalSubscribe(mapOfBroadcasts, type, m);
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
		Future f = new Future();
		MicroService m = mapOfEvents.get(e).poll();
		mapOfMS.get(m).add(e);
		return f;
	}

	@Override
	public void register(MicroService m) {
		BlockingQueue<Message> q = new LinkedBlockingQueue<>();
		mapOfMS.put(m, q);
	}

	@Override
	public void unregister (MicroService m){
		mapOfMS.remove(m);

			//also need to unsubscribe
	}

	@Override
	public Message awaitMessage (MicroService m) throws InterruptedException {
		BlockingQueue<Pair<Message,Future>> q = mapOfMS.get(m);
		return q.take().getLeft();
	}

	private void  generalSubscribe(ConcurrentHashMap<Object, BlockingQueue<MicroService>> map, Class<? extends Message> type, MicroService m){
		if (map.get(type) == null){
			BlockingQueue q = new LinkedBlockingQueue<MicroService>();
			try {
				map.put(type, q);
			} catch (Exception e){
				e.printStackTrace();
			}
		}
		try {
			map.get(type).add(m);
		} catch (Exception e){
			e.printStackTrace();
		}
	}
}


