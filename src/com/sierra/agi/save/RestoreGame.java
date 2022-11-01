package com.sierra.agi.save;

import com.sierra.agi.logic.LogicContext;
import com.sierra.agi.view.AnimatedObject;
import com.sierra.agi.view.SavedGame;
import com.sierra.agi.view.ViewScreen;
import com.sierra.agi.view.ViewTable;

public class RestoreGame {
    private LogicContext logicContext;

    public RestoreGame(LogicContext logicContext) {
        this.logicContext = logicContext;
    }

    public boolean restore() {
        String path = logicContext.getCache().getPath().getAbsolutePath();
        ChooseRestoreGameBox box = new ChooseRestoreGameBox(logicContext.getGameID(), path);
        SavedGame chosenGame = box.show(this.logicContext, this.logicContext.getViewScreen());

        if (chosenGame != null && chosenGame.exists) {
            try {
                return this.restoreFile(chosenGame.savedGameData);
            } catch (Exception e) {
                System.out.println("e = " + e);
                e.printStackTrace();
            }
        }
        return false;
    }


    public boolean restoreFile(byte[] rawData) throws Exception {
        int[] savedGameData = SaveUtils.convertToUnsignedInt(rawData);
        ViewTable viewTable = this.logicContext.getViewTable();
        ViewScreen viewScreen = this.logicContext.getViewScreen();

        // 0 - 30(31 bytes) SAVED GAME DESCRIPTION.
        int textEnd = 0;
        while (savedGameData[textEnd] != 0) {
            textEnd++;
        }
        String savedGameDescription = new String(rawData, 0, textEnd, "US-ASCII");

        // FIRST PIECE: SAVE VARIABLES
        // [0] 31 - 32(2 bytes) Length of save variables piece. Length depends on AGI interpreter version. [e.g. (0xE1 0x05) for some games, (0xDB 0x03) for some]
        int saveVarsLength = savedGameData[31] + (savedGameData[32] << 8);
        int aniObjsOffset = 33 + saveVarsLength;

        // [2] 33 - 39(7 bytes) Game ID("SQ2", "KQ3", "LLLLL", etc.), NUL padded.
        textEnd = 33;
        while ((savedGameData[textEnd] != 0) && ((textEnd - 33) < 7)) textEnd++;
        String gameId = new String(savedGameData, 33, textEnd - 33);
        //TODO: if (!gameId.Equals(state.GameId)) return false;

        // If we're sure that this saved game file is for this game, then continue.
        this.logicContext.reset();
        viewScreen.clearLines(0, 24, (short) 0);

        // [9] 40 - 295(256 bytes) Variables, 1 variable per byte
        for (int i = 0; i < 256; i++) {
            int intVal = savedGameData[40 + i];
            this.logicContext.setVar((short) i, (short) intVal);
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
                this.logicContext.addKeyToController((short) keyCode, (short) controllerNum);
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
            this.logicContext.setString((short) i, new String(rawData, stringOffset, textEnd - stringOffset, "US-ASCII"));
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
        System.out.println("numOfAniObjs = " + numOfAniObjs);
        for (int i = 0; i < numOfAniObjs; i++) {
            int aniObjOffset = aniObjsOffset + 2 + (i * 0x2B);
            AnimatedObject animatedObject = viewTable.getEntry((short) i);
            animatedObject.reset();

            // Each ANIOBJ entry is 0x2B in length, i.e. 43 bytes.
            // Example: KQ1 - ego - starting position in room 1
            // 01 01 00 6e 00 64 00 00 17 6b 00 04 24 6b 00 06
            // 31 6b 31 6b 2f 9c 6e 00 64 00 06 00 20 00 01 01
            // 01 00 00 00 09 53 40 00 00 00 00

            //UBYTE num;          /* object number                              */    e.g.   00
            animatedObject.setViewNumber(savedGameData[aniObjOffset + 2]);
            // TODO: crashes larry
            animatedObject.setView(this.logicContext, (short) savedGameData[aniObjOffset + 2]);
            //UBYTE movefreq;     /* number of animation cycles between motion  */    e.g.   01
            animatedObject.setStepTime((short) savedGameData[aniObjOffset + 0]);
            //UBYTE moveclk;      /* number of cycles between moves of object   */    e.g.   01
            animatedObject.setStepTimeCount((short) savedGameData[aniObjOffset + 1]);

            //COORD x;            /* current x coordinate                       */    e.g.   6e 00 (0x006e = )
            animatedObject.setX((short) (savedGameData[aniObjOffset + 3] + (savedGameData[aniObjOffset + 4] << 8)));
            //COORD y;            /* current y coordinate                       */    e.g.   64 00 (0x0064 = )
            animatedObject.setY((short) (savedGameData[aniObjOffset + 5] + (savedGameData[aniObjOffset + 6] << 8)));
            //UBYTE view;         /* current view number                        */    e.g.   00
            animatedObject.setCurrentView((short) savedGameData[aniObjOffset + 7]);
            //VIEW* viewptr;      /* pointer to current view                    */    e.g.   17 6b (0x6b17 = ) IGNORE.
            //UBYTE loop;         /* current loop in view                       */    e.g.   00
            animatedObject.setCurrentLoop((short) savedGameData[aniObjOffset + 10]);
            //UBYTE loopcnt;      /* number of loops in view                    */    e.g.   04                IGNORE
            //LOOP* loopptr;      /* pointer to current loop                    */    e.g.   24 6b (0x6b24 = ) IGNORE
            //UBYTE cel;          /* current cell in loop                       */    e.g.   00
            animatedObject.setCurrentCell((short) savedGameData[aniObjOffset + 14]);
            //UBYTE celcnt;       /* number of cells in current loop            */    e.g.   06                IGNORE
            //CEL* celptr;        /* pointer to current cell                    */    e.g.   31 6b (0x6b31 = ) IGNORE
            //CEL* prevcel;       /* pointer to previous cell                   */    e.g.   31 6b (0x6b31 = )
            if (animatedObject.getCurrentViewData() != null) {
                animatedObject.setPreviousCellData(animatedObject.getCurrentCellData());
            }

            //STRPTR save;        /* pointer to background save area            */    e.g.   2f 9c (0x9c2f = ) IGNORE
            //COORD prevx;        /* previous x coordinate                      */    e.g.   6e 00 (0x006e = )
            animatedObject.setxCopy((byte) (savedGameData[aniObjOffset + 22] + (savedGameData[aniObjOffset + 23] << 8)));
            //COORD prevy;        /* previous y coordinate                      */    e.g.   64 00 (0x0064 = )
            animatedObject.setyCopy((byte) (savedGameData[aniObjOffset + 24] + (savedGameData[aniObjOffset + 25] << 8)));
            //COORD xsize;        /* x dimension of current cell                */    e.g.   06 00 (0x0006 = ) IGNORE
            //COORD ysize;        /* y dimension of current cell                */    e.g.   20 00 (0x0020 = ) IGNORE
            //UBYTE stepsize;     /* distance object can move                   */    e.g.   01
            animatedObject.setStepSize((short) savedGameData[aniObjOffset + 30]);
            //UBYTE cyclfreq;     /* time interval between cells of object      */    e.g.   01
            animatedObject.setCycleTime((short) savedGameData[aniObjOffset + 31]);
            //UBYTE cycleclk;     /* counter for determining when object cycles */    e.g.   01
            animatedObject.setCycleTimeCount((short) savedGameData[aniObjOffset + 32]);
            //UBYTE dir;          /* object direction                           */    e.g.   00
            animatedObject.setDirection((short) savedGameData[aniObjOffset + 33]);
            //UBYTE motion;       /* object motion type                         */    e.g.   00
            // #define	WANDER	1		/* random movement */
            // #define	FOLLOW	2		/* follow an object */
            // #define	MOVETO	3		/* move to a given coordinate */
            animatedObject.setMotionType((short) savedGameData[aniObjOffset + 34]);
            //UBYTE cycle;        /* cell cycling type                          */    e.g.   00
            // #define NORMAL	0		/* normal repetative cycling of object */
            // #define ENDLOOP	1		/* animate to end of loop and stop */
            // #define RVRSLOOP	2		/* reverse of ENDLOOP */
            // #define REVERSE	3		/* cycle continually in reverse */
            animatedObject.setCycleType((short) savedGameData[aniObjOffset + 35]);
            //UBYTE pri;          /* priority of object                         */    e.g.   09
            animatedObject.setPriority((short) savedGameData[aniObjOffset + 36]);
            //UWORD control;      /* object control flag (bit mapped)           */    e.g.   53 40 (0x4053 = )
            int controlBits = (savedGameData[aniObjOffset + 37] + (savedGameData[aniObjOffset + 38] << 8));
            /* object control bits */
            // DRAWN     0x0001  /* 1 -> object is drawn on screen */
            if ((controlBits & 0x0001) > 0) {
                animatedObject.addFlags(AnimatedObject.FLAG_DRAWN);
            } else {
                animatedObject.removeFlags(AnimatedObject.FLAG_DRAWN);
            }

            // IGNRBLK   0x0002  /* 1 -> object ignores blocks */
            if ((controlBits & 0x0002) > 0) {
                animatedObject.addFlags(AnimatedObject.FLAG_IGNORE_BLOCKS);
            } else {
                animatedObject.removeFlags(AnimatedObject.FLAG_IGNORE_BLOCKS);
            }
            // FIXEDPRI  0x0004  /* 1 -> object has fixed priority */
            if ((controlBits & 0x0004) > 0) {
                animatedObject.addFlags(AnimatedObject.FLAG_FIX_PRIORITY);
            } else {
                animatedObject.removeFlags(AnimatedObject.FLAG_FIX_PRIORITY);
            }
            // IGNRHRZ   0x0008  /* 1 -> object ignores the horizon */
            if ((controlBits & 0x0008) > 0) {
                animatedObject.addFlags(AnimatedObject.FLAG_IGNORE_HORIZON);
            } else {
                animatedObject.removeFlags(AnimatedObject.FLAG_IGNORE_HORIZON);
            }
            // UPDATE    0x0010  /* 1 -> update the object */
            if ((controlBits & 0x0010) > 0) {
                animatedObject.addFlags(AnimatedObject.FLAG_UPDATE);
            } else {
                animatedObject.removeFlags(AnimatedObject.FLAG_UPDATE);
            }
            // CYCLE     0x0020  /* 1 -> cycle the object */
            if ((controlBits & 0x0020) > 0) {
                animatedObject.addFlags(AnimatedObject.FLAG_CYCLING);
            } else {
                animatedObject.removeFlags(AnimatedObject.FLAG_CYCLING);
            }
            // ANIMATED  0x0040  /* 1 -> object can move */
            if ((controlBits & 0x0040) > 0) {
                animatedObject.addFlags(AnimatedObject.FLAG_ANIMATE);
            } else {
                animatedObject.removeFlags(AnimatedObject.FLAG_ANIMATE);
            }
            // BLOCKED   0x0080  /* 1 -> object is blocked */
            if ((controlBits & 0x0080) > 0) {
                animatedObject.addFlags(AnimatedObject.FLAG_MOTION);
            } else {
                animatedObject.removeFlags(AnimatedObject.FLAG_MOTION);
            }
            // PRICTRL1  0x0100  /* 1 -> object must be on 'water' priority */
            if ((controlBits & 0x0100) > 0) {
                animatedObject.addFlags(AnimatedObject.FLAG_ON_WATER);
            } else {
                animatedObject.removeFlags(AnimatedObject.FLAG_ON_WATER);
            }
            // IGNROBJ   0x0200  /* 1 -> object won't collide with objects */
            if ((controlBits & 0x0200) > 0) {
                animatedObject.addFlags(AnimatedObject.FLAG_IGNORE_OBJECTS);
            } else {
                animatedObject.removeFlags(AnimatedObject.FLAG_IGNORE_OBJECTS);
            }
            // REPOS     0x0400  /* 1 -> object being reposn'd in this cycle */
            if ((controlBits & 0x0400) > 0) {
                animatedObject.addFlags(AnimatedObject.FLAG_UPDATE_POS);
            } else {
                animatedObject.removeFlags(AnimatedObject.FLAG_UPDATE_POS);
            }
            // PRICTRL2  0x0800  /* 1 -> object must not be entirely on water */
            if ((controlBits & 0x0800) > 0) {
                animatedObject.addFlags(AnimatedObject.FLAG_ON_LAND);
            } else {
                animatedObject.removeFlags(AnimatedObject.FLAG_ON_LAND);
            }
            // NOADVANC  0x1000  /* 1 -> don't advance object's cel in this loop */
            if ((controlBits & 0x1000) > 0) {
                animatedObject.addFlags(AnimatedObject.FLAG_DONT_UPDATE);
            } else {
                animatedObject.removeFlags(AnimatedObject.FLAG_DONT_UPDATE);
            }
            // FIXEDLOOP 0x2000  /* 1 -> object's loop is fixed */
            if ((controlBits & 0x2000) > 0) {
                animatedObject.addFlags(AnimatedObject.FLAG_FIX_LOOP);
            } else {
                animatedObject.removeFlags(AnimatedObject.FLAG_FIX_LOOP);
            }
            // STOPPED   0x4000  /* 1 -> object did not move during last animation cycle */
            if ((controlBits & 0x4000) > 0) {
                animatedObject.addFlags(AnimatedObject.FLAG_DIDNT_MOVE);
            } else {
                animatedObject.removeFlags(AnimatedObject.FLAG_DIDNT_MOVE);
            }
            //UBYTE parms[4];     /* space for various motion parameters        */    e.g.   00 00 00 00
            animatedObject.setTargetX((short) savedGameData[aniObjOffset + 39]);
            animatedObject.setTargetY((short) savedGameData[aniObjOffset + 40]);
            animatedObject.setOldStepSize((short) savedGameData[aniObjOffset + 41]);
            animatedObject.setEndFlag((short) savedGameData[aniObjOffset + 42]);
            // If motion type is follow, then force a re-initialisation of the follow path.
            if (animatedObject.getMotionType() == AnimatedObject.MOTION_FOLLOWEGO) {
                animatedObject.setOldStepSize((short) -1);
            }
        }

        // THIRD PIECE: OBJECTS
        // Almost an exact copy of the OBJECT file, but with the 3 byte header removed, and room
        // numbers reflecting the current location of each object.
        int objectsOffset = aniObjsOffset + 2 + aniObjectsLength;
        int objectsLength = (savedGameData[objectsOffset + 0] + (savedGameData[objectsOffset + 1] << 8));
        // TODO: state.Objects.NumOfAnimatedObjects = (byte)numOfAniObjs;
        int numOfObjects = (savedGameData[objectsOffset + 2] + (savedGameData[objectsOffset + 3] << 8)) / 3;
        // Set the saved room number of each Object.
        for (int objectNum = 0, roomPos = objectsOffset + 4; objectNum < numOfObjects; objectNum++, roomPos += 3) {
            logicContext.setObject((short) objectNum, (short) savedGameData[roomPos]);
        }

        // FOURTH PIECE: SCRIPT BUFFER EVENTS
        // A transcript of events leading to the current state in the current room.
        int scriptsOffset = objectsOffset + 2 + objectsLength;
        int scriptsLength = (savedGameData[scriptsOffset + 0] + (savedGameData[scriptsOffset + 1] << 8));
        // Each script entry is two unsigned bytes long:
        // UBYTE action;
        // UBYTE who;
        //
        // Action byte is a code defined as follows:
        // S_LOADLOG       0
        // S_LOADVIEW      1
        // S_LOADPIC       2
        // S_LOADSND       3
        // S_DRAWPIC       4
        // S_ADDPIC        5
        // S_DSCRDPIC      6
        // S_DSCRDVIEW     7
        // S_OVERLAYPIC    8
        //
        // Example:
        // c8 00 Length
        // 00 01 load.logic  0x01
        // 01 00 load.view   0x00
        // 00 66 load.logic  0x66
        // 01 4b load.view   0x4B
        // 01 57 load.view   0x57
        // 01 6e load.view   0x6e
        // 02 01 load.pic    0x01
        // 04 01 draw.pic    0x01
        // 06 01 discard.pic 0x01
        // 00 65 load.logic  0x65
        // 01 6b load.view   0x6B
        // 01 61 load.view   0x61
        // 01 5d load.view   0x5D
        // 01 46 load.view   0x46
        // 03 0d load.sound  0x0D
        // etc...
        /* TODO
        state.ScriptBuffer.InitScript();
        for (int i = 0; i < scriptEntryCount; i++)
        {
            int scriptOffset = scriptsOffset + 2 + (i * 2);
            int action = savedGameData[scriptOffset + 0];
            int resourceNum = savedGameData[scriptOffset + 1];
            byte[] data = null;
            if (action == (int)ScriptBuffer.ScriptBufferEventType.AddToPic)
            {
                // The add.to.pics are stored in the saved game file across 8 bytes, i.e. 4 separate script
                // entries (that is also how the original AGI interpreter stored it in memory).
                // What we do though is store these in an additional data array associated with
                // the script event since utilitising multiple event entries is a bit of a hack
                // really. I can understand why they did it though.
                data = new byte[] {
                        savedGameData[scriptOffset + 2], savedGameData[scriptOffset + 3], savedGameData[scriptOffset + 4],
                        savedGameData[scriptOffset + 5], savedGameData[scriptOffset + 6], savedGameData[scriptOffset + 7]
                };

                // Increase i to account for the fact that we've processed an additional 3 slots.
                i += 3;
            }
            state.ScriptBuffer.AddScript((ScriptBuffer.ScriptBufferEventType)action, resourceNum, data);
        }
         */

        // FIFTH PIECE: SCAN OFFSETS
        // Note: Not every logic can set a scan offset, as there is a max of 30. But only
        // loaded logics can have this set and I'd imagine you'd run out of memory before
        // loading that many logics at once.
        int scanOffsetsOffset = scriptsOffset + 2 + scriptsLength;
        int scanOffsetsLength = (savedGameData[scanOffsetsOffset + 0] + (savedGameData[scanOffsetsOffset + 1] << 8));
        int numOfScanOffsets = (scanOffsetsLength / 4);
        // Each entry is 4 bytes long, made up of 2 16-bit words:
        // COUNT num;                                    /* logic number         */
        // COUNT ofs;                                    /* offset to scan start */
        //
        // Example:
        // 18 00
        // 00 00 00 00  Start of list. Seems to always be 4 zeroes.
        // 00 00 00 00  Logic 0 - Offset 0
        // 01 00 00 00  Logic 1 - Offset 0
        // 66 00 00 00  Logic 102 - Offset 0
        // 65 00 00 00  Logic 101 - Offset 0
        // ff ff 00 00  End of list
        //
        // Quick Analysis of the above:
        // * Only logics that are current loaded are in the scan offset list, i.e. they're removed when the room changes.
        // * The order logics appear in this list is the order that they are loaded.
        // * Logics disappear from this list when they are unloaded (on new.room).
        // * The new.room command unloads all logics except for logic 0, so it never leaves this list.
        for (int i = 0; i < 256; i++) {
            this.logicContext.setScanStart((short) i, 0);
        }

        for (int i = 1; i < numOfScanOffsets; i++) {
            int scanOffsetOffset = scanOffsetsOffset + 2 + (i * 4);
            int logicNumber = (savedGameData[scanOffsetOffset + 0] + (savedGameData[scanOffsetOffset + 1] << 8));
            if (logicNumber < 256) {
                this.logicContext.setScanStart((short) logicNumber, (savedGameData[scanOffsetOffset + 2] + (savedGameData[scanOffsetOffset + 3] << 8)));
            }
        }

        this.logicContext.setFlag(this.logicContext.FLAG_RESTORE_JUST_RAN, true);

        return true;
    }
}
