/*
 *  StandardSoundProvider.java
 *  Adventure Game Interpreter Sound Package
 *
 *  Created by Dr. Z.
 *  Copyright (c) 2001 Dr. Z. All rights reserved.
 */

package com.sierra.agi.sound;

import com.sierra.agi.io.IOUtils;
import com.sierra.agi.sound.note.Note;
import com.sierra.agi.sound.note.NoteSound;

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

/**
 * This class is the base class of any AGI Standard
 * Sound Resource. It contains code to detect the presence
 * of compatible sound devices. The most important
 * method is the "loadSound" method. It can be used
 * to detect the kind of Sound Clip and load the
 * appropriate Interpretor and Player for that specific
 * kind.
 *
 * @author Dr. Z
 * @version 0.00.00.01
 */
public class StandardSoundProvider implements SoundProvider {
    /**
     * Sample Audio Clip. (Wave?)
     */
    protected static final byte TYPE_SAMPLE = (byte) 1;

    /**
     * MIDI Audio Clip.
     */
    protected static final byte TYPE_MIDI = (byte) 2;

    /**
     * AGI 4-Channel Audio Clip.
     */
    protected static final byte TYPE_4CHANNEL = (byte) 8;

    public StandardSoundProvider() {
    }

    public Sound loadSound(InputStream in) throws IOException {
        int type = in.read();

        try {
            if (type == TYPE_4CHANNEL) {
                IOUtils.skip(in, 7);
                return loadNote(in);
            }
        } finally {
            in.close();
        }

        return null;
    }

    protected Sound loadNote(InputStream in) throws IOException {
        Note note;
        Vector notes = null, channels;
        int durLo, durHi, freq0, freq1, vol;

        channels = new Vector();

        try {
            while (true) {
                durLo = in.read();
                durHi = in.read();

                if ((durLo == -1) || (durHi == -1)) {
                    break;
                }

                if ((durLo == 255) && (durHi == 255)) {
                    notes = null;
                    continue;
                }

                freq0 = in.read();
                freq1 = in.read();
                vol = in.read();

                note = new Note();
                note.dur = (durHi << 8) | durLo;
                note.freq = ((freq0 & 0x3f) << 4) | (freq1 & 0x0f);
                note.vol = (short) (vol & 0xf);

                if (notes == null) {
                    notes = new Vector();
                    channels.add(notes);
                }

                notes.add(note);
            }
        } catch (IOException ex) {
        }

        return new NoteSound(channels);
    }
}
