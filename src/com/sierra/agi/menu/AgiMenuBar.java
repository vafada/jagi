/*
 *  AgiMenuBar.java
 *  Adventure Game Interpreter Menu Package
 *
 *  Created by Dr. Z.
 *  Copyright (c) 2001 Dr. Z. All rights reserved.
 */

package com.sierra.agi.menu;

import com.sierra.agi.awt.EgaUtils;
import com.sierra.agi.view.ViewScreen;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AgiMenuBar {
    private List<AgiMenu> menus = new ArrayList<>();

    public void addMenu(String name) {
        menus.add(new AgiMenu(name));
    }

    public void addMenuItem(String name, short controller) {
        (menus.get(menus.size() - 1)).add(new AgiMenuItem(name, controller));
    }

    public void enableMenuItem(boolean enable, short controller) {
        for (int menu = 0; menu < menus.size(); menu++) {
            (menus.get(menu)).enableMenuItem(enable, controller);
        }
    }

    public void enableAllMenuItem() {
        for (AgiMenu menu : menus) {
            for (AgiMenuItem menuItem : menu.items) {
                menuItem.setEnable(true);
            }
        }
    }

    public boolean isEnabled(int menu, int item) {
        try {
            return (menus.get(menu)).isEnabled(item);
        } catch (Exception ex) {
            return false;
        }
    }

    public int getController(int menu, int item) {
        try {
            return (menus.get(menu)).getController(item);
        } catch (Exception ex) {
            return -1;
        }
    }

    public int getMenuCount() {
        return menus.size();
    }

    public int getItemCount(int menu) {
        return (menus.get(menu)).getItemCount();
    }

    public void drawMenuBar(ViewScreen viewScreen, int textColor, int backgroundColor, int selectedMenu) {
        String name;
        int[] screen = viewScreen.getScreenData();
        int[] font = viewScreen.getFont();

        Arrays.fill(screen, 0, ViewScreen.WIDTH * ViewScreen.CHAR_HEIGHT, backgroundColor);

        int x = 0;
        int c = menus.size();

        if (selectedMenu >= c) {
            selectedMenu %= c;
        }

        for (int i = 0; i < c; i++) {
            name = menus.get(i).toString();
            int l = name.length();
            x += ViewScreen.CHAR_WIDTH;

            if (selectedMenu == i) {
                for (int j = 0; j < l; j++) {
                    EgaUtils.putCharacter(screen, font, name.charAt(j), x, 0, ViewScreen.WIDTH, backgroundColor, textColor, true);
                    x += ViewScreen.CHAR_WIDTH;
                }
            } else {
                for (int j = 0; j < l; j++) {
                    EgaUtils.putCharacter(screen, font, name.charAt(j), x, 0, ViewScreen.WIDTH, textColor, 0, false);
                    x += ViewScreen.CHAR_WIDTH;
                }
            }
        }

        viewScreen.putBlock(0, 0, ViewScreen.WIDTH, ViewScreen.CHAR_HEIGHT);
    }

    public void drawMenu(ViewScreen viewScreen, int textColor, int disabledColor, int backgroundColor, int selectedMenu, int selectedItem, Rectangle changedRectangle) {
        int i, c, x;

        if (selectedMenu < 0) {
            if (changedRectangle != null) {
                changedRectangle.x = 0;
                changedRectangle.y = 0;
                changedRectangle.width = 0;
                changedRectangle.height = 0;
            }
            return;
        }

        x = 0;
        c = menus.size();
        selectedMenu %= c;

        for (i = 0; i < c; i++) {
            x += ViewScreen.CHAR_WIDTH;

            if (selectedMenu == i) {
                break;
            } else {
                x += menus.get(i).toString().length() * ViewScreen.CHAR_WIDTH;
            }
        }

        (menus.get(selectedMenu)).drawMenu(viewScreen, textColor, disabledColor, backgroundColor, selectedItem, x, changedRectangle);
    }
}
