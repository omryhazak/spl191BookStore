package bgu.spl.mics;
import bgu.spl.mics.application.messages.ResolveAllFutures;

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
            subscribeMessage(type, m, mapOfEvents);
    }

    @Override
    public  void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
            subscribeMessage(type, m, mapOfBroadcasts);
    }


    @Override
    public <T> void complete(Event<T> e, T result) {

        mapOfFutures.get(e).resolve(result);
        mapOfFutures.remove(e);

    }

    @Override
    public void sendBroadcast(Broadcast b) {
        synchronized (lock2) {
            LinkedList<MicroService> microServiceLinkedList = mapOfBroadcasts.get(b.getClass());
                for (MicroService m : microServiceLinkedList) {
                    mapOfMS.get(m).add(b);
                }
            }
//            int counter = 0;
//            while (counter < microServiceLinkedList.size()) {
//                microService = microServiceLinkedList.get(counter);
//                mapOfMS.get(microService).add(b);
//                counter++;
//            }
    }


    @Override
    public <T> Future<T> sendEvent(Event<T> e) {

        Future<T> f = new Future<T>();
        mapOfFutures.put(e, f);

        if (!e.getClass().equals(ResolveAllFutures.class)) {
            MicroService m;
            System.out.println(e.getClass());
            synchronized (lock2) {
                if (mapOfEvents != null && !mapOfEvents.isEmpty() && mapOfEvents.get(e.getClass()) != null && mapOfEvents.get(e.getClass()).size() > 0) {
                    m = mapOfEvents.get(e.getClass()).pollFirst();
                    mapOfEvents.get(e.getClass()).addLast(m);
                    if (m != null) {
                        mapOfMS.get(m).add(e);
                    } else {
                        f.resolve(null);
                        mapOfFutures.remove(f);
                    }
                    return f;
                } else {
                    f.resolve(null);
                    mapOfFutures.remove(f);
                }
            }
        }
        else{
            f.resolve(null);
            mapOfFutures.remove(f);
            resolveAllFutures();
        }
        return f;
    }

    private void resolveAllFutures() {
        for (Future f : mapOfFutures.values()){
            f.resolve(null);
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
        //TODO when the last ms get out, close the door
        //delelte m's queue and all the messages from it

        //delete m's event subscription
        //if the event's list is empty than remove it
        for (Object o : mapOfEvents.keySet()) {

            if(mapOfEvents.get(o) != null) {
                if (mapOfEvents.get(o).contains(m)) {
                    mapOfEvents.get(o).remove(m);
                    if (mapOfEvents.get(o).isEmpty()) {
                        mapOfEvents.remove(o);
                    }
                }
            }

        }

        //delete m's broadcast subscription
        //if the broadcast's list is empty than remove it
        synchronized (lock2) {
        for (Object o : mapOfBroadcasts.keySet()) {
            mapOfBroadcasts.get(o).remove(m);
            if(mapOfBroadcasts.get(o).isEmpty()) mapOfBroadcasts.remove(o);
        }
            mapOfMS.remove(m);
        }

    }

    @Override
    public Message awaitMessage(MicroService m) throws InterruptedException {
        return mapOfMS.get(m).take();
    }


    private void subscribeMessage(Class<? extends Message> type, MicroService m, ConcurrentHashMap<Object, LinkedList<MicroService>> map) {
        //if there is no Q for event E, we will create one
        synchronized (lock1) {
            if (map.get(type) == null) {
                LinkedList q = new LinkedList();
                try {
                    map.put(type, q);
                    map.get(type).add(m);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    map.get(type).add(m);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


}