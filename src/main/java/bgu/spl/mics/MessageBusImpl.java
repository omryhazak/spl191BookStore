package bgu.spl.mics;
import bgu.spl.mics.application.messages.ResolveAllFutures;

import java.util.Iterator;
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
    private ConcurrentHashMap<Object, LinkedList<MicroService>> mapOfEvents;

    //holding arrays f subscribed micro service per broadcast
    private ConcurrentHashMap<Object, LinkedList<MicroService>> mapOfBroadcasts;

    //holding all the pairs of future and message
    private ConcurrentHashMap<Event, Future> mapOfFutures;

    //lockers
    private Object lock1 = new Object();
    private Object lock2 = new Object();
    private Object lock3 = new Object();

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
    public  <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
        synchronized (lock1) {
            subscribeMessage(type, m, mapOfEvents);
        }
    }

    @Override
    public  void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
        synchronized (lock2) {

            subscribeMessage(type, m, mapOfBroadcasts);
            System.out.println(m.getName() + " was subscribed");
        }
    }


    @Override
    public <T> void complete(Event<T> e, T result) {

        mapOfFutures.get(e).resolve(result);
        mapOfFutures.remove(e);

    }

    @Override
    public void sendBroadcast(Broadcast b) {
        synchronized (lock3) {
            LinkedList<MicroService> microServiceLinkedList = mapOfBroadcasts.get(b.getClass());
            MicroService microService;
            int counter = 0;
            while (counter < microServiceLinkedList.size()) {
                microService = microServiceLinkedList.get(counter);
                mapOfMS.get(microService).add(b);
                counter++;
            }
        }
    }


    @Override
    public <T> Future<T> sendEvent(Event<T> e) {

        if (!e.getClass().equals(ResolveAllFutures.class)) {
            Future<T> f = new Future<T>();
            Object lock = new Object();
            MicroService m;

            if (mapOfEvents.get(e.getClass()) != null) {
                try {
                    synchronized (lock) {
                        m = mapOfEvents.get(e.getClass()).getFirst();
                        mapOfEvents.get(e.getClass()).addLast(m);
                    }
                    mapOfMS.get(m).add(e);
                    mapOfFutures.put(e, f);
                    return f;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                return null;
            }
        }
        else{
            resolveAllFutures();
            System.exit(12);
        }
        return null;
    }

    private void resolveAllFutures() {
        for (Future f : mapOfFutures.values()){
            f.resolve(null);
            System.out.println("f resolved");
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

        //delelte m's queue and all the messages from it
        LinkedList<Message> queue = new LinkedList<>();
//        mapOfMS.get(m).drainTo(queue);
//        Iterator<Message> iterator = queue.listIterator();
//        while(iterator.hasNext()){
//            mapOfFutures.get(iterator.next()).resolve(null);
//        }

        mapOfMS.remove(m);

        //delete m's event subscription
        for (LinkedList<MicroService> b : mapOfEvents.values()) {
            b.remove(m);
        }

        //delete m's broadcast subscription
        for (LinkedList<MicroService> l : mapOfBroadcasts.values()) {
            l.remove(m);
        }

    }

    @Override
    public Message awaitMessage(MicroService m) throws InterruptedException {

        return mapOfMS.get(m).take();
    }


    private void subscribeMessage(Class<? extends Message> type, MicroService m, ConcurrentHashMap<Object, LinkedList<MicroService>> map) {
        //if there is no Q for event E, we will create one
        if (map.get(type) == null) {
            LinkedList q = new LinkedList();
            try {
                map.put(type, q);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            try {
                map.get(type).add(m);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}