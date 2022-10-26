/*
 *  MenuTesterFrame.java
 *  Adventure Game Interpreter Debug Package
 *
 *  Created by Dr. Z.
 *  Copyright (c) 2002 Dr. Z. All rights reserved.
 */

package com.sierra.agi.debug;

import com.sierra.agi.menu.AgiMenuBar;
import com.sierra.agi.view.MessageBox;
import com.sierra.agi.view.ViewScreen;

import java.awt.*;

public class MenuTesterFrame extends Frame implements Runnable {
    protected ViewScreen screen;
    protected AgiMenuBar bar;

    public MenuTesterFrame() {
        super("Menu Tester");

        bar = new AgiMenuBar();
        screen = new ViewScreen();
        screen.reset();

        bar.addMenu("Sierra ");
        bar.addMenuItem("About AGId", (short) 1);
        bar.addMenuItem("About Sierra", (short) 2);
        bar.addMenuItem("------------", (short) 3);
        bar.addMenuItem("Quit        ", (short) 4);
        bar.addMenu("Fichier ");
        bar.addMenuItem("About AGId", (short) 5);
        bar.addMenuItem("About Sierra", (short) 6);
        bar.addMenuItem("------------", (short) 3);
        bar.addMenuItem("Quit        ", (short) 8);
        bar.addMenu("Jeux ");
        bar.addMenuItem("About AGId", (short) 9);
        bar.addMenuItem("About Sierra", (short) 10);
        bar.addMenuItem("------------", (short) 3);
        bar.addMenuItem("Quit        ", (short) 12);
        bar.addMenu("Misc ");
        bar.addMenuItem("About AGId", (short) 13);
        bar.addMenuItem("About Sierra", (short) 14);
        bar.addMenuItem("------------", (short) 3);
        bar.addMenuItem("Quit        ", (short) 15);
        bar.addMenu("Vitesse ");
        bar.addMenuItem("About AGId", (short) 16);
        bar.addMenuItem("About Sierra", (short) 17);
        bar.addMenuItem("------------", (short) 3);
        bar.addMenuItem("Quit        ", (short) 18);

        bar.enableMenuItem(false, (short) 3);
        bar.enableMenuItem(false, (short) 8);

        add(screen.getComponent());

        addKeyListener(screen.getComponent());
        addMouseListener(screen.getComponent());

        setResizable(false);
        pack();
    }

    public void dispose() {
        removeAll();
        screen = null;
        bar = null;
        super.dispose();
    }

    public void run() {
        MessageBox box;

        box = new MessageBox("You selected controller " + screen.menuLoop(bar) + ".\r\nYoink!");
        box.setTimeout(8192);
        box.show(null, screen, true);

        box = new MessageBox("This should be an extremely long text that should be warped to multiple lines.\r\n    Yabadabadou!");
        box.setTimeout(8192);
        box.show(null, screen, true);

        setVisible(false);

        try {
            Thread.sleep(2048);
        } catch (InterruptedException iex) {
        }

        dispose();
    }
}
