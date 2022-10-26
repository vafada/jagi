/*
 *  QuickerScaleFilter.java
 *  Adventure Game Interpreter AWT Package
 *
 *  Created by Dr. Z.
 *  Copyright (c) 2001 Dr. Z. All rights reserved.
 */

package com.sierra.agi.awt;

import com.sierra.agi.view.ViewScreen;

import java.awt.image.ColorModel;

/**
 * Scale Algorithm to stretch AGI 160x168 to 640x336 quicker than
 * ReplicateScaleFilter can do.
 *
 * @author Dr. Z
 * @version 0.00.00.01
 * @see java.awt.image.ReplicateScaleFilter
 */
public class QuickerScaleFilter extends java.awt.image.ReplicateScaleFilter {
    public QuickerScaleFilter() {
        super(ViewScreen.WIDTH * 2, ViewScreen.HEIGHT * 2);
    }

    public void setPixels(int x, int y, int w, int h, ColorModel model, int[] pixels, int off, int scansize) {
        int[] data;

        if ((outpixbuf == null) || !(outpixbuf instanceof int[])) {
            data = new int[destWidth];
            outpixbuf = data;
        } else {
            data = (int[]) outpixbuf;
        }

        int sof, sofe;
        int dy1;
        int dx2;
        int dx1 = (x * 2);
        int dw = (w * 2);

        for (int dy = y; dy < (y + h); dy++) {
            sof = off + (scansize * (dy - y));
            sofe = sof + w;
            dx2 = dx1;

            for (int dx = sof; dx < sofe; dx++) {
                data[dx2++] = data[dx2++] = pixels[dx];
            }

            dy1 = (dy * 2);
            consumer.setPixels(dx1, dy1, dw, 1, model, data, dx1, dw);
            consumer.setPixels(dx1, dy1 + 1, dw, 1, model, data, dx1, dw);
        }
    }
}