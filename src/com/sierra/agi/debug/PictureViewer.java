/**
 * PictureViewer.java
 * Adventure Game Interpreter Debug Package
 * <p>
 * Created by Dr. Z
 * Copyright (c) 2001 Dr. Z. All rights reserved.
 */

package com.sierra.agi.debug;

import com.keypoint.PngEncoder;
import com.sierra.agi.pic.Picture;
import com.sierra.agi.pic.PictureContext;
import com.sierra.agi.res.ResourceCache;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class PictureViewer extends Frame implements ActionListener {
    protected ResourceCache cache;
    protected PictureComponent component;

    protected Picture picture;
    protected PictureContext pictureContext;
    protected short pictureNumber;
    protected boolean picturePriority;

    protected Image onScreen;
    protected Image onScreenScaled;

    public PictureViewer(ResourceCache cache, short pictureNumber) {
        super("Picture " + pictureNumber);
        this.cache = cache;
        this.pictureNumber = pictureNumber;

        generate();
        add(component = new PictureComponent());

        MenuBar menuBar = new MenuBar();
        Menu menu;
        MenuItem item;

        menu = new Menu("Resource");
        item = new MenuItem("Show Picture");
        item.addActionListener(this);
        item.setShortcut(new MenuShortcut(KeyEvent.VK_P));
        item.setActionCommand("showpicture");
        menu.add(item);
        item = new MenuItem("Show Priority");
        item.addActionListener(this);
        item.setShortcut(new MenuShortcut(KeyEvent.VK_P, true));
        item.setActionCommand("showpriority");
        menu.add(item);
        menu.addSeparator();
        item = new MenuItem("Save as PNG File (320x168)");
        item.addActionListener(this);
        item.setShortcut(new MenuShortcut(KeyEvent.VK_S));
        item.setActionCommand("savepng320");
        menu.add(item);
        item = new MenuItem("Save as PNG File (640x336)");
        item.addActionListener(this);
        item.setShortcut(new MenuShortcut(KeyEvent.VK_S, true));
        item.setActionCommand("savepng640");
        menu.add(item);
        menuBar.add(menu);

        setMenuBar(menuBar);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent ev) {
                setVisible(false);
            }

            public void windowClosed(WindowEvent ev) {
                picture = null;
                pictureContext = null;
                onScreen = null;
                onScreenScaled = null;
                dispose();
            }
        });

        setResizable(false);
        pack();
    }

    protected void generate() {
        if (picture == null) {
            try {
                picture = cache.getPicture(pictureNumber);
                pictureContext = picture.draw();
            } catch (Exception ex) {
                ex.printStackTrace();
                ExceptionDialog.showException(this, ex);
                return;
            }
        }

        if (onScreen == null) {
            if (picturePriority) {
                onScreen = pictureContext.getPriorityImage(getToolkit());
            } else {
                onScreen = pictureContext.getPictureImage(getToolkit());
            }

            onScreenScaled = onScreen.getScaledInstance(onScreen.getWidth(this) * 4, onScreen.getHeight(this) * 2, Image.SCALE_FAST);
            prepareImage(onScreenScaled, this);
        }
    }

    public void setVisible(boolean visible) {
        if (visible) {
            generate();
        }

        super.setVisible(visible);
    }

    public void actionPerformed(ActionEvent ev) {
        String action = ev.getActionCommand();

        try {
            if (action.startsWith("savepng")) {
                FileDialog dialog = new FileDialog(this, "Save Picture to PNG", FileDialog.SAVE);
                String file, dir;

                dialog.setVisible(true);
                dir = dialog.getDirectory();
                file = dialog.getFile();
                dialog.dispose();

                if ((dir != null) && (file != null)) {
                    OutputStream out = new FileOutputStream(new File(dir, file));
                    PngEncoder encoder;

                    if (action.endsWith("320")) {
                        encoder = new PngEncoder(onScreen.getScaledInstance(onScreen.getWidth(this) * 2, onScreen.getHeight(this), Image.SCALE_FAST));
                    } else {
                        encoder = new PngEncoder(onScreenScaled);
                    }

                    encoder.setCompressionLevel(9);

                    out.write(encoder.pngEncode());
                    out.close();
                }
            } else if (action.equals("showpicture")) {
                if (picturePriority) {
                    picturePriority = false;
                    onScreen = null;
                    generate();
                    component.repaint();
                }
            } else if (action.equals("showpriority")) {
                if (!picturePriority) {
                    picturePriority = true;
                    onScreen = null;
                    generate();
                    component.repaint();
                }
            }
        } catch (Exception ex) {
            ExceptionDialog.showException(this, ex);
        }
    }

    public class PictureComponent extends Component {
        public Dimension getMinimumSize() {
            generate();
            return new Dimension(onScreenScaled.getWidth(this), onScreenScaled.getHeight(this));
        }

        public Dimension getPreferredSize() {
            generate();
            return new Dimension(onScreenScaled.getWidth(this), onScreenScaled.getHeight(this));
        }

        public void paint(Graphics g) {
            generate();
            g.drawImage(onScreenScaled, 0, 0, this);
        }
    }
}
