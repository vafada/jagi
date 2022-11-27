package com.sierra.agi.save;

import com.sierra.agi.inv.InventoryObjects;
import com.sierra.agi.logic.LogicContext;
import com.sierra.agi.logic.LogicVariables;
import com.sierra.agi.view.AnimatedObject;
import com.sierra.agi.view.ScriptBuffer;
import com.sierra.agi.view.ViewScreen;
import com.sierra.agi.view.ViewTable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;

import static com.sierra.agi.logic.LogicVariables.MAX_STRINGS;
import static com.sierra.agi.logic.LogicVariables.STRING_LENGTH;

public class SaveGame {
    private LogicContext logicContext;

    public SaveGame(LogicContext logicContext) {
        this.logicContext = logicContext;
    }



    private int getSaveVariablesLength(String version) {
        switch (version) {
            case "2.089":
            case "2.272":
            case "2.277":
                return 0x03DB;

            case "2.411":
            case "2.425":
            case "2.426":
            case "2.435":
            case "2.439":
            case "2.440":
                return 0x05DF;

            case "3.002.102":
            case "3.002.107":
                // TODO: Not yet sure what the additional 3 bytes are used for.
                return 0x05E4;

            case "3.002.149":
                // This difference between 3.002.107 and 3.002.149 is that the latter has only 12 strings (12x40=480=0x1E0)
                return 0x0404;

            // Default covers all the 2.9XX versions, 3.002.086 and 3.002.098.
            default:
                return 0x05E1;
        }
    }

    public void save(int slotNumber, String description) throws Exception {
        String absolutePath = logicContext.getCache().getPath().getAbsolutePath();

        // No saved game will ever be as big as 20000, but we put that as a theoretical lid
        // on the size based on rough calculations with all parts set to maximum size. We'll
        // only write the bytes that use when created the file.
        byte[] savedGameData = new byte[20000];
        int pos = 0;

        // 0 - 30(31 bytes) SAVED GAME DESCRIPTION.
        byte[] descBytes = description.getBytes();
        for (int i = 0; i < descBytes.length; i++) {
            savedGameData[pos++] = descBytes[i];
        }

        // FIRST PIECE: SAVE VARIABLES
        // [0] 31 - 32(2 bytes) Length of save variables piece. Length depends on AGI interpreter version. We use 0xE1 0x05
        int saveVarsLength = getSaveVariablesLength(logicContext.getVersion());
        int aniObjsOffset = 33 + saveVarsLength;
        savedGameData[31] = (byte) (saveVarsLength & 0xFF);
        savedGameData[32] = (byte) ((saveVarsLength >> 8) & 0xFF);

        // [2] 33 - 39(7 bytes) Game ID("SQ2", "KQ3", "LLLLL", etc.), NUL padded.
        pos = 33;
        byte[] gameIdBytes = logicContext.getGameID().getBytes();
        for (int i = 0; i < gameIdBytes.length; i++) {
            savedGameData[pos++] = gameIdBytes[i];
        }

        // [9] 40 - 295(256 bytes) Variables, 1 variable per byte
        for (short i = 0; i < 256; i++) {
            savedGameData[40 + i] = (byte) logicContext.getVar(i);
        }

        // [265] 296 - 327(32 bytes) Flags, 8 flags per byte
        pos = 296;
        for (short i = 0; i < 256; i += 8) {
            savedGameData[pos++] = (byte) (
                    (logicContext.getFlag((short) (i + 0)) ? 0x80 : 0x00) | (logicContext.getFlag((short) (i + 1)) ? 0x40 : 0x00) |
                            (logicContext.getFlag((short) (i + 2)) ? 0x20 : 0x00) | (logicContext.getFlag((short) (i + 3)) ? 0x10 : 0x00) |
                            (logicContext.getFlag((short) (i + 4)) ? 0x08 : 0x00) | (logicContext.getFlag((short) (i + 5)) ? 0x04 : 0x00) |
                            (logicContext.getFlag((short) (i + 6)) ? 0x02 : 0x00) | (logicContext.getFlag((short) (i + 7)) ? 0x01 : 0x00));
        }

        // [297] 328 - 331(4 bytes) Clock ticks since game started. 1 clock tick == 50ms.
        // TODO: int saveGameTicks = (int) (state.TotalTicks / 3);
        int saveGameTicks = 0;
        savedGameData[328] = (byte) (saveGameTicks & 0xFF);
        savedGameData[329] = (byte) ((saveGameTicks >> 8) & 0xFF);
        savedGameData[330] = (byte) ((saveGameTicks >> 16) & 0xFF);
        savedGameData[331] = (byte) ((saveGameTicks >> 24) & 0xFF);

        // [301] 332 - 333(2 bytes) Horizon
        savedGameData[332] = (byte) (logicContext.getHorizon() & 0xFF);
        savedGameData[333] = (byte) ((logicContext.getHorizon() >> 8) & 0xFF);

        // [303] 334 - 335(2 bytes) Key Dir
        // TODO: Not entirely sure what this is for, so not currently saving this.

        ViewTable viewTable = logicContext.getViewTable();

        // Currently active block.
        // [305] 336 - 337(2 bytes) Upper left X position for active block.
        savedGameData[336] = (byte) (viewTable.getBlockUpperLeftX() & 0xFF);
        savedGameData[337] = (byte) ((viewTable.getBlockUpperLeftX() >> 8) & 0xFF);
        // [307] 338 - 339(2 bytes) Upper Left Y position for active block.
        savedGameData[338] = (byte) (viewTable.getBlockUpperLeftY() & 0xFF);
        savedGameData[339] = (byte) ((viewTable.getBlockUpperLeftY() >> 8) & 0xFF);
        // [309] 340 - 341(2 bytes) Lower Right X position for active block.
        savedGameData[340] = (byte) (viewTable.getBlockLowerRightX() & 0xFF);
        savedGameData[341] = (byte) ((viewTable.getBlockLowerRightX() >> 8) & 0xFF);
        // [311] 342 - 343(2 bytes) Lower Right Y position for active block.
        savedGameData[342] = (byte) (viewTable.getBlockLowerRightY() & 0xFF);
        savedGameData[343] = (byte) ((viewTable.getBlockLowerRightY() >> 8) & 0xFF);

        // [313] 344 - 345(2 bytes) Player control (1) / Program control (0)
        savedGameData[344] = (byte) (logicContext.getPlayerControl() ? 1 : 0);
        // [315] 346 - 347(2 bytes) Current PICTURE number
        savedGameData[346] = (byte) logicContext.getPictureNumber();
        // savedGameData[346] = (byte) viewTable.getPictureContext().;
        // [317] 348 - 349(2 bytes) Blocking flag (1 = true, 0 = false)
        savedGameData[348] = (byte) (viewTable.isBlockSet() ? 1 : 0);

        // [319] 350 - 351(2 bytes) Max drawn. Always set to 15. Maximum number of animated objects that can be drawn at a time. Set by old max.drawn command in AGI v2.001.
        savedGameData[350] = (byte) 15;
        // [321] 352 - 353(2 bytes) Script size. Set by script.size. Max number of script event items. Default is 50.
        savedGameData[352] = (byte) logicContext.getScriptBuffer().getScriptSize();
        // [323] 354 - 355(2 bytes) Current number of script event entries.
        savedGameData[354] = (byte) logicContext.getScriptBuffer().getScriptEntries();

        // [325] 356 - 555(200 or 160 bytes) ? Key to controller map (4 bytes each). Earlier versions had less entries.
        pos = 356;
        Set<Map.Entry<Short, Short>> keyToControllerEntrySet = logicContext.getKeyToControllerMap().entrySet();
        for (Map.Entry<Short, Short> entry : keyToControllerEntrySet) {
            if (entry.getKey() != 0) {
                int keyCode = entry.getKey();
                short controllerNum = entry.getValue();
                savedGameData[pos++] = (byte) (keyCode & 0xFF);
                savedGameData[pos++] = (byte) ((keyCode >> 8) & 0xFF);
                savedGameData[pos++] = (byte) (controllerNum & 0xFF);
                savedGameData[pos++] = (byte) ((controllerNum >> 8) & 0xFF);
            }
        }

        int keyMapSize = SaveUtils.getNumberOfControllers(logicContext.getVersion());
        int postKeyMapOffset = 356 + (keyMapSize << 2);

        // [525] 556 - 1515(480 or 960 bytes) 12 or 24 strings, each 40 bytes long. For 2.4XX to 2.9XX, it was 24 strings.
        for (int i = 0; i < MAX_STRINGS; i++) {
            pos = 556 + (i * STRING_LENGTH);
            if ((logicContext.getString((short) i) != null) && (logicContext.getString((short) i).length() > 0)) {
                String s = logicContext.getString((short) i);
                byte[] stringBytes = s.getBytes();
                for (int j = 0; j < stringBytes.length; j++) {
                    savedGameData[pos++] = stringBytes[j];
                }
            }
        }

        int numOfStrings = SaveUtils.getNumberOfStrings(logicContext.getVersion());
        int postStringsOffset = postKeyMapOffset + (numOfStrings * STRING_LENGTH);
        ViewScreen viewScreen = viewTable.getViewScreen();

        // [1485] 1516(2 bytes) Foreground colour
        savedGameData[postStringsOffset + 0] = viewScreen.getForegroundColorByte();
        // [1487] 1518(2 bytes) Background colour
        savedGameData[postStringsOffset + 2] = viewScreen.getBackgroundColorByte();
        // [1489] 1520(2 bytes) Text Attribute value (combined foreground/background value)
        // TODO
        //int textAttribute = (savedGameData[postStringsOffset + 4] + (savedGameData[postStringsOffset + 5] << 8));
        // [1491] 1522(2 bytes) Accept input = 1, Prevent input = 0
        savedGameData[postStringsOffset + 6] = (byte) (logicContext.isAcceptInput() ? 1 : 0);

        // [1493] 1524(2 bytes) User input row on the screen
        savedGameData[postStringsOffset + 8] = (byte) (viewScreen.getLineUserInput());

        // [1495] 1526(2 bytes) Cursor character
        savedGameData[postStringsOffset + 10] = (byte) viewScreen.getCursorChar();

        // [1497] 1528(2 bytes) Show status line = 1, Don't show status line = 0
        savedGameData[postStringsOffset + 12] = (byte) (logicContext.isShouldShowStatusLine() ? 1 : 0);

        // [1499] 1530(2 bytes) Status line row on the screen
        savedGameData[postStringsOffset + 14] = (byte) viewScreen.getLineStatus();

        // [1501] 1532(2 bytes) Picture top row on the screen
        savedGameData[postStringsOffset + 16] = (byte) viewScreen.getLineMinPrint();

        // [1503] 1534(2 bytes) Picture bottom row on the screen
        savedGameData[postStringsOffset + 18] = (byte) (viewScreen.getLineMinPrint() + 21);

        // [1505] 1536(2 bytes) Stores a pushed position within the script event list
        // Note: Depends on interpreter version. 2.4xx and below didn't have push.script/pop.script, so they didn't have this saved game field.
        if ((postStringsOffset + 20) < aniObjsOffset) {
            // The spec is 2 bytes, but as with the fields above, there shouldn't be more than 255.
            savedGameData[1536] = (byte)(logicContext.getScriptBuffer().getSavedScript());
        }

        // SECOND PIECE: ANIMATED OBJECT STATE
        // 1538 - 1539(2 bytes) Length of piece
        // Each ANIOBJ entry is 0x2B in length, i.e. 43 bytes.
        InventoryObjects objects = logicContext.getCache().getObjects();
        AnimatedObject[] allAnimatedObjects = viewTable.getAnimatedObjects();
        int aniObjectsLength = ((objects.getNumOfAnimatedObjects() + 1) * 0x2B);
        savedGameData[aniObjsOffset + 0] = (byte) (aniObjectsLength & 0xFF);
        savedGameData[aniObjsOffset + 1] = (byte) ((aniObjectsLength >> 8) & 0xFF);

        for (int i = 0; i < (objects.getNumOfAnimatedObjects() + 1); i++) {
            int aniObjOffset = aniObjsOffset + 2 + (i * 0x2B);
            AnimatedObject aniObj = allAnimatedObjects[i];

            //UBYTE movefreq;     /* number of animation cycles between motion  */    e.g.   01
            savedGameData[aniObjOffset + 0] = (byte) aniObj.getStepTime();
            //UBYTE moveclk;      /* number of cycles between moves of object   */    e.g.   01
            savedGameData[aniObjOffset + 1] = (byte) aniObj.getStepTimeCount();
            //UBYTE num;          /* object number                              */    e.g.   00
            savedGameData[aniObjOffset + 2] = (byte) aniObj.getObjectNumber();
            //COORD x;            /* current x coordinate                       */    e.g.   6e 00 (0x006e = )
            savedGameData[aniObjOffset + 3] = (byte) (aniObj.getX() & 0xFF);
            savedGameData[aniObjOffset + 4] = (byte) ((aniObj.getX() >> 8) & 0xFF);
            //COORD y;            /* current y coordinate                       */    e.g.   64 00 (0x0064 = )
            savedGameData[aniObjOffset + 5] = (byte) (aniObj.getY() & 0xFF);
            savedGameData[aniObjOffset + 6] = (byte) ((aniObj.getY() >> 8) & 0xFF);
            //UBYTE view;         /* current view number                        */    e.g.   00
            savedGameData[aniObjOffset + 7] = (byte) aniObj.getCurrentView();
            //VIEW* viewptr;      /* pointer to current view                    */    e.g.   17 6b (0x6b17 = ) IGNORE.
            //UBYTE loop;         /* current loop in view                       */    e.g.   00
            savedGameData[aniObjOffset + 10] = (byte) aniObj.getCurrentLoop();
            //UBYTE loopcnt;      /* number of loops in view                    */    e.g.   04
            if (aniObj.getViewData() != null) {
                savedGameData[aniObjOffset + 11] = (byte) aniObj.getViewData().getLoopCount();
            }
            //LOOP* loopptr;      /* pointer to current loop                    */    e.g.   24 6b (0x6b24 = ) IGNORE
            //UBYTE cel;          /* current cell in loop                       */    e.g.   00
            savedGameData[aniObjOffset + 14] = (byte) aniObj.getCurrentCell();
            //UBYTE celcnt;       /* number of cells in current loop            */    e.g.   06
            if (aniObj.getViewData() != null) {
                savedGameData[aniObjOffset + 15] = (byte) aniObj.getViewData().getLoop(aniObj.getCurrentLoop()).getCellCount();
            }
            //CEL* celptr;        /* pointer to current cell                    */    e.g.   31 6b (0x6b31 = ) IGNORE
            //CEL* prevcel;       /* pointer to previous cell                   */    e.g.   31 6b (0x6b31 = ) IGNORE
            //STRPTR save;        /* pointer to background save area            */    e.g.   2f 9c (0x9c2f = ) IGNORE
            //COORD prevx;        /* previous x coordinate                      */    e.g.   6e 00 (0x006e = )
            savedGameData[aniObjOffset + 22] = (byte) (aniObj.getxCopy() & 0xFF);
            savedGameData[aniObjOffset + 23] = (byte) ((aniObj.getxCopy() >> 8) & 0xFF);
            //COORD prevy;        /* previous y coordinate                      */    e.g.   64 00 (0x0064 = )
            savedGameData[aniObjOffset + 24] = (byte) (aniObj.getyCopy() & 0xFF);
            savedGameData[aniObjOffset + 25] = (byte) ((aniObj.getyCopy() >> 8) & 0xFF);
            //COORD xsize;        /* x dimension of current cell                */    e.g.   06 00 (0x0006 = )
            if (aniObj.getViewData() != null) {
                savedGameData[aniObjOffset + 26] = (byte) (aniObj.getWidth() & 0xFF);
            }
            if (aniObj.getViewData() != null) {
                savedGameData[aniObjOffset + 27] = (byte) ((aniObj.getWidth() >> 8) & 0xFF);
            }
            //COORD ysize;        /* y dimension of current cell                */    e.g.   20 00 (0x0020 = )
            if (aniObj.getViewData() != null) {
                savedGameData[aniObjOffset + 28] = (byte) (aniObj.getHeight() & 0xFF);
            }
            if (aniObj.getViewData() != null) {
                savedGameData[aniObjOffset + 29] = (byte) ((aniObj.getHeight() >> 8) & 0xFF);
            }
            //UBYTE stepsize;     /* distance object can move                   */    e.g.   01
            savedGameData[aniObjOffset + 30] = (byte) aniObj.getStepSize();
            //UBYTE cyclfreq;     /* time interval between cells of object      */    e.g.   01
            savedGameData[aniObjOffset + 31] = (byte) aniObj.getCycleTime();
            //UBYTE cycleclk;     /* counter for determining when object cycles */    e.g.   01
            savedGameData[aniObjOffset + 32] = (byte) aniObj.getCycleTimeCount();
            //UBYTE dir;          /* object direction                           */    e.g.   00
            savedGameData[aniObjOffset + 33] = (byte) aniObj.getDirection();
            //UBYTE motion;       /* object motion type                         */    e.g.   00
            // #define	WANDER	1		/* random movement */
            // #define	FOLLOW	2		/* follow an object */
            // #define	MOVETO	3		/* move to a given coordinate */
            savedGameData[aniObjOffset + 34] = (byte) aniObj.getMotionType();
            //UBYTE cycle;        /* cell cycling type                          */    e.g.   00
            // #define NORMAL	0		/* normal repetative cycling of object */
            // #define ENDLOOP	1		/* animate to end of loop and stop */
            // #define RVRSLOOP	2		/* reverse of ENDLOOP */
            // #define REVERSE	3		/* cycle continually in reverse */
            savedGameData[aniObjOffset + 35] = (byte) aniObj.getCycleType();
            //UBYTE pri;          /* priority of object                         */    e.g.   09
            savedGameData[aniObjOffset + 36] = (byte) aniObj.getPriority();

            //UWORD control;      /* object control flag (bit mapped)           */    e.g.   53 40 (0x4053 = )
            int controlBits =
                    (aniObj.isSomeFlagsSet(AnimatedObject.FLAG_DRAWN) ? 0x0001 : 0x00) |
                            (aniObj.isSomeFlagsSet(AnimatedObject.FLAG_IGNORE_BLOCKS) ? 0x0002 : 0x00) |
                            (aniObj.isSomeFlagsSet(AnimatedObject.FLAG_FIX_PRIORITY) ? 0x0004 : 0x00) |
                            (aniObj.isSomeFlagsSet(AnimatedObject.FLAG_IGNORE_HORIZON) ? 0x0008 : 0x00) |
                            (aniObj.isSomeFlagsSet(AnimatedObject.FLAG_UPDATE) ? 0x0010 : 0x00) |
                            (aniObj.isSomeFlagsSet(AnimatedObject.FLAG_CYCLING) ? 0x0020 : 0x00) |
                            (aniObj.isSomeFlagsSet(AnimatedObject.FLAG_ANIMATE) ? 0x0040 : 0x00) |
                            (aniObj.isSomeFlagsSet(AnimatedObject.FLAG_MOTION) ? 0x0080 : 0x00) |
                            (aniObj.isSomeFlagsSet(AnimatedObject.FLAG_ON_WATER) ? 0x0100 : 0x00) |
                            (aniObj.isSomeFlagsSet(AnimatedObject.FLAG_IGNORE_OBJECTS) ? 0x0200 : 0x00) |
                            (aniObj.isSomeFlagsSet(AnimatedObject.FLAG_UPDATE_POS) ? 0x0400 : 0x00) |
                            (aniObj.isSomeFlagsSet(AnimatedObject.FLAG_ON_LAND) ? 0x0800 : 0x00) |
                            (aniObj.isSomeFlagsSet(AnimatedObject.FLAG_DONT_UPDATE) ? 0x1000 : 0x00) |
                            (aniObj.isSomeFlagsSet(AnimatedObject.FLAG_FIX_LOOP) ? 0x2000 : 0x00) |
                            (aniObj.isSomeFlagsSet(AnimatedObject.FLAG_DIDNT_MOVE) ? 0x4000 : 0x00);
            savedGameData[aniObjOffset + 37] = (byte) (controlBits & 0xFF);
            savedGameData[aniObjOffset + 38] = (byte) ((controlBits >> 8) & 0xFF);

            //UBYTE parms[4];     /* space for various motion parameters        */    e.g.   00 00 00 00
            savedGameData[aniObjOffset + 39] = (byte) aniObj.getTargetX();
            savedGameData[aniObjOffset + 40] = (byte) aniObj.getTargetY();
            savedGameData[aniObjOffset + 41] = (byte) aniObj.getOldStepSize();
            savedGameData[aniObjOffset + 42] = (byte) aniObj.getEndFlag();
        }

        // THIRD PIECE: OBJECTS
        // Almost an exact copy of the OBJECT file, but with the 3 byte header removed, and room
        // numbers reflecting the current location of each object.
        byte[] objectData = objects.getRawData();
        int objectsOffset = aniObjsOffset + 2 + aniObjectsLength;
        int objectsLength = objectData.length - 3;
        savedGameData[objectsOffset + 0] = (byte) (objectsLength & 0xFF);
        savedGameData[objectsOffset + 1] = (byte) ((objectsLength >> 8) & 0xFF);
        pos = objectsOffset + 2;
        for (int x = 3; x < objectData.length; x++) {
            savedGameData[pos++] = objectData[x];
        }

        // FOURTH PIECE: SCRIPT BUFFER EVENTS
        // A transcript of events leading to the current state in the current room.
        int scriptsOffset = objectsOffset + 2 + objectsLength;
        byte[] scriptEventData = logicContext.getScriptBuffer().encode();
        int scriptsLength = scriptEventData.length;
        savedGameData[scriptsOffset + 0] = (byte) (scriptsLength & 0xFF);
        savedGameData[scriptsOffset + 1] = (byte) ((scriptsLength >> 8) & 0xFF);
        pos = scriptsOffset + 2;
        for (int i = 0; i < scriptEventData.length; i++) {
            savedGameData[pos++] = scriptEventData[i];
        }

        // FIFTH PIECE: SCAN OFFSETS
        int scanOffsetsOffset = scriptsOffset + 2 + scriptsLength;
        int loadedLogicCount = 0;
        // There is a scan offset for each loaded logic.
        // foreach (ScriptBufferEvent e in state.ScriptBuffer.Events) if (e.type == ScriptBufferEventType.LoadLogic) loadedLogicCount++;
        // The scan offset data contains the offsets for loaded logics plus a 4 byte header, 4 bytes for logic 0, and 4 byte trailer.
        int scanOffsetsLength = (loadedLogicCount * 4) + 12;
        savedGameData[scanOffsetsOffset + 0] = (byte) (scanOffsetsLength & 0xFF);
        savedGameData[scanOffsetsOffset + 1] = (byte) ((scanOffsetsLength >> 8) & 0xFF);
        pos = scanOffsetsOffset + 2;
        // The scan offsets start with 00 00 00 00.
        savedGameData[pos++] = 0;
        savedGameData[pos++] = 0;
        savedGameData[pos++] = 0;
        savedGameData[pos++] = 0;
        // And this is then always followed by an entry for Logic 0
        savedGameData[pos++] = 0;
        savedGameData[pos++] = 0;
        savedGameData[pos++] = (byte) (logicContext.getScanStart((short) 0) & 0xFF);
        savedGameData[pos++] = (byte) ((logicContext.getScanStart((short) 0) >> 8) & 0xFF);
        // The scan offsets for the rest are stored in the order in which the logics were loaded.
        for (ScriptBuffer.ScriptBufferEvent event : logicContext.getScriptBuffer().getEvents()) {
            if (event.type == ScriptBuffer.ScriptBufferEventType.LoadLogic) {
                int logicNum = event.resourceNumber;
                int scanOffset = logicContext.getScanStart((short) logicNum);
                savedGameData[pos++] = (byte) (logicNum & 0xFF);
                savedGameData[pos++] = (byte) ((logicNum >> 8) & 0xFF);
                savedGameData[pos++] = (byte) (scanOffset & 0xFF);
                savedGameData[pos++] = (byte) ((scanOffset >> 8) & 0xFF);
            }
        }
        // The scan offset section ends with FF FF 00 00.
        savedGameData[pos++] = (byte) 0xFF;
        savedGameData[pos++] = (byte) 0xFF;
        savedGameData[pos++] = 0;
        savedGameData[pos++] = 0;

        byte[] trimmedData = new byte[pos];
        System.arraycopy(savedGameData, 0, trimmedData, 0, pos);

        try {
            String filename = String.format("%sSG.%d", logicContext.getGameID(), slotNumber);
            Path path = Paths.get(absolutePath, filename);
            Files.write(path, trimmedData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}