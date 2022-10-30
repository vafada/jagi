package com.sierra.agi.save;

public class ChooseSaveGameBox extends AbstractChooseBox {
    private static final String MESSAGE = "Use the arrow keys to select the slot in which you wish to save the game. Press ENTER to save in the slot, ESC to not save a game.";

    public ChooseSaveGameBox(String gameId, String path) {
        super(gameId, path);
    }

    protected String getMessage() {
        return MESSAGE;
    }
}
