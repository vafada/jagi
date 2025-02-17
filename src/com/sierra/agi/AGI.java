/*
 *  AGI.java
 *  Adventure Game Interpreter
 *
 *  Created by Dr. Z.
 *  Copyright (c) 2001 Dr. Z. All rights reserved.
 */

package com.sierra.agi;

import com.sierra.agi.awt.EgaComponent;
import com.sierra.agi.debug.ExceptionDialog;
import com.sierra.agi.logic.LogicContext;
import com.sierra.agi.logic.LogicException;
import com.sierra.agi.res.ResourceCache;
import com.sierra.agi.res.ResourceCacheFile;

import javax.swing.JFrame;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

public class AGI {
    protected LogicContext logicContext;
    protected JFrame frame;

    public AGI(String[] args) throws Exception {
        File resFile;

        if (args.length == 0) {
            resFile = obtainResourceFile();
        } else {
            resFile = new File(args[0]);
        }

        ResourceCache resCache = new ResourceCacheFile(resFile);
        LogicContext context = new LogicContext(resCache);

        frame = new JFrame(context.getGameName());
        frame.add(context.getComponent());
        frame.addKeyListener(context.getComponent());
        frame.addWindowListener(
                new WindowAdapter() {
                    public void windowClosing(WindowEvent ev) {
                        System.exit(0);
                    }
                });
        frame.setFocusable(true);
        frame.setFocusTraversalKeysEnabled(false);
        frame.pack();
        frame.setResizable(false);
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
        frame.setLocation(x, y);
        logicContext = context;
    }

    public static File obtainResourceFile() {
        FileDialog dialog = new FileDialog(new Frame(), "Open a Game's Resources", FileDialog.LOAD);
        String file, dir;

        dialog.setVisible(true);
        dir = dialog.getDirectory();
        file = dialog.getFile();
        dialog.dispose();

        if ((dir != null) && (file != null)) {
            return new File(dir, file);
        }

        System.exit(-1);
        return null;
    }

    public static void main(String[] args) throws LogicException {
        System.setProperty("com.sierra.agi.logic.LogicProvider", "com.sierra.agi.logic.interpret.InterpretedLogicProvider");

        try {
            /* Try to ask the JIT Compiler to compile these classes
               (which take the majority of the CPU time.) */
            Compiler.enable();
            Compiler.compileClass(com.sierra.agi.logic.LogicContext.class);
            Compiler.compileClass(com.sierra.agi.view.ViewTable.class);
            Compiler.compileClass(com.sierra.agi.view.ViewSprite.class);
            Compiler.compileClass(com.sierra.agi.view.ViewScreen.class);
            Compiler.compileClass(com.sierra.agi.awt.QuickerScaleFilter.class);
        } catch (Throwable thr) {
            thr.printStackTrace();
        }

        try {
            (new AGI(args)).run();
        } catch (Throwable thr) {
            ExceptionDialog.showException(thr);
        }
    }

    public void run() {
        frame.setVisible(true);
        logicContext.run();
    }
}