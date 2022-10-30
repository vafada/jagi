package com.sierra.agi.save;

public class ChooseRestoreGameBox extends AbstractChooseBox {

    private static final String MESSAGE = "Use the arrow keys to select the game which you wish to restore. Press ENTER to restore the game, ESC to not restore a game.";


    public ChooseRestoreGameBox(String gameId, String path) {
        super(gameId, path);
    }

    @Override
    protected String getMessage() {
        return MESSAGE;
    }
}
