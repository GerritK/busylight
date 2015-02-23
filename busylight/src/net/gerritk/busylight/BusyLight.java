/*
 * busylight
 *
 * BusyLight2
 * net.gerritk.busylight.BusyLight2
 *
 * Copyright (c) 2015 - K.Design
 * Licensed under Apache 2.0
 */

package net.gerritk.busylight;

import com.codeminders.hidapi.ClassPathLibraryLoader;
import com.codeminders.hidapi.HIDDevice;
import com.codeminders.hidapi.HIDDeviceInfo;
import com.codeminders.hidapi.HIDManager;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Instance of this class represents a Kuando BusyLight device.
 *
 * @author gerritkpunkt
 */
public class BusyLight {
    private static final int VENDOR_ID = 0x4D8;
    private static final int PRODUCT_ID = 0xF848;

    private static final int RED = 3;
    private static final int GREEN = 4;
    private static final int BLUE = 5;
    private static final int TONE = 8;

    private static boolean initialized;

    private final HIDDevice device;
    private final byte[] buffer;
    private boolean turnedOff;

    /**
     * Constructor to create a new Kuando BusyLight device.
     *
     * @param device the hid device which represents the BusyLight
     */
    protected BusyLight(HIDDevice device) {
        if (device == null) {
            throw new IllegalArgumentException("device must not be null!");
        }

        this.device = device;
        this.buffer = new byte[9];
    }

    /**
     * Sets the color of the BusyLight.
     *
     * @param color the color of the light
     */
    public void setColor(Color color) {
        buffer[RED] = (byte) color.getRed();
        buffer[GREEN] = (byte) color.getGreen();
        buffer[BLUE] = (byte) color.getBlue();
        send();
    }

    /**
     * Sets the color of the BusyLight.
     *
     * @param r the red part of the color; must be between 0..255
     * @param g the green part of the color; must be between 0..255
     * @param b the blue part of the color; must be between 0..255
     */
    public void setColor(int r, int g, int b) {
        setColor(new Color(r, g, b));
    }

    /**
     * Sets the ringtone of the BusyLight.
     *
     * @param tone   the tone to play
     * @param volume the volume to play with
     */
    public void ring(Tone tone, int volume) {
        if (tone == null) {
            throw new IllegalArgumentException("tone must not be null!");
        }
        if (volume > 7 || volume < 0) {
            throw new IllegalArgumentException("volume must be between 0..7!");
        }

        if(buffer[TONE] / 8 != Tone.NONE.getValue()) {
            buffer[TONE] = (byte) Tone.NONE.getValue();
            send();
        }

        buffer[TONE] = (byte) (tone.getValue() + volume);
        send();
    }

    /**
     * Turns the device off.
     * Changes the color to {@link java.awt.Color#BLACK}, turns the tone to {@link net.gerritk.busylight.Tone#NONE} and closes the {@link com.codeminders.hidapi.HIDDevice}.
     */
    public void turnOff() {
        if (turnedOff) {
            return;
        }

        try {
            setColor(Color.BLACK);
            ring(Tone.NONE, 0);
            device.close();
        } catch (IOException ignored) {
        } finally {
            turnedOff = true;
        }
    }

    /**
     * Returns the manufacturer string of the BusyLight given by the {@link com.codeminders.hidapi.HIDDevice}.
     *
     * @return the manufacturer string; null if {@link BusyLight#turnOff()} already called or an error occurred
     */
    public String getManufacturer() {
        String result = null;
        if (!turnedOff) {
            try {
                result = device.getManufacturerString();
            } catch (IOException ignored) {
            }
        }
        return result;
    }

    /**
     * Returns the product string of the BusyLight given by the {@link com.codeminders.hidapi.HIDDevice}.
     *
     * @return the product string; null if {@link BusyLight#turnOff()} already called or an error occurred
     */
    public String getProduct() {
        String result = null;
        if (!turnedOff) {
            try {
                result = device.getProductString();
            } catch (IOException ignored) {
            }
        }
        return result;
    }

    /**
     * Returns the unique serial of the BusyLight given by the {@link com.codeminders.hidapi.HIDDevice}.
     *
     * @return the unique serial; null if {@link BusyLight#turnOff()} already called or an error occurred
     */
    public String getSerial() {
        String result = null;
        if (!turnedOff) {
            try {
                result = device.getSerialNumberString();
            } catch (IOException ignored) {
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return getProduct() + "@" + getSerial() + " by " + getManufacturer();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BusyLight) {
            final BusyLight busyLight = (BusyLight) obj;
            return device.equals(busyLight.device);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return device.hashCode();
    }

    @Override
    protected void finalize() throws Throwable {
        turnOff();
        super.finalize();
    }

    /**
     * Sends the byte buffer to the device to change color and/or tone.
     *
     * @return <code>true</code> if communication with the device succeeded; <code>false</code> otherwise
     */
    private boolean send() {
        if (turnedOff) {
            return false;
        }

        try {
            device.write(buffer);
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    /*
     * Static Methods
     */

    /**
     * Returns the first found {@link BusyLight}.
     *
     * @return the first found {@link BusyLight}; <code>null</code> if no {@link BusyLight} found or an error occurred
     */
    public static BusyLight findFirst() {
        HIDDeviceInfo[] infos = findDeviceInfos();
        BusyLight result = null;
        if (infos.length > 0) {
            try {
                result = new BusyLight(infos[0].open());
            } catch (IOException ignored) {
            }
        }
        return result;
    }

    /**
     * Returns the {@link BusyLight} with the given unique serial.
     *
     * @param serial the serial to look for
     * @return the {@link BusyLight} with the given unique serial; <code>null</code> if no {@link BusyLight} found or an error occurred
     */
    public static BusyLight findBySerial(String serial) {
        HIDDeviceInfo[] infos = findDeviceInfos();
        BusyLight result = null;
        for (HIDDeviceInfo info : infos) {
            if (info.getSerial_number().equals(serial)) {
                try {
                    result = new BusyLight(info.open());
                    break;
                } catch (IOException ignored) {
                }
            }
        }
        return result;
    }

    /**
     * Returns all connected {@link BusyLight}s.
     *
     * @return all connected {@link BusyLight}s; empty array if no {@link BusyLight} found or an error occurred
     */
    public static BusyLight[] findAll() {
        HIDDeviceInfo[] infos = findDeviceInfos();
        ArrayList<BusyLight> result = new ArrayList<BusyLight>();
        for (HIDDeviceInfo info : infos) {
            try {
                BusyLight busyLight = new BusyLight(info.open());
                result.add(busyLight);
            } catch (IOException ignored) {
            }
        }
        return result.toArray(new BusyLight[result.size()]);
    }

    /**
     * Returns an array of {@link com.codeminders.hidapi.HIDDeviceInfo} which represents {@link BusyLight}s.
     *
     * @return an array of {@link com.codeminders.hidapi.HIDDeviceInfo} which represents {@link BusyLight}s
     */
    private static HIDDeviceInfo[] findDeviceInfos() {
        if (!initialized) {
            initialized = true;
            ClassPathLibraryLoader.loadNativeHIDLibrary();
        }

        ArrayList<HIDDeviceInfo> deviceList = new ArrayList<HIDDeviceInfo>();

        try {
            final HIDManager hidManager = HIDManager.getInstance();
            HIDDeviceInfo[] infos = hidManager.listDevices();

            for (HIDDeviceInfo info : infos) {
                if (info.getVendor_id() == VENDOR_ID && info.getProduct_id() == PRODUCT_ID) {
                    deviceList.add(info);
                }
            }
        } catch (IOException ignored) {
        }

        return deviceList.toArray(new HIDDeviceInfo[deviceList.size()]);
    }
}
