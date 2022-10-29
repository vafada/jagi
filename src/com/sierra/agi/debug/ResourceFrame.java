/**
 * ResourceFrame.java
 * Adventure Game Interpreter Debug Package
 * <p>
 * Created by Dr. Z
 * Copyright (c) 2001 Dr. Z. All rights reserved.
 */

package com.sierra.agi.debug;

import com.sierra.agi.inv.InventoryObjects;
import com.sierra.agi.logic.Logic;
import com.sierra.agi.logic.debug.LogicContextDebug;
import com.sierra.agi.res.ResourceCache;
import com.sierra.agi.res.ResourceException;
import com.sierra.agi.res.ResourceProvider;
import com.sierra.agi.word.Words;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Hashtable;

public class ResourceFrame extends JFrame implements MouseListener, ActionListener {
    protected ResourceCache cache;
    protected JTree tree;

    protected Hashtable pictures;
    protected Hashtable sounds;
    protected Hashtable logics;
    protected Hashtable views;
    protected ObjectViewer objects;
    protected WordViewer words;
    protected Monitor monitor;

    public ResourceFrame(ResourceCache cache) {
        super("Resources");
        this.cache = cache;

        addWindowListener(new WindowAdapter() {
            public void windowClosed(WindowEvent e) {
                ResourceFrame.this.cache = null;
                pictures = null;
                sounds = null;
                logics = null;
                views = null;
                objects = null;
                words = null;
            }
        });

        MenuBar menubar = new MenuBar();
        Menu menu;
        MenuItem item;

        menu = new Menu("Debug");
        item = new MenuItem("New Execution Environment");
        item.setActionCommand("newenv");
        item.addActionListener(this);
        item.setShortcut(new MenuShortcut(KeyEvent.VK_N));
        menu.add(item);

        item = new MenuItem("New Menu Tester");
        item.setActionCommand("newmenutest");
        item.addActionListener(this);
        menu.add(item);
        menu.addSeparator();

        item = new MenuItem("Garbage Collect");
        item.setActionCommand("gc");
        item.addActionListener(this);
        item.setShortcut(new MenuShortcut(KeyEvent.VK_C));
        menu.add(item);
        menu.addSeparator();

        item = new MenuItem("Dump Environment to Standard Output");
        item.setActionCommand("dumpinfo");
        item.addActionListener(this);
        item.setShortcut(new MenuShortcut(KeyEvent.VK_D));
        menu.add(item);

        item = new MenuItem("Dump Environment to File");
        item.setActionCommand("dumpinfofs");
        item.addActionListener(this);
        menu.add(item);
        menu.addSeparator();

        item = new MenuItem("Show Resource Monitor");
        item.setActionCommand("monitor");
        item.addActionListener(this);
        item.setShortcut(new MenuShortcut(KeyEvent.VK_R));
        menu.add(item);
        menubar.add(menu);

        setMenuBar(menubar);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        generateTree();
        pack();
    }

    protected void generateTree() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Resources", true);
        DefaultMutableTreeNode node;
        JComponent comp;

        node = new DefaultMutableTreeNode("Logics", true);
        if (generateTree(node, ResourceProvider.TYPE_LOGIC)) {
            root.add(node);
        }

        node = new DefaultMutableTreeNode("Pictures", true);
        if (generateTree(node, ResourceProvider.TYPE_PICTURE)) {
            root.add(node);
        }

        node = new DefaultMutableTreeNode("Sounds", true);
        if (generateTree(node, ResourceProvider.TYPE_SOUND)) {
            root.add(node);
        }

        node = new DefaultMutableTreeNode("Views", true);
        if (generateTree(node, ResourceProvider.TYPE_VIEW)) {
            root.add(node);
        }

        root.add(new DefaultMutableTreeNode(new ResourceEntry(ResourceProvider.TYPE_OBJECT, (short) 0), false));
        root.add(new DefaultMutableTreeNode(new ResourceEntry(ResourceProvider.TYPE_WORD, (short) 0), false));

        addMouseListener(this);

        tree = new JTree(root, true);
        tree.addMouseListener(this);

        comp = new JScrollPane(tree);
        comp.setMinimumSize(new Dimension(150, 200));
        comp.setPreferredSize(new Dimension(170, 200));
        getContentPane().add(comp);
    }

    protected boolean generateTree(DefaultMutableTreeNode root, byte resType) {
        ResourceProvider provider = cache.getResourceProvider();
        short[] list;
        int i;

        try {
            list = provider.enumerate(resType);

            if ((list == null) || (list.length == 0)) {
                return false;
            }

            for (i = 0; i < list.length; i++) {
                root.add(new DefaultMutableTreeNode(new ResourceEntry(resType, list[i]), false));
            }

            return true;
        } catch (ResourceException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void mousePressed(MouseEvent ev) {
        if (ev.getClickCount() == 2) {
            TreePath path = tree.getSelectionPath();
            Object object;
            ResourceEntry entry;

            if (path != null) {
                object = ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();

                if (object instanceof ResourceEntry) {
                    entry = (ResourceEntry) object;

                    try {
                        switch (entry.resType) {
                            case ResourceProvider.TYPE_PICTURE:
                                openPicture(entry.resNumber);
                                break;
                            case ResourceProvider.TYPE_SOUND:
                                openSound(entry.resNumber);
                                break;
                            case ResourceProvider.TYPE_LOGIC:
                                openLogic(entry.toString(), cache.getLogic(entry.resNumber));
                                break;
                            case ResourceProvider.TYPE_VIEW:
                                openView(entry.resNumber);
                                break;
                            case ResourceProvider.TYPE_WORD:
                                openWords(cache.getWords());
                                break;
                            case ResourceProvider.TYPE_OBJECT:
                                openObjects(cache.getObjects());
                                break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void mouseEntered(MouseEvent ev) {
    }

    public void mouseExited(MouseEvent ev) {
    }

    public void mouseClicked(MouseEvent ev) {
    }

    public void mouseReleased(MouseEvent ev) {
    }

    public void openPicture(short pictureNumber) {
        Short s = Short.valueOf(pictureNumber);
        Object o;
        PictureViewer p;

        if (pictures == null) {
            pictures = new Hashtable();
        }

        o = pictures.get(s);

        if (o != null) {
            o = ((Reference) o).get();
        }

        if (o == null) {
            p = new PictureViewer(cache, pictureNumber);
            positionWindow(p);
            pictures.put(s, new WeakReference(p));
        } else {
            p = (PictureViewer) o;
        }

        p.setVisible(true);
    }

    public void openSound(short soundNumber) {
        Short s = Short.valueOf(soundNumber);
        Object o;
        SoundPlayer p;

        if (sounds == null) {
            sounds = new Hashtable();
        }

        o = sounds.get(s);

        if (o != null) {
            o = ((Reference) o).get();
        }

        if (o == null) {
            p = new SoundPlayer(cache, soundNumber);
            positionWindow(p);
            sounds.put(s, new WeakReference(p));
        } else {
            p = (SoundPlayer) o;
        }

        p.setVisible(true);
    }

    public void openView(short viewNumber) {
        Short s = Short.valueOf(viewNumber);
        Object o;
        ViewViewer v;

        if (views == null) {
            views = new Hashtable();
        }

        o = views.get(s);

        if (o != null) {
            o = ((Reference) o).get();
        }

        if (o == null) {
            v = new ViewViewer(cache, viewNumber);
            positionWindow(v);
            views.put(s, new WeakReference(v));
        } else {
            v = (ViewViewer) o;
        }

        v.setVisible(true);
    }

    public void openLogic(String title, Logic logic) {
        Object o;
        JFrame s;

        if (logics == null) {
            logics = new Hashtable();
        }

        o = logics.get(logic);

        if (o == null) {
            s = new LogicViewer(cache, title, logic);
            s.addWindowListener(new ResourceWindowListener(logics, logic));
            positionWindow(s);
            logics.put(logic, s);
        } else {
            s = (JFrame) o;
        }

        s.setVisible(true);
    }

    public void openObjects(InventoryObjects objects) {
        if (this.objects == null) {
            this.objects = new ObjectViewer(objects);
            this.objects.addWindowListener(new ObjectsWindowListener());
            positionWindow(this.objects);
        }

        this.objects.setVisible(true);
    }

    public void openWords(Words words) {
        if (this.words == null) {
            this.words = new WordViewer(words);
            this.words.addWindowListener(new WordsWindowListener());
            positionWindow(this.words);
        }

        this.words.setVisible(true);
    }

    public void positionWindow(Window window) {
        Rectangle rect, rect2;

        rect = getBounds();
        rect2 = window.getBounds();
        rect2.x = rect.x + rect.width;

        window.setBounds(rect2);
    }

    public void actionPerformed(ActionEvent ev) {
        String s = ev.getActionCommand();

        if (s.equals("newenv")) {
            ContextFrame frame;

            frame = new ContextFrame(new LogicContextDebug(cache));
            positionWindow(frame);
            frame.setVisible(true);
        } else if (s.equals("newmenutest")) {
            MenuTesterFrame frame = new MenuTesterFrame();
            positionWindow(frame);
            frame.setVisible(true);

            (new Thread(frame, "Menu Loop")).start();
        } else if (s.equals("gc")) {
            System.gc();
            System.runFinalization();
        } else if (s.equals("monitor")) {
            if (monitor == null) {
                monitor = new Monitor();
            }

            monitor.setVisible(true);
        } else if (s.equals("dumpinfo")) {
            DebugUtils.printInfo(System.out);
        } else if (s.equals("dumpinfofs")) {
            FileDialog dialog = new FileDialog(this, "Dump Information to File", FileDialog.SAVE);
            String file, dir;

            dialog.setVisible(true);
            dir = dialog.getDirectory();
            file = dialog.getFile();
            dialog.dispose();

            if ((dir != null) && (file != null)) {
                try {
                    FileOutputStream out = new FileOutputStream(new File(dir, file));

                    DebugUtils.printInfo(new PrintStream(out));
                    out.close();
                } catch (IOException iex) {
                    iex.printStackTrace();
                }
            }
        } else if (s.equals("exit")) {
            System.exit(0);
        }
    }

    public static class ResourceEntry {
        public byte resType;
        public short resNumber;

        public ResourceEntry(byte resType, short resNumber) {
            this.resType = resType;
            this.resNumber = resNumber;
        }

        public String toString() {
            switch (resType) {
                case ResourceProvider.TYPE_SOUND:
                    return "Sound " + Integer.toString(resNumber);
                case ResourceProvider.TYPE_PICTURE:
                    return "Picture " + Integer.toString(resNumber);
                case ResourceProvider.TYPE_LOGIC:
                    return "Logic " + Integer.toString(resNumber);
                case ResourceProvider.TYPE_VIEW:
                    return "View " + Integer.toString(resNumber);
                case ResourceProvider.TYPE_WORD:
                    return "Words";
                case ResourceProvider.TYPE_OBJECT:
                    return "Objects";
            }

            return "Unknown " + Integer.toString(resNumber);
        }
    }

    protected static class ResourceWindowListener extends WindowAdapter {
        protected Hashtable windows;
        protected Object object;

        public ResourceWindowListener(Hashtable windows, Object object) {
            this.windows = windows;
            this.object = object;
        }

        public void windowClosed(WindowEvent e) {
            synchronized (getClass()) {
                windows.remove(object);
            }
        }
    }

    protected class WordsWindowListener extends WindowAdapter {
        public void windowClosed(WindowEvent ev) {
            synchronized (getClass()) {
                words = null;
            }
        }
    }

    protected class ObjectsWindowListener extends WindowAdapter {
        public void windowClosed(WindowEvent ev) {
            synchronized (getClass()) {
                objects = null;
            }
        }
    }
}
