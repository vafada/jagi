/*
 *  NoteMixer.java
 *  Adventure Game Interpreter Sound Package
 *
 *  Created by Dr. Z.
 *  Copyright (c) 2001 Dr. Z. All rights reserved.
 */

package com.sierra.agi.sound.note;

import java.util.Arrays;

/**
 * The AGI Note mixer. Uses a Note Sequencer to
 * produce streaming wave of the AGI 4-channel
 * sound tracks.
 *
 * @author Dr. Z
 * @version 0.00.00.01
 * @see com.sierra.agi.sound.note.NoteSequencer
 */
public class NoteMixer {
    /**
     * Enveloppe Flags. (for the <CODE>flags</CODE> field)
     */
    public final static short FLAG_ENVELOPPE = 1;
    /**
     * Interpolation Flags. (for the <CODE>flags</CODE> field)
     */
    public final static short FLAG_INTERPOLATION = 2;
    /**
     * Decay values.
     */
    protected final static short ENV_DECAY = 800;
    /**
     * Sustain values.
     */
    protected final static short ENV_SUSTAIN = 160;
    /**
     * The waveform buffer.
     */
    protected short[] buffer;
    /**
     * The flags.
     */
    protected short flags;
    /**
     * The sequence position.
     */
    protected int position = 0;
    /**
     * The AGI Note sequencer.
     */
    protected NoteSequencer[] seq;
    /**
     * The Waveform to use.
     */
    protected short[] waveform;

    /**
     * Creates a new NoteMixer.
     *
     * @param seq Note Sequencers to use.
     */
    public NoteMixer(NoteSequencer[] seq, byte waveid) {
        this.seq = seq;
        this.flags = FLAG_INTERPOLATION | FLAG_ENVELOPPE;

        setWaveform(waveid);
    }

    /**
     * Mixs the AGI Note from the sequencer. Results
     * are stored in the <CODE>buffer</CODE> field.
     */
    protected void mix() {
        NoteSequencer channel;
        int i, j, phase, vol, b, channelCount, n = 0;

        if (buffer == null) {
            buffer = new short[410];
        } else {
            Arrays.fill(buffer, (short) 0);
        }

        channelCount = seq.length;
        for (i = 0; i < channelCount; i++) {
            channel = seq[i];

            if (channel.cycle()) {
                n++;

                if (channel.vol != 0) {
                    phase = channel.phase;
                    vol = ((flags & FLAG_ENVELOPPE) == FLAG_ENVELOPPE) ? channel.vol * channel.env >> 16 : channel.vol;

                    for (j = 0; j < 410; j++) {
                        b = waveform[phase >> 8];

                        if ((flags & FLAG_INTERPOLATION) == FLAG_INTERPOLATION) {
                            b += ((waveform[((phase >> 8) + 1) % waveform.length] - waveform[phase >> 8]) * (phase & 0xff)) >> 8;
                        }

                        buffer[j] += (b * vol) >> 8;
                        phase += 11860 * 4 / channel.freq;

                        if ((channel.flags & NoteSequencer.FLAG_LOOP) == NoteSequencer.FLAG_LOOP) {
                            phase %= waveform.length << 8;
                        } else {
                            if (phase >= waveform.length << 8) {
                                phase = 0;
                                channel.vol = 0;
                                channel.end = true;
                                break;
                            }
                        }
                    }

                    if (channel.env > channel.vol * ENV_SUSTAIN) {
                        channel.env -= ENV_DECAY;
                    }

                    channel.phase = phase;
                }
            }
        }

        position++;

        if (n == 0) {
            buffer = null;
            return;
        }

        for (i = 0; i < buffer.length; i++) {
            buffer[i] <<= 5;
        }
    }

    /**
     * Generate the next sample.
     *
     * @param b The buffer to store the result.
     * @return Returns <CODE>true</CODE> if result has been stored in the
     * <CODE>b</CODE> parameter.
     */
    public synchronized boolean nextMix(byte[] b) {
        int i, j;
        short bu;

        mix();

        if (buffer == null) {
            return false;
        }

        for (i = 0, j = 0; i < 410; i++) {
            bu = buffer[i];
            b[j++] = (byte) ((bu & 0xFF));
            b[j++] = (byte) ((bu & 0xFF00) >> 8);
        }

        return true;
    }

    /**
     * Skip the Next Mix.
     */
    protected synchronized boolean nextMix() {
        int i, channelCount, n = 0;

        channelCount = seq.length;
        for (i = 0; i < channelCount; i++) {
            if (seq[i].cycle()) {
                n++;
            }
        }

        position++;
        return (n != 0);
    }

    /**
     * Retreive the current position.
     *
     * @return Returns the current position.
     */
    public synchronized int getPosition() {
        return position;
    }

    /**
     * Change the current position.
     *
     * @param newPos The new position.
     */
    public synchronized void setPosition(int newPos) {
        int i, j;
        NoteSequencer s;

        for (i = 0; i < seq.length; i++) {
            s = seq[i];
            s.reset();

            for (j = 0; j < newPos; j++) {
                s.cycle();
            }
        }

        position = newPos;
    }

    /**
     * Reset the Mixer.
     */
    public synchronized void reset() {
        int i;

        for (i = 0; i < seq.length; i++) {
            seq[i].reset();
        }

        position = 0;
    }

    /**
     * Obtain the length of the audio clip in sample.
     *
     * @return The length audio clip.
     */
    public int getDuration() {
        int i, n;
        int max = 0;

        for (i = 0; i < seq.length; i++) {
            n = seq[i].getDuration();

            if (n > max) {
                max = n;
            }
        }

        return max;
    }

    /**
     * Retreive the array of <CODE>NoteSequencer</CODE> used
     * to mix the audio clip data.
     *
     * @return Returns the <CODE>NoteSequencer</CODE> used.
     * @see com.sierra.agi.sound.note.NoteSequencer
     */
    public NoteSequencer[] getSequencers() {
        return seq;
    }

    public void setWaveform(byte waveid) {
        switch (waveid) {
            case Note.TYPE_RAMP:
            default:
                waveform = Note.waveformRamp;
                break;
            case Note.TYPE_SQUARE:
                waveform = Note.waveformSquare;
                break;
            case Note.TYPE_MAC:
                waveform = Note.waveformMac;
                break;
        }
    }
}