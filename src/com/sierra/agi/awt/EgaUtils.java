/*
 *  EgaUtil.java
 *  Adventure Game Interpreter AWT Package
 *
 *  Created by Dr. Z.
 *  Copyright (c) 2001 Dr. Z. All rights reserved.
 */

package com.sierra.agi.awt;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;

/**
 * Misc. Utilities for EGA support in Java's AWT.
 *
 * @author Dr. Z
 * @version 0.00.00.01
 */
public abstract class EgaUtils {
    /**
     * EGA Colors Red Band
     */
    protected static final byte[] r = {(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xa8, (byte) 0xa8, (byte) 0xa8, (byte) 0xa8, (byte) 0x54, (byte) 0x54, (byte) 0x54, (byte) 0x54, (byte) 0xfc, (byte) 0xfc, (byte) 0xfc, (byte) 0xfc};
    /**
     * EGA Colors Green Band
     */
    protected static final byte[] g = {(byte) 0x00, (byte) 0x00, (byte) 0xa8, (byte) 0xa8, (byte) 0x00, (byte) 0x00, (byte) 0x54, (byte) 0xa8, (byte) 0x54, (byte) 0x54, (byte) 0xfc, (byte) 0xfc, (byte) 0x54, (byte) 0x54, (byte) 0xfc, (byte) 0xfc};
    /**
     * EGA Colors Blue Band
     */
    protected static final byte[] b = {(byte) 0x00, (byte) 0xa8, (byte) 0x00, (byte) 0xa8, (byte) 0x00, (byte) 0xa8, (byte) 0x00, (byte) 0xa8, (byte) 0x54, (byte) 0xfc, (byte) 0x54, (byte) 0xfc, (byte) 0x54, (byte) 0xfc, (byte) 0x54, (byte) 0xfc};
    /**
     * Standard EGA Font
     */
    protected static final int[] egaFont = {
            0x00000000, 0x00000000,
            0x7e424242, 0x4242427e,
            0x7e7e7e7e, 0x7e7e7e7e,
            0x00000000, 0x00000000,
            0x10387cfe, 0x7c381000,
            0x3c3c18ff, 0xe7183c00,
            0x10387cfe, 0xee103800,
            0x0000183c, 0x3c180000,
            0xffffe7c3, 0xc3e7ffff,
            0x003c6642, 0x42663c00,
            0x00000000, 0x00000000,
            0x0f070f7d, 0xcccccc78,
            0x3c666666, 0x3c187e18,
            0x080c0a0a, 0x0878f000,
            0x18141a16, 0x72e20e1c,
            0x105438ee, 0x38541000,
            0x80e0f8fe, 0xf8e08000,
            0x020e3efe, 0x3e0e0200,
            0x183c5a18, 0x5a3c1800,
            0x66666666, 0x66006600,
            0x7fdbdbdb, 0x7b1b1b00,
            0x1c223844, 0x44388870,
            0x00000000, 0x7e7e7e00,
            0x183c5a18, 0x5a3c187e,
            0x183c5a18, 0x18181800,
            0x18181818, 0x5a3c1800,
            0x00180cfe, 0x0c180000,
            0x003060fe, 0x60300000,
            0x0000c0c0, 0xc0fe0000,
            0x002442ff, 0x42240000,
            0x0010387c, 0xfefe0000,
            0x00fefe7c, 0x38100000,
            0x00000000, 0x00000000 /*   */,
            0x183c3c18, 0x18001800 /* ! */,
            0x6c242400, 0x00000000 /* " */,
            0x6c6cfe6c, 0xfe6c6c00 /* # */,
            0x107cd07c, 0x16fc1000 /* $ */,
            0x0066acd8, 0x366acc00 /* % */,
            0x384c3878, 0xcecc7a00 /* & */,
            0x30102000, 0x00000000 /* ' */,
            0x18306060, 0x60301800 /* ( */,
            0x60301818, 0x18306000 /* ) */,
            0x00663cff, 0x3c660000 /* * */,
            0x003030fc, 0x30300000 /* + */,
            0x00000000, 0x00301020 /* , */,
            0x000000fc, 0x00000000 /* - */,
            0x00000000, 0x00003000 /* . */,
            0x02060c18, 0x3060c000 /* / */,
            0x7ccedef6, 0xe6e67c00 /* 0 */,
            0x18387818, 0x18187e00 /* 1 */,
            0x7cc6061c, 0x70c6fe00 /* 2 */,
            0x7cc6063c, 0x06c67c00 /* 3 */,
            0x1c3c6ccc, 0xfe0c1e00 /* 4 */,
            0xfec0fc06, 0x06c67c00 /* 5 */,
            0x7cc6c0fc, 0xc6c67c00 /* 6 */,
            0xfec60c18, 0x30303000 /* 7 */,
            0x7cc6c67c, 0xc6c67c00 /* 8 */,
            0x7cc6c67e, 0x06c67c00 /* 9 */,
            0x00300000, 0x00300000 /* : */,
            0x00300000, 0x00301020 /* ; */,
            0x0c183060, 0x30180c00 /* < */,
            0x00007e00, 0x007e0000 /* = */,
            0x6030180c, 0x18306000 /* > */,
            0x78cc0c18, 0x30003000 /* ? */,
            0x7c829ea6, 0x9e807c00 /* @ */,
            0x7cc6c6fe, 0xc6c6c600 /* A */,
            0xfc66667c, 0x6666fc00 /* B */,
            0x7cc6c0c0, 0xc0c67c00 /* C */,
            0xfc666666, 0x6666fc00 /* D */,
            0xfe626878, 0x6862fe00 /* E */,
            0xfe626878, 0x6860f000 /* F */,
            0x7cc6c6c0, 0xcec67e00 /* G */,
            0xc6c6c6fe, 0xc6c6c600 /* H */,
            0x3c181818, 0x18183c00 /* I */,
            0x1e0c0c0c, 0xcccc7800 /* J */,
            0xe6666c78, 0x6c66e600 /* K */,
            0xf0606060, 0x6266fe00 /* L */,
            0x82c6eefe, 0xd6c6c600 /* M */,
            0xc6e6f6de, 0xcec6c600 /* N */,
            0x7cc6c6c6, 0xc6c67c00 /* O */,
            0xfc66667c, 0x6060f000 /* P */,
            0x7cc6c6c6, 0xd6de7c06 /* Q */,
            0xfc66667c, 0x6666e600 /* R */,
            0x7cc6c07c, 0x06c67c00 /* S */,
            0x7e5a5a18, 0x18183c00 /* T */,
            0xc6c6c6c6, 0xc6c67c00 /* U */,
            0xc6c6c6c6, 0x6c381000 /* V */,
            0xc6c6d6fe, 0xeec68200 /* W */,
            0xc66c3838, 0x386cc600 /* X */,
            0x6666663c, 0x18183c00 /* Y */,
            0xfec68c18, 0x3266fe00 /* Z */,
            0x78606060, 0x60607800 /* [ */,
            0xc0603018, 0x0c060200 /* \ */,
            0x78181818, 0x18187800 /* ] */,
            0x10386cc6, 0x00000000 /* ^ */,
            0x00000000, 0x000000ff /* _ */,
            0x30201000, 0x00000000 /* ` */,
            0x0000780c, 0x7ccc7600 /* a */,
            0xe060607c, 0x66667c00 /* b */,
            0x00007cc6, 0xc0c67c00 /* c */,
            0x1c0c0c7c, 0xcccc7600 /* d */,
            0x00007cc6, 0xfec07c00 /* e */,
            0x1c363078, 0x30307800 /* f */,
            0x000076cc, 0xcc7c0c78 /* g */,
            0xe0606c76, 0x6666e600 /* h */,
            0x18003818, 0x18183c00 /* i */,
            0x000c001c, 0x0c0ccc78 /* j */,
            0xe060666c, 0x786ce600 /* k */,
            0x38181818, 0x18183c00 /* l */,
            0x0000ccfe, 0xd6d6d600 /* m */,
            0x0000dc66, 0x66666600 /* n */,
            0x00007cc6, 0xc6c67c00 /* o */,
            0x0000dc66, 0x667c60f0 /* p */,
            0x00007ccc, 0xcc7c0c1e /* q */,
            0x0000de76, 0x6060f000 /* r */,
            0x00007cc0, 0x7c067c00 /* s */,
            0x1030fc30, 0x30341800 /* t */,
            0x0000cccc, 0xcccc7600 /* u */,
            0x0000c6c6, 0x6c381000 /* v */,
            0x0000c6d6, 0xd6fe6c00 /* w */,
            0x0000c66c, 0x386cc600 /* x */,
            0x0000cccc, 0xcc7c0cf8 /* y */,
            0x0000fc98, 0x3064fc00 /* z */,
            0x0e181830, 0x18180e00 /* { */,
            0x18181800, 0x18181800 /* | */,
            0xe0303018, 0x3030e000 /* } */,
            0x76dc0000, 0x00000000 /* ~ */,
            0xffffffff, 0xffffffff,
            0x1e366666, 0x7e666600,
            0x7c60607c, 0x66667c00,
            0x7c66667c, 0x66667c00,
            0x7e606060, 0x60606000,
            0x386c6c6c, 0x6c6cfec6,
            0x7e60607c, 0x60607e00,
            0xdbdb7e3c, 0x7edbdb00,
            0x3c66061c, 0x06663c00,
            0x66666e7e, 0x76666600,
            0x3c666e7e, 0x76666600,
            0x666c7870, 0x786c6600,
            0x1e366666, 0x66666600,
            0xc6eefefe, 0xd6c6c600,
            0x6666667e, 0x66666600,
            0x3c666666, 0x66663c00,
            0x7e666666, 0x66666600,
            0x7c666666, 0x7c606000,
            0x3c666060, 0x60663c00,
            0x7e181818, 0x18181800,
            0x6666663e, 0x06663c00,
            0x7edbdbdb, 0x7e181800,
            0x66663c18, 0x3c666600,
            0x66666666, 0x66667f03,
            0x6666663e, 0x06060600,
            0xdbdbdbdb, 0xdbdbff00,
            0xdbdbdbdb, 0xdbdbff03,
            0xe060607c, 0x66667c00,
            0xc6c6c6f6, 0xdedef600,
            0x6060607c, 0x66667c00,
            0x788c063e, 0x068c7800,
            0xcedbdbfb, 0xdbdbce00,
            0x3e666666, 0x3e366600,
            0x00003c06, 0x3e663a00,
            0x003c603c, 0x66663c00,
            0x00007c66, 0x7c667c00,
            0x00007e60, 0x60606000,
            0x00003c6c, 0x6c6cfec6,
            0x00003c66, 0x7e603c00,
            0x0000db7e, 0x3c7edb00,
            0x00003c66, 0x0c663c00,
            0x0000666e, 0x7e766600,
            0x0018666e, 0x7e766600,
            0x0000666c, 0x786c6600,
            0x00001e36, 0x66666600,
            0x0000c6fe, 0xfed6c600,
            0x00006666, 0x7e666600,
            0x00003c66, 0x66663c00,
            0x00007e66, 0x66666600,
            0x11441144, 0x11441144,
            0x55aa55aa, 0x55aa55aa,
            0xdd77dd77, 0xdd77dd77,
            0x18181818, 0x18181818,
            0x181818f8, 0x18181818,
            0x18f818f8, 0x18181818,
            0x363636f6, 0x36363636,
            0x000000fe, 0x36363636,
            0x00f818f8, 0x18181818,
            0x36f606f6, 0x36363636,
            0x36363636, 0x36363636,
            0x00fe06f6, 0x36363636,
            0x36f606fe, 0x00000000,
            0x363636fe, 0x00000000,
            0x18f818f8, 0x00000000,
            0x000000f8, 0x18181818,
            0x1818181f, 0x00000000,
            0x181818ff, 0x00000000,
            0x000000ff, 0x18181818,
            0x1818181f, 0x18181818,
            0x000000ff, 0x00000000,
            0x181818ff, 0x18181818,
            0x181f181f, 0x18181818,
            0x36363637, 0x36363636,
            0x3637303f, 0x00000000,
            0x003f3037, 0x36363636,
            0x36f700ff, 0x00000000,
            0x00ff00f7, 0x36363636,
            0x36373037, 0x36363636,
            0x00ff00ff, 0x00000000,
            0x36f700f7, 0x36363636,
            0x18ff00ff, 0x00000000,
            0x363636ff, 0x00000000,
            0x00ff00ff, 0x18181818,
            0x000000ff, 0x36363636,
            0x3636363f, 0x00000000,
            0x181f181f, 0x00000000,
            0x001f181f, 0x18181818,
            0x0000003f, 0x36363636,
            0x363636ff, 0x36363636,
            0x18ff18ff, 0x18181818,
            0x181818f8, 0x00000000,
            0x0000001f, 0x18181818,
            0xffffffff, 0xffffffff,
            0x000000ff, 0xffffffff,
            0xf0f0f0f0, 0xf0f0f0f0,
            0x0f0f0f0f, 0x0f0f0f0f,
            0xffffff00, 0x00000000,
            0x00007c66, 0x667c6000,
            0x00003c66, 0x60663c00,
            0x00007e18, 0x18181800,
            0x00006666, 0x3e067c00,
            0x00007edb, 0xdb7e1800,
            0x0000663c, 0x183c6600,
            0x00006666, 0x66667f03,
            0x00006666, 0x3e060600,
            0x0000dbdb, 0xdbdbff00,
            0x0000dbdb, 0xdbdbff03,
            0x0000e060, 0x7c667c00,
            0x0000c6c6, 0xf6def600,
            0x00006060, 0x7c667c00,
            0x00007c06, 0x3e067c00,
            0x0000cedb, 0xfbdbce00,
            0x00003e66, 0x3e366600,
            0x0000fe00, 0xfe00fe00,
            0x10107c10, 0x10007c00,
            0x0030180c, 0x060c1830,
            0x000c1830, 0x6030180c,
            0x0e1b1b18, 0x18181818,
            0x18181818, 0x18d8d870,
            0x00181800, 0x7e001818,
            0x0076dc00, 0x76dc0000,
            0x00386c6c, 0x38000000,
            0x00000000, 0x18000000,
            0x00000038, 0x38000000,
            0x03020604, 0xcc683810,
            0x3c4299a1, 0xa199423c,
            0x30481020, 0x78000000,
            0x00007c7c, 0x7c7c0000,
            0x00000000, 0x00427e00};
    /**
     * Keyboard Handling
     */
    private static final int[] KEYS =
            {
                    /* Function Keys */
                    KeyEvent.VK_F1, 0x3b00,
                    KeyEvent.VK_F2, 0x3c00,
                    KeyEvent.VK_F3, 0x3d00,
                    KeyEvent.VK_F4, 0x3e00,
                    KeyEvent.VK_F5, 0x3f00,
                    KeyEvent.VK_F6, 0x4000,
                    KeyEvent.VK_F7, 0x4100,
                    KeyEvent.VK_F8, 0x4200,
                    KeyEvent.VK_F9, 0x4300,
                    KeyEvent.VK_F10, 0x4400,

                    KeyEvent.VK_TAB, 0x0009,

                    KeyEvent.VK_BACK_SPACE, 0x0008,
                    KeyEvent.VK_ESCAPE, 0x001b,
                    KeyEvent.VK_ENTER, 0x000d,

                    /* Arrows */
                    KeyEvent.VK_UP, 0x4800,
                    KeyEvent.VK_DOWN, 0x5000,
                    KeyEvent.VK_LEFT, 0x4b00,
                    KeyEvent.VK_RIGHT, 0x4d00,

                    /* Numeric Pad */
                    KeyEvent.VK_NUMPAD1, 0x4f00,
                    KeyEvent.VK_NUMPAD2, 0x5000,
                    KeyEvent.VK_NUMPAD3, 0x5100,
                    KeyEvent.VK_NUMPAD4, 0x4b00,
                    KeyEvent.VK_NUMPAD5, 0x4c00,
                    KeyEvent.VK_NUMPAD6, 0x4d00,
                    KeyEvent.VK_NUMPAD7, 0x4700,
                    KeyEvent.VK_NUMPAD8, 0x4800,
                    KeyEvent.VK_NUMPAD9, 0x4900,

                    /* Page / Line Navigator */
                    KeyEvent.VK_PAGE_UP, 0x4900,
                    KeyEvent.VK_PAGE_DOWN, 0x5100,
                    KeyEvent.VK_HOME, 0x4700,
                    KeyEvent.VK_END, 0x4f00,

                    KeyEvent.VK_EQUALS, KeyEvent.VK_EQUALS,
            };
    /**
     * EGA Color Model Cache
     */
    protected static IndexColorModel indexModel;
    /**
     * Native Color Model Cache
     */
    protected static DirectColorModel nativeModel;

    /**
     * Returns the ColorModel used by EGA Adapters.
     * <p>
     * Used to convert visual resource from EGA Color Model to the
     * Native Color Model.
     */
    public static synchronized IndexColorModel getIndexColorModel() {
        int i;

        if (indexModel == null) {
            indexModel = new IndexColorModel(8, 16, r, g, b);
        }

        return indexModel;
    }

    /**
     * Returns a ColorModel representing the nativiest ColorModel of the
     * current system configuration.
     * <p>
     * In order to reduce the number of ColorModel convertions, each visual
     * resource is converted as soon as possible to this ColorModel.
     */
    public static synchronized DirectColorModel getNativeColorModel() {
        if (nativeModel == null) {
            ColorModel model = Toolkit.getDefaultToolkit().getColorModel();
            DirectColorModel direct;

            if ((model.getTransferType() != DataBuffer.TYPE_INT) ||
                    !(model instanceof DirectColorModel)) {
                model = ColorModel.getRGBdefault();
            }

            if (model.getTransparency() != Transparency.OPAQUE) {
                direct = (DirectColorModel) model;
                model = new DirectColorModel(
                        direct.getColorSpace(),
                        direct.getPixelSize(),
                        direct.getRedMask(),
                        direct.getGreenMask(),
                        direct.getBlueMask(),
                        0,
                        false,
                        DataBuffer.TYPE_INT);
            }

            nativeModel = (DirectColorModel) model;
        }

        return nativeModel;
    }

    /**
     * Returns the Default EGA Font.
     */
    public static int[] getEgaFont() {
        return egaFont;
    }

    public static void putString(int[] buffer, int[] font, String s, int x, int y, int scanSize, int foreground, int background, boolean opaque) {
        int index;
        int length = s.length();

        for (index = 0; index < length; index++) {
            putCharacter(buffer, font, s.charAt(index), x, y, scanSize, foreground, background, opaque);
            x += 8;
        }
    }

    /**
     * Print a Character in a Array of Integer representing a Screen Area.
     */
    public static void putCharacter(int[] buffer, int[] font, char c, int x, int y, int scanSize, int foreground, int background, boolean opaque) {
        int bufferOffset = (y * scanSize) + x;
        int bufferRemaining = scanSize - 8;
        int fontOffset = (int) c * 2;
        int bits, bit;

        // If Not Font is Specified, Use the default EGA Font
        if (font == null) {
            font = egaFont;
        }

        for (int line = 0; line < 8; line++) {
            switch (line) {
                default:
                case 4:
                    fontOffset++;
                case 0:
                    bits = (font[fontOffset] >> 24) & 0xff;
                    break;

                case 1:
                case 5:
                    bits = (font[fontOffset] >> 16) & 0xff;
                    break;

                case 2:
                case 6:
                    bits = (font[fontOffset] >> 8) & 0xff;
                    break;

                case 3:
                case 7:
                    bits = (font[fontOffset]) & 0xff;
                    break;
            }

            for (int col = 0; col < 8; col++) {
                switch (col) {
                    default:
                    case 0:
                        bit = bits & 0x80;
                        break;
                    case 1:
                        bit = bits & 0x40;
                        break;
                    case 2:
                        bit = bits & 0x20;
                        break;
                    case 3:
                        bit = bits & 0x10;
                        break;
                    case 4:
                        bit = bits & 0x08;
                        break;
                    case 5:
                        bit = bits & 0x04;
                        break;
                    case 6:
                        bit = bits & 0x02;
                        break;
                    case 7:
                        bit = bits & 0x01;
                        break;
                }

                if (bit != 0) {
                    buffer[bufferOffset] = foreground;
                } else if (opaque) {
                    buffer[bufferOffset] = background;
                }

                bufferOffset++;
            }

            bufferOffset += bufferRemaining;
        }
    }

    public static short convertKey(int keyCode) {
        for (int index = 0; index < KEYS.length; index += 2) {
            if (keyCode == KEYS[index]) {
                return (short) KEYS[index + 1];
            }
        }

        return (short) 0xff00;
    }
}