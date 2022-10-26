/*
 *  NoteClip.java
 *  Adventure Game Interpreter Sound Package
 *
 *  Created by Dr. Z.
 *  Copyright (c) 2001 Dr. Z. All rights reserved.
 */

package com.sierra.agi.sound.note;

import com.sierra.agi.io.LittleEndianOutputStream;
import com.sierra.agi.sound.SoundClip;
import com.sierra.agi.sound.SoundListener;

import javax.sound.sampled.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class NoteClip extends NoteClipDummy implements SoundClip, Runnable {
    /**
     * Default Gain
     */
    protected float defaultGain;

    /**
     * Clip
     */
    protected SourceDataLine clip;

    public NoteClip(NoteMixer mixer) {
        super(mixer);
    }

    public static boolean test() {
        try {
            AudioFormat format = new AudioFormat(22050, 16, 1, true, false);
            SourceDataLine clip;

            clip = (SourceDataLine) AudioSystem.getLine(new DataLine.Info(SourceDataLine.class, format));
            clip.close();
            return true;
        } catch (Throwable thr) {
            thr.printStackTrace();
            return false;
        }
    }

    public void save(OutputStream out) throws IOException {
        ByteArrayOutputStream outw = new ByteArrayOutputStream();

        byte[] data = new byte[820];
        int size = 0;

        while (true) {
            if (!mixer.nextMix(data)) {
                break;
            }

            outw.write(data);
            size += data.length;
        }

        ByteArrayOutputStream outb = new ByteArrayOutputStream();
        LittleEndianOutputStream outs = new LittleEndianOutputStream(outb);

        outs.write(new byte[]{'R', 'I', 'F', 'F'}); // ChuckID
        outs.writeInt(36 + outw.size());              // ChuckSize
        outs.write(new byte[]{'W', 'A', 'V', 'E'}); // Format
        outs.write(new byte[]{'f', 'm', 't', ' '}); // Subchunk1ID
        outs.writeInt(16);        // Subchunk1Size
        outs.writeShort(1);       // AudioFormat
        outs.writeShort(1);       // NumChannels
        outs.writeInt(22050);     // SampleRate
        outs.writeInt(22050 * 2); // ByteRate
        outs.writeShort(2);       // BlockAlign
        outs.writeShort(16);      // BitsPerSample
        outs.write(new byte[]{'d', 'a', 't', 'a'}); // Subchunk2ID
        outs.writeInt(outw.size());
        outs.flush();

        outb.writeTo(out);
        outw.writeTo(out);
    }

    public void run() {
        byte[] data = new byte[820];
        AudioFormat format = new AudioFormat(22050, 16, 1, true, false);
        byte reason;

        try {
            clip = (SourceDataLine) AudioSystem.getLine(new DataLine.Info(SourceDataLine.class, format));
        } catch (Throwable thr) {
            thr.printStackTrace();
            return;
        }

        playing = true;
        raiseStartEvent();

        if (!mixer.nextMix(data)) {
            raiseStopEvent(SoundListener.STOP_REASON_FINISHED);
            return;
        }

        try {
            clip.open(format);

            defaultGain = ((FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN)).getValue();
            setVolume(volume);

            clip.write(data, 0, 820);
            clip.start();

            while (playing) {
                if (!mixer.nextMix(data)) {
                    break;
                }

                clip.write(data, 0, 820);
            }

            synchronized (this) {
                if (playing) {
                    reason = SoundListener.STOP_REASON_FINISHED;
                    playing = false;
                    //clip.drain();
                    mixer.setPosition(0);
                } else {
                    reason = SoundListener.STOP_REASON_PROGRAMMATICALLY;
                    clip.flush();
                }

                clip.stop();
                clip.close();
                clip = null;
            }
        } catch (Throwable thr) {
            thr.printStackTrace();
            reason = SoundListener.STOP_REASON_EXCEPTION;
            playing = false;
        }

        raiseStopEvent(reason);
    }


    /**
     * Set volume.
     *
     * @param volume New volume (must be between 0 and f)
     */
    public synchronized void setVolume(int volume) {
        if (volume > 0xf) {
            volume = 0xf;
        }

        try {
            if (clip != null) {
                float f, s;
                int i;

                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

                if (volume == 0) {
                    f = gainControl.getMinimum();
                } else {
                    f = defaultGain;
                    s = (defaultGain - gainControl.getMinimum()) / 0x24;

                    for (i = 0; i < (0xf - volume); i++) {
                        f -= s;
                    }
                }

                if (playing && (gainControl.getUpdatePeriod() > 0)) {
                    gainControl.shift(gainControl.getValue(), f, gainControl.getUpdatePeriod() * 4);
                } else {
                    gainControl.setValue(f);
                }
            }

            if (this.volume != volume) {
                this.volume = volume;
                raiseVolumeEvent(volume);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public synchronized void setPosition(long newPosition) {
        super.setPosition((int) newPosition);

        if (clip != null) {
            clip.flush();
        }
    }
}