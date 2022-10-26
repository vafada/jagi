/*
 *  Note.java
 *  Adventure Game Interpreter Sound Package
 *
 *  Created by Dr. Z.
 *  Copyright (c) 2001 Dr. Z. All rights reserved.
 */

package com.sierra.agi.sound.note;

/**
 * This structure is used by NoteMixer to
 * procuce a wave version of the Sierra's 4-channel
 * note sequence.
 *
 * @author Dr. Z
 * @version 0.00.00.01
 * @see com.sierra.agi.sound.NoteMixer
 */
public final class Note {
    /**
     * Ramp waveform style.
     */
    public static final byte TYPE_RAMP = 0;
    /**
     * Square waveform style.
     */
    public static final byte TYPE_SQUARE = 1;
    /**
     * PC waveform style. (Equivalent to TYPE_SQUARE)
     */
    public static final byte TYPE_PC = 1;
    /**
     * Mac waveform style.
     */
    public static final byte TYPE_MAC = 2;
    /**
     * Waveform that emulate the "wanted" result.
     */
    public static final short[] waveformRamp =
            {0, 8, 16, 24, 32, 40, 48, 56,
                    64, 72, 80, 88, 96, 104, 112, 120,
                    128, 136, 144, 152, 160, 168, 176, 184,
                    192, 200, 208, 216, 224, 232, 240, 255,
                    0, -248, -240, -232, -224, -216, -208, -200,
                    -192, -184, -176, -168, -160, -152, -144, -136,
                    -128, -120, -112, -104, -96, -88, -80, -72,
                    -64, -56, -48, -40, -32, -24, -16, -8};
    /**
     * Waveform that emulate the PC internal speaker.
     */
    public static final short[] waveformSquare =
            {255, 230, 220, 220, 220, 220, 220, 220,
                    220, 220, 220, 220, 220, 220, 220, 220,
                    220, 220, 220, 220, 220, 220, 220, 220,
                    220, 220, 220, 220, 220, 220, 220, 110,
                    -255, -230, -220, -220, -220, -220, -220, -220,
                    -220, -220, -220, -220, -220, -220, -220, -220,
                    -220, -220, -220, -220, -220, -220, -220, -220,
                    -220, -220, -220, -110, 0, 0, 0, 0};
    /**
     * Waveform that emulate the old Mac sound system.
     */
    public static final short[] waveformMac =
            {45, 110, 135, 161, 167, 173, 175, 176,
                    156, 137, 123, 110, 91, 72, 35, -2,
                    -60, -118, -142, -165, -170, -176, -177, -179,
                    -177, -176, -164, -152, -117, -82, -17, 47,
                    92, 137, 151, 166, 170, 173, 171, 169,
                    151, 133, 116, 100, 72, 43, -7, -57,
                    -99, -141, -156, -170, -174, -177, -178, -179,
                    -175, -172, -165, -159, -137, -114, -67, -19};
    /**
     * Duration of the note.
     */
    public int dur;
    /**
     * Frequency of the note.
     */
    public int freq;
    /**
     * Volume of the note.
     */
    public short vol;
}