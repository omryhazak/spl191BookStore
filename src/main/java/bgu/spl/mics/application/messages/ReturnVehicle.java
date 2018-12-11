package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

public class ReturnVehicle implements Event{
    private DeliveryVehicle deliveryVehicle;

    public ReturnVehicle(DeliveryVehicle deliveryVehicle) {
        this.deliveryVehicle = deliveryVehicle;
    }

    public DeliveryVehicle getDeliveryVehicle() {
        return deliveryVehicle;
    }
}