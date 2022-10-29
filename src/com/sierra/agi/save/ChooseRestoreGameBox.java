package com.sierra.agi.save;

import com.sierra.agi.awt.EgaComponent;
import com.sierra.agi.awt.EgaUtils;
import com.sierra.agi.logic.LogicContext;
import com.sierra.agi.view.Box;
import com.sierra.agi.view.SavedGame;
import com.sierra.agi.view.ViewScreen;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import static com.sierra.agi.save.SaveUtils.NUM_GAMES;
import static com.sierra.agi.save.SaveUtils.POINTER_CHAR;

public class ChooseRestoreGameBox {
    private static final int MAX_COLUMN = 35;
    private static final String MESSAGE = "Use the arrow keys to select the game which you wish to restore. Press ENTER to restore the game, ESC to not restore a game.";

    private String[] lines;
    private int x = -1;
    private int y = -1;
    private String gameId;
    private String path;

    private SavedGame[] savedGames = new SavedGame[NUM_GAMES];
    private int pointerIndex;

    public ChooseRestoreGameBox(String gameId, String path) {
        this.gameId = gameId;
        this.path = path;
        init();
    }

    private void init() {
        List lines = new ArrayList<String>();

        StringBuffer current = new StringBuffer();
        StringTokenizer words = new StringTokenizer(MESSAGE, " ", true);

        while (words.hasMoreTokens()) {
            String word = words.nextToken();

            if ((current.length() + word.length()) > MAX_COLUMN) {
                lines.add(current.toString());
                current = new StringBuffer();
            }

            if (word.equals(" ") && (current.length() == 0)) {
                continue;
            }

            current.append(word);
        }

        lines.add(current.toString());
        // spacer
        lines.add("");

        long mostRecentTime = 0;

        for (int i = 0; i < NUM_GAMES; i++) {
            savedGames[i] = getGameByNumber(i + 1);

            if (savedGames[i].exists) {
                if (savedGames[i].fileTime > mostRecentTime) {
                    mostRecentTime = savedGames[i].fileTime;
                    this.pointerIndex = i;
                }

                lines.add(" - " + savedGames[i].description);
            } else {
                lines.add(" - ");
            }
        }

        lines.toArray(this.lines = new String[lines.size()]);

    }

    public SavedGame show(LogicContext logicContext, ViewScreen viewScreen, boolean modal) {
        KeyEvent ev;
        SavedGame returnedGame = null;

        if (logicContext != null) {
            logicContext.stopClock();
        }

        viewScreen.save();

        draw(viewScreen);

        EgaComponent ega = viewScreen.getComponent();
        boolean looping = true;

        ega.clearEvents();

        do {
            if ((ev = ega.popCharEvent(-1)) == null) {
                break;
            }

            switch (ev.getKeyCode()) {
                case KeyEvent.VK_ENTER:
                    if (this.savedGames[pointerIndex].exists) {
                        returnedGame = this.savedGames[pointerIndex];
                    }
                    looping = false;
                    break;
                case KeyEvent.VK_ESCAPE:
                    looping = false;
                    break;
                case KeyEvent.VK_UP:
                    this.pointerIndex--;
                    if (this.pointerIndex < 0) {
                        this.pointerIndex = 0;
                    } else {
                        draw(viewScreen);
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    this.pointerIndex++;
                    if (this.pointerIndex == 12) {
                        this.pointerIndex = 11;
                    } else {
                        draw(viewScreen);
                    }
                    break;
            }
        } while (looping);

        viewScreen.restore(true);

        if (logicContext != null) {
            logicContext.startClock();
        }

        return returnedGame;
    }

    public void draw(ViewScreen viewScreen) {
        int x = this.x;
        int y = this.y;
        int width = ViewScreen.CHAR_WIDTH * (MAX_COLUMN + 2);
        int height = ViewScreen.CHAR_HEIGHT * (lines.length + 2);
        int textColor = viewScreen.translatePixel(Color.black);
        int backColor = viewScreen.translatePixel(Color.white);
        int borderColor = viewScreen.translatePixel(Color.red.darker());
        int[] screen = viewScreen.getScreenData();
        int[] font = viewScreen.getFont();

        if (x < 0) {
            x = (ViewScreen.WIDTH - width) / 2;
        }

        if (y < 0) {
            y = (ViewScreen.HEIGHT - height) / 2;
        }

        int oy = y;
        int end = x + width - ViewScreen.CHAR_WIDTH;

        viewScreen.drawTopLine(borderColor, backColor, x, y, width);

        y += ViewScreen.CHAR_HEIGHT;

        int pointerYStart = y + (ViewScreen.CHAR_HEIGHT * 5);
        for (int line = 0; line < lines.length; line++) {
            String text = lines[line];
            int textLength = text.length();
            int textEnd = x + ((textLength + 1) * ViewScreen.CHAR_WIDTH);

            viewScreen.drawLeftLine(borderColor, backColor, x, y);

            EgaUtils.putString(screen, font, text, x + ViewScreen.CHAR_WIDTH, y, ViewScreen.WIDTH, textColor, backColor, true);

            if (end != textEnd) {
                viewScreen.drawBlanks(backColor, textEnd, y, end - textEnd);
            }

            viewScreen.drawRightLine(borderColor, backColor, end, y);
            y += ViewScreen.CHAR_HEIGHT;
        }

        int pointerY = (pointerYStart + (ViewScreen.CHAR_HEIGHT * this.pointerIndex));
        EgaUtils.putString(screen, font, new String(POINTER_CHAR), 10 + ViewScreen.CHAR_WIDTH, pointerY, ViewScreen.WIDTH, textColor, backColor, true);

        viewScreen.drawBottomLine(borderColor, backColor, x, y, width);
        viewScreen.putBlock(x, oy, width, height);

    }

    private SavedGame getGameByNumber(int num) {
        SavedGame theGame = new SavedGame();
        theGame.num = num;

        // Build full path to the saved game of this number for this game ID.
        theGame.fileName = String.format("%sSG.%d", this.gameId, num);

        try {
            Path path = Paths.get(this.path, theGame.fileName);
            byte[] rawData = Files.readAllBytes(path);
            theGame.savedGameData = rawData;

            // Get last modified time
            FileTime fileTime = Files.getLastModifiedTime(path);
            theGame.fileTime = fileTime.toMillis();

            // 0 - 30(31 bytes) SAVED GAME DESCRIPTION.
            int textEnd = 0;
            while (theGame.savedGameData[textEnd] != 0) {
                textEnd++;
            }
            theGame.description = new String(rawData, 0, textEnd, "US-ASCII");

            // 33 - 39(7 bytes) Game ID("SQ2", "KQ3", "LLLLL", etc.), NUL padded.
            textEnd = 33;
            while ((theGame.savedGameData[textEnd] != 0) && ((textEnd - 33) < 7)) {
                textEnd++;
            }
            String gameId = new String(theGame.savedGameData, 33, textEnd - 33, "US-ASCII");

            // If the saved Game ID  doesn't match the current, don't use  this game.
            if (!gameId.equals(this.gameId)) {
                theGame.description = "";
                theGame.exists = false;
                return theGame;
            }

            // If we get this far, there is a valid saved game with this number for this game.
            theGame.exists = true;
            return theGame;
        } catch (Exception ex) {
            // Something unexpected happened. Bad file I guess. Return false.
            theGame.description = "";
            theGame.exists = false;
            return theGame;
        }
    }
}
