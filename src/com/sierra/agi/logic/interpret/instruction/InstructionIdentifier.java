//
//  InstructionIdentifier.java
//  AGI Debugger
//
//  Created by Dr. Z on Sun Jul 07 2002.
//  Copyright (c) 2001 __MyCompanyName__. All rights reserved.
//

package com.sierra.agi.logic.interpret.instruction;

import java.util.Hashtable;

public abstract class InstructionIdentifier {
    protected static Hashtable viewInstructions;
    protected static Hashtable viewAlterInstructions;
    protected static Hashtable viewQueryInstructions;
    protected static Hashtable pictureInstructions;

    static {
        Boolean trueObject = Boolean.TRUE;

        viewInstructions = new Hashtable();
        viewAlterInstructions = new Hashtable();
        viewQueryInstructions = new Hashtable();
        pictureInstructions = new Hashtable();

        viewQueryInstructions.put(InstructionLastCell.class, trueObject);
        viewQueryInstructions.put(InstructionCurrentCell.class, trueObject);
        viewQueryInstructions.put(InstructionCurrentLoop.class, trueObject);
        viewQueryInstructions.put(InstructionCurrentView.class, trueObject);
        viewQueryInstructions.put(InstructionGetPriority.class, trueObject);
        viewQueryInstructions.put(InstructionPosition.class, trueObject);
        viewQueryInstructions.put(InstructionGetPosition.class, trueObject);
        viewQueryInstructions.put(InstructionLastLoop.class, trueObject);
        viewQueryInstructions.put(InstructionGetDir.class, trueObject);
        viewQueryInstructions.put(InstructionDistance.class, trueObject);

        viewAlterInstructions.put(InstructionAnimateObject.class, trueObject);
        viewAlterInstructions.put(InstructionUnanimateAll.class, trueObject);
        viewAlterInstructions.put(InstructionSetView.class, trueObject);
        viewAlterInstructions.put(InstructionSetLoop.class, trueObject);
        viewAlterInstructions.put(InstructionFixLoop.class, trueObject);
        viewAlterInstructions.put(InstructionReleaseLoop.class, trueObject);
        viewAlterInstructions.put(InstructionSetCell.class, trueObject);
        viewAlterInstructions.put(InstructionSetPriority.class, trueObject);
        viewAlterInstructions.put(InstructionReleasePriority.class, trueObject);
        viewAlterInstructions.put(InstructionDraw.class, trueObject);
        viewAlterInstructions.put(InstructionErase.class, trueObject);
        viewAlterInstructions.put(InstructionStartCycling.class, trueObject);
        viewAlterInstructions.put(InstructionStopCycling.class, trueObject);
        viewAlterInstructions.put(InstructionNormalCycling.class, trueObject);
        viewAlterInstructions.put(InstructionReverseCycling.class, trueObject);
        viewAlterInstructions.put(InstructionReverseLoop.class, trueObject);
        viewAlterInstructions.put(InstructionCycleTime.class, trueObject);
        viewAlterInstructions.put(InstructionStopMotion.class, trueObject);
        viewAlterInstructions.put(InstructionStartMotion.class, trueObject);
        viewAlterInstructions.put(InstructionStepSize.class, trueObject);
        viewAlterInstructions.put(InstructionStepTime.class, trueObject);
        viewAlterInstructions.put(InstructionMoveObject.class, trueObject);
        viewAlterInstructions.put(InstructionFollowEgo.class, trueObject);
        viewAlterInstructions.put(InstructionWander.class, trueObject);
        viewAlterInstructions.put(InstructionNormalMotion.class, trueObject);
        viewAlterInstructions.put(InstructionSetDir.class, trueObject);
        viewAlterInstructions.put(InstructionObjectOnWater.class, trueObject);
        viewAlterInstructions.put(InstructionObjectOnLand.class, trueObject);
        viewAlterInstructions.put(InstructionObjectOnAnything.class, trueObject);
        viewAlterInstructions.put(InstructionReposition.class, trueObject);
        viewAlterInstructions.put(InstructionStopUpdate.class, trueObject);
        viewAlterInstructions.put(InstructionStartUpdate.class, trueObject);
        viewAlterInstructions.put(InstructionForceUpdate.class, trueObject);

        viewInstructions.putAll(viewAlterInstructions);
        viewInstructions.putAll(viewQueryInstructions);

        pictureInstructions.put(InstructionDrawPic.class, trueObject);
        pictureInstructions.put(InstructionOverlayPic.class, trueObject);
        pictureInstructions.put(InstructionAddToPic.class, trueObject);
        pictureInstructions.put(InstructionShowPic.class, trueObject);
    }

    public static boolean isViewInstruction(Instruction instruction) {
        return viewInstructions.get(instruction.getClass()) != null;
    }

    public static boolean isViewAlterInstruction(Instruction instruction) {
        return viewAlterInstructions.get(instruction.getClass()) != null;
    }

    public static boolean isViewQueryInstruction(Instruction instruction) {
        return viewQueryInstructions.get(instruction.getClass()) != null;
    }

    public static boolean isPictureInstruction(Instruction instruction) {
        return pictureInstructions.get(instruction.getClass()) != null;
    }
}
