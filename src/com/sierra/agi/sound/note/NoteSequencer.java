/*
 *  NoteSequencer.java
 *  Adventure Game Interpreter Sound Package
 *
 *  Created by Dr. Z.
 *  Copyright (c) 2001 Dr. Z. All rights reserved.
 */

package com.sierra.agi.sound.note;

import java.util.Enumeration;
import java.util.Vector;

/**
 * AGI Channel Sequencer. Contains variables
 * specific to a note channel.
 *
 * @author Dr. Z
 * @version 0.00.00.01
 */
public class NoteSequencer {
    /**
     * Loop Flag
     */
    public final static short FLAG_LOOP = 1;
    /**
     * Channel position
     */
    public int pos;
    /**
     * Channel note
     */
    public Vector notes;
    /**
     * Note Remaining Duration
     */
    public int dur;
    /**
     * End Flag
     */
    public boolean end;
    /**
     * Env persistant variable
     */
    public int env;
    /**
     * Flags
     */
    public short flags;
    /**
     * Note Frequency
     */
    public int freq;
    /**
     * Note Phase Status
     */
    public int phase;
    /**
     * Note Volume
     */
    public int vol;
    /**
     * Full Duration (All note combined)
     */
    protected int fulldir = -1;

    /**
     * Creates a new NoteSequencer.
     *
     * @param notes   Notes to play.
     * @param channel Channel ID
     */
    public NoteSequencer(Vector notes) {
        this.notes = notes;

        dur = 0;
        end = false;
        freq = 0;
        vol = 0;
        flags = FLAG_LOOP;
    }

    /**
     * Do a note cycle. Decrements the duration. When
     * the duration is zero, it loads another note.
     */
    public boolean cycle() {
        if (end) {
            return false;
        }

        dur--;

        if (dur <= 0) {
            try {
                Note note = (Note) notes.get(pos++);

                dur = note.dur;
                freq = note.freq;

                if (freq != 0) {
                    vol = (note.vol == 0xf ? 0 : 0xff - (note.vol << 1));
                } else {
                    vol = 0;
                    freq = 1;
                }

                env = 0x10000;
                phase = 0;
            } catch (ArrayIndexOutOfBoundsException e) {
                end = true;
                vol = 0;
                return false;
            }
        }

        return true;
    }

    public void reset() {
        end = false;
        dur = 0;
        pos = 0;
    }

    public int getDuration() {
        if (fulldir == -1) {
            Enumeration e = notes.elements();
            Note n;

            fulldir = 0;

            while (e.hasMoreElements()) {
                n = (Note) e.nextElement();
                fulldir += n.dur;
            }

        }

        return fulldir;
    }
}