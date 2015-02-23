/*
 * busylight
 *
 * Tone
 * net.gerritk.busylight.Tone
 *
 * Copyright (c) 2015 - K.Design
 * Licensed under Apache 2.0
 */

package net.gerritk.busylight;

/**
 * Tones which can be played by the {@link BusyLight} device.
 */
public enum Tone {
    NONE(128),
    OPEN_OFFICE(136),
    QUIET(144),
    FUNKY(152),
    FAIRY_TALE(160),
    KUANDO_TRAIN(168),
    TELEPHONE_NORDIC(176),
    TELEPHONE_ORIGINAL(184),
    TELEPHONE_PICK_ME_UP(192),
    BUZZ(216);

    private int value;

    Tone(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
