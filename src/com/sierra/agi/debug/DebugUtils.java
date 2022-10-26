/**
 * DebugUtils.java
 * Adventure Game Interpreter Debug Package
 * <p>
 * Created by Dr. Z.
 * Copyright (c) 2001 Dr. Z. All rights reserved.
 */

package com.sierra.agi.debug;

import java.awt.*;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Properties;

public abstract class DebugUtils {
    public static void printInfo(PrintStream out) {
        out.println("Memory Informations");
        out.println("~~~~~~~~~~~~~~~~~~~");
        printMemoryInfo(out);

        out.println("Thread Informations");
        out.println("~~~~~~~~~~~~~~~~~~~");

        printThreadGroupInfo(getRootThreadGroup(), out, 0);

        out.println("Environment Informations");
        out.println("~~~~~~~~~~~~~~~~~~~~~~~~");

        printEnvironmentInfo(out);

        out.println("AWT Frames");
        out.println("~~~~~~~~~~");

        printFramesInfo(out);
    }

    public static void printMemoryInfo(PrintStream out) {
        Runtime runtime = Runtime.getRuntime();
        long total = runtime.totalMemory();
        long free = runtime.freeMemory();

        out.print("Total�memory: ");
        out.print(total);
        out.println(" bytes");
        out.print("Free�memory:  ");
        out.print(free);
        out.println(" bytes");
        out.println();
    }

    public static void printEnvironmentInfo(PrintStream out) {
        Properties props = System.getProperties();
        Enumeration en = props.propertyNames();
        String s;

        while (en.hasMoreElements()) {
            s = (String) en.nextElement();

            out.print(s);
            out.print("=");
            out.println(props.getProperty(s));
        }

        out.println();
    }

    public static void printThreadGroupInfo(PrintStream out) {
        printThreadGroupInfo(getRootThreadGroup(), out, 0);
    }

    public static void printThreadGroupInfo(ThreadGroup group, PrintStream out, int level) {
        ThreadGroup[] groups = new ThreadGroup[64];
        Thread[] threads = new Thread[64];
        int i, j, count;

        printChars(out, ' ', level);
        out.print("Thread Group \"");
        out.print(group.getName());
        out.println("\"");
        printChars(out, ' ', level);
        printChars(out, '~', group.getName().length() + 15);
        out.println();

        count = group.enumerate(threads, false);
        printChars(out, ' ', level);
        out.print("Thread Count: ");
        out.println(count);

        for (i = 0; i < count; i++) {
            printChars(out, ' ', level);
            out.print("Thread #");
            out.print(i);
            out.print(": ");
            out.print(threads[i].toString());
            out.println();
        }

        out.println();
        count = group.enumerate(groups, false);

        for (i = 0; i < count; i++) {
            printThreadGroupInfo(groups[i], out, level + 1);
        }
    }

    protected static void printChars(PrintStream out, char c, int count) {
        int i;

        for (i = 0; i < count; i++) {
            out.print(c);
        }
    }

    public static void printFramesInfo(PrintStream out) {
        Frame[] frames = Frame.getFrames();
        int i;

        for (i = 0; i < frames.length; i++) {
            out.println(frames[i]);
        }
    }

    public static ThreadGroup getRootThreadGroup() {
        ThreadGroup group = Thread.currentThread().getThreadGroup();

        while (group.getParent() != null) {
            group = group.getParent();
        }

        return group;
    }
}
