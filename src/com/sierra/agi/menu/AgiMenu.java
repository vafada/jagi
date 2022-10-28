/*
 *  AgiMenu.java
 *  Adventure Game Interpreter Menu Debugger
 *
 *  Created by Dr. Z.
 *  Copyright (c) 2001 Dr. Z. All rights reserved.
 */

package com.sierra.agi.menu;

import com.sierra.agi.awt.EgaUtils;
import com.sierra.agi.view.ViewScreen;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

public class AgiMenu {
    protected String name;
    protected List<AgiMenuItem> items = new ArrayList<>();
    protected int maxLength = -1;

    public AgiMenu(String name) {
        this.name = name;
    }

    public void add(AgiMenuItem item) {
        items.add(item);
        maxLength = -1;
    }

    public void enableMenuItem(boolean enable, short controller) {
        for (int item = 0; item < items.size(); item++) {
            AgiMenuItem itemO = items.get(item);

            if (itemO.getController() == controller) {
                itemO.setEnable(enable);
            }
        }
    }

    public boolean isEnabled(int item) {
        return ((AgiMenuItem) items.get(item)).isEnabled();
    }

    public int getController(int item) {
        return ((AgiMenuItem) items.get(item)).getController();
    }

    public int getItemCount() {
        return items.size();
    }

    public String toString() {
        return name;
    }

    protected int getMaxLength() {
        int m = 1;

        if (maxLength < 0) {
            for (int i = 0; i < items.size(); i++) {
                int l = items.get(i).toString().length();

                if (m < l) {
                    m = l;
                }
            }

            maxLength = m;
        }

        return maxLength;
    }

    public void drawMenu(ViewScreen viewScreen, int textColor, int disabledColor, int backgroundColor, int selectedItem, int x, Rectangle changedRectangle) {
        int maxLength = getMaxLength();
        int maxWidth = (maxLength + 2) * ViewScreen.CHAR_WIDTH;

        int c = items.size();
        int[] screen = viewScreen.getScreenData();
        int y = ViewScreen.CHAR_HEIGHT;

        if ((x + maxWidth) > ViewScreen.WIDTH) {
            x = ViewScreen.WIDTH - maxWidth;
        }

        changedRectangle.x = x;
        changedRectangle.y = y;
        changedRectangle.width = maxWidth;

        viewScreen.drawTopLine(textColor, backgroundColor, x, y, maxWidth);

        for (int i = 0; i < c; i++) {
            boolean enabled = isEnabled(i);
            String text = items.get(i).toString();
            y += ViewScreen.CHAR_HEIGHT;

            viewScreen.drawLeftLine(textColor, backgroundColor, x, y);

            if (selectedItem == i) {
                EgaUtils.putString(
                        screen,
                        viewScreen.getFont(),
                        text,
                        x + ViewScreen.CHAR_WIDTH,
                        y,
                        ViewScreen.WIDTH,
                        enabled ? backgroundColor : disabledColor,
                        textColor,
                        true);

                viewScreen.drawBlanks(
                        textColor,
                        x + ((text.length() + 1) * ViewScreen.CHAR_WIDTH),
                        y,
                        (maxLength - text.length()) * ViewScreen.CHAR_WIDTH);
            } else {
                EgaUtils.putString(
                        screen,
                        viewScreen.getFont(),
                        text,
                        x + ViewScreen.CHAR_WIDTH,
                        y,
                        ViewScreen.WIDTH,
                        enabled ? textColor : disabledColor,
                        backgroundColor,
                        true);

                viewScreen.drawBlanks(
                        backgroundColor,
                        x + ((text.length() + 1) * ViewScreen.CHAR_WIDTH),
                        y,
                        (maxLength - text.length()) * ViewScreen.CHAR_WIDTH);
            }

            viewScreen.drawRightLine(textColor, backgroundColor, x + ((maxLength + 1) * ViewScreen.CHAR_WIDTH), y);
        }

        y += ViewScreen.CHAR_HEIGHT;

        viewScreen.drawBottomLine(textColor, backgroundColor, x, y, maxWidth);

        changedRectangle.height = (y + ViewScreen.CHAR_HEIGHT) - changedRectangle.y;

        viewScreen.putBlock(changedRectangle.x, changedRectangle.y, changedRectangle.width, changedRectangle.height);
    }
}
