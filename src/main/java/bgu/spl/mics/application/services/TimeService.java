package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.ResolveAllFutures;
import bgu.spl.mics.application.messages.TickBroadcast;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link //Tick Broadcast}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link //ResourcesHolder}, {@link //MoneyRegister}, {@link //Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService {

	//fields
	private int currentTime;
	private long speed;
	private long duration;
	private Timer timer;
	private TimerTask timerTask;



	public TimeService(long speed, long duration) {
		super("TimeService");
		this.currentTime = 1;
		this.speed = speed;
		this.duration = duration;
		this.timer = new Timer();
		timerTask = new TimerTask()
		//start anonymous class
		{
			@Override
			public void run() {

				sendBroadcast(new TickBroadcast(currentTime));
				System.out.println(currentTime);
				currentTime++;
				if(currentTime == duration+1) {

					timerTask.cancel();
					timer.cancel();
					sendEvent(new ResolveAllFutures());
					terminate();
				}
			}
		};
	}

	@Override
	protected void initialize() {

		timer.scheduleAtFixedRate(timerTask, 30, 30);
		try {
			Thread.sleep(30*25);
		} catch (InterruptedException e) {
			System.out.println("TIME SERVICE WAS INTERRUPTED WHILE SLEEPING");;
		}
		System.out.println("END OF INITIAL");
	}

	public void setTimeService(){
		currentTime = 1;
		timer = new Timer();
		timerTask = new TimerTask()
				//start annonymous class
		{
			@Override
			public void run() {
				sendBroadcast(new TickBroadcast(currentTime));
				System.out.println(currentTime);
				currentTime++;
				if(currentTime == duration+1) {

					timerTask.cancel();
					timer.cancel();
					sendEvent(new ResolveAllFutures());
					terminate();
				}
			}
		};
	}

	public long getDuration() {
		return duration;
	}

	public long getSpeed() {
		return speed;
	}

	private void terminateTimeService(){
		this.terminate();
	}
}


