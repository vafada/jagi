package com.agifans.jagi.appleii.kq2;

import com.agifans.jagi.res.v1.LogicalFile;
import com.agifans.jagi.res.v1.LogicalFileType;
import com.sierra.agi.res.CorruptedResourceException;
import com.sierra.agi.res.ResourceCache;
import com.sierra.agi.res.ResourceProvider;

import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * A base class for any tool built as part of the investigation in to
 * the Apple II KQ2 AGI v1.10 interpreter.
 *
 * @author Lance Ewing
 */
public abstract class KQ2Tool {

    /**
     * The directory chosen by the user as being the one with the KQ2 disk images in it.
     */
    protected File diskImageDirectory;

    protected ResourceProvider resourceProvider;

    protected ResourceCache resourceCache;

    /**
     * Constructor for KQ2Tool.
     *
     * @param args The command line arguments.
     * @throws IOException
     * @throws CorruptedResourceException
     */
    public KQ2Tool(String[] args) throws IOException, CorruptedResourceException {
        if (args.length == 0) {
            // If there are no command line parameters, then ask the user to choice where
            // the Apple II KQ2 disk images are.
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
                new LogicalFile(diskImageDirectory, "kq2_1a.do", 0x001000, true, true, LogicalFileType.DIR, "LOG"),
                new LogicalFile(diskImageDirectory, "kq2_1a.do", 0x001300, true, true, LogicalFileType.DIR, "PIC"),
                new LogicalFile(diskImageDirectory, "kq2_1a.do", 0x001900, true, true, LogicalFileType.DIR, "VIEW"),
                new LogicalFile(diskImageDirectory, "kq2_1a.do", 0x001600, true, true, LogicalFileType.DIR, "SND"),
                new LogicalFile(diskImageDirectory, "kq2_1a.do", 0x002900, true, true, LogicalFileType.OBJECT, "OBJECT"),
                new LogicalFile(diskImageDirectory, "kq2_1a.do", 0x003000, true, true, LogicalFileType.WORDS, "WORDS.TOK"),
                new LogicalFile(diskImageDirectory, "kq2_1a.do", 0x01A000, true, false, LogicalFileType.VOL, 0),
                new LogicalFile(diskImageDirectory, "kq2_1a.do", 0x012000, true, false, LogicalFileType.VOL, 1),
                new LogicalFile(diskImageDirectory, "kq2_1b.do", 0x000000, true, false, LogicalFileType.VOL, 2),
                new LogicalFile(diskImageDirectory, "kq2_2a.do", 0x000000, true, false, LogicalFileType.VOL, 3),
                new LogicalFile(diskImageDirectory, "kq2_2b.do", 0x000000, true, false, LogicalFileType.VOL, 4),
                new LogicalFile(diskImageDirectory, "kq2_3a.do", 0x000000, true, false, LogicalFileType.VOL, 5)
        };

        resourceProvider = new KQ2ResourceProvider(diskImageDirectory, logicalFiles);
        resourceCache = new ResourceCache(resourceProvider);
    }

    /**
     * Ask the user to chose the first disk of the Apple II KQ2 game.
     *
     * @return File representing the first disk of the Apple II KQ2 game.
     */
    protected File getDiskImageDirectory() {
        FileDialog dialog = new FileDialog(new Frame(), "Choose the first disk of the Apple II KQ2 game", FileDialog.LOAD);
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
}
