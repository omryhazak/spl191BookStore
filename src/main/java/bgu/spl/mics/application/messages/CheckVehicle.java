package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

public class CheckVehicle implements Event<Future<DeliveryVehicle>> {

    public CheckVehicle() {
    }
}