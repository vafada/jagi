/*
 *  SoundListener.java
 *  Adventure Game Interpreter Sound Package
 *
 *  Created by Dr. Z.
 *  Copyright (c) 2001 Dr. Z. All rights reserved.
 */

package com.sierra.agi.sound;

/**
 * Interface used by <CODE>Sound</CODE> derivated classes
 * to be notified of specific events.
 *
 * @author Dr. Z
 * @version 0.00.00.01
 * @see com.sierra.agi.sound.Sound
 */
public interface SoundListener extends java.util.EventListener {
    /**
     * The sound has finished
     */
    byte STOP_REASON_FINISHED = (byte) 0;

    /**
     * The sound has been stopped programmatically
     */
    byte STOP_REASON_PROGRAMMATICALLY = (byte) 1;

    /**
     * The sound has been stopped by an exception
     */
    byte STOP_REASON_EXCEPTION = (byte) 2;

    /**
     * Called when the <CODE>Sound</CODE> starts playing.
     */
    void soundStarted(SoundClip sound);

    /**
     * Called when the <CODE>Sound</CODE> has stopped.
     */
    void soundStopped(SoundClip sound, byte reason);

    /**
     * Called when the <CODE>Sound</CODE> volume has been modified.
     */
    void soundVolumeChanged(SoundClip sound, int volume);
}