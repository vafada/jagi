package com.sierra.agi.inv;

import com.sierra.agi.awt.EgaComponent;
import com.sierra.agi.logic.LogicContext;
import com.sierra.agi.view.ViewScreen;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import static com.sierra.agi.logic.LogicVariables.EGO_OWNED;
import static com.sierra.agi.logic.LogicVariables.FLAG_STATUS_SELECTS_ITEMS;
import static com.sierra.agi.logic.LogicVariables.VAR_SEL_ITEM;

public class InventoryScreen {
    private final LogicContext logicContext;

    public InventoryScreen(LogicContext logicContext) {
        this.logicContext = logicContext;
    }

    public void show() throws Exception {
        ViewScreen viewScreen = this.logicContext.getViewScreen();
        viewScreen.save();

        viewScreen.textMode((byte) 15);

        List<InvItem> invItems = new ArrayList<>();
        int selectedItemIndex = 0;
        int howMany = 0;
        int row = 2;

        InventoryObjects objects = this.logicContext.getCache().getObjects();

        short[] agiObjects = this.logicContext.getObjects();

        for (short i = 0; i < agiObjects.length; i++) {
            short agiObjectLocation = agiObjects[i];
            if (agiObjectLocation == EGO_OWNED) {
                InvItem invItem = new InvItem();
                invItem.num = i;
                invItem.name = objects.getObject(i).getName();
                invItem.row = row;

                if ((howMany & 1) == 0) {
                    invItem.col = 1;
                } else {
                    row++;
                    invItem.col = 39 - invItem.name.length();
                }

                if (i == logicContext.getVar(VAR_SEL_ITEM)) {
                    selectedItemIndex = invItems.size();
                }

                invItems.add(invItem);
                howMany++;
            }
        }

        // If no objects in inventory, then say so.
        if (howMany == 0) {
            InvItem invItem = new InvItem();
            invItem.num = 0;
            invItem.name = "nothing";
            invItem.row = row;
            invItem.col = 16;
            invItems.add(invItem);
        }

        // Display the inventory items.
        drawInventoryItems(invItems, invItems.get(selectedItemIndex));

        EgaComponent ega = viewScreen.getComponent();
        ega.clearEvents();

        KeyEvent ev;

        if (!logicContext.getFlag(FLAG_STATUS_SELECTS_ITEMS)) {
            if ((ega.popCharEvent(-1)) == null) {
                // noop
            }
        } else {
            while (true) {
                if ((ev = ega.popCharEvent(-1)) == null) {
                    break;
                }

                int key = ev.getKeyCode();
                if (key == KeyEvent.VK_ESCAPE) {
                    logicContext.setVar(VAR_SEL_ITEM, (short) 0xFF);
                    break;
                } else if (key == KeyEvent.VK_ENTER) {
                    logicContext.setVar(VAR_SEL_ITEM, invItems.get(selectedItemIndex).num);
                    break;
                } else if (key == KeyEvent.VK_UP || key == KeyEvent.VK_DOWN || key == KeyEvent.VK_LEFT || key == KeyEvent.VK_RIGHT) {
                    selectedItemIndex = moveSelect(invItems, key, selectedItemIndex);
                }
            }
        }

        viewScreen.restore(true);
    }

    private void drawInventoryItems(List<InvItem> invItems, InvItem selectedItem) {
        logicContext.getViewScreen().displayLine(11, 0, "You are carrying:", (byte) 0, (byte) 15);

        for (InvItem invItem : invItems) {
            if ((invItem == selectedItem) && logicContext.getFlag(FLAG_STATUS_SELECTS_ITEMS)) {
                logicContext.getViewScreen().displayLine(invItem.col, invItem.row, invItem.name, (byte) 15, (byte) 0);
            } else {
                logicContext.getViewScreen().displayLine(invItem.col, invItem.row, invItem.name, (byte) 0, (byte) 15);
            }
        }

        if (logicContext.getFlag(FLAG_STATUS_SELECTS_ITEMS)) {
            logicContext.getViewScreen().displayLine(2, 24, "Press ENTER to select, ESC to cancel", (byte) 0, (byte) 15);
        } else {
            logicContext.getViewScreen().displayLine(4, 24, "Press a key to return to the game", (byte) 0, (byte) 15);
        }
    }

    private int moveSelect(List<InvItem> invItems, int dirKey, int oldSelectedItemIndex) {
        int newSelectedItemIndex = oldSelectedItemIndex;

        switch (dirKey) {
            case KeyEvent.VK_UP:
                newSelectedItemIndex -= 2;
                break;
            case KeyEvent.VK_RIGHT:
                newSelectedItemIndex += 1;
                break;
            case KeyEvent.VK_DOWN:
                newSelectedItemIndex += 2;
                break;
            case KeyEvent.VK_LEFT:
                newSelectedItemIndex -= 1;
                break;
        }

        if ((newSelectedItemIndex < 0) || (newSelectedItemIndex >= invItems.size())) {
            newSelectedItemIndex = oldSelectedItemIndex;
        } else {
            InvItem previousItem = invItems.get(oldSelectedItemIndex);
            InvItem newItem = invItems.get(newSelectedItemIndex);

            logicContext.getViewScreen().displayLine(previousItem.col, previousItem.row, previousItem.name, (byte) 0, (byte) 15);
            logicContext.getViewScreen().displayLine(newItem.col, newItem.row, newItem.name, (byte) 15, (byte) 0);
        }

        return newSelectedItemIndex;
    }

    class InvItem {
        public short num;
        public String name;
        public int row;
        public int col;
    }
}
