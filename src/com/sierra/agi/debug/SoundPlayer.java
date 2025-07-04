/**
 * SoundPlayer.java
 * Adventure Game Interpreter Debug Package
 * <p>
 * Created by Dr. Z
 * Copyright (c) 2001 Dr. Z. All rights reserved.
 */

package com.sierra.agi.debug;

import com.sierra.agi.res.ResourceCache;
import com.sierra.agi.res.ResourceException;
import com.sierra.agi.sound.SoundClip;
import com.sierra.agi.sound.SoundListener;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class SoundPlayer extends JFrame implements ActionListener, ChangeListener, SoundListener {
    protected ResourceCache cache;
    protected SoundClip sound;
    protected short soundNumber;

    protected boolean changingValue;

    protected JButton playButton;
    protected JButton stopButton;
    protected JSlider slider;
    protected Timer timer;

    public SoundPlayer(ResourceCache cache, short soundNumber) {
        super("Sound " + soundNumber);
        this.cache = cache;
        this.soundNumber = soundNumber;

        timer = new Timer(128, this);

        GridBagLayout gridBag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        Container cont = getContentPane();

        cont.setLayout(gridBag);
        allocSound();

        slider = new JSlider(0, (int) this.sound.getMaxPosition(), (int) this.sound.getPosition());
        slider.addChangeListener(this);
        slider.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    dispose();
                }
            }
        });
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        c.insets = new Insets(10, 0, 0, 0);
        gridBag.setConstraints(slider, c);
        cont.add(slider);

        playButton = new JButton("Play");
        playButton.addActionListener(this);
        c.gridx = 0;
        c.gridy = 1;
        c.insets = new Insets(10, 10, 10, 10);
        c.gridwidth = 1;
        gridBag.setConstraints(playButton, c);
        cont.add(playButton);

        stopButton = new JButton("Stop");
        stopButton.addActionListener(this);
        stopButton.setEnabled(false);
        c.gridx = 1;
        c.gridy = 1;
        gridBag.setConstraints(stopButton, c);
        cont.add(stopButton);

        addWindowListener(new WindowAdapter() {
            public void windowClosed(WindowEvent e) {
                freeSound();
            }
        });

        setResizable(false);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        pack();
    }

    public void actionPerformed(ActionEvent ev) {
        Object o = ev.getSource();

        if (o instanceof Timer) {
            changingValue = true;

            if (sound != null) {
                slider.setValue((int) sound.getPosition());
            }

            changingValue = false;
        } else if (o == playButton) {
            allocSound();
            sound.play();
        } else if (o == stopButton) {
            sound.stop();
        }
    }

    public void stateChanged(ChangeEvent e) {
        if (!changingValue) {
            sound.setPosition(slider.getValue());
        }
    }

    public void soundStarted(SoundClip sound) {
        timer.start();
        playButton.setEnabled(false);
        stopButton.setEnabled(true);
    }

    public void soundStopped(SoundClip sound, byte reason) {
        timer.stop();
        playButton.setEnabled(true);
        stopButton.setEnabled(false);

        changingValue = true;
        slider.setValue((int) sound.getPosition());
        changingValue = false;
    }

    public void soundVolumeChanged(SoundClip sound, int volume) {
    }

    protected void allocSound() {
        if (sound != null) {
            return;
        }

        try {
            sound = cache.getSound(soundNumber).createClip();
            sound.addSoundListener(this);
        } catch (IOException ioex) {
        } catch (ResourceException rex) {
        }
    }

    protected void freeSound() {
        if (sound != null) {
            sound.removeSoundListener(this);
            sound.stop();
            slider.setValue(0);
            soundStopped(sound, SoundListener.STOP_REASON_PROGRAMMATICALLY);
            sound = null;
        }
    }
}
