package com.agifans.jagi.appleii.kq2;

import com.sierra.agi.debug.ResourceFrame;
import com.sierra.agi.logic.LogicException;
import com.sierra.agi.pic.PictureException;
import com.sierra.agi.res.CorruptedResourceException;
import com.sierra.agi.res.ResourceException;
import com.sierra.agi.view.ViewException;

import java.io.IOException;

/**
 * A KQ2 tool that uses the Java AGI interpreter's ResourceFrame that provides
 * viewers for the various resource types.
 *
 * @author Lance Ewing
 */
public class KQ2Viewer extends KQ2Tool {

    /**
     * Constructor for KQ2Viewer.
     *
     * @param args The command line arguments.
     * @throws IOException
     * @throws CorruptedResourceException
     */
    public KQ2Viewer(String[] args) throws IOException, CorruptedResourceException {
        super(args);
    }

    public static void main(String[] args) throws IOException, ResourceException, PictureException, LogicException, ViewException {
        System.setProperty("com.sierra.agi.logic.LogicProvider", "com.sierra.agi.logic.debug.DebugLogicProvider");
        System.setProperty("com.sierra.agi.word.WordsProvider", "com.agifans.jagi.appleii.kq2.KQ2WordProvider");

        KQ2Viewer viewer = new KQ2Viewer(args);
        viewer.run();
    }

    public void run() {
        ResourceFrame frame = new ResourceFrame(resourceCache);
        frame.setVisible(true);
    }
}
