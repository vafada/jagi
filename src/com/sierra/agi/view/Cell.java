/*
 *  Cell.java
 *  Adventure Game Interpreter View Package
 *
 *  Created by Dr. Z.
 *  Copyright (c) 2001 Dr. Z. All rights reserved.
 */

package com.sierra.agi.view;

import com.sierra.agi.awt.EgaUtils;
import com.sierra.agi.io.ByteCaster;

import java.awt.*;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.MemoryImageSource;

/**
 * @author Dr. Z
 * @version 0.00.00.01
 */
public class Cell {
    /**
     * Cell's Width
     */
    protected short width;

    /**
     * Cell's Height
     */
    protected short height;

    /**
     * Cell's Data
     */
    protected int[] data;

    /**
     * Cell's Transparent Color
     */
    protected int transparent;

    /**
     * Creates new Cell
     */
    public Cell(byte[] b, int start, int loopNumber) {
        short trans;
        short mirrorInfo;
        byte transColor;

        width = ByteCaster.lohiUnsignedByte(b, start);
        height = ByteCaster.lohiUnsignedByte(b, start + 1);
        trans = ByteCaster.lohiUnsignedByte(b, start + 2);

        transColor = (byte) (trans & 0x0F);
        mirrorInfo = (short) ((trans & 0xF0) >> 4);

        loadData(b, start + 3, transColor);

        if ((mirrorInfo & 0x8) != 0) {
            if ((mirrorInfo & 0x7) != loopNumber) {
                mirror();
            }
        }
    }

    protected void loadData(byte[] b, int off, byte transColor) {
        int i, j, x, y, color, count;
        int[] pixel;
        IndexColorModel indexModel = EgaUtils.getIndexColorModel();
        ColorModel nativeModel = EgaUtils.getNativeColorModel();

        pixel = new int[1];
        data = new int[width * height];

        for (j = 0, y = 0; y < height; y++) {
            for (x = 0; b[off] != 0; off++) {
                color = (b[off] & 0xF0) >> 4;
                count = (b[off] & 0x0F);

                for (i = 0; i < count; i++, j++, x++) {
                    nativeModel.getDataElements(indexModel.getRGB(color), pixel);
                    data[j] = pixel[0];
                }
            }

            nativeModel.getDataElements(indexModel.getRGB(transColor), pixel);

            for (; x < width; j++, x++) {
                data[j] = pixel[0];
            }

            off++;
        }

        nativeModel.getDataElements(indexModel.getRGB(transColor), pixel);
        transparent = pixel[0];
    }

    protected void mirror() {
        int i1, i2, x1, x2, y;
        int b;

        for (y = 0; y < height; y++) {
            for (x1 = width - 1, x2 = 0; x1 > x2; x1--, x2++) {
                i1 = (y * width) + x1;
                i2 = (y * width) + x2;

                b = data[i1];
                data[i1] = data[i2];
                data[i2] = b;
            }
        }
    }

    public short getWidth() {
        return width;
    }

    public short getHeight() {
        return height;
    }

    public int[] getPixelData() {
        return data;
    }

    public int getTransparentPixel() {
        return transparent;
    }

    /**
     * Obtain an standard Image object that is a graphical representation of the
     * cell.
     *
     * @param context Game context used to generate the image.
     */
    public Image getImage() {
        int[] data = this.data.clone();
        DirectColorModel colorModel = (DirectColorModel) ColorModel.getRGBdefault();
        DirectColorModel nativeModel = EgaUtils.getNativeColorModel();
        int mask = colorModel.getAlphaMask();
        int[] pixel = new int[1];
        int i;

        for (i = 0; i < (width * height); i++) {
            colorModel.getDataElements(nativeModel.getRGB(data[i]), pixel);

            if (data[i] != transparent) {
                data[i] = pixel[0];
            } else {
                data[i] = 0x00ffffff;
            }
        }

        return Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(width, height, colorModel, data, 0, width));
    }
}