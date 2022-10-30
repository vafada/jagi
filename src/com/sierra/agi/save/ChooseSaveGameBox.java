package com.sierra.agi.save;

import com.sierra.agi.awt.EgaComponent;
import com.sierra.agi.awt.EgaUtils;
import com.sierra.agi.logic.LogicContext;
import com.sierra.agi.view.SavedGame;
import com.sierra.agi.view.ViewScreen;

import java.awt.Color;
import java.awt.event.KeyEvent;

import static com.sierra.agi.save.SaveUtils.POINTER_CHAR;

public class ChooseSaveGameBox extends AbstractChooseBox {
    private static final String MESSAGE = "Use the arrow keys to select the slot in which you wish to save the game. Press ENTER to save in the slot, ESC to not save a game.";

    public ChooseSaveGameBox(String gameId, String path) {
        super(gameId, path);
    }

    protected String getMessage() {
        return MESSAGE;
    }
}
