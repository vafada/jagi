/**
 * LogicContext.java
 * Adventure Game Interpreter Logic Package
 * <p>
 * Created by Dr. Z.
 * Copyright (c) 2001 Dr. Z. All rights reserved.
 */

package com.sierra.agi.logic;

import com.sierra.agi.awt.EgaComponent;
import com.sierra.agi.awt.EgaEvent;
import com.sierra.agi.awt.EgaUtils;
import com.sierra.agi.debug.ExceptionDialog;
import com.sierra.agi.inv.InventoryObjects;
import com.sierra.agi.menu.AgiMenuBar;
import com.sierra.agi.res.ResourceCache;
import com.sierra.agi.res.ResourceException;
import com.sierra.agi.sound.SoundClip;
import com.sierra.agi.sound.SoundListener;
import com.sierra.agi.view.AnimatedObject;
import com.sierra.agi.view.ViewScreen;
import com.sierra.agi.view.ViewTable;
import com.sierra.agi.word.Word;
import com.sierra.agi.word.Words;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Logic Context that Logic Instruction are run with. Contains all variables
 * flags, and needed information in order to make AGI Instruction runnable.
 *
 * <P><B>Variables</B><BR>
 * On interpreter startup all variables are set to 0.</P>
 *
 * <TABLE>
 * <TR><TD VALIGN=TOP>0</TD><TD>Current room number (parameter new_room cmd), initially 0.</TD></TR>
 * <TR><TD VALIGN=TOP>1</TD><TD>Previous room number.</TD></TR>
 * <TR><TD VALIGN=TOP>2</TD><TD>Code of the border touched by Ego:<BR>
 * 0 - Touched nothing;<BR>
 * 1 - Top edge of the screen or the horizon;<BR>
 * 2 - Right edge of the screen;<BR>
 * 3 - Bottom edge of the screen;<BR>
 * 4 - Left edge of the screen.</TD></TR>
 * <TR><TD VALIGN=TOP>3</TD><TD>Current score.</TD></TR>
 * <TR><TD VALIGN=TOP>4</TD><TD>Number of object, other than Ego, that touched the border.</TD></TR>
 * <TR><TD VALIGN=TOP>5</TD><TD>The code of border touched by the object in Var (4).</TD></TR>
 * <TR><TD VALIGN=TOP>6</TD><TD>Direction of Ego's motion.<PRE>
 * 1
 * 8     |     2
 * \   |   /
 * \ | /
 * 7 ------------- 3      0 - the object
 * / | \                is motionless
 * /   |   \
 * 6     |     4
 * 5
 * </PRE></TD></TR>
 * <TR><TD VALIGN=TOP>7</TD><TD>Maximum score.</TD></TR>
 * <TR><TD VALIGN=TOP>8</TD><TD>Number of free 256-byte pages of the interpreter's memory.</TD></TR>
 * <TR><TD VALIGN=TOP>9</TD><TD>If == 0, it is the number of the word in the user message that was not found in the dictionary. (I would assume they mean "if != 0", but that's what they say. --VB)</TD></TR>
 * <TR><TD VALIGN=TOP>10</TD><TD>Time delay between interpreter cycles in 1/20 second intervals.</TD></TR>
 * <TR><TD VALIGN=TOP>11</TD><TD>Seconds (interpreter's internal clock)</TD></TR>
 * <TR><TD VALIGN=TOP>12</TD><TD>Minutes (interpreter's internal clock)</TD></TR>
 * <TR><TD VALIGN=TOP>13</TD><TD>Hours (interpreter's internal clock)</TD></TR>
 * <TR><TD VALIGN=TOP>14</TD><TD>Days (interpreter's internal clock)</TD></TR>
 * <TR><TD VALIGN=TOP>15</TD><TD>Joystick sensitivity (if Flag (8) = 1).</TD></TR>
 * <TR><TD VALIGN=TOP>16</TD><TD>ID number of the view-resource associated with Ego.</TD></TR>
 * <TR><TD VALIGN=TOP>17</TD><TD>Interpreter error code (if == 0) (Again I would expect this to say ``if != 0''. --VB)</TD></TR>
 * <TR><TD VALIGN=TOP>18</TD><TD>Additional information that goes with the error code.</TD></TR>
 * <TR><TD VALIGN=TOP>19</TD><TD>Key pressed on the keyboard.</TD></TR>
 * <TR><TD VALIGN=TOP>20</TD><TD>Computer type. For IBM-PC it is always 0.</TD></TR>
 * <TR><TD VALIGN=TOP>21</TD><TD>If Flag (15) == 0 (command reset 15 was issued) and Var (21) is not equal to 0, the window is automatically closed after 1/2 * Var (21) seconds.</TD></TR>
 * <TR><TD VALIGN=TOP>22</TD><TD>Sound generator type:<BR>
 * 1 - PC<BR>
 * 3 - Tandy</TD></TR>
 * <TR><TD VALIGN=TOP>23</TD><TD>0:F - sound volume (for Tandy).<BR>
 * <TR><TD VALIGN=TOP>24</TD><TD>This variable stores the maximum number that can be entered in the input line. By default, this variable is set to 41 (29h). (information by Dark Minister)
 * <TR><TD VALIGN=TOP>25</TD><TD>ID number of the item selected using status command or 0xFF if ESC was pressed.
 * <TR><TD VALIGN=TOP>26</TD><TD>monitor type<BR>
 * 0 - CGA<BR>
 * 2 - Hercules<BR>
 * 3 - EGA</TD></TR>
 * </TABLE>
 *
 * <P><B>Flags</B><BR>
 * On interpreter startup all flags are set to 0.</P>
 *
 * <TABLE>
 * <TR><TD VALIGN=TOP>0</TD><TD>Ego base line is completely on pixels with priority = 3 (water surface).</TD></TR>
 * <TR><TD VALIGN=TOP>1</TD><TD>Ego is invisible of the screen (completely obscured by another object).</TD></TR>
 * <TR><TD VALIGN=TOP>2</TD><TD>the player has issued a command line.</TD></TR>
 * <TR><TD VALIGN=TOP>3</TD><TD>Ego base line has touched a pixel with priority 2 (signal).</TD></TR>
 * <TR><TD VALIGN=TOP>4</TD><TD><CODE>said</CODE> command has accepted the user input.</TD></TR>
 * <TR><TD VALIGN=TOP>5</TD><TD>The new room is executed for the first time.</TD></TR>
 * <TR><TD VALIGN=TOP>6</TD><TD><CODE>restart.game</CODE> command has been executed.</TD></TR>
 * <TR><TD VALIGN=TOP>7</TD><TD>if this flag is 1, writing to the script buffer is blocked.</TD></TR>
 * <TR><TD VALIGN=TOP>8</TD><TD>if 1, Var(15) determines the joystick sensitivity.</TD></TR>
 * <TR><TD VALIGN=TOP>9</TD><TD>sound on/off.</TD></TR>
 * <TR><TD VALIGN=TOP>10</TD><TD>1 turns on the built-in debugger.</TD></TR>
 * <TR><TD VALIGN=TOP>11</TD><TD>Logic 0 is executed for the first time.</TD></TR>
 * <TR><TD VALIGN=TOP>12</TD><TD><CODE>restore.game</CODE> command has been executed.</TD></TR>
 * <TR><TD VALIGN=TOP>13</TD><TD>1 allows the <CODE>status</CODE> command to select items.</TD></TR>
 * <TR><TD VALIGN=TOP>14</TD><TD>1 allows the menu to work.</TD></TR>
 * <TR><TD VALIGN=TOP>15</TD><TD>Determines the output mode of <CODE>print</CODE> and <CODE>print.at</CODE> commands:<BR>
 * 1 - message window is left on the screen<BR>
 * 0 - message window is closed when ENTER or ESC key are pressed. If Var(21) is not 0, the window is closed automatically after 1/2 * Var(21) seconds</TD></TR>
 * </TABLE>
 *
 * @author Dr. Z
 * @version 0.00.00.01
 */
public class LogicContext extends LogicVariables implements Cloneable, Runnable {
    protected ResourceCache cache;
    protected AgiMenuBar menuBar;
    protected ViewTable viewTable;

    protected boolean playerControl;
    protected int pictureNumber;
    protected boolean graphicMode;
    protected boolean shouldShowMenu;

    protected boolean shouldShowStatusLine;

    protected volatile boolean clockActive;
    protected volatile int tickCount;
    protected volatile boolean running;

    protected Stack logicStack = new Stack();

    protected int soundNumber;
    protected SoundClip soundClip;
    protected short soundFlag;

    protected StringBuffer commandLine = new StringBuffer();
    protected String commandLineP = "";
    protected String commandLineC;
    protected boolean acceptInput;
    protected Word[] words;

    protected Map<Short, Short> keyToControllerMap = new HashMap<>();

    public LogicContext(LogicContext logicContext) {
        // Persistent Data
        System.arraycopy(flags, 0, logicContext.flags, 0, MAX_FLAGS);
        System.arraycopy(vars, 0, logicContext.vars, 0, MAX_VARS);
        System.arraycopy(strings, 0, logicContext.strings, 0, MAX_STRINGS);
        System.arraycopy(scanStarts, 0, logicContext.scanStarts, 0, MAX_LOGICS);
        System.arraycopy(objects, 0, logicContext.objects, 0, MAX_OBJECTS);
        horizon = logicContext.horizon;
        gameID = logicContext.gameID;

        // Volatile
        logicStack.addAll(logicContext.logicStack);
        cache = logicContext.cache;
    }

    public LogicContext(ResourceCache cache) {
        this.cache = cache;
        this.gameID = "";
        this.viewTable = new ViewTable(this);
    }

    public boolean said(int[] wordNumbers) {
        if (getFlag(FLAG_SAID_ACCEPTED_INPUT) || !getFlag(FLAG_ENTERED_COMMAND)) {
            return false;
        }

        if (words == null) {
            return false;
        }

        int c = 0;
        int z = 0;
        int n = words.length;
        int nwords = wordNumbers.length;
        int i = 0;

        for (; (nwords != 0) && (n != 0); c++, nwords--, n--) {
            z = wordNumbers[i++];

            switch (z) {
                case 9999:
                    nwords = 1;
                    break;
                case 1:
                    break;
                default:
                    if (words[c].number != z) {
                        return false;
                    }
                    break;
            }
        }

        /* The entry string should be entirely parsed, or last word = 9999 */
        if ((n != 0) && (z != 9999)) {
            return false;
        }

        /* The interpreter string shouldn't be entirely parsed, but next
         * word must be 9999.
         */
        if ((nwords != 0) && (wordNumbers[i] != 9999)) {
            return false;
        }

        setFlag(FLAG_SAID_ACCEPTED_INPUT, true);
        return true;
    }

    public ViewTable getViewTable() {
        return viewTable;
    }

    public ViewScreen getViewScreen() {
        return viewTable.getViewScreen();
    }

    public AgiMenuBar getMenuBar() {
        return menuBar;
    }

    public void showMenu() {
        shouldShowMenu = true;
    }

    public void showStatusLine() {
        shouldShowStatusLine = true;
        ViewScreen viewScreen = getViewScreen();
        // 15 = white
        viewScreen.clearStatusLine((short) 15);
        this.updateStatusLine();
    }

    public void hideStatusLine() {
        shouldShowStatusLine = false;
        ViewScreen viewScreen = getViewScreen();
        // 0 = black
        viewScreen.clearStatusLine((short) 0);
    }

    public boolean haveKey() {
        short key;
        EgaComponent component = getComponent();
        EgaEvent event = component.mapKeyEventToAGI(component.popCharEvent(0));

        if (event != null) {
            key = event.data;
        } else {
            key = (short) 0;
        }

        setVar(VAR_KEY, key);
        return (key != 0);
    }

    public void setError(short errorCode) {
        setVar(VAR_AGI_ERR_CODE, errorCode);
        setVar(VAR_AGI_ERR_CODE_INFO, (short) 0);

        throw new RuntimeException("AGI Error " + errorCode);
    }

    public void setError(short errorCode, short errorInfo) {
        setVar(VAR_AGI_ERR_CODE, errorCode);
        setVar(VAR_AGI_ERR_CODE_INFO, errorInfo);

        throw new RuntimeException("AGI Error " + errorCode + " (" + errorInfo + ")");
    }

    public void reset() {
        Arrays.fill(controllers, false);
        keyToControllerMap.clear();
        Arrays.fill(flags, false);
        Arrays.fill(vars, (short) 0);
        Arrays.fill(objects, (short) 0);
        Arrays.fill(strings, "");

        try {
            cache.getObjects().resetLocationTable(objects);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        vars[VAR_COMPUTER] = (short) 0;   // Computer Type (0 for PC)
        vars[VAR_FREE_PAGES] = (short) 255; // 255 Pages of 256 bytes are free.
        vars[VAR_SOUND_GENERATOR] = (short) 3;   // Tandy Compatible Sound Generator. (because we support 4 channel sound output)
        vars[VAR_MONITOR] = (short) 3;   // EGA Compatible Graphic Generator. (because we support 16 colors video output)
        vars[VAR_MAX_INPUT_CHARS] = (short) 41;
        vars[VAR_TIME_DELAY] = (short) 2;

        flags[FLAG_NEW_ROOM_EXEC] = true;
        flags[FLAG_LOGIC_ZERO_FIRSTTIME] = true;

        playerControl = true;
        pictureNumber = 0;
        horizon = DEFAULT_HORIZON;
        graphicMode = true;
        menuBar = new AgiMenuBar();

        viewTable.reset();
    }

    public String getGameName() {
        return cache.getResourceProvider().getConfiguration().name;
    }

    public boolean getPlayerControl() {
        return playerControl;
    }

    public void setPlayerControl(boolean playerControl) {
        this.playerControl = playerControl;
    }

    public EgaComponent getComponent() {
        return getViewScreen().getComponent();
    }

    public Object clone() {
        return new LogicContext(this);
    }

    public ResourceCache getCache() {
        return cache;
    }

    public void newRoom(short p) throws Exception {
        /* 1 */
        viewTable.resetNewRoom();

        /* 2 */
        /* 3 */
        /* 4 */
        /* 5 */
        AnimatedObject ego = viewTable.getEntry((short) 0);
        switch (vars[LogicContext.VAR_EGO_EDGE]) {
            case LogicContext.TOP:
                ego.setY(LogicContext.MAXY);
                break;

            case LogicContext.RIGHT:
                ego.setX(LogicContext.MINX);
                break;

            case LogicContext.BOTTOM:
                ego.setY((short) (LogicContext.HORIZON + 1));
                break;

            case LogicContext.LEFT:
                ego.setX((short) (LogicContext.MAXX + 1 - ego.getWidth()));
                break;
        }
        setHorizon(LogicContext.DEFAULT_HORIZON);

        /* 6 */
        setVar(LogicContext.VAR_PREVIOUS_ROOM, vars[LogicContext.VAR_CURRENT_ROOM]);
        setVar(LogicContext.VAR_CURRENT_ROOM, p);

        /* 7 */
        cache.loadLogic(p);

        /* 8 */

        /* 9 */
        setVar(LogicContext.VAR_EGO_EDGE, (short) 0);

        /* 10 */
        setFlag(LogicContext.FLAG_NEW_ROOM_EXEC, true);

        /* 11 */

        /* The New Room Instruction is a ideal place to force a garbage collection! */
        System.gc();

        throw new LogicExitAll();
    }

    public String processMessage(String s) {
        String b, c, e;
        int i, j, k, n, w;

        Logic logic0 = null;
        InventoryObjects objects = null;

        if (s == null) {
            return null;
        }

        // Scan %g
        while (true) {
            i = s.indexOf("%g");

            if (i == -1) {
                break;
            }

            if (logic0 == null) {
                try {
                    logic0 = cache.getLogic((short) 0);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            b = s.substring(0, i);
            i += 2;
            j = i;

            while (Character.isDigit(s.charAt(j))) {
                j++;
            }

            n = Integer.valueOf(s.substring(i, j)).intValue();
            e = s.substring(j);
            c = processMessage(logic0.getMessageProcessed(n));
            s = b + c + e;
        }

        // Scan %0
        while (true) {
            i = s.indexOf("%0");

            if (i == -1) {
                break;
            }

            if (objects == null) {
                try {
                    objects = cache.getObjects();
                } catch (Exception ex) {
                }
            }

            b = s.substring(0, i);
            i += 2;
            j = i;

            while (Character.isDigit(s.charAt(j))) {
                j++;
            }

            n = Integer.valueOf(s.substring(i, j)).intValue();
            e = s.substring(j);
            c = objects.getObject((short) n).getName();
            s = b + c + e;
        }

        // Scan %s
        while (true) {
            i = s.indexOf("%s");

            if (i == -1) {
                break;
            }

            b = s.substring(0, i);
            i += 2;
            j = i;

            while (Character.isDigit(s.charAt(j))) {
                j++;
            }

            n = Integer.valueOf(s.substring(i, j)).intValue();
            e = s.substring(j);
            c = strings[n];
            s = b + c + e;
        }

        // Scan %v
        while (true) {
            i = s.indexOf("%v");

            if (i == -1) {
                break;
            }

            b = s.substring(0, i);
            i += 2;
            j = i;

            while (Character.isDigit(s.charAt(j))) {
                j++;
            }

            if (s.charAt(j) == '|') {
                k = j + 1;

                while (Character.isDigit(s.charAt(k))) {
                    k++;
                }

                w = Integer.valueOf(s.substring(j + 1, k)).intValue();
            } else {
                k = j;
                w = 0;
            }

            n = Integer.valueOf(s.substring(i, j)).intValue();
            e = s.substring(k);
            c = String.valueOf(vars[n]);

            while (w > c.length()) {
                c = "0" + c;
            }

            s = b + c + e;
        }

        return s;
    }

    public void startClock() {
        clockActive = true;
    }

    public void stopClock() {
        clockActive = false;
    }

    protected void doDelay() {
        int delay = vars[VAR_TIME_DELAY];

        while (true) {
            if (tickCount > delay) {
                break;
            }

            try {
                if ((tickCount + 1) > delay) {
                    Thread.sleep(10);
                } else {
                    Thread.sleep(55L * (delay - tickCount));
                }
            } catch (Throwable thr) {
            }
        }

        tickCount = 0;
    }

    public final boolean isRunning() {
        return running;
    }

    protected Logic prepareRun() throws LogicException, ResourceException, IOException {
        Logic logic0;

        try {
            Thread.currentThread().setName("AGI Executer");
        } catch (Exception ex) {
        }

        reset();
        flags[FLAG_SOUND_ON] = true;

        cache.loadLogic((short) 0);
        logic0 = cache.getLogic((short) 0);

        /* Do Clean up ! */
        System.gc();
        Thread.yield();

        running = true;
        clockActive = true;
        (new Thread(new ClockTimer(), "AGI Timer")).start();

        return logic0;
    }

    public void run() {
        Logic logic0;
        short oldScore;
        boolean oldSound;
        int controller;

        try {
            logic0 = prepareRun();

            // game loop
            while (true) {
                doDelay();

                setFlag(FLAG_ENTERED_COMMAND, false);
                setFlag(FLAG_SAID_ACCEPTED_INPUT, false);

                // Normally, we should pool the joystick, but Java has no support for it.
                // poolJoystick();

                // Should give control to menu?
                if (shouldShowMenu) {
                    controller = getViewScreen().menuLoop(menuBar);

                    if (controller >= 0) {
                        setController((short) controller);
                    }

                    shouldShowMenu = false;
                }

                pollKeyboard();

                if (playerControl) {
                    setVar(VAR_EGO_DIRECTION, viewTable.getDirection((short) 0));
                } else {
                    viewTable.setDirection((short) 0, vars[VAR_EGO_DIRECTION]);
                }

                viewTable.checkAllMotion();

                oldScore = vars[VAR_SCORE];
                oldSound = flags[FLAG_SOUND_ON];

                while (true) {
                    try {
                        logic0.execute(this);
                    } catch (LogicExitAll lea) {
                        setVar(VAR_WORD_NOT_FOUND, (short) 0);
                        setVar(VAR_BORDER_TOUCHING, (short) 0);
                        setVar(VAR_BORDER_CODE, (short) 0);
                        setFlag(FLAG_ENTERED_COMMAND, false);
                        oldScore = vars[VAR_SCORE];
                        continue;
                    }

                    break;
                }

                viewTable.setDirection((short) 0, vars[VAR_EGO_DIRECTION]);

                if ((oldScore != vars[VAR_SCORE]) || (oldSound != flags[FLAG_SOUND_ON])) {
                    writeStatus();
                }

                setVar(VAR_BORDER_TOUCHING, (short) 0);
                setVar(VAR_BORDER_CODE, (short) 0);
                setFlag(FLAG_NEW_ROOM_EXEC, false);
                setFlag(FLAG_RESTART_GAME, false);
                setFlag(FLAG_RESTORE_JUST_RAN, false);
                setFlag(FLAG_LOGIC_ZERO_FIRSTTIME, false); // Not Seen in Dissassembly

                if (graphicMode) {
                    viewTable.update();
                    viewTable.doUpdate();
                }
            }
        } catch (Throwable thr) {
            ExceptionDialog.showException(thr);
        } finally {
            running = false;
        }
    }

    public void preventInput() {
        if (acceptInput) {
            acceptInput = false;
            getViewScreen().setInputLine(null);
            getComponent().clearEvents();
        }
    }

    public void acceptInput() {
        if (!acceptInput) {
            acceptInput = true;
            getViewScreen().setInputLine(commandLine.toString());
            getComponent().clearEvents();
        }
    }

    public void clearInput() {
        commandLine = new StringBuffer();
        getViewScreen().setInputLine("");
    }

    public Word[] getWords() {
        return words;
    }

    public void pollKeyboard() {
        boolean changed;

        setVar(VAR_KEY, (short) 0);
        setVar(VAR_WORD_NOT_FOUND, (short) 0);

        words = null;
        commandLineC = null;

        if (acceptInput) {
            changed = false;

            while (true) {
                short dir = (short) 0;

                KeyEvent ev = getComponent().popCharEvent(0);

                if (ev == null) {
                    break;
                }

                short keyCode = (short) ev.getKeyCode();
                short convertedKeyCode = EgaUtils.convertKey(keyCode);

                if (this.keyToControllerMap.containsKey(convertedKeyCode)) {
                    short controllerNum = this.keyToControllerMap.get(convertedKeyCode);
                    this.controllers[controllerNum] = true;
                }

                switch (keyCode) {
                    case 8:
                    case KeyEvent.VK_BACK_SLASH:
                    case KeyEvent.VK_DELETE:
                        if (commandLine.length() > 0) {
                            commandLine.deleteCharAt(commandLine.length() - 1);
                            changed = true;
                        }
                        break;

                    case KeyEvent.VK_SPACE:
                        commandLine.append(ev.getKeyChar());
                        changed = true;
                        break;

                    case KeyEvent.VK_UP:
                    case KeyEvent.VK_NUMPAD8:
                        dir = (short) 1;
                        break;

                    case KeyEvent.VK_PAGE_UP:
                    case KeyEvent.VK_NUMPAD9:
                        dir = (short) 2;
                        break;

                    case KeyEvent.VK_RIGHT:
                    case KeyEvent.VK_NUMPAD6:
                        dir = (short) 3;
                        break;

                    case KeyEvent.VK_PAGE_DOWN:
                    case KeyEvent.VK_NUMPAD3:
                        dir = (short) 4;
                        break;

                    case KeyEvent.VK_DOWN:
                    case KeyEvent.VK_NUMPAD2:
                        dir = (short) 5;
                        break;

                    case KeyEvent.VK_END:
                    case KeyEvent.VK_NUMPAD1:
                        dir = (short) 6;
                        break;

                    case KeyEvent.VK_LEFT:
                    case KeyEvent.VK_NUMPAD4:
                        dir = (short) 7;
                        break;

                    case KeyEvent.VK_HOME:
                    case KeyEvent.VK_NUMPAD7:
                        dir = (short) 8;
                        break;

                    case KeyEvent.VK_NUMPAD5:
                        dir = (short) -1;
                        break;
                }

                if ((ev.getKeyCode() >= KeyEvent.VK_A) && (ev.getKeyCode() <= KeyEvent.VK_Z)) {
                    commandLine.append(ev.getKeyChar());
                    changed = true;
                }

                if (ev.getKeyCode() == KeyEvent.VK_ENTER) {
                    commandLineC = commandLine.toString();
                    commandLine = new StringBuffer();
                    changed = true;
                    break;
                }

                if ((dir != 0) && playerControl) {
                    if (dir < 0) {
                        dir = 0;
                    }

                    AnimatedObject entry = getViewTable().getEntry(ViewTable.EGO_ENTRY);
                    entry.setDirection(entry.getDirection() == dir ? (short) 0 : dir);
                }
            }

            if (commandLineC != null) {
                // User said something!
                enterCommand(commandLineC);
            }

            if (changed) {
                getViewScreen().setInputLine(commandLine.toString());
            }
        }
    }

    public void enterCommand(String command) {
        try {
            Words words = getCache().getWords();
            List w = words.parse(command);

            if ((w != null) && (w.size() != 0)) {
                this.words = new Word[w.size()];
                w.toArray(this.words);

                setFlag(FLAG_ENTERED_COMMAND, true);
                setFlag(FLAG_SAID_ACCEPTED_INPUT, false);
            }
        } catch (IOException ioex) {
            ioex.printStackTrace();
        } catch (ResourceException rex) {
            rex.printStackTrace();
        }
    }

    public void writeStatus() {
    }

    public void pushLogic(Object logicInfo) {
        logicStack.push(logicInfo);
    }

    public void pushLogic(short logicNumber) {
        logicStack.push(logicNumber);
    }

    public Object peekLogic() {
        return logicStack.peek();
    }

    public Object popLogic() {
        return logicStack.pop();
    }

    public Object[] getLogicStack() {
        return logicStack.toArray();
    }

    public void playSound(short sound, short flag) throws ResourceException, IOException {
        if (soundClip != null) {
            setFlag(soundFlag, true);
            soundClip.stop();
        }

        while (soundClip != null) {
            Thread.yield();
        }

        setFlag(flag, false);

        soundNumber = sound;
        soundFlag = flag;
        soundClip = cache.getSound(sound).createClip();
        playSound();
    }

    public void playSound() {
        if (soundClip != null) {
            soundClip.addSoundListener(new SoundAdapter());
            soundClip.play();
        }
    }

    public void stopSound() {
        if (soundClip != null) {
            if (clockActive) {
                setFlag(soundFlag, true);
            }

            soundClip.stop();
        }
    }

    public void addKeyToController(short keyCode, short controllerNum) {
        this.keyToControllerMap.put(keyCode, controllerNum);
    }

    public void updateStatusLine() {
        if (this.shouldShowStatusLine) {
            ViewScreen viewScreen = getViewScreen();
            // 15 = white
            viewScreen.clearStatusLine((short) 15);

            StringBuilder scoreStatus = new StringBuilder();
            scoreStatus.append(" Score:");
            scoreStatus.append(vars[VAR_SCORE]);
            scoreStatus.append(" of ");
            scoreStatus.append(vars[VAR_MAX_SCORE]);
            String scoreString = String.format("%-30s", scoreStatus.toString());

            StringBuilder soundStatus = new StringBuilder();
            soundStatus.append("Sound:");
            soundStatus.append(flags[FLAG_SOUND_ON] ? "on" : "off");
            String soundString = String.format("%-10s", soundStatus.toString());

            viewScreen.displayStatusLine(scoreString + soundString);
        }
    }

    // ** Execution ***************************
    public final class ClockTimer implements Runnable {
        public void run() {
            int tickSecond = 0;

            while (running) {
                try {
                    /* Interrupt 1C in 8086 is called exactly 18.2 times per second (54.94505 ms)
                       which is used has a base timer resolution for AGI games. */

                    Thread.sleep(55);
                } catch (Throwable thr) {
                }

                if (clockActive) {
                    tickSecond++;
                    tickCount++;

                    if (tickSecond >= 20) {
                        tickSecond = 0;

                        if (vars[VAR_SECONDS]++ > 59) {
                            vars[VAR_SECONDS] = 0;
                            vars[VAR_MINUTES]++;
                        }

                        if (vars[VAR_MINUTES] > 59) {
                            vars[VAR_MINUTES] = 0;
                            vars[VAR_HOURS]++;
                        }

                        if (vars[VAR_HOURS] > 23) {
                            vars[VAR_HOURS] = 0;
                            vars[VAR_DAYS]++;
                        }
                    }
                }
            }
        }
    }

    /* Sound */
    public class SoundAdapter implements SoundListener {
        public void soundStarted(SoundClip soundClip) {
        }

        public void soundStopped(SoundClip soundClip, byte reason) {
            if (reason == STOP_REASON_FINISHED) {
                setFlag(soundFlag, true);
            }

            soundClip.removeSoundListener(this);
            LogicContext.this.soundClip = null;
        }

        public void soundVolumeChanged(SoundClip soundClip, int volume) {
        }
    }
}
