package com.agifans.jagi.appleii.kq2;

import com.agifans.jagi.res.v1.LogicalFile;
import com.agifans.jagi.res.v1.LogicalFileType;
import com.sierra.agi.debug.ResourceFrame;
import com.sierra.agi.logic.LogicException;
import com.sierra.agi.pic.PictureException;
import com.sierra.agi.res.CorruptedResourceException;
import com.sierra.agi.res.ResourceCache;
import com.sierra.agi.res.ResourceException;
import com.sierra.agi.res.ResourceProvider;
import com.sierra.agi.view.ViewException;

import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * A Black Cauldron tool that uses the Java AGI interpreter's ResourceFrame that provides
 * viewers for the various resource types.
 *
 * @author Lance Ewing
 */
public class BCViewer {

    /**
     * The directory chosen by the user as being the one with the BC disk images in it.
     */
    protected File diskImageDirectory;

    protected ResourceProvider resourceProvider;

    protected ResourceCache resourceCache;

    /**
     * Constructor for BCViewer.
     *
     * @param args The command line arguments.
     * @throws IOException
     * @throws CorruptedResourceException
     */
    public BCViewer(String[] args) throws IOException, CorruptedResourceException {
        if (args.length == 0) {
            // If there are no command line parameters, then ask the user to choice where
            // the Apple II BC disk images are.
            File chosenFile = getDiskImageDirectory();
            if (chosenFile != null) {
                if (chosenFile.isDirectory()) {
                    diskImageDirectory = chosenFile;
                } else {
                    diskImageDirectory = chosenFile.getParentFile();
                }
            } else {
                // No file was chosen, so return immediately.
                return;
            }

        } else {
            diskImageDirectory = new File(args[0]);
        }

        // This array holds the configuration of where to find the various data files within the disk images.
        LogicalFile[] logicalFiles = new LogicalFile[]{
                new LogicalFile(diskImageDirectory, "bc_1f.do", 0x001700, true, true, LogicalFileType.DIR, "LOG"),
                new LogicalFile(diskImageDirectory, "bc_1f.do", 0x001C00, true, true, LogicalFileType.DIR, "PIC"),
                new LogicalFile(diskImageDirectory, "bc_1f.do", 0x001D00, true, true, LogicalFileType.DIR, "VIEW"),
                new LogicalFile(diskImageDirectory, "bc_1f.do", 0x001900, true, true, LogicalFileType.DIR, "SND"),
                new LogicalFile(diskImageDirectory, "bc_1f.do", 0x001300, true, true, LogicalFileType.OBJECT, "OBJECT"),
                new LogicalFile(diskImageDirectory, "bc_1f.do", 0x001500, true, true, LogicalFileType.WORDS, "WORDS.TOK"),
                new LogicalFile(diskImageDirectory, "bc_1f.do", 0x01BA00, true, false, LogicalFileType.VOL, 0),
                new LogicalFile(diskImageDirectory, "bc_1f.do", 0x015400, true, false, LogicalFileType.VOL, 1),
                new LogicalFile(diskImageDirectory, "bc_1b.do", 0x000000, true, false, LogicalFileType.VOL, 2),
                new LogicalFile(diskImageDirectory, "bc_2f.do", 0x000000, true, false, LogicalFileType.VOL, 3),
                new LogicalFile(diskImageDirectory, "bc_2b.do", 0x000000, true, false, LogicalFileType.VOL, 4),
                new LogicalFile(diskImageDirectory, "bc_3f.do", 0x000000, true, false, LogicalFileType.VOL, 5)
        };

        resourceProvider = new KQ2ResourceProvider(diskImageDirectory, logicalFiles);
        resourceCache = new ResourceCache(resourceProvider);
    }

    public static void main(String[] args) throws IOException, ResourceException, PictureException, LogicException, ViewException {
        System.setProperty("com.sierra.agi.logic.LogicProvider", "com.sierra.agi.logic.debug.DebugLogicProvider");
        System.setProperty("com.sierra.agi.word.WordsProvider", "com.agifans.jagi.appleii.kq2.KQ2WordProvider");

        BCViewer viewer = new BCViewer(args);
        viewer.run();
    }

    /**
     * Ask the user to chose the first disk of the Apple II BC game.
     *
     * @return File representing the first disk of the Apple II BC game.
     */
    protected File getDiskImageDirectory() {
        FileDialog dialog = new FileDialog(new Frame(), "Choose the first disk of the Apple II BC game", FileDialog.LOAD);
        dialog.setVisible(true);

        String dir = dialog.getDirectory();
        String file = dialog.getFile();

        dialog.dispose();

        if ((dir != null) && (file != null)) {
            return new File(dir, file);
        } else {
            return null;
        }
    }

    public void run() {
        ResourceFrame frame = new ResourceFrame(resourceCache);
        frame.setVisible(true);
    }
}
