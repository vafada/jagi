/*
 *  NoteSound.java
 *  Adventure Game Interpreter Sound Package
 *
 *  Created by Dr. Z.
 *  Copyright (c) 2001 Dr. Z. All rights reserved.
 */

package com.sierra.agi.sound.note;

import com.sierra.agi.sound.Sound;
import com.sierra.agi.sound.SoundClip;

import java.util.Vector;

public class NoteSound implements Sound {
    /**
     * Probed Status.
     */
    protected static boolean probed = false;
    /**
     * Audio Support Status.
     */
    protected static boolean audioSupport;
    protected Vector channels;

    public NoteSound(Vector channels) {
        this.channels = channels;

        if (!probed) {
            probe();
        }
    }

    public static void disableSound() {
        probed = true;
        audioSupport = false;
    }

    protected static void probe() {
        try {
            Class c;

            if (System.getProperty("com.sierra.agi.sound.disabled", null) != null) {
                audioSupport = false;
                return;
            }

            c = Class.forName("javax.sound.sampled.AudioFormat");
            c = Class.forName("javax.sound.sampled.AudioSystem");
            c = Class.forName("javax.sound.sampled.DataLine");

            audioSupport = NoteClip.test();
        } catch (ClassNotFoundException e) {
            /* Java 1.2 or Java 1.3 without Audio Support. */
            audioSupport = false;
        } finally {
            probed = true;
        }
    }

    public SoundClip createClip() {
        NoteSequencer[] seqs;
        int i;

        seqs = new NoteSequencer[channels.size()];

        for (i = 0; i < seqs.length; i++) {
            seqs[i] = new NoteSequencer((Vector) channels.get(i));
        }

        if (audioSupport) {
            return new NoteClip(new NoteMixer(seqs, Note.TYPE_RAMP));
        } else {
            return new NoteClipDummy(new NoteMixer(seqs, Note.TYPE_RAMP));
        }
    }
}
