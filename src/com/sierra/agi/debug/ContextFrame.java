/**
 * ContextFrame.java
 * Adventure Game Interpreter Debug Package
 * <p>
 * Created by Dr. Z.
 * Copyright (c) 2001 Dr. Z. All rights reserved.
 */

package com.sierra.agi.debug;

import com.sierra.agi.awt.EgaComponent;
import com.sierra.agi.logic.debug.LogicContextDebug;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class ContextFrame extends JFrame implements ActionListener {
    protected LogicContextDebug logicContext;
    protected ContextDebugger debugger;

    public ContextFrame(LogicContextDebug logicContext) {
        super(logicContext.getGameName());

        EgaComponent ega = logicContext.getComponent();

        add(ega);
        addKeyListener(ega);

        this.logicContext = logicContext;

        setJMenuBar(addMenu());
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        setResizable(false);
        pack();

        debugger = new ContextDebugger(logicContext);

        logicContext.resumeExecution();
    }

    protected JMenuBar addMenu() {
        JMenuBar menubar = new JMenuBar();
        JMenuItem item;

        JMenuItem menu = new JMenu("Engine");
        item = new JMenuItem("Start Execution");
        item.setActionCommand("start");
        item.addActionListener(this);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, 0));
        //item.setShortcut(new MenuShortcut(KeyEvent.VK_R, false));
        menu.add(item);

        item = new JMenuItem("Break Execution");
        item.setActionCommand("break");
        item.addActionListener(this);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,java.awt.Event.SHIFT_MASK));
        //item.setShortcut(new MenuShortcut(KeyEvent.VK_R, true));
        menu.add(item);
        menubar.add(menu);

        return menubar;
    }

    public void setVisible(boolean v) {
        super.setVisible(v);
        debugger.setVisible(v);
    }

    public void setBounds(Rectangle r) {
        Rectangle s = (Rectangle) r.clone();

        s.y = r.y + r.height;
        s.height = debugger.getBounds().height;

        super.setBounds(r);
        debugger.setBounds(s);
    }

    public void actionPerformed(ActionEvent ev) {
        String s = ev.getActionCommand();

        if (s.equals("start")) {
            logicContext.resumeExecution();
        } else if (s.equals("break")) {
            logicContext.breakExecution();
        }
    }
}
