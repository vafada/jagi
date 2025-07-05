/**
 * ViewViewer.java
 * AGI Debugger
 * <p>
 * Created by Dr. Z.
 * Copyright (c) 2001 Dr. Z. All rights reserved.
 */

package com.sierra.agi.debug;

import com.sierra.agi.res.ResourceCache;
import com.sierra.agi.view.Cel;
import com.sierra.agi.view.Loop;
import com.sierra.agi.view.View;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;


public class ViewViewer extends JFrame implements ActionListener {
    protected ResourceCache cache;

    protected JButton prevLoop;
    protected JButton prevCell;
    protected JButton play;
    protected JButton nextCell;
    protected JButton nextLoop;
    protected ViewComponent cellComp;
    protected Timer timer;

    protected Dimension preferred;
    protected View view;
    protected short viewNumber;
    protected Image[][] images;
    protected short loopn, celln;

    public ViewViewer(ResourceCache cache, short viewNumber) {
        super("View " + viewNumber);
        this.cache = cache;
        this.viewNumber = viewNumber;

        MenuBar menuBar = new MenuBar();
        Menu menu;
        MenuItem item;

        menu = new Menu("Resource");
        item = new MenuItem("Save as PNG File (1x)");
        item.addActionListener(this);
        item.setShortcut(new MenuShortcut(KeyEvent.VK_S));
        item.setActionCommand("savepng1x");
        menu.add(item);
        item = new MenuItem("Save as PNG File (2x)");
        item.addActionListener(this);
        item.setShortcut(new MenuShortcut(KeyEvent.VK_S, true));
        item.setActionCommand("savepng2x");
        menu.add(item);
        menuBar.add(menu);
        setMenuBar(menuBar);


        GridBagLayout gridBag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        Container cont = getContentPane();

        cont.setLayout(gridBag);

        generate();

        KeyAdapter escAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    dispose();
                }
            }
        };

        cellComp = new ViewComponent();
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 5;
        c.insets = new Insets(5, 5, 5, 5);
        gridBag.setConstraints(cellComp, c);
        cont.add(cellComp);

        prevLoop = new JButton("< Loop");
        prevLoop.setActionCommand("loopp");
        prevLoop.addActionListener(this);
        prevLoop.addKeyListener(escAdapter);
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        c.insets = new Insets(5, 5, 5, 0);
        gridBag.setConstraints(prevLoop, c);
        cont.add(prevLoop);

        prevCell = new JButton("< Cell");
        prevCell.setActionCommand("cellp");
        prevCell.addActionListener(this);
        prevCell.addKeyListener(escAdapter);
        c.gridx = 1;
        gridBag.setConstraints(prevCell, c);
        cont.add(prevCell);

        play = new JButton("Play");
        play.setActionCommand("play");
        play.addActionListener(this);
        play.addKeyListener(escAdapter);
        c.gridx = 2;
        gridBag.setConstraints(play, c);
        cont.add(play);

        nextCell = new JButton("Cell >");
        nextCell.setActionCommand("celln");
        nextCell.addActionListener(this);
        nextCell.addKeyListener(escAdapter);
        c.gridx = 3;
        gridBag.setConstraints(nextCell, c);
        cont.add(nextCell);

        nextLoop = new JButton("Loop >");
        nextLoop.setActionCommand("loopn");
        nextLoop.addActionListener(this);
        nextLoop.addKeyListener(escAdapter);
        c.gridx = 4;
        c.insets = new Insets(5, 5, 5, 5);
        gridBag.setConstraints(nextLoop, c);
        cont.add(nextLoop);

        addWindowListener(new WindowAdapter() {
            public void windowClosed(WindowEvent ev) {
                timer.stop();
                timer = null;
                view = null;
                images = null;
                loopn = 0;
                celln = 0;
            }
        });

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        pack();

        timer = new Timer(128, this);
    }

    public void setVisible(boolean visible) {
        if (visible) {
            generate();
        }

        super.setVisible(visible);
    }

    protected void generateImages() {
        short i, j, cellc, loopc;
        short w = 0, w2 = 0, h = 0, h2 = 0;
        Loop loop;
        Cel cel;

        loopc = view.getLoopCount();
        images = new Image[loopc][];

        for (i = 0; i < loopc; i++) {
            loop = view.getLoop(i);
            cellc = loop.getCellCount();
            images[i] = new Image[cellc];

            for (j = 0; j < cellc; j++) {
                cel = loop.getCell(j);
                w2 = cel.getWidth();
                h2 = cel.getHeight();

                if (w < w2) {
                    w = w2;
                }

                if (h < h2) {
                    h = h2;
                }
            }
        }

        w *= 4;
        h *= 2;

        preferred = new Dimension(w, h);
    }

    protected Image generateImage(short loop, short cell) {
        Image image;

        generate();
        image = images[loop][cell];

        if (image == null) {
            image = view.getLoop(loop).getCell(cell).getImage();
            image = image.getScaledInstance(image.getWidth(this) * 4, image.getHeight(this) * 2, Image.SCALE_REPLICATE);
            images[loop][cell] = image;
        }

        return image;
    }

    protected void generate() {
        if (view == null) {
            try {
                this.view = cache.getView(viewNumber);
            } catch (Exception ex) {
                ExceptionDialog.showException(this, ex);
            }
        }

        if (images == null) {
            generateImages();
        }
    }

    public void actionPerformed(ActionEvent ev) {
        String action = ev.getActionCommand();
        Object source = ev.getSource();

        try {
            if (source instanceof Timer) {
                if (celln >= (images[loopn].length - 1)) {
                    celln = 0;
                } else {
                    celln++;
                }

                cellComp.repaint();
            } else if (action.equals("play")) {
                if (timer.isRunning()) {
                    play.setText("Play");
                    timer.stop();
                } else {
                    play.setText("Stop");
                    timer.start();
                }
            } else if (action.equals("loopp")) {
                if (loopn > 0) {
                    loopn--;
                }

                celln = 0;
                cellComp.repaint();
            } else if (action.equals("cellp")) {
                if (celln > 0) {
                    celln--;
                }

                cellComp.repaint();
            } else if (action.equals("loopn")) {
                if (loopn < (images.length - 1)) {
                    loopn++;
                }

                celln = 0;
                cellComp.repaint();
            } else if (action.equals("celln")) {
                if (celln < (images[loopn].length - 1)) {
                    celln++;
                }

                cellComp.repaint();
            } else if (action.startsWith("savepng")) {
                FileDialog dialog = new FileDialog(this, "Save Picture to PNG", FileDialog.SAVE);
                String file, dir;

                dialog.setVisible(true);
                dir = dialog.getDirectory();
                file = dialog.getFile();
                dialog.dispose();

                if ((dir != null) && (file != null)) {
                    if (action.endsWith("1x")) {
                        Image image = view.getLoop(loopn).getCell(celln).getImage();
                        image = image.getScaledInstance(image.getWidth(this) * 2, image.getHeight(this), Image.SCALE_REPLICATE);

                        image.flush();

                        BufferedImage bufferedImage = ImageUtils.toBufferedImage(image);
                        ImageIO.write(bufferedImage, "PNG", new File(dir, file));
                    } else {
                        Image image = generateImage(loopn, celln);
                        BufferedImage bufferedImage = ImageUtils.toBufferedImage(image);
                        ImageIO.write(bufferedImage, "PNG", new File(dir, file));
                    }
                }
            }
        } catch (Exception ex) {
            ExceptionDialog.showException(this, ex);
        }
    }

    public class ViewComponent extends Component {
        public Dimension getMinimumSize() {
            return preferred;
        }

        public Dimension getPreferredSize() {
            return preferred;
        }

        public void paint(Graphics g) {
            Image image = generateImage(loopn, celln);
            g.drawImage(image, 0, preferred.height - image.getHeight(this), this);
        }
    }
}
