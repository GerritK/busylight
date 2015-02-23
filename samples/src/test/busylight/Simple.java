/*
 * busylight
 *
 * Simple
 * test.busylight.Simple
 *
 * Copyright (c) 2015 - K.Design
 * Licensed under Apache 2.0
 */

package test.busylight;

import net.gerritk.busylight.BusyLight;
import net.gerritk.busylight.Tone;

import java.awt.*;

public class Simple {
    public static void main(String[] args) throws InterruptedException {
        BusyLight busyLight = BusyLight.findFirst();
        busyLight.setColor(Color.RED);
        Thread.sleep(500);
        busyLight.setColor(Color.GREEN);
        Thread.sleep(500);
        busyLight.setColor(Color.BLUE);
        Thread.sleep(500);
        busyLight.ring(Tone.FAIRY_TALE, 1);
        Thread.sleep(1500);
        busyLight.ring(Tone.OPEN_OFFICE, 1);
        Thread.sleep(1500);
        busyLight.turnOff();
    }
}
