/**
 * LogicViewer.java
 * Adventure Game Interpreter Debug Package
 * <p>
 * Created by Dr. Z.
 * Copyright (c) 2001 Dr. Z. All rights reserved.
 */

package com.sierra.agi.debug;

import com.sierra.agi.debug.logic.LogicComponent;
import com.sierra.agi.logic.Logic;
import com.sierra.agi.logic.debug.LogicDebug;
import com.sierra.agi.logic.interpret.LogicInterpreter;
import com.sierra.agi.res.ResourceCache;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

public class LogicViewer extends JFrame implements ActionListener {
    protected LogicComponent component;

    public LogicViewer(ResourceCache cache, String title, Logic logic) {
        super(title);

        JScrollPane pane;

        component = new LogicComponent(cache, (LogicDebug) logic);
        pane = new JScrollPane(component);
        pane.setPreferredSize(new Dimension(350, 350));

        setMenuBar(generateMenubar());

        getContentPane().add(pane, BorderLayout.CENTER);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        pack();
    }

    protected MenuBar generateMenubar() {
        MenuBar menubar = new MenuBar();
        Menu menu;
        MenuItem item;

        menu = new Menu("Resource");
        item = new MenuItem("Save as Text File");
        item.addActionListener(this);
        item.setShortcut(new MenuShortcut(KeyEvent.VK_T));
        item.setActionCommand("save");
        menu.add(item);
        menubar.add(menu);

        return menubar;
    }

    public void actionPerformed(ActionEvent ev) {
        String s = ev.getActionCommand();
        LogicInterpreter logic = component.getLogic();
        PrintWriter writer;
        String file, dir;
        FileDialog dialog;
        int i, j;

        if (s.equals("save")) {
            dialog = new FileDialog(this, "Save Logic to Text File", FileDialog.SAVE);
            dialog.setVisible(true);
            dir = dialog.getDirectory();
            file = dialog.getFile();
            dialog.dispose();

            if ((dir != null) && (file != null)) {
                try {
                    String[] m;

                    writer = new PrintWriter(new FileOutputStream(new File(dir, file)));

                    for (i = 0; i < component.getLineCount(); i++) {
                        writer.println(component.getLineText(i));
                    }

                    writer.close();
                } catch (IOException ioex) {
                }
            }
        }
    }
}