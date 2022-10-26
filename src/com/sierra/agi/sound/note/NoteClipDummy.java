/*
 *  NoteClipDummy.java
 *  Adventure Game Interpreter Sound Package
 *
 *  Created by Dr. Z.
 *  Copyright (c) 2001 Dr. Z. All rights reserved.
 */

package com.sierra.agi.sound.note;

import com.sierra.agi.sound.SoundClip;
import com.sierra.agi.sound.SoundListener;

import java.util.Enumeration;
import java.util.Vector;

public class NoteClipDummy implements SoundClip, Runnable {
    /**
     * Sequencers
     */
    protected NoteMixer mixer;

    /**
     * Note Thread
     */
    protected Thread th;

    /**
     * Volume
     */
    protected int volume = 0xf;

    /**
     * Listeners
     */
    protected Vector listeners = new Vector();

    /**
     * Playing
     */
    protected volatile boolean playing;

    public NoteClipDummy(NoteMixer mixer) {
        this.mixer = mixer;
    }

    /**
     * Play sound in an isolated thread.
     */
    public synchronized void play() {
        if (playing) {
            return;
        }

        th = new Thread(this, "AGI Note Player");
        th.start();
    }

    /**
     * Play sound in the current thread.
     */
    public void playSync() {
        run();
    }

    /**
     * Stop sound.
     *
     * @return Returns <code>true</code> if the sound has
     * been stopped. Returns <code>false</code> if
     * the sound is already stopped.
     */
    public synchronized boolean stop() {
        if (!playing) {
            return false;
        }

        playing = false;
        return true;
    }

    public synchronized boolean isPlaying() {
        return playing;
    }

    public void setVolume(int volume) {
        if (this.volume != volume) {
            this.volume = volume;
            raiseVolumeEvent(volume);
        }
    }

    /**
     * Add a sound listener.
     *
     * @param listener Listener to add.
     */
    public synchronized void addSoundListener(SoundListener listener) {
        listeners.add(listener);

        if (playing) {
            listener.soundStarted(this);
        }
    }

    /**
     * Remove a sound listener.
     *
     * @param listener Listener to remove.
     */
    public synchronized void removeSoundListener(SoundListener listener) {
        listeners.remove(listener);
    }

    protected synchronized void raiseStartEvent() {
        Enumeration e = listeners.elements();
        SoundListener l;

        while (e.hasMoreElements()) {
            l = (SoundListener) e.nextElement();
            l.soundStarted(this);
        }
    }

    protected synchronized void raiseStopEvent(byte reason) {
        Enumeration e = listeners.elements();
        SoundListener l;

        while (e.hasMoreElements()) {
            l = (SoundListener) e.nextElement();
            l.soundStopped(this, reason);
        }
    }

    protected synchronized void raiseVolumeEvent(int volume) {
        Enumeration e = listeners.elements();
        SoundListener l;

        while (e.hasMoreElements()) {
            l = (SoundListener) e.nextElement();
            l.soundVolumeChanged(this, volume);
        }
    }

    public long getPosition() {
        return mixer.getPosition();
    }

    public synchronized void setPosition(long newPosition) {
        mixer.setPosition((int) newPosition);
    }

    public long getMaxPosition() {
        return mixer.getDuration();
    }

    public void run() {
        byte r;

        playing = true;
        raiseStartEvent();

        if (!mixer.nextMix()) {
            raiseStopEvent(SoundListener.STOP_REASON_FINISHED);
            return;
        }

        try {
            while (playing) {
                Thread.sleep(22);

                if (!mixer.nextMix()) {
                    break;
                }
            }

            Thread.sleep(22);

            synchronized (this) {
                if (playing) {
                    r = SoundListener.STOP_REASON_FINISHED;
                    playing = false;
                    mixer.setPosition(0);
                } else {
                    r = SoundListener.STOP_REASON_PROGRAMMATICALLY;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            r = SoundListener.STOP_REASON_EXCEPTION;
            playing = false;
        }

        raiseStopEvent(r);
    }
}
