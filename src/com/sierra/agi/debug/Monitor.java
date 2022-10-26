/**
 * Monitor.java
 * Adventure Game Interpreter Debug Package
 * <p>
 * Created by Dr. Z
 * Copyright (c) 2001 Dr. Z. All rights reserved.
 */

package com.sierra.agi.debug;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Monitor extends JFrame implements ActionListener {
    protected JProgressBar memBar;
    protected JLabel memTotal;
    protected JLabel thrTotal;

    protected Timer timer;

    public Monitor() {
        super("Monitor");

        GridBagLayout gridBag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        Container cont = getContentPane();
        JLabel label;

        cont.setLayout(gridBag);

        label = new JLabel("Memory:");
        c.insets = new Insets(4, 7, 4, 4);
        gridBag.setConstraints(label, c);
        cont.add(label);

        label = new JLabel("Threads:");
        c.gridy = 1;
        gridBag.setConstraints(label, c);
        cont.add(label);

        memBar = new JProgressBar(0, 1000);
        c.gridx = 1;
        c.gridy = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        c.insets = new Insets(4, 4, 4, 4);
        gridBag.setConstraints(memBar, c);
        cont.add(memBar);

        memTotal = new JLabel("? Megs");
        c.gridx = 2;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0.0;
        gridBag.setConstraints(memTotal, c);
        cont.add(memTotal);

        thrTotal = new JLabel("?");
        c.gridx = 1;
        c.gridy = 1;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.0;
        gridBag.setConstraints(thrTotal, c);
        cont.add(thrTotal);

        timer = new Timer(3000, this);
        timer.start();

        actionPerformed(new ActionEvent(timer, 0, null));

        addWindowListener(new WindowAdapter() {
            public void windowClosed(WindowEvent ev) {
                timer.stop();
            }
        });

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        pack();
    }

    public void actionPerformed(ActionEvent ev) {
        Object o = ev.getSource();

        if (o instanceof Timer) {
            Runtime runtime = Runtime.getRuntime();
            long total = runtime.totalMemory();
            long free = runtime.freeMemory();

            memTotal.setText(((total - free) / (1024 * 1024)) + "/" + (total / (1024 * 1024)) + " Megs");
            memBar.setValue((int) (1000 - free * 1000 / total));
            thrTotal.setText(Integer.toString(DebugUtils.getRootThreadGroup().activeCount()));
        }
    }
}
