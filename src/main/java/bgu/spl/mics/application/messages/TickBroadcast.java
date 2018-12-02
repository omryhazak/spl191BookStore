package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.Callback;

public class TickBroadcast implements Broadcast {
    private int currentTick;

    public TickBroadcast(int currentTick) {
        this.currentTick = currentTick;
    }
}
