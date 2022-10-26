/**
 * ExceptionDialog.java
 * Adventure Game Interface Debugger Package
 * <p>
 * Created by Dr. Z.
 * Copyright (c) 2001 Dr. Z. All rights reserved.
 */

package com.sierra.agi.debug;

import javax.swing.*;
import java.awt.*;
import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionDialog extends JDialog {
    public ExceptionDialog(Frame owner, Throwable thr, boolean modal) {
        super(owner, "Exception", modal);

        StringWriter writer = new StringWriter();
        Font font = new Font("Monospaced", Font.PLAIN, 12);
        JTextArea area;
        JScrollPane pane;
        Dimension d;

        thr.printStackTrace(new PrintWriter(writer, true));
        area = new JTextArea(writer.toString());
        area.setFont(font);
        area.setEditable(false);
        d = area.getPreferredSize();

        pane = new JScrollPane(area);

        if (d.width > 640) {
            d.width = 640;
        }

        if (d.height > 480) {
            d.height = 480;
        }

        pane.setPreferredSize(d);
        getContentPane().add(pane);
        pack();
    }

    public static void showException(Throwable thr) {
        showException(new Frame(), thr, true);
    }

    public static void showException(Frame owner, Throwable thr) {
        showException(owner, thr, true);
    }

    public static void showException(Frame owner, Throwable thr, boolean modal) {
        ExceptionDialog dialog;

        if (EventQueue.isDispatchThread()) {
            modal = false;
        }

        dialog = new ExceptionDialog(owner, thr, modal);
        dialog.setVisible(true);

        if (!modal) {
            dialog.dispose();
        }
    }
}
