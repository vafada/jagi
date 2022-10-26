/*
 *  SoundClip.java
 *  Adventure Game Interpreter Sound Package
 *
 *  Created by Dr. Z.
 *  Copyright (c) 2001 Dr. Z. All rights reserved.
 */

package com.sierra.agi.sound;

public interface SoundClip {
    /**
     * Play sound in an isolated thread.
     */
    void play();

    /**
     * Play sound in the current thread.
     */
    void playSync();

    /**
     * Stop sound.
     *
     * @return Returns <code>true</code> if the sound has
     * been stopped. Returns <code>false</code> if
     * the sound is already stopped.
     */
    boolean stop();

    /**
     * Set volume.
     *
     * @param volume New volume (must be between 0 and f)
     */
    void setVolume(int volume);

    /**
     * Get position
     *
     * @returns Returns the current position.
     */
    long getPosition();

    void setPosition(long newPosition);

    long getMaxPosition();

    boolean isPlaying();

    /**
     * Add a sound listener.
     *
     * @param listener Listener to add.
     */
    void addSoundListener(SoundListener listener);

    /**
     * Remove a sound listener.
     *
     * @param listener Listener to remove.
     */
    void removeSoundListener(SoundListener listener);
}
