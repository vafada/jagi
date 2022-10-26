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

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class ContextFrame extends Frame implements ActionListener {
    protected LogicContextDebug logicContext;
    protected ContextDebugger debugger;

    public ContextFrame(LogicContextDebug logicContext) {
        super(logicContext.getGameName());

        EgaComponent ega = logicContext.getComponent();

        add(ega);
        addKeyListener(ega);

        this.logicContext = logicContext;

        setMenuBar(addMenu());
        setResizable(false);
        pack();

        debugger = new ContextDebugger(logicContext);
    }

    protected MenuBar addMenu() {
        MenuBar menubar = new MenuBar();
        Menu menu;
        MenuItem item;

        menu = new Menu("Engine");
        item = new MenuItem("Start Execution");
        item.setActionCommand("start");
        item.addActionListener(this);
        item.setShortcut(new MenuShortcut(KeyEvent.VK_R, false));
        menu.add(item);

        item = new MenuItem("Break Execution");
        item.setActionCommand("break");
        item.addActionListener(this);
        item.setShortcut(new MenuShortcut(KeyEvent.VK_R, true));
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
