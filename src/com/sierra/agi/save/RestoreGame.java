package com.sierra.agi.save;

import com.sierra.agi.awt.EgaUtils;
import com.sierra.agi.logic.LogicContext;
import com.sierra.agi.res.ResourceCache;
import com.sierra.agi.res.ResourceCacheFile;
import com.sierra.agi.view.ViewEntry;
import com.sierra.agi.view.ViewScreen;
import com.sierra.agi.view.ViewTable;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class RestoreGame {
    private LogicContext logicContext;

    public RestoreGame(LogicContext logicContext) {
        this.logicContext = logicContext;
    }

    public void restore() {
        String path = logicContext.getCache().getPath().getAbsolutePath();
        System.out.println("path = " + path);
    }

    public void restoreFile(String filename) throws Exception {
        byte[] savedGameData = Files.readAllBytes(Paths.get(filename));
        ViewTable viewTable = this.logicContext.getViewTable();
        ViewScreen viewScreen = this.logicContext.getViewScreen();

        // 0 - 30(31 bytes) SAVED GAME DESCRIPTION.
        int textEnd = 0;
        while (savedGameData[textEnd] != 0) textEnd++;
        String savedGameDescription = new String(savedGameData, 0, textEnd, "US-ASCII");
        System.out.println("savedGameDescription = " + savedGameDescription);

        // FIRST PIECE: SAVE VARIABLES
        // [0] 31 - 32(2 bytes) Length of save variables piece. Length depends on AGI interpreter version. [e.g. (0xE1 0x05) for some games, (0xDB 0x03) for some]
        int saveVarsLength = savedGameData[31] + (savedGameData[32] << 8);
        int aniObjsOffset = 33 + saveVarsLength;

        // [2] 33 - 39(7 bytes) Game ID("SQ2", "KQ3", "LLLLL", etc.), NUL padded.
        textEnd = 33;
        while ((savedGameData[textEnd] != 0) && ((textEnd - 33) < 7)) textEnd++;
        String gameId = new String(savedGameData, 33, textEnd - 33);
        System.out.println("gameId = " + gameId);
        //TODO: if (!gameId.Equals(state.GameId)) return false;

        // If we're sure that this saved game file is for this game, then continue.
        this.logicContext.reset();
        //textGraphics.ClearLines(0, 24, 0);

        // [9] 40 - 295(256 bytes) Variables, 1 variable per byte
        for (int i = 0; i < 256; i++) {
            this.logicContext.setVar((short) i, savedGameData[40 + i]);
        }

        // [265] 296 - 327(32 bytes) Flags, 8 flags per byte
        for (int i = 0; i < 256; i++) {
            this.logicContext.setFlag((short) i, (savedGameData[(i >> 3) + 296] & (0x80 >> (i & 0x07))) > 0);
        }

        // [297] 328 - 331(4 bytes) Clock ticks since game started. 1 clock tick == 50ms.
        //TODO: state.TotalTicks = (savedGameData[328] + (savedGameData[329] << 8) + (savedGameData[330] << 16) + (savedGameData[331] << 24)) * 3;

        // [301] 332 - 333(2 bytes) Horizon
        this.logicContext.setHorizon((short) (savedGameData[332] + (savedGameData[333] << 8)));

        // [303] 334 - 335(2 bytes) Key Dir
        // TODO: Not entirely sure what this is for.
        int keyDir = (savedGameData[334] + (savedGameData[335] << 8));

        // Currently active block.
        // [305] 336 - 337(2 bytes) Upper left X position for active block.
        viewTable.setBlockUpperLeftX((short) (savedGameData[336] + (savedGameData[337] << 8)));
        // [307] 338 - 339(2 bytes) Upper Left Y position for active block.
        viewTable.setBlockUpperLeftY((short) (savedGameData[338] + (savedGameData[339] << 8)));
        // [309] 340 - 341(2 bytes) Lower Right X position for active block.
        viewTable.setBlockLowerRightX((short) (savedGameData[340] + (savedGameData[341] << 8)));
        // [311] 342 - 343(2 bytes) Lower Right Y position for active block.
        viewTable.setBlockLowerRightY((short) (savedGameData[342] + (savedGameData[343] << 8)));

        // [313] 344 - 345(2 bytes) Player control (1) / Program control (0)
        this.logicContext.setPlayerControl((savedGameData[344] + (savedGameData[345] << 8)) == 1);
        // [315] 346 - 347(2 bytes) Current PICTURE number
        // TODO: state.CurrentPicture = null; // Will be set via load.pic script entry later on.
        // [317] 348 - 349(2 bytes) Blocking flag (1 = true, 0 = false)
        viewTable.setBlockSet((savedGameData[348] + (savedGameData[349] << 8)) == 1);

        // [319] 350 - 351(2 bytes) Max drawn. Always set to 15. Maximum number of animated objects that can be drawn at a time. Set by old max.drawn command in AGI v2.001.
        // TODO: state.MaxDrawn = (savedGameData[350] + (savedGameData[351] << 8));
        // [321] 352 - 353(2 bytes) Script size. Set by script.size. Max number of script event items. Default is 50.
        // TODO: state.ScriptBuffer.SetScriptSize(savedGameData[352] + (savedGameData[353] << 8));
        // [323] 354 - 355(2 bytes) Current number of script event entries.
        int scriptEntryCount = (savedGameData[354] + (savedGameData[355] << 8));

        // [325] 356 - 555(200 or 160 bytes) ? Key to controller map (4 bytes each)
        int keyMapSize = (saveVarsLength < 1000 ? 40 : 50);   // TODO: This is a version check hack. Need a better way.
        for (int i = 0; i < keyMapSize; i++) {
            int keyMapOffset = i << 2;
            int keyCode = (savedGameData[356 + keyMapOffset] + (savedGameData[357 + keyMapOffset] << 8));
            int controllerNum = (savedGameData[358 + keyMapOffset] + (savedGameData[359 + keyMapOffset] << 8));
            if (!((keyCode == 0) && (controllerNum == 0))) {
                int interKeyCode = EgaUtils.convertKey(keyCode);
                this.logicContext.addKeyToController((short) interKeyCode, (short) controllerNum);
            }
        }

        // For the saved game formats we support (2.4XX to 2.9XX), the keymap always starts at 356.
        int postKeyMapOffset = 356 + (keyMapSize << 2);

        // [525] 556 - 1515(480 or 960 bytes) 12 or 24 strings, each 40 bytes long
        int numOfStrings = (saveVarsLength < 1000 ? 12 : 24);  // TODO: This is a version check hack. Need a better way.
        for (int i = 0; i < numOfStrings; i++) {
            int stringOffset = postKeyMapOffset + (i * this.logicContext.STRING_LENGTH);
            textEnd = stringOffset;
            while ((savedGameData[textEnd] != 0) && ((textEnd - stringOffset) < this.logicContext.STRING_LENGTH)) {
                textEnd++;
            }
            this.logicContext.setString((short) i, new String(savedGameData, stringOffset, textEnd - stringOffset, "US-ASCII"));
        }

        int postStringsOffset = postKeyMapOffset + (numOfStrings * this.logicContext.STRING_LENGTH);

        // [1485] 1516(2 bytes) Foreground colour

        viewScreen.setForegroundColor((byte) (savedGameData[postStringsOffset + 0] + (savedGameData[postStringsOffset + 1] << 8)));

        // [1487] 1518(2 bytes) Background colour
        // TODO: Interpreter doesn't yet properly handle AGI background colour.
        // TODO: viewScreen.setBackgroundColor((byte)(savedGameData[postStringsOffset + 2] + (savedGameData[postStringsOffset + 3] << 8)));

        // [1489] 1520(2 bytes) Text Attribute value (combined foreground/background value)
        int textAttribute = (savedGameData[postStringsOffset + 4] + (savedGameData[postStringsOffset + 5] << 8));

        // [1491] 1522(2 bytes) Accept input = 1, Prevent input = 0
        if ((savedGameData[postStringsOffset + 6] + (savedGameData[postStringsOffset + 7] << 8)) == 1) {
            logicContext.acceptInput();
        } else {
            logicContext.preventInput();
        }

        // [1493] 1524(2 bytes) User input row on the screen
        viewScreen.setLineUserInput(savedGameData[postStringsOffset + 8] + (savedGameData[postStringsOffset + 9] << 8));

        // [1495] 1526(2 bytes) Cursor character
        viewScreen.setCursorChar((char) (savedGameData[postStringsOffset + 10] + (savedGameData[postStringsOffset + 11] << 8)));

        // [1497] 1528(2 bytes) Show status line = 1, Don't show status line = 0
        if ((savedGameData[postStringsOffset + 12] + (savedGameData[postStringsOffset + 13] << 8)) == 1) {
            this.logicContext.showStatusLine();
        } else {
            this.logicContext.hideStatusLine();
        }

        // [1499] 1530(2 bytes) Status line row on the screen
        viewScreen.setLineStatus(savedGameData[postStringsOffset + 14] + (savedGameData[postStringsOffset + 15] << 8));

        // [1501] 1532(2 bytes) Picture top row on the screen
        viewScreen.setLineMinPrint(savedGameData[postStringsOffset + 16] + (savedGameData[postStringsOffset + 17] << 8));

        // [1503] 1534(2 bytes) Picture bottom row on the screen
        // Note: Not needed by this intepreter.
        int picBottom = (savedGameData[postStringsOffset + 18] + (savedGameData[postStringsOffset + 19] << 8));

        if ((postStringsOffset + 20) < aniObjsOffset) {
            // [1505] 1536(2 bytes) Stores a pushed position within the script event list
            // Note: Depends on interpreter version. 2.4xx and below didn't have push.script/pop.script, so they didn't have this saved game field.
            // TODO: state.ScriptBuffer.SavedScript = (savedGameData[postStringsOffset + 20] + (savedGameData[postStringsOffset + 21] << 8));
        }

        // SECOND PIECE: ANIMATED OBJECT STATE
        // 17 aniobjs = 0x02DB length, 18 aniobjs = 0x0306, 20 aniobjs = 0x035C, 21 aniobjs = 0x0387, 91 = 0x0F49] 2B, 2B, 2B, 2B, 2B
        // 1538 - 1539(2 bytes) Length of piece (ANIOBJ should divide evenly in to this length)
        int aniObjectsLength = (savedGameData[aniObjsOffset + 0] + (savedGameData[aniObjsOffset + 1] << 8));
        // Each ANIOBJ entry is 0x2B in length, i.e. 43 bytes.
        // 17 aniobjs = 0x02DB length, 18 aniobjs = 0x0306, 20 aniobjs = 0x035C, 21 aniobjs = 0x0387, 91 = 0x0F49] 2B, 2B, 2B, 2B, 2B
        int numOfAniObjs = (aniObjectsLength / 0x2B);

        for (int i = 0; i < numOfAniObjs; i++) {
            int aniObjOffset = aniObjsOffset + 2 + (i * 0x2B);
            ViewEntry viewEntry = viewTable.getEntry((short) i);
            viewEntry.reset();

            // Each ANIOBJ entry is 0x2B in length, i.e. 43 bytes.
            // Example: KQ1 - ego - starting position in room 1
            // 01 01 00 6e 00 64 00 00 17 6b 00 04 24 6b 00 06
            // 31 6b 31 6b 2f 9c 6e 00 64 00 06 00 20 00 01 01
            // 01 00 00 00 09 53 40 00 00 00 00

            //UBYTE movefreq;     /* number of animation cycles between motion  */    e.g.   01
            viewEntry.setStepTime(savedGameData[aniObjOffset + 0]);
            //UBYTE moveclk;      /* number of cycles between moves of object   */    e.g.   01
            viewEntry.setStepTimeCount(savedGameData[aniObjOffset + 1]);
            //UBYTE num;          /* object number                              */    e.g.   00
            viewEntry.setViewNumber(savedGameData[aniObjOffset + 2]);
            //COORD x;            /* current x coordinate                       */    e.g.   6e 00 (0x006e = )
            viewEntry.setX((short) (savedGameData[aniObjOffset + 3] + (savedGameData[aniObjOffset + 4] << 8)));
            //COORD y;            /* current y coordinate                       */    e.g.   64 00 (0x0064 = )
            viewEntry.setY((short) (savedGameData[aniObjOffset + 5] + (savedGameData[aniObjOffset + 6] << 8)));
            //UBYTE view;         /* current view number                        */    e.g.   00
            viewEntry.setCurrentView(savedGameData[aniObjOffset + 7]);
            //VIEW* viewptr;      /* pointer to current view                    */    e.g.   17 6b (0x6b17 = ) IGNORE.
            //UBYTE loop;         /* current loop in view                       */    e.g.   00
            viewEntry.setCurrentLoop(savedGameData[aniObjOffset + 10]);
            //UBYTE loopcnt;      /* number of loops in view                    */    e.g.   04                IGNORE
            //LOOP* loopptr;      /* pointer to current loop                    */    e.g.   24 6b (0x6b24 = ) IGNORE
            //UBYTE cel;          /* current cell in loop                       */    e.g.   00
            viewEntry.setCurrentCell(savedGameData[aniObjOffset + 14]);
            //UBYTE celcnt;       /* number of cells in current loop            */    e.g.   06                IGNORE
            //CEL* celptr;        /* pointer to current cell                    */    e.g.   31 6b (0x6b31 = ) IGNORE
            //CEL* prevcel;       /* pointer to previous cell                   */    e.g.   31 6b (0x6b31 = )
            if (viewEntry.getCurrentViewData() != null) {
                viewEntry.setPreviousCellData(viewEntry.getCurrentCellData());
            }
            ;
            //STRPTR save;        /* pointer to background save area            */    e.g.   2f 9c (0x9c2f = ) IGNORE
            //COORD prevx;        /* previous x coordinate                      */    e.g.   6e 00 (0x006e = )
            viewEntry.setxCopy((byte) (savedGameData[aniObjOffset + 22] + (savedGameData[aniObjOffset + 23] << 8)));
            //COORD prevy;        /* previous y coordinate                      */    e.g.   64 00 (0x0064 = )
            viewEntry.setyCopy((byte) (savedGameData[aniObjOffset + 24] + (savedGameData[aniObjOffset + 25] << 8)));
            //COORD xsize;        /* x dimension of current cell                */    e.g.   06 00 (0x0006 = ) IGNORE
            //COORD ysize;        /* y dimension of current cell                */    e.g.   20 00 (0x0020 = ) IGNORE
            //UBYTE stepsize;     /* distance object can move                   */    e.g.   01
            viewEntry.setStepSize(savedGameData[aniObjOffset + 30]);
            //UBYTE cyclfreq;     /* time interval between cells of object      */    e.g.   01
            viewEntry.setCycleTime(savedGameData[aniObjOffset + 31]);
            //UBYTE cycleclk;     /* counter for determining when object cycles */    e.g.   01
            viewEntry.setCycleTimeCount(savedGameData[aniObjOffset + 32]);
            //UBYTE dir;          /* object direction                           */    e.g.   00
            viewEntry.setDirection(savedGameData[aniObjOffset + 33]);
            //UBYTE motion;       /* object motion type                         */    e.g.   00
            // #define	WANDER	1		/* random movement */
            // #define	FOLLOW	2		/* follow an object */
            // #define	MOVETO	3		/* move to a given coordinate */
            viewEntry.setMotionType(savedGameData[aniObjOffset + 34]);
            //UBYTE cycle;        /* cell cycling type                          */    e.g.   00
            // #define NORMAL	0		/* normal repetative cycling of object */
            // #define ENDLOOP	1		/* animate to end of loop and stop */
            // #define RVRSLOOP	2		/* reverse of ENDLOOP */
            // #define REVERSE	3		/* cycle continually in reverse */
            viewEntry.setCycleType(savedGameData[aniObjOffset + 35]);
            //UBYTE pri;          /* priority of object                         */    e.g.   09
            viewEntry.setPriority(savedGameData[aniObjOffset + 36]);
            //UWORD control;      /* object control flag (bit mapped)           */    e.g.   53 40 (0x4053 = )
            int controlBits = (savedGameData[aniObjOffset + 37] + (savedGameData[aniObjOffset + 38] << 8));
            /* object control bits */
            // DRAWN     0x0001  /* 1 -> object is drawn on screen */
            if ((controlBits & 0x0001) > 0) {
                viewEntry.addFlags(ViewEntry.FLAG_DRAWN);
            } else {
                viewEntry.removeFlags(ViewEntry.FLAG_DRAWN);
            }

            // IGNRBLK   0x0002  /* 1 -> object ignores blocks */
            if ((controlBits & 0x0002) > 0) {
                viewEntry.addFlags(ViewEntry.FLAG_IGNORE_BLOCKS);
            } else {
                viewEntry.removeFlags(ViewEntry.FLAG_IGNORE_BLOCKS);
            }
            // FIXEDPRI  0x0004  /* 1 -> object has fixed priority */
            if ((controlBits & 0x0004) > 0) {
                viewEntry.addFlags(ViewEntry.FLAG_FIX_PRIORITY);
            } else {
                viewEntry.removeFlags(ViewEntry.FLAG_FIX_PRIORITY);
            }
            // IGNRHRZ   0x0008  /* 1 -> object ignores the horizon */
            if ((controlBits & 0x0008) > 0) {
                viewEntry.addFlags(ViewEntry.FLAG_IGNORE_HORIZON);
            } else {
                viewEntry.removeFlags(ViewEntry.FLAG_IGNORE_HORIZON);
            }
            // UPDATE    0x0010  /* 1 -> update the object */
            if ((controlBits & 0x0010) > 0) {
                viewEntry.addFlags(ViewEntry.FLAG_UPDATE);
            } else {
                viewEntry.removeFlags(ViewEntry.FLAG_UPDATE);
            }
            // CYCLE     0x0020  /* 1 -> cycle the object */
            if ((controlBits & 0x0020) > 0) {
                viewEntry.addFlags(ViewEntry.FLAG_CYCLING);
            } else {
                viewEntry.removeFlags(ViewEntry.FLAG_CYCLING);
            }
            // ANIMATED  0x0040  /* 1 -> object can move */
            if ((controlBits & 0x0040) > 0) {
                viewEntry.addFlags(ViewEntry.FLAG_ANIMATE);
            } else {
                viewEntry.removeFlags(ViewEntry.FLAG_ANIMATE);
            }
            // BLOCKED   0x0080  /* 1 -> object is blocked */
            if ((controlBits & 0x0080) > 0) {
                viewEntry.addFlags(ViewEntry.FLAG_MOTION);
            } else {
                viewEntry.removeFlags(ViewEntry.FLAG_MOTION);
            }
            // PRICTRL1  0x0100  /* 1 -> object must be on 'water' priority */
            if ((controlBits & 0x0100) > 0) {
                viewEntry.addFlags(ViewEntry.FLAG_ON_WATER);
            } else {
                viewEntry.removeFlags(ViewEntry.FLAG_ON_WATER);
            }
            // IGNROBJ   0x0200  /* 1 -> object won't collide with objects */
            if ((controlBits & 0x0200) > 0) {
                viewEntry.addFlags(ViewEntry.FLAG_IGNORE_OBJECTS);
            } else {
                viewEntry.removeFlags(ViewEntry.FLAG_IGNORE_OBJECTS);
            }
            // REPOS     0x0400  /* 1 -> object being reposn'd in this cycle */
            if ((controlBits & 0x0400) > 0) {
                viewEntry.addFlags(ViewEntry.FLAG_UPDATE_POS);
            } else {
                viewEntry.removeFlags(ViewEntry.FLAG_UPDATE_POS);
            }
            // PRICTRL2  0x0800  /* 1 -> object must not be entirely on water */
            if ((controlBits & 0x0800) > 0) {
                viewEntry.addFlags(ViewEntry.FLAG_ON_LAND);
            } else {
                viewEntry.removeFlags(ViewEntry.FLAG_ON_LAND);
            }
            // NOADVANC  0x1000  /* 1 -> don't advance object's cel in this loop */
            if ((controlBits & 0x1000) > 0) {
                viewEntry.addFlags(ViewEntry.FLAG_DONT_UPDATE);
            } else {
                viewEntry.removeFlags(ViewEntry.FLAG_DONT_UPDATE);
            }
            // FIXEDLOOP 0x2000  /* 1 -> object's loop is fixed */
            if ((controlBits & 0x2000) > 0) {
                viewEntry.addFlags(ViewEntry.FLAG_FIX_LOOP);
            } else {
                viewEntry.removeFlags(ViewEntry.FLAG_FIX_LOOP);
            }
            // STOPPED   0x4000  /* 1 -> object did not move during last animation cycle */
            if ((controlBits & 0x4000) > 0) {
                viewEntry.addFlags(ViewEntry.FLAG_DIDNT_MOVE);
            } else {
                viewEntry.removeFlags(ViewEntry.FLAG_DIDNT_MOVE);
            }
            //UBYTE parms[4];     /* space for various motion parameters        */    e.g.   00 00 00 00
            viewEntry.setTargetX(savedGameData[aniObjOffset + 39]);
            viewEntry.setTargetY(savedGameData[aniObjOffset + 40]);
            viewEntry.setOldStepSize(savedGameData[aniObjOffset + 41]);
            viewEntry.setEndFlag(savedGameData[aniObjOffset + 42]);
            // If motion type is follow, then force a re-initialisation of the follow path.
            if (viewEntry.getMotionType() == ViewEntry.MOTION_FOLLOWEGO) {
                viewEntry.setOldStepSize((short)-1);
            } ;
        }
    }

    public static void main(String[] args) {
        try {
            String filename = "C:\\agigames\\kq2\\sg.1";
            ResourceCache resCache = new ResourceCacheFile(new File(filename));
            LogicContext context = new LogicContext(resCache);
            RestoreGame rg = new RestoreGame(context);

            rg.restoreFile(filename);
        } catch (Exception e) {
            System.out.println("Error " + e);
        }
    }
}
